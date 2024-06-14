package View;

import Controller.ChatController;
import Model.Chat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ChatPane extends BorderPane {
    private ChatController chatController;
    private TextFlow chatArea;
    private TextField messageField;
    private Button sendButton;
    private SimpleDateFormat timestampFormat;

    public ChatPane(int gameId) {
        this.chatController = new ChatController(gameId);
        this.timestampFormat = new SimpleDateFormat("HH:mm"); // Only hours and minutes
        initializeUI();
        startAutoRefresh(); // Start the auto-refresh thread
        refreshChat(); // Call refreshChat after initializing the UI
    }

    private void initializeUI() {
        // Chat area
        chatArea = new TextFlow();
        chatArea.setPadding(new Insets(10, 10, 10, 15)); // Reduced padding for TextFlow
        chatArea.setPrefSize(300, 300); // Set preferred size for chat area
        chatArea.setMaxWidth(300); // Set maximum width for chat area

        ScrollPane scrollPane = new ScrollPane(chatArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(300, 300);

        // Message input field
        messageField = new TextField();
        messageField.setPromptText("Enter your message");
        messageField.setPrefWidth(200); // Set preferred width for message field
        messageField.setMaxWidth(200); // Set maximum width for message field

        // Send button
        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        // Layout
        HBox inputBox = new HBox(10, messageField, sendButton);
        HBox.setHgrow(messageField, Priority.ALWAYS);

        VBox chatBox = new VBox(10, scrollPane, inputBox);
        chatBox.setPadding(new Insets(10));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        BorderPane.setMargin(chatBox, new Insets(10));
        this.setRight(chatBox); // Set chatBox to the right side
        BorderPane.setAlignment(chatBox, Pos.TOP_RIGHT);
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            boolean success = chatController.sendMessage(message);
            if (success) {
                messageField.clear();
                refreshChat();
            } else {
                // Optioneel: Toon een foutmelding
                Alert alert = new Alert(Alert.AlertType.ERROR, "You can only send one message per timestamp.");
                alert.showAndWait();
            }
        }
    }

    private void refreshChat() {
        List<Chat> messages = chatController.getMessages();
        Map<Integer, String> playerMap = chatController.getPlayerMap();
        chatArea.getChildren().clear();
        for (Chat message : messages) {
            String playerName = playerMap.getOrDefault(message.getIdPlayer(), "Unknown");
            String timestamp = timestampFormat.format(message.getTime());

            Text nameText = new Text(playerName);
            nameText.setStyle("-fx-font-weight: bold");

            Text timeText = new Text(" [" + timestamp + "]: ");
            timeText.setStyle("-fx-font-weight: bold");

            Text messageText = new Text(message.getMessage() + "\n");

            chatArea.getChildren().addAll(nameText, timeText, messageText);
        }
    }

    private void startAutoRefresh() {
//        Thread autoRefreshThread = new Thread(() -> {
//            try {
//                while (true) {
//                    Thread.sleep(5000); // Refresh every 5 seconds
//                    refreshChat();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        autoRefreshThread.setDaemon(true);
//        autoRefreshThread.start();
    }
}
