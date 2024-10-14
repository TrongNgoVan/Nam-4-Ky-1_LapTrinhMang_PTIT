
package TCP;
import java.io.*;
import java.net.*;

/**
 *
 * @author Ngọ Văn Trọng
 */
public class B3_ByteStreamEX{
    




    public static void main(String[] args) {
         String svh = "203.162.10.109";
         int svp = 2207;
         
        String msv = "B21DCCN726";
        String code = "iKFLJmo0";

        try {
           
            Socket socket = new Socket(svh, svp);
            System.out.println("Kết nối tới server thành công!");

            
            BufferedWriter gui = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader doc = new BufferedReader(new InputStreamReader(socket.getInputStream()));

           
            String mess = msv + ";" + code;
            gui.write(mess);
            gui.newLine(); 
            gui.flush(); 

            
            String dsmien = doc.readLine();
            System.out.println("Danh sách tên miền nhận được từ server: " + dsmien);

            // Tìm kiếm các tên miền .edu
            String[] miens = dsmien.split(", ");
            StringBuilder miencopy = new StringBuilder();
            for (String mien : miens) {
                if (mien.endsWith(".edu")) {
                    miencopy.append(mien).append(", ");
                }
            }

            // Xóa dấu phẩy và khoảng trắng cuối cùng nếu có
            if (miencopy.length() > 0) {
                miencopy.setLength(miencopy.length() - 2); // Xóa 2 ký tự ", "
            }

            // Gửi danh sách tên miền .edu lên server
            gui.write(miencopy.toString());
            gui.newLine();
            gui.flush();

            // Đóng kết nối
            doc.close();
            doc.close();
            socket.close();
            System.out.println("Kết nối đã đóng.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }
}

