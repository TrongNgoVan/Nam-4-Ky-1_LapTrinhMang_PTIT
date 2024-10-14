
package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



/**
 *
 * @author Ngọ Văn Trọng
 */



public class B1_CharactorStream{
    public static void main(String[] arg) throws IOException{
        
        try{
         String svh = "203.162.10.109";
         int svp = 2208;
         
         String msv = "B21DCCN726";
         String code = "EojTsZdX";
         
         Socket sk = new Socket(svh,svp);
         
         System.out.println("Ket noi thanh cong");
         
         DataOutputStream out = new DataOutputStream(sk.getOutputStream());
         DataInputStream in = new DataInputStream(sk.getInputStream());
         
         String gui = msv + ";" + code ;
         out.writeUTF(gui);
         out.flush();
         int a = in.readInt();
         int b = in.readInt();
         
         System.out.println("Nhan duoc a= " + a + " va b= " + b);
         
         int ucln = gcd(a,b);
         int bcnn = lcm(a,b);
         
         out.writeInt(ucln);
         
         out.writeInt(bcnn);
         
         out.flush();
         
         in.close();
         out.close();
         
         sk.close();
         
         
         
         
        }catch(Exception e){
              e.printStackTrace();
        }
            
    }
    
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
}