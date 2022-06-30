package ClientGUI;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Client {

    Socket mySocket;
    DataInputStream dis;
    PrintStream ps;
    String username;

    public Client() throws IOException {
        mySocket = new Socket("127.0.0.1", 5005);
        dis = new DataInputStream(mySocket.getInputStream());
        ps = new PrintStream(mySocket.getOutputStream());
    }

    public void sendMsg(String msg) {
        ps.println(msg);
    }

    public void closeConnection() throws IOException {
        ps.close();
        dis.close();
        mySocket.close();
    }

    public String AcceptMessage() throws IOException {
        String replyMsg = dis.readLine();
        return replyMsg;
    }
    
    public void setUsername(String un)
    {
        username = un + "";
    }

}
