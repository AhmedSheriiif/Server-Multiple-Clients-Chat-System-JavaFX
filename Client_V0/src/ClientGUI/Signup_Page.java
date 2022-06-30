package ClientGUI;

import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Signup_Page extends Application {

    // Create A Client
    Client client;  // Client that talks with the Server


    @Override
    public void start(Stage primaryStage) throws Exception {

        // CONNECTING TO THE SERVER
        try {
            client = new Client(); // Create A Client and connect to server
        } catch (Exception ex) { // if a problem happened output an error and close the program
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("CONNECTING TO SERVER ERROR");
            alert.setContentText("Error in connecting to the server, try again later..");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    primaryStage.close(); // close the program
                }
            });
        }

        ////////////////////////////// CONTROLS /////////////////
        // Labels
        Label usernameLabel = new Label("Username: ");
        Label passwordLabel = new Label("Password: ");
        Label confirmPassLabel = new Label("Confirm Password: ");
        Label resultMsg = new Label("");

        // TextFields
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPassField = new PasswordField();

        // Buttons
        Button signupBtn = new Button("Sign Up");
        Button cancelBtn = new Button("Cancel");
        signupBtn.setMinWidth(100);
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
        gridPane.add(confirmPassLabel, 0, 2);
        gridPane.add(confirmPassField, 1, 2);
        gridPane.add(signupBtn, 0, 3);
        gridPane.add(cancelBtn, 1, 3);
        gridPane.add(resultMsg, 0, 4, 2, 1);   // adding the resultmsg with 2 colspan to center it in the GUI

        StackPane rootPane = new StackPane(gridPane);

        
        
        ///////////////////////////////// EVENTS /////////////////////////
        // SignUp Button
        signupBtn.setOnAction(e -> {
            // Getting values from text fields
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPass = confirmPassField.getText();

            boolean allGood = true; // boolean that everything in entries is valid

            // Checking That all Fields have values
            if (username == "" || password == "" || confirmPass == "") {
                resultMsg.setText("All fields must have a value");
                allGood = false;
            }

            // Checking Both Password Fields (Password and confirm password)
            if (!password.equals(confirmPass)) {
                resultMsg.setText("Password Not Match");
                allGood = false;
            }

            if (allGood) {
                // Send username and password to Server - Send 'SIGNUP' Flag to the Server
                String Flag = "SIGNUP";
                String msg = username + "$" + password + "$" + Flag; // creating the message
                client.sendMsg(msg);  // sending the message
                try {
                    String ans = client.AcceptMessage(); // Geting reply from server
                    
                    if (ans.equals("0")) {  // Problem in user authentication
                        resultMsg.setText("Username Already Exists, try another one");

                    } 
                    else if (ans.equals("1")) { // User Signup Success - Opening Chat Page
                        client.setUsername(username); // updating the client with the username
                        Chat_Page chat = new Chat_Page(); // opening the chat page
                        Stage newStage = new Stage();
                        newStage.setUserData(client); // sending the client information to the chat page

                        // Start Chat Page
                        try {
                            primaryStage.close(); // closing current stage
                            chat.start(newStage); // starting chat page
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
            MainPage main = new MainPage();
            Stage newStage = new Stage();
            try {
                main.start(newStage);
                client.closeConnection(); // Closing the current client
                primaryStage.close(); // Closing Current Page
            } catch (Exception ex) {
            }
        }
        );

        
        ////////////////////////// SCENE & STAGE PROPERTIES /////////////////////
        Scene scene = new Scene(rootPane, 400, 300); // size of scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("CHAT | Register"); // title of window
        primaryStage.setResizable(false); // cannot be resized
        primaryStage.show();
    }

}
