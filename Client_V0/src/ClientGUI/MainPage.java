package ClientGUI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainPage extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        /////////////////////// CONTROLS ////////////////////////
        // Labels
        Label text = new Label("Welcome to our Chatting Program");
        // Buttons
        Button Signup = new Button("Sign Up");
        Button Login = new Button("Login");
        Button Close = new Button("Close");
        
        ////////////////////// LAYOUT $ DESIGN //////////////////////
        Close.setMinWidth(100);
        Signup.setMinWidth(100);
        Login.setMinWidth(100);
        
        VBox vbox = new VBox();   // VBOx layout to add all elements in vertical position
        vbox.getChildren().addAll(text, Signup, Login, Close); // adding the elements
        vbox.setAlignment(Pos.CENTER); // Centralizing all elements
        vbox.setSpacing(10); // spacing between elements
        
        StackPane rootPane = new StackPane(vbox); // Root panel

        ////////////////////// EVENTS //////////////////////
        // Login Btn Action
        Login.setOnAction(e -> {
            Login_Page login = new Login_Page();
            Stage newStage = new Stage();
            try {
                primaryStage.close();
                login.start(newStage);
                
            } catch (Exception ex) {
            }
            primaryStage.close();
        });

        // Signup Btn Action
        Signup.setOnAction(e -> {
            Signup_Page signup = new Signup_Page();
            Stage newStage = new Stage();
            try {
                primaryStage.close();
                signup.start(newStage);    
            } catch (Exception ex) {
            }   
        });
        // Close Btn Action
        Close.setOnAction(e -> {
            primaryStage.close();
        });

        ///////////////////////// SCENE & STAGE PROPERTIES //////////////////////////
        Scene scene = new Scene(rootPane, 300, 150); // size of scene
        
        primaryStage.setScene(scene);  // Adding the scene
        primaryStage.setTitle("Chat Home Page"); // Stage Title
        primaryStage.setResizable(false);  // Remove resize of stage
        primaryStage.show();  // Showing stage
    }

    // Main Function
    public static void main(String[] args) {
        // Start the Application
        Application.launch(args);
    }
}