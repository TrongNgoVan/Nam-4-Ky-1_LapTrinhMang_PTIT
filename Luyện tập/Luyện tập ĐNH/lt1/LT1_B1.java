/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt1;

/**
 *
 * @author MEDIAMART PHU SON
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author MEDIAMART PHU SON
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;
public class LT1_B1{

    /**
     * @param args the command line arguments
     */
    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    // Hàm tính BCNN (Bội chung nhỏ nhất)
    public static int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }
    public static void main(String[] args) {
        // Địa chỉ IP của server và cổng kết nối
        String serverAddress = "172.188.19.218";
        int serverPort = 1605;

        // Mã sinh viên và mã câu hỏi
        String studentCode = "B21DCCN197";
        String questionCode = "B8QvFC5";

        try {
            // Kết nối tới server
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Kết nối tới server thành công!");

            // Tạo luồng dữ liệu để gửi và nhận từ server
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // Gửi chuỗi mã sinh viên và mã câu hỏi
            String message = studentCode + ";" + questionCode;
            dos.writeUTF(message);
            dos.flush(); // Đảm bảo dữ liệu được gửi ngay

            // Nhận hai số nguyên a và b từ server
            int a = dis.readInt();
            int b = dis.readInt();
            System.out.println("Nhận được hai số từ server: a = " + a + ", b = " + b);

            // Tính UCLN và BCNN
            int ucln = gcd(a, b);
            int bcnn = lcm(a, b);
            System.out.println("UCLN = " + ucln + ", BCNN = " + bcnn);

            // Gửi UCLN và BCNN lần lượt về server
            dos.writeInt(ucln);
            dos.writeInt(bcnn);
            dos.flush();

            // Đóng kết nối
            dis.close();
            dos.close();
            socket.close();
            System.out.println("Kết nối đã đóng.");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
  
    
}
