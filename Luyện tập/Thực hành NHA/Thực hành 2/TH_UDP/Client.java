package TH_UDP;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Client extends JFrame {
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort = 8889; // Cổng của server

    private JTextArea displayArea;
    private JComboBox<String> optionsComboBox;

    // Các ô nhập liệu cho từng loại tìm kiếm
    private JTextField searchNameField;
    private JTextField searchGPAFromField;
    private JTextField searchGPAToField;
    private JTextField updateIdField;
    private JTextField updateCodeField;
    private JTextField updateNameField;
    private JTextField updateBirthYearField;
    private JTextField updateHometownField;
    private JTextField updateGPAField;

    private JButton sendButton;
    private JButton updateButton;

    public Client() {
        // Khởi tạo giao diện
        setTitle("UDP Client");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Khu vực hiển thị phản hồi từ server
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Khu vực tùy chọn
        optionsComboBox = new JComboBox<>(new String[]{
            "Tìm kiếm sinh viên theo tên",
            "Tìm kiếm sinh viên theo khoảng điểm GPA",
            "Cập nhật thông tin sinh viên"
        });

        // Khu vực nhập liệu cho tìm kiếm theo tên
        JPanel searchByNamePanel = new JPanel(new FlowLayout());
        searchNameField = new JTextField(15);
        searchByNamePanel.add(new JLabel("Nhập tên sinh viên:"));
        searchByNamePanel.add(searchNameField);
        JButton searchNameButton = new JButton("Tìm kiếm");
        searchByNamePanel.add(searchNameButton);

        // Khu vực nhập liệu cho tìm kiếm theo GPA
        JPanel searchByGpaPanel = new JPanel(new FlowLayout());
        searchGPAFromField = new JTextField(5);
        searchGPAToField = new JTextField(5);
        searchByGpaPanel.add(new JLabel("Từ GPA:"));
        searchByGpaPanel.add(searchGPAFromField);
        searchByGpaPanel.add(new JLabel("Đến GPA:"));
        searchByGpaPanel.add(searchGPAToField);
        JButton searchGPAButton = new JButton("Tìm kiếm");
        searchByGpaPanel.add(searchGPAButton);

        // Khu vực nhập liệu cho cập nhật thông tin sinh viên
        JPanel updatePanel = new JPanel(new GridLayout(6, 2));
        updateIdField = new JTextField();
        updateCodeField = new JTextField();
        updateNameField = new JTextField();
        updateBirthYearField = new JTextField();
        updateHometownField = new JTextField();
        updateGPAField = new JTextField();
        
        updatePanel.add(new JLabel("Nhập ID sinh viên:"));
        updatePanel.add(updateIdField);
        updatePanel.add(new JLabel("Nhập mã sinh viên mới:"));
        updatePanel.add(updateCodeField);
        updatePanel.add(new JLabel("Nhập họ tên mới:"));
        updatePanel.add(updateNameField);
        updatePanel.add(new JLabel("Nhập năm sinh mới:"));
        updatePanel.add(updateBirthYearField);
        updatePanel.add(new JLabel("Nhập quê quán mới:"));
        updatePanel.add(updateHometownField);
        updatePanel.add(new JLabel("Nhập GPA mới:"));
        updatePanel.add(updateGPAField);
        updateButton = new JButton("Cập nhật");

        // Khu vực lựa chọn
        JPanel optionsPanel = new JPanel();
        optionsPanel.add(optionsComboBox);
        
        // Khởi tạo nút gửi
        sendButton = new JButton("Gửi");
        optionsPanel.add(sendButton);
        
        add(optionsPanel, BorderLayout.NORTH);

        // Khu vực hiển thị nhập liệu
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(searchByNamePanel, "Tìm kiếm sinh viên theo tên");
        inputPanel.add(searchByGpaPanel, "Tìm kiếm sinh viên theo khoảng điểm GPA");
        inputPanel.add(updatePanel, "Cập nhật thông tin");

        add(inputPanel, BorderLayout.SOUTH);
        
        // Kết nối tới server
        try {
            clientSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName("localhost"); // Địa chỉ của server
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Lắng nghe sự kiện khi chọn tùy chọn
        optionsComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (inputPanel.getLayout());
                cl.show(inputPanel, (String) optionsComboBox.getSelectedItem());
                clearUpdateFields(); // Xóa các trường nhập khi chuyển qua cập nhật
            }
        });

        // Lắng nghe sự kiện khi nhấn nút tìm kiếm theo tên
        searchNameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSearchByName();
            }
        });

        // Lắng nghe sự kiện khi nhấn nút tìm kiếm theo GPA
        searchGPAButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSearchByGPA();
            }
        });

        // Lắng nghe sự kiện khi nhấn nút cập nhật
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleUpdateStudent();
            }
        });

        // Lắng nghe sự kiện khi nhấn nút gửi
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Xử lý gửi thông điệp ở đây nếu cần
            }
        });
    }

    private void handleSearchByName() {
        String name = searchNameField.getText();
        String messageToSend = "SEARCH_BY_NAME;" + name;
        sendMessage(messageToSend);
        searchNameField.setText(""); // Xóa ô nhập sau khi gửi
    }

    private void handleSearchByGPA() {
        String gpaFrom = searchGPAFromField.getText();
        String gpaTo = searchGPAToField.getText();
        String messageToSend = "SEARCH_BY_GPA_RANGE;" + gpaFrom + ";" + gpaTo;
        sendMessage(messageToSend);
        searchGPAFromField.setText(""); // Xóa ô nhập sau khi gửi
        searchGPAToField.setText("");   // Xóa ô nhập sau khi gửi
    }

    private void handleUpdateStudent() {
        String id = updateIdField.getText();
        String code = updateCodeField.getText();
        String name = updateNameField.getText();
        String birthYear = updateBirthYearField.getText();
        String hometown = updateHometownField.getText();
        String gpa = updateGPAField.getText();

        String messageToSend = "UPDATE_STUDENT;" + id + ";" + code + ";" + name + ";" + birthYear + ";" + hometown + ";" + gpa;
        sendMessage(messageToSend);
        clearUpdateFields(); // Xóa các trường cập nhật sau khi gửi
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

            // Hiển thị phản hồi trong giao diện
            displayArea.append("Phản hồi từ server: " + "\n" + serverResponse + "\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearUpdateFields() {
        updateIdField.setText("");
        updateCodeField.setText("");
        updateNameField.setText("");
        updateBirthYearField.setText("");
        updateHometownField.setText("");
        updateGPAField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Client client = new Client();
            client.setVisible(true);
        });
    }
}
