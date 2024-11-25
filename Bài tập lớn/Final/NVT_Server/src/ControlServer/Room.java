package ControlServer;

import ControlServer.*;
import controller.MatchsController;
import controller.UserController;
import model.DemTG;
import model.KieuTG;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import model.Matchs;
import model.User;
import run.ServerRun;
/**
 *
 * @author Ngọ Văn Trọng
 */
public class Room {
    String id;
    String time = "00:00";
    Client client1 = null, client2 = null;
    ArrayList<Client> clients = new ArrayList<>();
    
    boolean gameStarted = false;
    DemTG matchTimer;
    DemTG waitingTimer;
    
    String resultClient1 = null;
    String resultClient2 = null;
    
    String playAgainC1;
    String playAgainC2;
    String waitingTime= "00:00";

    public LocalDateTime startedTime;

    public Room(String id) {
        // room id
        this.id = id;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        gameStarted = true;
        
        matchTimer = new DemTG(20);
        matchTimer.setTimerCallBack(
            null,
            (Callable) () -> {
                time = "" + KieuTG.secondsToMinutes(matchTimer.getCurrentTick());
                System.out.println(time);
                if (time.equals("00:00")) {
                    waitingClientTimer();
                    if (resultClient1 == null && resultClient2 == null) {
                        draw();
                        String result = "RESULT_GAME;success;DRAW;" + client1.getLoginUser() + ";" + client2.getLoginUser() + ";" + id ;
                        broadcast(result);
                       
                        System.out.println("Đã gửi cho Client: ");
                        System.out.println(result);

                      
                        Matchs match = new Matchs();
                        match.setId_match(id); 
                        match.setUser1(client1.getLoginUser());
                        match.setUser2(client2.getLoginUser());          
                        match.setUser_win("DRAW");
                        match.setScore_win(0.5f);
                        match.setScore_lose(0.5f);
                        

                        match.setTime_begin(LocalDateTime.now());


                         new MatchsController().addMatch(match);
                    } 
                }
                return null;
            },
            1
        );
    }
    
    public void waitingClientTimer() {
        waitingTimer = new DemTG(10);
        waitingTimer.setTimerCallBack(
            null,
            (Callable) () -> {
                waitingTime = "" + KieuTG.secondsToMinutes(waitingTimer.getCurrentTick());
                System.out.println("waiting: " + waitingTime);
                if (waitingTime.equals("00:00")) {
                    if (playAgainC1 == null && playAgainC2 == null) {
                        broadcast("ASK_PLAY_AGAIN;NO");
                        deleteRoom();
                    } 
                }
                return null;
            },
            1
        );
    }
    
    public void deleteRoom () {
        client1.setJoinedRoom(null);
        client1.setcCompetitor(null);
        client2.setJoinedRoom(null);
        client2.setcCompetitor(null);
        ServerRun.roomManager.remove(this);
    }
    
    public void resetRoom() {
        gameStarted = false;
        resultClient1 = null;
        resultClient2 = null;
        playAgainC1 = null;
        playAgainC2 = null;
        time = "00:00";
        waitingTime = "00:00";
    }
    
    public String handleResultClient() throws SQLException {
        int timeClient1 = 0;
        int timeClient2 = 0;
        int tong1 =0;
        int tong2=0;
        if (resultClient1 != null) {
            String[] splitted1 = resultClient1.split(";");
            timeClient1 = Integer.parseInt(splitted1[5]);
            tong1 = Integer.parseInt(splitted1[6]);
            
        }
        if (resultClient2 != null) {
            String[] splitted2 = resultClient2.split(";");
            timeClient2 = Integer.parseInt(splitted2[5]);
            tong2 = Integer.parseInt(splitted2[6]);
        }
        
        if (resultClient1 == null && resultClient2 == null) {
            draw();
            return "DRAW";
        } else if (resultClient1 != null && resultClient2 == null) {
            if (calculateResult(resultClient1)) {
                client1Win(timeClient1);
                return client1.getLoginUser();
            } else {
                draw();
                return "DRAW";
            }
        } else if (resultClient1 == null && resultClient2 != null) {
            if (calculateResult(resultClient2)) {
                client2Win(timeClient2);
                return client2.getLoginUser();
            } else {
                draw();
                return "DRAW";
            }
        } else if (resultClient1 != null && resultClient2 != null) {
            boolean result1Correct = calculateResult(resultClient1);
            boolean result2Correct = calculateResult(resultClient2);

            if (result1Correct && result2Correct) {
                // Nếu cả hai đều đúng, so sánh số lượt thao tác
                 if (tong1 < tong2){
                    client1Win(timeClient1);
                    return client1.getLoginUser();
                 }else if(tong1 >tong2){
                    client2Win(timeClient2);
                    return client2.getLoginUser();
                 }else{
                        if (timeClient1 < timeClient2) {
                            client1Win(timeClient1);
                            return client1.getLoginUser();
                        } else if (timeClient1 > timeClient2) {
                            client2Win(timeClient2);
                            return client2.getLoginUser();
                        } else {
                    
                            draw();
                            return "DRAW";
                        }
                    }
                
            } else if (result1Correct) {
                client1Win(timeClient1);
                return client1.getLoginUser();
            } else if (result2Correct) {
                client2Win(timeClient2);
                return client2.getLoginUser();
            } else {
                draw();
                return "DRAW";
            }
        }
        return null;
    }
    
