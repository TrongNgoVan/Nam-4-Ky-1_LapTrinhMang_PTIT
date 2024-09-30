package LoginClient;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String fullname;
    private String msv;

    public User() {
    }

    public User(String username, String password, String fullname, String msv) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.msv = msv;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
    }

    // Override phương thức toString để hiển thị thông tin chi tiết của User
    @Override
    public String toString() {
        return "Username: " + username + ", Fullname: " + fullname + ", MSSV: " + msv;
    }
}
