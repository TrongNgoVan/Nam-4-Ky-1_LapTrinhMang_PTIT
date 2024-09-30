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
public class ServerRun {
    public static void main(String[] args) {
        try {
            ServerView view = new ServerView(); // Đảm bảo khởi tạo không bị lỗi
            ServerControl control = new ServerControl(view);
        } catch (Exception e) {
            System.out.println("Lỗi khi khởi tạo ServerView hoặc ServerControl: " + e.getMessage());
        }
    }
}

