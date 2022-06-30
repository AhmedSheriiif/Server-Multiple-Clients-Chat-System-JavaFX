# Server-Multiple-Clients-Chat-System-JavaFX
- Building a chatting system with server and multiple clients using MySQL database
- The GUI is built using JavaFX
- Using TCP connections, we can connect multiple clients together to the same server

## Steps to Run the Chat GUI
1- Download the sql file and import it in the phpMyAdmin (Xampp) with database named chat, this will create a single table (users), each user has ID, username, password and status <br>
2- Connect your workspace (Apache Netbeans) to the created database<br>
3- Run the ***ServerGUI\src\ServerGUI\ChatServer.java***  --> This will start the Server Gui<br> 
4- Next is to run client(s) ***Client_V0\src\ClientGUI\MainPage.java*** and enjoy chatting <br>

## Features
- Signup/Login feature
- Status (Available - Busy - Offline), that will appear to the server and all clients
- Many clients can chat together at the same time
- Connecting the Server to the database to request signup, logins, status changing and chat messages requests

