/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaoracoban;

/**
 *
 * @author MEDIAMART PHU SON
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Calendar;

public class Vaoracoban {

    /**
     * @param args the command line arguments
     */
    




    public static class LogWriter extends Thread { // Thêm từ khóa static
        private String filename;
        private long time;

        public LogWriter(String name, String filename, long time) {
            super(name);
            this.filename = filename;
            this.time = time;
        }

        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    Writer wr = new BufferedWriter(new FileWriter(filename, true));
                    this.sleep(time);
                    wr.append(getName() + "[" + Calendar.getInstance().getTime() + "]: Log-" + i + "\r\n");
                    wr.close();
                } catch (Exception e) {
                    e.printStackTrace(); // Sử dụng e.printStackTrace() để in lỗi chi tiết hơn
                }
            }
        }
    }

    // Hàm main
    public static void main(String[] args) {
        LogWriter lw1 = new LogWriter("thread1", "Log.txt", 3000);
        LogWriter lw2 = new LogWriter("thread2", "Log.txt", 4000);
        lw1.start();
        lw2.start();
    }
}

    

