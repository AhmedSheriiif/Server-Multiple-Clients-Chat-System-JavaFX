package ServerGUI;
import java.sql.*;

public class DataBase {
    public Connection con;
    
    public DataBase()
    {
        Connection con = null;
    }
    
    public void connect() throws ClassNotFoundException, SQLException
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con=DriverManager.getConnection("jdbc:mysql://localhost:3306/chat?zeroDateTimeBehavior=CONVERT_TO_NULL","root","");
    }
    
    public ResultSet SelectQuery(String query) throws SQLException
    {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }
    
    public void InsertQuery(String query) throws SQLException
    {
        Statement stmt = con.createStatement();
        boolean rs = stmt.execute(query);
    } 
    
    public void ResetUsersStatus() throws SQLException
    {
        PreparedStatement prStmt = con.prepareStatement("Update users set Status = ?");
        prStmt.setString(1, "Offline");
        prStmt.executeUpdate();
    }
    
    public void ChangeUserStatus(String Username, String Status) throws SQLException
    {
        PreparedStatement prStmt = con.prepareStatement("Update users set Status = ?  where Username = ?");
        prStmt.setString(1, Status);
        prStmt.setString(2, Username);
        prStmt.executeUpdate();
    }
}
