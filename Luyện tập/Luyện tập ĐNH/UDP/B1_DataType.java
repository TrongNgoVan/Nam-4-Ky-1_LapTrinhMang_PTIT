
package UDP;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;



/**
 *
 * @author Ngọ Văn Trọng
 */
public class B1_DataType {
    
      public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            
            socket = new DatagramSocket();
            InetAddress svh = InetAddress.getByName("203.162.10.109");
            int svp = 2207;

          
            String msv = "B21DCCN726";  
            String mb = "Nvad8CYa";          
            String mess = ";" + msv + ";" + mb;
//            Tất cả đều quy về gửi byte Data hết
            byte[] guidata = mess.getBytes();
            DatagramPacket guigoi = new DatagramPacket(guidata, guidata.length, svh, svp);
            socket.send(guigoi);

            // Nhận thông điệp từ server
            byte[] nhandata = new byte[1024];
            DatagramPacket nhangoi = new DatagramPacket(nhandata, nhandata.length);
            socket.receive(nhangoi);

            String response = new String(nhangoi.getData(), 0, nhangoi.getLength());
            System.out.println("Nhan duoc tu server: " + response);

            // ở bước này là phân tách chuỗi thông điệp nhận 
            String[] thongdiep = response.split(";");
            String id = thongdiep[0];  // requestId
            String[] chuoiso = thongdiep[1].split(",");  // Chuỗi các số a1, a2, ..., a50

            // sau đó ta xử lý
            int[] mangso = Arrays.stream(chuoiso)
                                  .mapToInt(Integer::parseInt)
                                  .toArray();

           
            int max = Arrays.stream(mangso).max().getAsInt();
            int min = Arrays.stream(mangso).min().getAsInt();

            System.out.println("Max: " + max + ", Min: " + min);

// gửi lại thông điệp thôi
            String messgui = id + ";" + max + "," + min;
            byte[] datagui = messgui.getBytes();
            DatagramPacket resultPacket = new DatagramPacket(datagui, datagui.length, svh, svp);
            socket.send(resultPacket);

            // Nhớ đóng kết nối
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}

