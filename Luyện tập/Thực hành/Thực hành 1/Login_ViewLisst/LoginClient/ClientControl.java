/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoginClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientControl {
    private ClientView view;
    private String serverHost = "localhost";
    private int serverPort = 8889;

    public ClientControl(ClientView view) {
        this.view = view;
        this.view.addLoginListener(new LoginListener());
    }

    // Lắng nghe sự kiện login
    class LoginListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                // Lấy thông tin user từ giao diện
                User user = view.getUser();

                // Kết nối tới server
                Socket mySocket = new Socket(serverHost, serverPort);
                ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
                oos.writeObject(user); // Gửi user tới server

                // Nhận phản hồi từ server
                ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                Object o = ois.readObject();

                // Kiểm tra phản hồi là "ok" hay "false"
                if (o instanceof String) {
                    String result = (String) o;
                    if (result.equals("ok")) {
                        view.showMessage("Login successfully!");

                        // Sau khi đăng nhập thành công, nhận danh sách user
                        Object userListObj = ois.readObject(); // Nhận danh sách user
                        if (userListObj instanceof ArrayList<?>) {
                            ArrayList<User> userList = (ArrayList<User>) userListObj;

                            // Hiển thị danh sách user trên giao diện
                            view.showUserList(userList); // Gọi phương thức để hiển thị danh sách user
                        }
                    } else {
                        view.showMessage("Invalid username and/or password!");
                    }
                }

                // Đóng kết nối socket
                mySocket.close();
            } catch (Exception ex) {
                view.showMessage(ex.getMessage());
            }
        }
    }
}