    public boolean calculateResult(String received) {
        String[] splitted = received.split(";");

        String user1 = splitted[0];

        String answer = splitted[4];

        String[] numbers = answer.split(",");

        for (int i = 0; i < numbers.length - 1; i++) {
            int current = Integer.parseInt(numbers[i]);
            int next = Integer.parseInt(numbers[i + 1]);

            if (current >= next) {
                return false;
            }
        }
        return true;
    }

    public void draw () throws SQLException {
        User user1 = new UserController().getUser(client1.getLoginUser());
        User user2 = new UserController().getUser(client2.getLoginUser());
        
        user1.setDraw(user1.getDraw() + 1);
        user2.setDraw(user2.getDraw() + 1);
        
        user1.setScore(user1.getScore()+ 0.5f);
        user2.setScore(user2.getScore()+ 0.5f);
        
        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
        
       
        
//        newAvgCompetitor1 = Math.round(newAvgCompetitor1 * 100) / 100;
//        newAvgCompetitor2 = Math.round(newAvgCompetitor2 * 100) / 100;
        
      
        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
    }
    
    public void client1Win(int time) throws SQLException {
        User user1 = new UserController().getUser(client1.getLoginUser());
        User user2 = new UserController().getUser(client2.getLoginUser());
        
        user1.setWin(user1.getWin() + 1);
        user2.setLose(user2.getLose() + 1);
        
        user1.setScore(user1.getScore()+ 1);
        
        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
        
     
        
        float newAvgTime1 = (totalMatchUser1 * user1.getAvgTime() + time) / (totalMatchUser1 + 1);
        System.out.println("newAvgTime1: " + newAvgTime1);
        user1.setAvgTime(newAvgTime1);
        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
    }
    
    public void client2Win(int time) throws SQLException {
        User user1 = new UserController().getUser(client1.getLoginUser());
        User user2 = new UserController().getUser(client2.getLoginUser());
        
        user2.setWin(user2.getWin() + 1);
        user1.setLose(user1.getLose() + 1);
        
        user2.setScore(user2.getScore()+ 1);
        
        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
        
    

        
        float newAvgTime2 = (totalMatchUser2 * user2.getAvgTime() + time) / (totalMatchUser2 + 1);
        System.out.println("newAvgTime2: " + newAvgTime2);
        user2.setAvgTime(newAvgTime2);
        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
    }
    
    public void userLeaveGame (String username) throws SQLException {
        if (client1.getLoginUser().equals(username)) {
            client2Win(0);
        } else if (client2.getLoginUser().equals(username)) {
            client1Win(0);
        }
    }
    
    public String handlePlayAgain () {
        if (playAgainC1 == null || playAgainC2 == null) {
            return "NO";
        } else if (playAgainC1.equals("YES") && playAgainC2.equals("YES")) {
            return "YES";
        } else if (playAgainC1.equals("NO") && playAgainC2.equals("YES")) {
//            ServerRun.clientManager.sendToAClient(client2.getLoginUser(), "ASK_PLAY_AGAIN;NO");
//            deleteRoom();
            return "NO";
        } else if (playAgainC2.equals("NO") && playAgainC2.equals("YES")) {
//            ServerRun.clientManager.sendToAClient(client1.getLoginUser(), "ASK_PLAY_AGAIN;NO");
//            deleteRoom();
            return "NO";
        } else {
            return "NO";
        }
    }
    
    // add/remove client
    public boolean addClient(Client c) {
        if (!clients.contains(c)) {
            clients.add(c);
            if (client1 == null) {
                client1 = c;
            } else if (client2 == null) {
                client2 = c;
            }
            return true;
        }
        return false;
    }

    public boolean removeClient(Client c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }

    // broadcast messages
    public void broadcast(String msg) {
        clients.forEach((c) -> {
            c.sendData(msg);
        });
    }
    
    public Client find(String username) {
        for (Client c : clients) {
            if (c.getLoginUser()!= null && c.getLoginUser().equals(username)) {
                return c;
            }
        }
        return null;
    }

    // gets sets
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient1() {
        return client1;
    }

    public void setClient1(Client client1) {
        this.client1 = client1;
    }

    public Client getClient2() {
        return client2;
    }

    public void setClient2(Client client2) {
        this.client2 = client2;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }
    
    public int getSizeClient() {
        return clients.size();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResultClient1() {
        return resultClient1;
    }

    public void setResultClient1(String resultClient1) {
        this.resultClient1 = resultClient1;
    }

    public String getResultClient2() {
        return resultClient2;
    }

    public void setResultClient2(String resultClient2) {
        this.resultClient2 = resultClient2;
    }

    public String getPlayAgainC1() {
        return playAgainC1;
    }

    public void setPlayAgainC1(String playAgainC1) {
        this.playAgainC1 = playAgainC1;
    }

    public String getPlayAgainC2() {
        return playAgainC2;
    }

    public void setPlayAgainC2(String playAgainC2) {
        this.playAgainC2 = playAgainC2;
    }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }
    
    
}
