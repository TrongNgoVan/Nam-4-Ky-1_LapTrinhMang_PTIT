package TH_UDP;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UpdateStudentInfo extends JFrame {
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort = 8889; // Cổng của server

    private JTextField idField;
    private JTextField codeField;
    private JTextField nameField;
    private JTextField birthYearField;
    private JTextField hometownField;
    private JTextField gpaField;
    private JButton updateButton;
    private JTextArea displayArea;

    public UpdateStudentInfo() {
        // Khởi tạo giao diện
        setTitle("Quản lý sinh viên");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2));

        // Tạo các ô nhập liệu
        idField = new JTextField();
        codeField = new JTextField();
        nameField = new JTextField();
        birthYearField = new JTextField();
        hometownField = new JTextField();
        gpaField = new JTextField();

        // Thêm các thành phần vào giao diện
        add(new JLabel("ID sinh viên:"));
        add(idField);
        add(new JLabel("Mã sinh viên:"));
        add(codeField);
        add(new JLabel("Họ tên:"));
        add(nameField);
        add(new JLabel("Năm sinh:"));
        add(birthYearField);
        add(new JLabel("Quê quán:"));
        add(hometownField);
        add(new JLabel("GPA:"));
        add(gpaField);

        updateButton = new JButton("Cập nhật");
        add(updateButton);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea));

        // Kết nối tới server
        initializeSocket();

        // Lắng nghe sự kiện khi nhấn nút cập nhật
        updateButton.addActionListener(e -> handleUpdateStudent());
    }

    private void initializeSocket() {
        try {
            clientSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName("localhost"); // Địa chỉ của server
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleUpdateStudent() {
        String id = idField.getText();
        String code = codeField.getText();
        String name = nameField.getText();
        String birthYear = birthYearField.getText();
        String hometown = hometownField.getText();
        String gpa = gpaField.getText();

        if (!id.isEmpty() && !code.isEmpty() && !name.isEmpty() && !birthYear.isEmpty() && !hometown.isEmpty() && !gpa.isEmpty()) {
            String messageToSend = "UPDATE_STUDENT;" + id + ";" + code + ";" + name + ";" + birthYear + ";" + hometown + ";" + gpa;
            sendMessage(messageToSend); // Gửi yêu cầu cập nhật
            clearFields(); // Xóa các trường nhập sau khi gửi
        } else {
            displayArea.append("Vui lòng điền đầy đủ thông tin sinh viên.\n");
        }
    }

    private void sendMessage(String messageToSend) {
        // Gửi yêu cầu tới server với mã hóa UTF-8
        try {
            byte[] sendData = messageToSend.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);

            // Nhận phản hồi từ server
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim();

            // Hiển thị phản hồi cập nhật
            displayArea.append("Phản hồi từ server: " + serverResponse + "\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        idField.setText("");
        codeField.setText("");
        nameField.setText("");
        birthYearField.setText("");
        hometownField.setText("");
        gpaField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UpdateStudentInfo client = new UpdateStudentInfo();
            client.setVisible(true);
        });
    }
}
