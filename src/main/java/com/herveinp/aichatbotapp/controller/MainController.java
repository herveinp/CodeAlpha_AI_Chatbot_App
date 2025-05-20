package com.herveinp.aichatbotapp.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.kordamp.ikonli.javafx.FontIcon;

import com.herveinp.aichatbotapp.util.EncryptorAndDecryptorUtil;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class MainController {
	// FXML injected fields
	@FXML
	private TextArea userInputTextArea;

	@FXML
	private FontIcon sendButtonIcon;

	@FXML
	private VBox chatDisplayVBox;

	private Stage mainStage;
	private SecretKey secretKey;
	private AIController aiController;

	// Runs some java codes when initialization is complete.
	@FXML
	public void initialize() throws Exception {
		String userName = getLoggedInUsername();
		String welcomeMessage = String.format("Hello, %s. I'm your AI assistant. How can I help you today?", userName);
		displayAiMessage(welcomeMessage);
		
		secretKey = EncryptorAndDecryptorUtil.getSecretKey();
		String decryptedHuggingFaceAPIToken = getDecryptedHuggingFaceAPIToken();

		if (decryptedHuggingFaceAPIToken != null) {
			aiController = new AIController(decryptedHuggingFaceAPIToken);
		} else {
			displayAiMessage("The app can't find your Hugging Face API token. Please enter it in the login view.");
		}
	}

	// Method getLoggedInUsername()
	private String getLoggedInUsername() {
		try (BufferedReader reader = new BufferedReader(new FileReader("data/cookies.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("name=")) {
					return line.substring("name=".length());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "User";
	}

	// Method handleSendMessage()
	@FXML
	private void handleSendMessage(MouseEvent event) {
		String userPrompt = getUserPrompt();
		if (!userPrompt.isEmpty()) {
			displayUserMessage(userPrompt);
			userInputTextArea.clear();

			// Send user prompt to AI
			try {
				aiController.getAiResponse(userPrompt).thenAccept(aiResponse -> {
					javafx.application.Platform.runLater(() -> {
						displayAiMessage(aiResponse);
					});
				}).exceptionally(e -> {
					javafx.application.Platform.runLater(() -> {
						displayAiMessage("Sorry, an error occured: " + e.getMessage());
					});

					return null;
				});
			} catch (Exception e) {
				javafx.application.Platform.runLater(() -> {
					displayAiMessage("\"An error occurred while sending the request to AI: " + e.getMessage());
				});
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Main App Error");
			alert.setHeaderText("User Prompt is empty");
			alert.setContentText("You have to enter your prompt into the text area field.");
			alert.showAndWait();
		}
	}

	// Method getUserPrompt()
	public String getUserPrompt() {
		return userInputTextArea.getText().trim();
	}

	// Method displayUserMessage(String userMessage)
	private void displayUserMessage(String userMessage) {
		HBox messageContainer = new HBox(10);
		messageContainer.setAlignment(Pos.CENTER_LEFT);
		messageContainer.setPrefHeight(53.0);
		messageContainer.setPrefWidth(476.0);

		FontIcon userIcon = new FontIcon("fas-user-circle:25");
		HBox.setMargin(userIcon, new Insets(0, 10, 0, 0));

		Text messageText = new Text(userMessage);
		TextFlow textFlow = new TextFlow(messageText);
		textFlow.setPrefHeight(75.0);
		textFlow.setPrefWidth(454.0);
		textFlow.setTextAlignment(TextAlignment.JUSTIFY);

		messageContainer.getChildren().addAll(userIcon, textFlow);
		chatDisplayVBox.getChildren().add(messageContainer);
	}

	// Method getEncryptedHuggingFaceAPIToken()
	private String getEncryptedHuggingFaceAPIToken() {
		try (BufferedReader reader = new BufferedReader(new FileReader("data/cookies.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("huggingFaceAPIToken=")) {
					return line.substring("huggingFaceAPIToken=".length());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// Method getDecryptedHuggingFaceAPIToken()
	private String getDecryptedHuggingFaceAPIToken() {
		String encryptedHuggingFaceAPIToken = getEncryptedHuggingFaceAPIToken();
		if (encryptedHuggingFaceAPIToken != null && secretKey != null) {
			try {
				return EncryptorAndDecryptorUtil.decrypt(encryptedHuggingFaceAPIToken, secretKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	// Method displayAiMessage(String aiMessage)
	private void displayAiMessage(String aiMessage) {
		HBox messageContainer = new HBox(10);
		messageContainer.setAlignment(Pos.CENTER_LEFT);
		messageContainer.setPrefHeight(53.0);
		messageContainer.setPrefWidth(476.0);

		FontIcon aiIcon = new FontIcon("fas-robot:20");
		HBox.setMargin(aiIcon, new Insets(0, 10, 0, 0));

		Text messageText = new Text(aiMessage);
		TextFlow textFlow = new TextFlow(messageText);
		textFlow.setPrefHeight(75.0);
		textFlow.setPrefWidth(454.0);
		textFlow.setTextAlignment(TextAlignment.JUSTIFY);

		messageContainer.getChildren().addAll(aiIcon, textFlow);
		chatDisplayVBox.getChildren().add(messageContainer);
	}

	public void setMainStage(Stage stage) {
	    this.mainStage = stage;
	    this.mainStage.setOnCloseRequest(event -> {
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setTitle("Close App");
	        alert.setContentText(
	                "This app will be closed and will automatically delete cookies and save your chat history. " +
	                "Are you sure you want to continue?");

	        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
	        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
	            System.out.println("User confirmed closing.");
	            deleteCookiesFile();
	            saveChatHistory();
	        } else {
	            System.out.println("User cancelled closing.");
	            event.consume();
	        }
	    });
	}

	private void deleteCookiesFile() {
		File cookiesFile = new File("data/cookies.txt");
		if (cookiesFile.exists()) {
			if (cookiesFile.delete()) {
				System.out.println("cookies.txt deleted successfully.");
			} else {
				System.err.println("Failed to delete cookies.txt.");
			}
		} else {
			System.out.println("cookies.txt does not exist.");
		}
	}

	private void saveChatHistory() {
		File historyFile = new File("data/historyChat.txt");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile))) {
			for (Node node : chatDisplayVBox.getChildren()) {
				if (node instanceof HBox) {
					HBox messageContainer = (HBox) node;
					String message = "";
					for (Node child : messageContainer.getChildren()) {
						if (child instanceof TextFlow) {
							TextFlow textFlow = (TextFlow) child;
							message = textFlow.getChildren().stream().filter(n -> n instanceof Text)
									.map(n -> ((Text) n).getText()).collect(Collectors.joining(" "));
							break;
						}
					}

					if (!message.isEmpty()) {
						writer.write(getMessagePrefix(messageContainer) + message);
						writer.newLine();
					}
				}
			}

			System.out.println("Chat history saved to historyChat.txt");

		} catch (IOException e) {
			System.err.println("Failed to save chat history: " + e.getMessage());
		}
	}

	private String getMessagePrefix(HBox messageContainer) {
		Node firstNode = messageContainer.getChildren().get(0);

		if (firstNode instanceof FontIcon) {
			String iconLiteral = ((FontIcon) firstNode).getIconLiteral();

			if (iconLiteral.startsWith("fas-user-circle")) {
				return "User: ";
			} else if (iconLiteral.startsWith("fas-robot")) {
				return "AI: ";
			}
		}

		return "";
	}
}