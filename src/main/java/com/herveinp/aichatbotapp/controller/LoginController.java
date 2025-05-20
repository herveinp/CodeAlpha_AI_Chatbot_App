package com.herveinp.aichatbotapp.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.crypto.SecretKey;

import com.herveinp.aichatbotapp.util.EncryptorAndDecryptorUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginController {
	// FXML injected fields
	@FXML
	private Label nameLabel;

	@FXML
	private TextField nameField;

	@FXML
	private Label emailLabel;

	@FXML
	private TextField emailField;

	@FXML
	private Label huggingFaceAPITokenLabel;

	@FXML
	private PasswordField huggingFaceAPITokenField;

	@FXML
	private Button submitButton;

	private SecretKey secretKey;
	
	private String encryptedHuggingFaceAPIToken;

	// Runs some java codes when initialization is complete.
	@FXML
	public void initialize() {
		try {
			secretKey = EncryptorAndDecryptorUtil.getSecretKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method focusOnNameField().
	@FXML
	public void focusOnNameField(MouseEvent event) {
		nameField.requestFocus();
	}

	// Method focusOnEmailField().
	@FXML
	public void focusOnEmailField(MouseEvent event) {
		emailField.requestFocus();
	}

	// Method focusOnHuggingFaceAPIToken().
	@FXML
	public void focusOnHuggingFaceAPITokenField(MouseEvent event) {
		huggingFaceAPITokenField.requestFocus();
	}

	// Method handleSubmitAction().
	@FXML
	public void handleSubmitAction(ActionEvent event) {
		String name = nameField.getText().trim();
		String email = emailField.getText().trim();
		String huggingFaceAPIToken = huggingFaceAPITokenField.getText().trim();
			
		// Show an alert error to user if they don't input these required fields data.
		if (name.isEmpty() || email.isEmpty() || huggingFaceAPIToken.isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Log In Error");
			alert.setHeaderText(null);
			alert.setContentText("You have to input your full name, your email address, and your hugging face api token.");
			alert.showAndWait();
			return;
		}

		// Encrypt the Hugging Face API Token.
		try {
			encryptedHuggingFaceAPIToken = EncryptorAndDecryptorUtil.encrypt(huggingFaceAPIToken, secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create 'data' folder
		String directoryName = "data";
		File directory = new File(directoryName);

		if (directory.mkdir()) {
			System.out.println("Directory created successfully: " + directory.getAbsolutePath());
		} else {
			System.out.println("Failed to create directory: " + directory.getAbsolutePath());
		}
		
		// Save data into cookies.txt file.
		try (FileWriter writer = new FileWriter("data/cookies.txt")) {
			writer.write("name=" + name + "\n");
			writer.write("email=" + email + "\n");
			writer.write("huggingFaceAPIToken=" + encryptedHuggingFaceAPIToken);
			
			// Show an alert success to user as their input data was saved into application.
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Log In Success");
			alert.setHeaderText(null);
			alert.setContentText("Congratulations, you're now log in to AI Chatbot App.");
			alert.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Redirect to Main View.
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/herveinp/aichatbotapp/view/MainView.fxml"));
			Parent mainViewRoot = loader.load();
			MainController mainController = loader.getController();
			
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene mainViewScene = new Scene(mainViewRoot);
			currentStage.setScene(mainViewScene);
			
			mainController.setMainStage(currentStage);
			
            currentStage.show();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Main View Load Error");
			alert.setHeaderText(null);
			alert.setContentText("An error occurred while trying to open the application's main view: " + e.getMessage());
		}
	}
}
