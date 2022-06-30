package ServerGUI;

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

class ChatHandler extends Thread {

    DataInputStream dis;
    PrintStream ps;
    String username;
    String FLAG;
    DataBase db;
    TextArea chatArea;

    VBox Online_VBox;
    VBox Busy_VBox;
    VBox Offline_VBox;

    // Vector to add Clients in it (Like Array)
    static Vector<ChatHandler> clientsVector = new Vector<>();

    public ChatHandler(Socket cs, DataBase database, TextArea chat, VBox user_online, VBox user_busy, VBox user_offline) throws IOException, ClassNotFoundException, SQLException {
        db = database;
        chatArea = chat;
        Online_VBox = user_online;
        Busy_VBox = user_busy;
        Offline_VBox = user_offline;

        dis = new DataInputStream(cs.getInputStream());
        ps = new PrintStream(cs.getOutputStream());
        clientsVector.add(this);

        start();   // Start a Thread
    }

    // Function of start of thread
    @Override
    public void run() {
        chatArea.appendText("Client Is contacting the Server ****\n");
        while (true) {
            try {
                // Accepting Msgs from Client
                String str = dis.readLine();
                chatArea.appendText("Message Recieved: " + str + "\n");

                // Extracting Flag
                String[] contents = str.split("\\$");
                FLAG = contents[contents.length - 1];

                //// Checking Flags
                // SignUp
                if (FLAG.equals("SIGNUP")) {
                    String ans = SignupFunction(contents[0], contents[1]);
                    ps.println(ans);
                    if (ans.equals("1")) {
                        username = contents[0];
                        sendMessageToAll(username, "Connected");

                        // Update Status of User
                        updateStatusBox(username, "Online");
                    }
                    // Login
                } else if (FLAG.equals("LOGIN")) {
                    String ans = LoginFunction(contents[0], contents[1]);
                    ps.println(ans);
                    if (ans.equals("1")) {
                        username = contents[0];
                        sendMessageToAll(username, "Connected");

                        // Update Status of User
                        updateStatusBox(username, "Online");
                    }

                    // Chat Message
                } else if (FLAG.equals("CHATMSG")) {
                    sendMessageToAll(username, contents[0]);

                } else if (FLAG.equals("CH_STATUS")) {
                    username = contents[0];
                    String status = contents[1];
                    updateStatusBox(username, status);
                    chatArea.appendText(username + "Changed Status to -->" + status + "\n");
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    // Send message to all connected clients, (clients in the Vector)
    void sendMessageToAll(String username, String msg) {
        for (ChatHandler ch : clientsVector) {
            ch.ps.println(username + ": " + msg);
        }
        chatArea.appendText(username + ": " + msg + "\n");
    }

    // CheckLogin
    public String LoginFunction(String username, String password) {
        // User Authentication
        String query = MessageFormat.format("select * from users where Username = \"{0}\" and Password = \"{1}\"", username, password);
        try {
            ResultSet result = db.SelectQuery(query);
            if (result.next()) {
                return "1";
            } else {
                return "0";
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return "0";
    }

    // Register
    public String SignupFunction(String username, String password) {
        // Checking if username Exists in DB
        String query = MessageFormat.format("select * from users where Username = \"{0}\"", username);
        try {
            ResultSet result = db.SelectQuery(query);
            if (result.next()) {
                return ("0");
            } else {
                query = MessageFormat.format("INSERT INTO users values (DEFAULT, \"{0}\", \"{1}\", \"{2}\")", username, password, "Online");
                try {
                    db.InsertQuery(query);
                    return ("1");
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return ("0");
    }

    void updateStatusBox(String username, String status) {
        // To update Box OF Users
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    db.ChangeUserStatus(username, status);
                    updateUserStatusBoxes();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
        });
    }

    public void updateUserStatusBoxes() throws SQLException {
        ResultSet usersResultSet = db.SelectQuery("SELECT * from users");
        Online_VBox.getChildren().clear();
        Busy_VBox.getChildren().clear();
        Offline_VBox.getChildren().clear();
        while (usersResultSet.next()) {
            String status = usersResultSet.getString("Status");

            if (status.equals("Online")) {
                Label test = new Label(usersResultSet.getString("Username"));
                Online_VBox.getChildren().add(test);
            } else if (status.equals("Busy")) {
                Label test = new Label(usersResultSet.getString("Username"));
                Busy_VBox.getChildren().add(test);
            } else if (status.equals("Offline")) {
                Label test = new Label(usersResultSet.getString("Username"));
                Offline_VBox.getChildren().add(test);
            }
        }
    }
}
