package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import DAO.DBConnect;
import model.User;
/**
 *
 * @author Ngọ Văn Trọng
 */
public class UserController {
    //  SQL
    private final String INSERT_USER = "INSERT INTO users (username, password, score, win, draw, lose,  avgTime) VALUES (?, ?, 0, 0, 0, 0, 0)";
    
    private final String CHECK_USER = "SELECT userId from users WHERE username = ? limit 1";
    
    private final String LOGIN_USER = "SELECT * FROM users WHERE username=? AND password=?";
    
    private final String GET_INFO_USER = "SELECT username, password, score, win, draw, lose,  avgTime FROM users WHERE username=?";
    
    private final String UPDATE_USER = "UPDATE users SET score = ?, win = ?, draw = ?, lose = ?, avgTime = ? WHERE username=?";
    
    private final String GET_RANKING = "SELECT username, score, win, lose,  avgTime FROM users ORDER BY score DESC, win DESC, lose ASC";


    //  Instance
    private final Connection con;
    
    public UserController() {
        this.con = DBConnect.getInstance().getConnection();
    }

    public String register(String username, String password) {
    	//  Check user exit
        try {
            PreparedStatement p = con.prepareStatement(CHECK_USER);
            p.setString(1, username);
            ResultSet r = p.executeQuery();
            if (r.first()) {
                return "failed;" + "Người dùng đã tồn tại";
            } else {
                r.close();
                p.close();
                p = con.prepareStatement(INSERT_USER);
                p.setString(1, username);
                p.setString(2, password);
                p.executeUpdate();
                p.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "success;";
    }
  
    public String login(String username, String password) {
    	//  Check user exit
        try {
            PreparedStatement p = con.prepareStatement(LOGIN_USER);
            //  Login User 
            p.setString(1, username);
            p.setString(2, password);
            ResultSet r = p.executeQuery();
            
            if (r.first()) {
                float score = r.getFloat("score");
                int win = r.getInt("win");
                int draw = r.getInt("draw");
                int lose = r.getInt("lose");
                float avgTime = r.getFloat("avgTime");
                return "success;" + username + ";" + score + ";" + win + ";" + draw +";" + lose + ";" + avgTime;
            } else {
                return "failed;" + "Mày hãy điền đúng tài khoản và mật khẩu";
            }
        } catch (SQLException e) {
        }
        return null;
    }
    
    public String getInfoUser(String username) {
    User user = new User();
    try {
        PreparedStatement p = con.prepareStatement(GET_INFO_USER);
        p.setString(1, username);
        
        ResultSet r = p.executeQuery();
        while (r.next()) {
            user.setUserName(r.getString("username"));
            user.setScore(r.getFloat("score"));
            user.setWin(r.getInt("win"));
            user.setDraw(r.getInt("draw"));
            user.setLose(r.getInt("lose"));
            user.setAvgTime(r.getFloat("avgTime"));
        }

        // Sử dụng String.format để định dạng số với 2 chữ số sau dấu phẩy
        return "success;" + user.getUserName() + ";" + 
               String.format("%.2f", user.getScore()) + ";" + 
               user.getWin() + ";" + 
               user.getDraw() + ";" + 
               user.getLose() + ";" + 
               String.format("%.2f", user.getAvgTime());
    } catch (SQLException e) {
        e.printStackTrace();
    }   
    return null;
}
  
    
    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        PreparedStatement p = con.prepareStatement(UPDATE_USER);
        //  Login User 
        p.setFloat(1, user.getScore());
        p.setInt(2, user.getWin());
        p.setInt(3, user.getDraw());
        p.setInt(4, user.getLose());
        p.setFloat(5, user.getAvgTime());
        p.setString(6, user.getUserName());

//            ResultSet r = p.executeQuery();
        rowUpdated = p.executeUpdate() > 0;
        return rowUpdated;
    }

    public User getUser(String username) {
        User user = new User();
        try {
            PreparedStatement p = con.prepareStatement(GET_INFO_USER);
            p.setString(1, username);
            
            ResultSet r = p.executeQuery();
            while(r.next()) {
                user.setUserName(r.getString("username"));
                user.setScore(r.getFloat("score"));
                user.setWin(r.getInt("win"));
                user.setDraw(r.getInt("draw"));
                user.setLose(r.getInt("lose"));
                user.setAvgTime(r.getFloat("avgTime"));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return null;
    }
      
   public String getRanking() {
    StringBuilder ranking = new StringBuilder();
    try {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(GET_RANKING);

        ranking.append("Rank\tUsername\tScore\tWin\tLose\n");
        int rank = 1;
        while (rs.next()) {
            String username = rs.getString("username");
            float score = rs.getFloat("score");
            int win = rs.getInt("win");
            int lose = rs.getInt("lose");

            ranking.append(rank).append("\t")
                   .append(username).append("\t")
                   .append(score).append("\t")
                   .append(win).append("\t")
                   .append(lose).append("\n");
            rank++;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "success;" + ranking.toString();
}

        
   
    public static void main(String[] args) {
        // Tạo một đối tượng UserController
         String username = "Ngọ Văn Trọng"; // Thay bằng tên tài khoản để kiểm tra
        String password = "123"; // Thay bằng mật khẩu để kiểm tra

        UserController userController = new UserController();
        String result = userController.login(username, password);

        // In kết quả ra console để kiểm tra
        if (result != null) {
            System.out.println("Kết quả đăng nhập: " + result);
        } else {
            System.out.println("Có lỗi xảy ra trong quá trình đăng nhập.");
        }
    }


}