package ServerGUI;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ChatServer extends Application {

    ServerSocket serverSocket;
    DataBase db = new DataBase();

    // CONTROLLERS
    TextArea logArea = new TextArea();
    VBox userOnline = new VBox();
    VBox userBusy = new VBox();
    VBox userOffline = new VBox();
    TitledPane titledOnline = new TitledPane("Online: ", userOnline);
    TitledPane titledBusy = new TitledPane("Busy: ", userBusy);
    TitledPane titledOffline = new TitledPane("Offline: ", userOffline);

    // Thread to Start Accepting Clients and Handle them
    Thread startAcceptingClients = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Socket s = serverSocket.accept();
                    new ChatHandler(s, db, logArea, userOnline, userBusy, userOffline);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
    });

    public void updateUserStatusBoxes() throws SQLException {
        ResultSet usersResultSet = db.SelectQuery("SELECT * from users");
        while (usersResultSet.next()) {
            String status = usersResultSet.getString("Status");
            if (status.equals("Online")) {
                Label test = new Label(usersResultSet.getString("Username"));
                userOnline.getChildren().add(test);
            }
            else if (status.equals("Busy")) {
                Label test = new Label(usersResultSet.getString("Busy"));
                userBusy.getChildren().add(test);
            }
            else if (status.equals("Offline")) {
                Label test = new Label(usersResultSet.getString("Username"));
                userOffline.getChildren().add(test);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ///////////////////// LAYOUT & DESIGN ///////////////////
        FlowPane flow = new FlowPane();
        flow.getChildren().addAll(logArea, titledOnline, titledBusy, titledOffline);

        StackPane rootPane = new StackPane(flow); // Root panel
        Scene scene = new Scene(rootPane, 700, 300); // size of scene
        primaryStage.setScene(scene);  // Adding the scene
        primaryStage.setTitle("SERVER"); // Stage Title
        primaryStage.setResizable(false);  // Remove resize of stage
        primaryStage.show();  // Showing stage

        /////////////////////  STARTING SERVER   //////////////
        logArea.appendText("Connecting to Database.....\n");
        db = new DataBase();
        db.connect();
        logArea.appendText("Connected to DB Successfully\n");

        logArea.appendText("Started Server..\n");
        serverSocket = new ServerSocket(5005);

        logArea.appendText("Waiting for Clients to Join....\n");

        ///////////////////   STARTING TO ACCEPT CLIENTS   ////////////////
        startAcceptingClients.start();

        ////////////////////   GETTING ALL USERS FROM DATABASE /////////////////
        db.ResetUsersStatus();
        updateUserStatusBoxes();

    }

    public static void main(String[] args) {
        System.out.println("Chat Server Started...");
        Application.launch(args);
    }
}
