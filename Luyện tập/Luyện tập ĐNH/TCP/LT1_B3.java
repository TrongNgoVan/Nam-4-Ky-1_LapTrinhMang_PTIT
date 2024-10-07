/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt1;
import java.io.*;
import java.net.*;

/**
 *
 * @author MEDIAMART PHU SON
 */
public class LT1_B3 {
    




    public static void main(String[] args) {
        // Địa chỉ IP của server và cổng kết nối
        String serverAddress = "172.188.19.218";
        int serverPort = 1606;

        // Mã sinh viên và mã câu hỏi
        String studentCode = "B21DCCN197";
        String questionCode = "8ApX920";

        try {
            // Kết nối tới server
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Kết nối tới server thành công!");

            // Tạo BufferedWriter và BufferedReader để gửi và nhận dữ liệu
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi chuỗi mã sinh viên và mã câu hỏi
            String message = studentCode + ";" + questionCode;
            writer.write(message);
            writer.newLine(); // Thêm dòng mới để gửi dữ liệu
            writer.flush(); // Đảm bảo dữ liệu được gửi ngay

            // Nhận danh sách tên miền từ server
            String domainList = reader.readLine();
            System.out.println("Danh sách tên miền nhận được từ server: " + domainList);

            // Tìm kiếm các tên miền .edu
            String[] domains = domainList.split(", ");
            StringBuilder eduDomains = new StringBuilder();
            for (String domain : domains) {
                if (domain.endsWith(".edu")) {
                    eduDomains.append(domain).append(", ");
                }
            }

            // Xóa dấu phẩy và khoảng trắng cuối cùng nếu có
            if (eduDomains.length() > 0) {
                eduDomains.setLength(eduDomains.length() - 2); // Xóa 2 ký tự ", "
            }

            // Gửi danh sách tên miền .edu lên server
            writer.write(eduDomains.toString());
            writer.newLine();
            writer.flush();

            // Đóng kết nối
            reader.close();
            writer.close();
            socket.close();
            System.out.println("Kết nối đã đóng.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
}

