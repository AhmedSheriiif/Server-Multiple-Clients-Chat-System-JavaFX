package ClientGUI;

import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Login_Page extends Application {

    private Client client;  // Client that talks with the Server

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // CONNECTING TO THE SERVER
        try {
            client = new Client(); // Create A Client and connect to server
        } catch (Exception ex) { // if a problem happened output an error and close the program
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("CONNECTING TO SERVER ERROR");
            alert.setContentText("Error in connecting to the server, try again later..");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    primaryStage.close(); // close the program
                }
            });
        }

        //////////////////////// CONTROLS //////////////////////////
        // Labels
        Label usernameLabel = new Label("Username: ");
        Label passwordLabel = new Label("Password: ");
        Label resultMsg = new Label("");

        // TextFields
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Buttons
        Button loginBtn = new Button("Login");
        Button cancelBtn = new Button("Cancel");
        loginBtn.setMinWidth(100);
        cancelBtn.setMinWidth(100);

        /////////////////////////////// LAYOUT //////////////////////////
        GridPane gridPane = new GridPane(); // adding elements in a grid layout
        gridPane.setPadding(new Insets(10, 10, 10, 10)); // padding

        gridPane.setVgap(5); //Setting the vertical and horizontal gaps between the columns 
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER); // alignment of elements

        // Adding Elements
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginBtn, 0, 2);
        gridPane.add(cancelBtn, 1, 2);
        gridPane.add(resultMsg, 0, 3, 2, 1);   // adding the resultmsg with 2 colspan to center it in the GUI

        StackPane rootPane = new StackPane(gridPane);

        /////////////////////////////// EVENTS /////////////////////////
        // Login Button
        loginBtn.setOnAction(e -> {
            // Getting Values from textfields
            String username = usernameField.getText();
            String password = passwordField.getText();

            boolean allGood = true; // boolean that everything entered is correct

            // Checking that all fields have values
            if (username == "" || password == "") {
                resultMsg.setText("All fields must have values");
                allGood = false;
            }

            // All is Good
            if (allGood) {
                // Send username and password to Server - Send 'LOGIN' Flag to the Server
                String Flag = "LOGIN";
                String msg = username + "$" + password + "$" + Flag;  // Building up the msg to send to server with delimeter "$"

                client.sendMsg(msg); // sending the msg to server
                try {
                    String ans = client.AcceptMessage(); // Geting reply from server

                    if (ans.equals("0")) {  // Problem in user authentication
                        resultMsg.setText("Invalid Username/Password");
                    } else if (ans.equals("1")) { // User Login Success - Opening Chat Page
                        client.setUsername(username); // updating the client with the username

                        Chat_Page chat = new Chat_Page(); // opening the chat page
                        Stage newStage = new Stage();
                        newStage.setUserData(client); // sending the client information to the chat page

                        // Opening Chat Page
                        try {
                            primaryStage.close(); // closing current stage
                            chat.start(newStage);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                       
                    }
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
        );

        // Cancel Button
        cancelBtn.setOnAction(e
                -> {
            MainPage main = new MainPage(); // opening Main Page
            Stage newStage = new Stage();

            try {
                main.start(newStage);
                client.closeConnection(); // Closing the current client
                primaryStage.close(); // Closing Current Page
            } catch (Exception ex) {
            }
        }
        );

        /////////////////////////// SCENE & STAGE PROPERTIES /////////////////////
        Scene scene = new Scene(rootPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CHAT | Login");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
