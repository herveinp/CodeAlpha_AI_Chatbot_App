# AI Chatbot App

AI Chatbot App is a JavaFX desktop application that allows users to interact with an AI assistant powered by Hugging Face's Llama model. The app features a modern user interface, secure credential storage, and persistent chat history.

## Features

- **Modern JavaFX UI:** Clean, responsive interface with FontAwesome icons.
- **User Authentication:** Login screen for user name, email, and Hugging Face API token.
- **Secure Token Storage:** API token is encrypted and stored locally.
- **Real-Time AI Chat:** Send prompts and receive AI-generated responses.
- **Chat History:** Chat is saved to a local file when the app closes.
- **Error Handling:** User-friendly alerts for errors and missing information.

## Technologies

- Java 21
- JavaFX
- Maven
- OkHttp (HTTP client)
- Jackson (JSON processing)
- Apache Commons Codec (encryption)
- Ikonli (FontAwesome icons)

## Getting Started

### Prerequisites

- Java 21+
- JavaFX SDK (JavaFX 21+)
  - Download from [GluonHQ](https://gluonhq.com/products/javafx/)
  - Set the `PATH_TO_FX` environment variable to the JavaFX SDK path
- Maven 3.6+
- A Hugging Face account and API token

### Build & Run

1. Clone the repository:
```bash
git clone cd aichatbotapp 
```
2. Build the project
```bash
mvn clean install
```
3. Run the application:
```bash
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar .\target\aichatbotapp-1.0.0.jar
```
### Usage

1. **Login:**  
   On startup, enter your full name, email, and Hugging Face API token. The token is encrypted and stored in `data/cookies.txt`.

2. **Chat:**  
   After login, type your message in the input area and click the send icon. The AI will respond in the chat window.

3. **Chat History:**  
   When you close the app, your chat history is saved to `data/historyChat.txt` and your credentials are deleted for privacy.

## Project Structure

```
aichatbotapp/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/herveinp/aichatbotapp/
│       │       ├── App.java
│       │       ├── controller/
│       │       ├── model/
│       │       └── util/
│       └── resources/
│           └── com/herveinp/aichatbotapp/view/
│               ├── LoginView.fxml
│               └── MainView.fxml
├── data/
│   ├── cookies.txt         # (created at runtime)
│   └── historyChat.txt     # chat history
├── pom.xml
└── readme.md
```

## Main Components

- [`App`](src/main/java/com/herveinp/aichatbotapp/App.java): JavaFX application entry point.
- [`LoginController`](src/main/java/com/herveinp/aichatbotapp/controller/LoginController.java): Handles login logic and credential storage.
- [`MainController`](src/main/java/com/herveinp/aichatbotapp/controller/MainController.java): Manages chat UI, message display, and chat history.
- [`AIController`](src/main/java/com/herveinp/aichatbotapp/controller/AIController.java): Handles API requests to Hugging Face.
- [`EncryptorAndDecryptorUtil`](src/main/java/com/herveinp/aichatbotapp/util/EncryptorAndDecryptorUtil.java): Encrypts and decrypts the API token.

## Security

- The Hugging Face API token is encrypted using AES and stored in `data/cookies.txt`.
- Credentials are deleted when the app closes.

## Customization

- To change the AI model, edit the model name in [`AIBodyRequest`](src/main/java/com/herveinp/aichatbotapp/model/AIBodyRequest.java).
- UI can be customized via the FXML files in [`src/main/resources/com/herveinp/aichatbotapp/view/`](src/main/resources/com/herveinp/aichatbotapp/view/).

## License

This project is licensed under the MIT License.

## Contact

For any questions or feedback, feel free to reach out:
- **Email:** [herve.inp@outlook.com](mailto:herve.inp@outlook.com)
- **Github:** [herveinp](https://github.com/herveinp)
- **LinkedIn:** [herveinp](https://www.linkedin.com/in/herveinp/)