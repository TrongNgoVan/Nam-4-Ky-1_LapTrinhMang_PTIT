
package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import run.ClientRun;

/**
 *
 * @author Ngọ Văn Trọng
 */
// Luồng chung của hệ thống: ClientController, có các phương thức listen để lắng nghe và received để nhận kết quả từ Server.
// phương thức còn lại để gửi dữ liệu lên Server.

public class ClientController {
     
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    String loginUser = null; // lưu tài khoản đăng nhập hiện tại
    String roomIdPresent = null; // lưu room hiện tại
    float score = 0;
    int win =0;
    int draw = 0;
    int lose = 0;
    float avgTime =0;
    float avgCompetitor = 0;
    
    
    Thread listener = null;

    public String connect(String addr, int port) {
        try {
            // getting ip 
            InetAddress ip = InetAddress.getByName(addr);

            // establish the connection with server port 
            s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 4000);
            System.out.println("Đã kết nối tới " + ip + ":" + port + ", localport:" + s.getLocalPort());

            // obtaining input and output streams
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connect success
            return "success";

        } catch (IOException e) {

            // connect failed
            return "failed;" + e.getMessage();
        }
    }

    private void listen() {
        boolean running = true;

        while (running) {
            try {
                // receive the data from server
                String received = dis.readUTF();

                System.out.println("RECEIVED: " + received);

                String type = received.split(";")[0];

                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "GET_LIST_ONLINE":
                        onReceiveGetListOnline(received);
                        break;
                    case "LOGOUT":
                        onReceiveLogout(received);
                        break;
                    case "INVITE_TO_CHAT":
                        onReceiveInviteToChat(received);
                        break;
                    case "GET_INFO_USER":
                        onReceiveGetInfoUser(received);
                        break;
                    case "GET_RANKING":
                        onReceiveGetRanking(received);
                        break;
                    case "ACCEPT_MESSAGE":
                        onReceiveAcceptMessage(received);
                        break;
                    case "NOT_ACCEPT_MESSAGE":
                        onReceiveNotAcceptMessage(received);
                        break;
                    case "LEAVE_TO_CHAT":
                        onReceiveLeaveChat(received);
                        break;
                    case "CHAT_MESSAGE":
                        onReceiveChatMessage(received);
                        break;
                    case "INVITE_TO_PLAY":
                        onReceiveInviteToPlay(received);
                        break;
                    case "ACCEPT_PLAY":
                        onReceiveAcceptPlay(received);
                        break;
                    case "NOT_ACCEPT_PLAY":
                        onReceiveNotAcceptPlay(received);
                        break;
                    case "LEAVE_TO_GAME":
                        onReceiveLeaveGame(received);
                        break;
                    case "CHECK_STATUS_USER":
                        onReceiveCheckStatusUser(received);
                        break;
                    case "START_GAME":
                        onReceiveStartGame(received);
                        break; 
                    case "RESULT_GAME":
                        onReceiveResultGame(received);
                        break;
                    case "ASK_PLAY_AGAIN":
                        onReceiveAskPlayAgain(received);
                        break;
                    case "GET_HISTORY":
                        onReceivedHistory(received);
                        break;
                        
                    case "EXIT":
                        running = false;
                }

            } catch (IOException ex) {
                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                running = false;
            }
        }

        try {
            // closing resources
            s.close();
            dis.close();
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // alert if connect interup
        JOptionPane.showMessageDialog(null, "Mất kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        ClientRun.closeAllScene();
        ClientRun.openScene(ClientRun.SceneName.CONNECTSERVER);
    }
    
    /***
     * Handle from client
     */
    public void login(String email, String password) {
        // prepare data
        String data = "LOGIN" + ";" + email + ";" + password;
        // send data
        sendData(data);
    }
    
    public void register(String email, String password) {
        // prepare data
        String data = "REGISTER" + ";" + email + ";" + password;
        // send data
        sendData(data);
    }
    
    public void logout () {
        this.loginUser = null;
        sendData("LOGOUT");
    }
    
    public void close () {
        sendData("CLOSE");
    }
    
    public void getListOnline() {
        sendData("GET_LIST_ONLINE");
    }
    
    
    public void getInfoUser(String username) {
        sendData("GET_INFO_USER;" + username);
    }
     public void getRanking(){
        sendData("GET_RANKING");
    }
     public void getHistory(String user) {
         sendData("GET_HISTORY;" + user);
    }
    public void checkStatusUser(String username) {
        sendData("CHECK_STATUS_USER;" + username);
    }
    
    // Chat
    public void inviteToChat (String userInvited) {
        sendData("INVITE_TO_CHAT;" + loginUser + ";" + userInvited);
    }
    
    public void leaveChat (String userInvited) {
        sendData("LEAVE_TO_CHAT;" + loginUser + ";" + userInvited);
    }
    
    public void sendMessage (String userInvited, String message) {
        
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        String formattedTime = currentTime.format(formatter);
        
        String chat = "[" + loginUser + "] : " + message + "\n";
        ClientRun.messageView.addSendMessage(chat, formattedTime);
            
        sendData("CHAT_MESSAGE;" + loginUser + ";" + userInvited  + ";" + message);
    }
   
    // Play game
    public void inviteToPlay (String userInvited) {
        sendData("INVITE_TO_PLAY;" + loginUser + ";" + userInvited);
    }
   
    public void leaveGame (String userInvited) { 
        sendData("LEAVE_TO_GAME;" + loginUser + ";" + userInvited + ";" + roomIdPresent);
    }
    
    public void startGame (String userInvited) { 
        sendData("START_GAME;" + loginUser + ";" + userInvited + ";" + roomIdPresent);
    }
    
    public void submitResult (String competitor,String result, String tong) { 
      
        if (result == "") {
            ClientRun.gameView.showMessage("Hãy điền đầy đủ!");
        } else {
//            ClientRun.gameView.pauseTime();
//            // Handle calculate time
            String[] splitted = ClientRun.gameView.pbgTimer.getString().split(":");
            String countDownTime = splitted[1];
            int time = 30 - Integer.parseInt(countDownTime);
          
            String data = "SUBMIT_RESULT;" + loginUser + ";" + competitor + ";" + roomIdPresent + ";" + result + ";" + time + ";"+ tong;
            System.out.println(data);
            sendData(data);
            ClientRun.gameView.afterSubmit();
        }
    }
    
    public void acceptPlayAgain() {
        sendData("ASK_PLAY_AGAIN;YES;" + loginUser);
    }
    
    public void notAcceptPlayAgain() {
        sendData("ASK_PLAY_AGAIN;NO;" + loginUser);
    }
    
    /***
     * Handle send data to server
     */
    public void sendData(String data) {
        try {
            dos.writeUTF(data);
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class
                .getName()).log(Level.SEVERE, null, ex);
        }
    }
      private void onReceiveGetRanking(String received) {
    // Kiểm tra xem nhận được dữ liệu thành công
      String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
  
        // Tách dữ liệu thành từng dòng
         String[] lines = splitted[2].split("\n");

        // Khởi tạo một StringBuilder để lưu thông tin xếp hạng
        StringBuilder rankingData = new StringBuilder();

        // Bắt đầu từ dòng đầu tiên (dòng tiêu đề) và bỏ qua nó
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) continue; // Bỏ qua dòng trống
            rankingData.append(lines[i].trim()).append("\n"); // Thêm mỗi dòng vào StringBuilder
        }

        // Gọi phương thức setRanking để cập nhật thông tin xếp hạng
        ClientRun.openScene(ClientRun.SceneName.RANKINGVIEW); // Chuyển đến cảnh xếp hạng
        ClientRun.rankingView.setRanking(rankingData.toString()); // Gọi phương thức hiển thị thông tin xếp hạng
    }
}
    private void onReceivedHistory(String received) {
            String[] splited = received.split(";");
            String result = splited[1]; 
            
            ClientRun.openScene(ClientRun.SceneName.HISTORYVIEW); 
            ClientRun.historyView.setHistory(result,loginUser); 
    }
           
    /***
     * Handle receive data from server
     */
    private void onReceiveLogin(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.loginView, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            // lưu user login
            this.loginUser = splitted[2];
            this.score = Float.parseFloat(splitted[3]) ;
            this.win = Integer.parseInt(splitted[4]);
            this.draw = Integer.parseInt(splitted[5]);
            this.lose = Integer.parseInt(splitted[6]);
            this.avgTime = Float.parseFloat(splitted[7]) ;
           
            // chuyển scene
            ClientRun.closeScene(ClientRun.SceneName.LOGIN);
            ClientRun.openScene(ClientRun.SceneName.HOMEVIEW);

            // auto set info user
            ClientRun.homeView.setUsername(loginUser);
            ClientRun.homeView.setUserScore(score);
            ClientRun.homeView.setUserWin(win);
            ClientRun.homeView.setUserDraw(draw);
            ClientRun.homeView.setUserLose(lose);
            ClientRun.homeView.setUserTime(avgTime);
        }
    }
    
    private void onReceiveRegister(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.registerView, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            JOptionPane.showMessageDialog(ClientRun.registerView, "Đăng ký thành công, vui lòng đăng nhập!");
            // chuyển scene
            ClientRun.closeScene(ClientRun.SceneName.REGISTER);
            ClientRun.openScene(ClientRun.SceneName.LOGIN);
        }
    }
    
    private void onReceiveGetListOnline(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            int userCount = Integer.parseInt(splitted[2]);
            ClientRun.homeView.setTotalOnline(userCount);
           
            Vector vheader = new Vector();
            vheader.add("Online");

            Vector vdata = new Vector();
            if (userCount > 1) {
                for (int i = 3; i < userCount + 3; i++) {
                    String user = splitted[i];
                    if (!user.equals(loginUser) && !user.equals("null")) {
                        Vector vrow = new Vector();
                        vrow.add(user);
                        vdata.add(vrow);
                    }
                }

                ClientRun.homeView.setListUser(vdata, vheader);
            } else {
                ClientRun.homeView.resetTblUser();
            }
            
        } else {
            JOptionPane.showMessageDialog(ClientRun.loginView, "Have some error!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onReceiveGetInfoUser(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userName = splitted[2];
            String userScore =  splitted[3];
            String userWin =  splitted[4];
            String userDraw =  splitted[5];
            String userLose =  splitted[6];
          
            String userAvgTime =  splitted[7];
            String userStatus = splitted[8];
            
            ClientRun.openScene(ClientRun.SceneName.INFOPLAYER);
            ClientRun.infoPlayerView.setInfoUser(userName, userScore, userWin, userDraw, userLose, userAvgTime, userStatus);
        }
    }
  


    
    private void onReceiveLogout(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            ClientRun.closeScene(ClientRun.SceneName.HOMEVIEW);
            ClientRun.openScene(ClientRun.SceneName.LOGIN);
        }
    }
    
    // chat
    private void onReceiveInviteToChat(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            if (JOptionPane.showConfirmDialog(ClientRun.homeView, userHost + " Muốn nói gì đó với bạn?", "Chat?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
                ClientRun.openScene(ClientRun.SceneName.MESSAGEVIEW);
                ClientRun.messageView.setInfoUserChat(userHost);
                sendData("ACCEPT_MESSAGE;" + userHost + ";" + userInvited);
            } else {
                sendData("NOT_ACCEPT_MESSAGE;" + userHost + ";" + userInvited);
            }
        }
    }
    
    private void onReceiveAcceptMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            ClientRun.openScene(ClientRun.SceneName.MESSAGEVIEW);
            ClientRun.messageView.setInfoUserChat(userInvited);
        }
    }
    
    private void onReceiveNotAcceptMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            JOptionPane.showMessageDialog(ClientRun.homeView, userInvited + " không muốn trò chuyện với bạn!");
        }
    }
    
    private void onReceiveLeaveChat(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            
            ClientRun.closeScene(ClientRun.SceneName.MESSAGEVIEW);   
            JOptionPane.showMessageDialog(ClientRun.homeView, userHost + " đã rời khỏi cuộc trò chuyện!");
        }
    }
    
    private void onReceiveChatMessage(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            String message = splitted[4];
            
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            String formattedTime = currentTime.format(formatter);
            
            String chat = "[" + userHost + "] : " + message + "\n";
            ClientRun.messageView.addRecieveMessage(chat, formattedTime);
        }
    }
    
    // play game
    private void onReceiveInviteToPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            String roomId = splitted[4];
            if (JOptionPane.showConfirmDialog(ClientRun.homeView, userHost + " muốn chơi game với bạn?", "Game?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
                ClientRun.openScene(ClientRun.SceneName.GAMEVIEW);
                ClientRun.gameView.setInfoPlayer(userHost);
                roomIdPresent = roomId;
                ClientRun.gameView.setStateUserInvited();
                sendData("ACCEPT_PLAY;" + userHost + ";" + userInvited + ";" + roomId);
            } else {
                sendData("NOT_ACCEPT_PLAY;" + userHost + ";" + userInvited + ";" + roomId);
            }
        }
    }
    
    private void onReceiveAcceptPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
            roomIdPresent = splitted[4];
            ClientRun.openScene(ClientRun.SceneName.GAMEVIEW);
            ClientRun.gameView.setInfoPlayer(userInvited);
            ClientRun.gameView.setStateHostRoom();
        }
    }
    
    private void onReceiveNotAcceptPlay(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String userHost = splitted[2];
            String userInvited = splitted[3];
                
            JOptionPane.showMessageDialog(ClientRun.homeView, userInvited + " không muốn chơi với bạn!");
        }
    }
    
    private void onReceiveLeaveGame(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("success")) {
            String user1 = splitted[2];
            String user2 = splitted[3];

            roomIdPresent = null;
            ClientRun.closeScene(ClientRun.SceneName.GAMEVIEW);   
            JOptionPane.showMessageDialog(ClientRun.homeView, user1 + " leave to game!");
        }
    }
     
    private void onReceiveCheckStatusUser(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[2];
        ClientRun.homeView.setStatusCompetitor(status);
    }
    
    private void onReceiveStartGame(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];
        String dayso = splitted[3];

        if (status.equals("success")) {      
          
            ClientRun.gameView.setStartGame(20, dayso);
          
        }
    }
    
    private void onReceiveResultGame(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];
        String result = splitted[2];
        String user1 = splitted[3];
        String user2 = splitted[4];
        String roomId = splitted[5];
        
        if (status.equals("success")) {
            ClientRun.gameView.setWaitingRoom();
            if (result.equals("DRAW")) {
                ClientRun.gameView.showAskPlayAgain("Game hòa, cả hai được 0.5 điểm, bạn có muốn tiếp tục chơi không?");
            } else if (result.equals(loginUser)) {
                ClientRun.gameView.showAskPlayAgain("Chúc mừng bạn đã thắng, bạn được 1 điểm, tiếp tục chứ?");
            } else {
                ClientRun.gameView.showAskPlayAgain("Rất tiếc bạn đã thua, 0 điểm, tiếp tục chơi ?");
            }
        }
    }
    
    private void onReceiveAskPlayAgain(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];
        
        if (status.equals("NO")) {
            ClientRun.closeScene(ClientRun.SceneName.GAMEVIEW);
            JOptionPane.showMessageDialog(ClientRun.homeView, "End Game!");
        } else {
            if (loginUser.equals(splitted[2])) {
                ClientRun.gameView.setStateHostRoom();
            } else {
                ClientRun.gameView.setStateUserInvited();
            }
        }
    }  
    
    
    // get set
    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public Socket getS() {
        return s;
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public String getRoomIdPresent() {
        return roomIdPresent;
    }

    public void setRoomIdPresent(String roomIdPresent) {
        this.roomIdPresent = roomIdPresent;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
     public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public float getAvgCompetitor() {
        return avgCompetitor;
    }

    public void setAvgCompetitor(float avgCompetitor) {
        this.avgCompetitor = avgCompetitor;
    }

    public float getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(float avgTime) {
        this.avgTime = avgTime;
    }

   
}
