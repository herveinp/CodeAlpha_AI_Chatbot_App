package com.herveinp.aichatbotapp.model;

import java.util.List;

public class AIBodyRequest {
	private List<AIChatMessage> messages;
	private String model;
	private boolean stream;

	public AIBodyRequest(String userPrompt) {
		this.messages = List.of(new AIChatMessage("user", userPrompt));
		this.model = "meta-llama/Llama-3.1-8B-Instruct";
		this.stream = false;
	}

	public List<AIChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<AIChatMessage> messages) {
		this.messages = messages;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public boolean isStream() {
		return stream;
	}

	public void setStream(boolean stream) {
		this.stream = stream;
	}
}
