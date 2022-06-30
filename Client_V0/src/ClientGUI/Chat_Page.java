package ClientGUI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Chat_Page extends Application {

    private Client client;     // Client will be passed from Login/Signup Page
    private TextArea chatArea = new TextArea(); // Chatting Area, is made global so we access it by the acceptMessage fn.

    // Thread To Read Messages from Server while GUI is Running
    // This will be a while true loop that reads any message sent by server and show it on the chatting area
    Thread acceptMessage = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) { // infininte loop
                try {
                    String msg = client.AcceptMessage(); // reading message from the server
                    chatArea.appendText(msg + "\n"); // adding the message to the chat area
                } catch (IOException ex) {
                    System.out.println(ex);
                    break;
                }
            }
        }
    });

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Getting user name from register/login page
        client = (Client) primaryStage.getUserData(); // getting the client information passed by lgoin/signup 

        acceptMessage.start(); // Starting thread of accepting Messages from server

        /////////////////////////////// CONTROLS /////////////////////////
        // Radio Buttons Status (Online, Busy)
        RadioButton availavleRB = new RadioButton("Online");
        RadioButton busyRB = new RadioButton("Busy");
        RadioButton offlineRB = new RadioButton("Offline");

        ToggleGroup group = new ToggleGroup(); // Toggle Group to add RadioButtons to them
        availavleRB.setToggleGroup(group);
        busyRB.setToggleGroup(group);
        offlineRB.setToggleGroup(group);
        availavleRB.setSelected(true); // deafult selected

        VBox status = new VBox();
        status.getChildren().addAll(availavleRB, busyRB, offlineRB);
        TitledPane statusPane = new TitledPane("Status: Online", status); // TitledPane to add RadioButtons inside it
        statusPane.setExpanded(false); // Default is not Expanded
        statusPane.setMinWidth(100);

        // TextField
        TextField messageField = new TextField();

        // ChatArea
        chatArea.setEditable(false);
        chatArea.setMinWidth(350);

        // Buttons
        Button sendBtn = new Button("Send");
        Button saveBtn = new Button("Save");
        Button closeBtn = new Button("Close");
        sendBtn.setMinWidth(75);
        saveBtn.setMinWidth(150);
        closeBtn.setMinWidth(150);

        //////////////////////////////////////// LAYOUT //////////////////////////////////
        GridPane gridPane = new GridPane(); // adding elements in a grid layout

        gridPane.setVgap(5); //Setting the vertical and horizontal gaps between the columns 
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER); // alignment of elements

        // Adding Elements
        gridPane.add(chatArea, 0, 0, 3, 1);
        gridPane.add(statusPane, 3, 0);
        gridPane.add(messageField, 0, 1, 2, 1);
        gridPane.add(sendBtn, 2, 1);
        gridPane.add(saveBtn, 0, 2, 2, 1);
        gridPane.add(closeBtn, 2, 2, 2, 1);

        StackPane rootPane = new StackPane(gridPane);

        //////////////////////////////////////////// EVENTS //////////////////////////////////////
        // Send Button
        sendBtn.setOnAction(e -> {
            String message = messageField.getText(); // getting Text from text field
            String Flag = "CHATMSG";

            String msg = message + "$" + Flag; // Adding the message with the Flag
            client.sendMsg(msg); // Sending message to the server
            messageField.clear(); // clear the text field
        });

        // Save Button
        saveBtn.setOnAction(e -> {
            // Save Dialog and File Chooser Create
            FileChooser fileChooserSave = new FileChooser();
            fileChooserSave.setTitle("Save File");
            fileChooserSave.getExtensionFilters().addAll(
                    new ExtensionFilter("Text Files", "*.txt"));
            String allChat = chatArea.getText();  // Getting Messages from the chat area

            File file = fileChooserSave.showSaveDialog(primaryStage); // Creating a file to save in it

            if (file != null) { // if file is ok
                saveTextToFile(allChat, file); // save to file
            }
        });

        // RadioButton Group Selecting
        group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) -> {
            RadioButton rb = (RadioButton) group.getSelectedToggle();
            if (rb != null) {
                String s = rb.getText();
                statusPane.setText("Status: " + s);

                // Updating the Server Status
                String Flag = "CH_STATUS";
                String msg = client.username + "$" + s + "$" + Flag;  // Building up the msg to send to server with delimeter "$"

                client.sendMsg(msg); // sending the msg to server
            }
        });

        // Close Button
        closeBtn.setOnAction(e -> {
            // Updating the Server Status
            String Flag = "CH_STATUS";
            String msg = client.username + "$" + "Offline" + "$" + Flag;  // Building up the msg to send to server with delimeter "$"
            client.sendMsg(msg); // sending the msg to server
            
            primaryStage.close(); // closing the window
        });

        /////////////////////////// SCENE & STAGE PROPERTIES /////////////////////
        Scene scene = new Scene(rootPane, 630, 400); // size of window
        primaryStage.setScene(scene);
        primaryStage.setTitle("CHAT: " + client.username); // title
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Function to Save Text to File
    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer; // creating PrintWriter 
            writer = new PrintWriter(file); // Adding file to PrintWriter
            writer.println(content); // Printing the content
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
