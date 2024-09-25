/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoginServer;

/**
 *
 * @author MEDIAMART PHU SON
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import LoginClient.User;
import java.util.ArrayList;
public class ServerControl {
    private ServerView view;
    private Connection con;
    private ServerSocket myServer;
    private Socket clientSocket;
    private int serverPort = 8889;
    public ServerControl(ServerView view) {
        this.view = view;
        con = connectDb(); // Sử dụng connectDb để kết nối CSDL
        if (con != null) {
            openServer(serverPort);
            view.showMessage("TCP server is running...");
            while (true) {
                listenning();
            }
        } else {
            view.showMessage("Không thể kết nối đến cơ sở dữ liệu.");
        }
    }
    
   public static Connection connectDb(){
        
        try{
            
            Class.forName("com.mysql.jdbc.Driver");
            
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/loginsockettcp", "root", "");
            return connect;
        }catch(Exception e){e.printStackTrace();}
        return null;
    }
    private void openServer(int portNumber){
    try {
    myServer = new ServerSocket(portNumber);
    }catch(IOException e) {
    view.showMessage(e.toString());
    }
    }
    private void listenning(){
    try {
    clientSocket = myServer.accept();
    ObjectInputStream ois = 
    new ObjectInputStream(clientSocket.getInputStream());
    ObjectOutputStream oos = 
    new ObjectOutputStream(clientSocket.getOutputStream());
    Object o = ois.readObject();
    if(o instanceof User){
    User user = (User)o;
    if(checkUser(user)){
        oos.writeObject("ok");
        ArrayList<User> userList = getUsersFromDB();
        oos.writeObject(userList); 
    }
    else
        oos.writeObject("false");
    }
    }catch (Exception e) {
        view.showMessage(e.toString());
    }
    }
    private boolean checkUser(User user) throws Exception {
     String query = "Select * FROM users WHERE username ='" + user.getUsername() + "' AND password ='" + user.getPassword() + "'";
     try {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
     if (rs.next()) {
        return true;
     }
     }catch(Exception e) {
        throw e;
     } 
     return false;

    }
  private ArrayList<User> getUsersFromDB() throws Exception {
    ArrayList<User> userList = new ArrayList<>();
    String query = "SELECT username, fullname, msv FROM users"; // Giả sử bảng users có cột MSSV và Họ tên
    try {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            String username = rs.getString("username");
            String fullname = rs.getString("fullname");
            String msv = rs.getString("msv");
            String password = ""; // Nếu không cần mật khẩu từ DB, có thể để trống hoặc lấy từ đâu khác
            User user = new User(username, password, fullname, msv); // Sử dụng hàm khởi tạo mới của User
            userList.add(user);
        }
    } catch (Exception e) {
        throw e;
    }
    return userList;
}

}