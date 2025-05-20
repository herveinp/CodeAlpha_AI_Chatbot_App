package com.herveinp.aichatbotapp.controller;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herveinp.aichatbotapp.model.AIBodyRequest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIController {
	private final String apiURL = "https://router.huggingface.co/hf-inference/models/meta-llama/Llama-3.1-8B-Instruct/v1/chat/completions";
	private final String huggingFaceApiToken;
	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	private final OkHttpClient httpClient = new OkHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();

	// AIController Constructor
	public AIController(String huggingFaceApiToken) {
		this.huggingFaceApiToken = huggingFaceApiToken;
	}

	// Method getAiResponse(String userPrompt)
	public CompletableFuture<String> getAiResponse(String userPrompt) throws Exception {
		CompletableFuture<String> future = new CompletableFuture<>();
		
		try {
			AIBodyRequest aiBodyRequest = new AIBodyRequest(userPrompt);
			String jsonPayLoad = mapper.writeValueAsString(aiBodyRequest);
			RequestBody body = RequestBody.create(jsonPayLoad, JSON);
			
			Request request = new Request.Builder().url(apiURL).addHeader("Authorization", "Bearer " + huggingFaceApiToken)
					.addHeader("Content-Type", "application/json").post(body).build();
			
			httpClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					future.completeExceptionally(e);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					try (response) {
						String jsonResponse = response.body().string();
						
						if (response.isSuccessful()) {
							try {
								JsonNode root = mapper.readTree(jsonResponse);
								if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
									JsonNode firstChoice = root.get("choices").get(0);
									if (firstChoice.has("message")) {
										JsonNode message = firstChoice.get("message");
										if (message.has("content")) {
											future.complete(message.get("content").asText());
											return;
										}
									}
								} else if (root.isArray() && root.size() > 0) {
									if (root.get(0).has("generated_text")) {
										future.complete(root.get(0).get("generated_text").asText());
									} else if (root.get(0).isTextual()) {
										future.complete(root.get(0).asText());
									} else {
										System.err.println("API response format isn't recognized: " + jsonResponse);
										future.complete("Sorry, the AI response can't be processed.");
									}
								} else {
									System.err.println("API response format isn't recognized (It isn't array): " + jsonResponse);
									future.complete("Sorry, the AI response doesn't match the expected format.");
								}
							} catch (IOException e) {
								System.err.println("Failed to process JSON response: " + e.getMessage());
								future.complete("Sorry, an error occurred while processing the AI response.");
							}
						} else {
							System.err.println("API request failed with status code: " + response.code());
							System.err.println("Body: " + jsonResponse);
							future.complete("Sorry, an error occurred while contacting the AI.");
						}
					}
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		
		return future;
	}
}