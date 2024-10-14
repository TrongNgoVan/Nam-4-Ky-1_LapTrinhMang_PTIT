package TCP;
import java.io.*;
import java.net.*;

/**
 *
 * @author Ngọ Văn Trọng
 */


public class B2_ByteStream{
    
    public static void main(String[] args) {
     
         String svh = "203.162.10.109";
         int svp = 806;
         
         String msv = "B21DCCN726";
         String code = "k0NdHaeG";
         

        try {
           
            Socket socket = new Socket(svh, svp);
            System.out.println("Kết nối tới server thành công!");

      
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

         
            String mess = msv + ";" + code;
            out.write(mess.getBytes());
            out.flush(); 

            // Nhận dữ liệu từ server là một chuỗi gồm các giá trị nguyên được phân tách bằng "|"
            byte[] bytenhan = new byte[1024];
            int docbyte = in.read(bytenhan);
            String chuoinhan = new String(bytenhan, 0, docbyte);
            System.out.println("Dữ liệu nhận được từ server: " + chuoinhan);

            // Tách các số nguyên từ chuỗi nhận được và tính tổng
            String[] mangso = chuoinhan.split("\\|");
            int sum = 0;
            for (String so : mangso) {
                sum += Integer.parseInt(so.trim());
            }
            System.out.println("Tổng các số nguyên: " + sum);

            // Gửi giá trị tổng lên server
            String tong = String.valueOf(sum);
            out.write(tong.getBytes());
            out.flush();

            // Đóng kết nối
            in.close();
            out.close();
            socket.close();
            System.out.println("Kết nối đã đóng.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
