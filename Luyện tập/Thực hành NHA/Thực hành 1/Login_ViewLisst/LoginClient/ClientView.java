package LoginClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ClientView extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JTable userTable; // Sử dụng JTable để hiển thị danh sách người dùng
    private DefaultTableModel tableModel; // Mô hình bảng để quản lý dữ liệu trong JTable
    private JPanel inputPanel; // Để chứa các trường nhập liệu

    public ClientView() {
        super("Thực hành Ngọ Văn Trọng_ B21DCCN726");
        
        // Cài đặt kích thước và màu sắc cho JFrame
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(240, 240, 240)); // Màu nền nhẹ nhàng

        // Khởi tạo các thành phần giao diện
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        btnLogin = new JButton("Login");

        // Đặt màu cho nút Login
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(80, 30)); // Kích thước nút

        // Tạo mô hình bảng với các cột
        String[] columnNames = {"Username", "Full Name", "MSSV"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true); // Đảm bảo bảng điền đầy không gian

        // Đưa bảng vào JScrollPane để có thanh cuộn
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Tạo layout và thêm các thành phần vào JPanel
        inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 1, 0, 10)); // Giảm khoảng cách giữa các ô
        inputPanel.setBackground(new Color(255, 255, 255)); // Màu nền cho panel nhập liệu
        
        // Thêm nhãn và trường nhập liệu
        JPanel usernamePanel = new JPanel(new GridLayout(1, 2));
        usernamePanel.add(new JLabel("Tên Đăng Nhập:"));
        usernamePanel.add(txtUsername);
      

        JPanel passwordPanel = new JPanel(new GridLayout(1, 2));
        passwordPanel.add(new JLabel("Mật Khẩu:"));
        passwordPanel.add(txtPassword);
    
        // Thêm các panel vào inputPanel
        inputPanel.add(usernamePanel);
        inputPanel.add(passwordPanel);
        inputPanel.add(new JLabel()); // Ô trống
        inputPanel.add(btnLogin);

        // Cài đặt font cho nhãn
        for (Component component : inputPanel.getComponents()) {
            if (component instanceof JLabel) {
                component.setFont(new Font("Arial", Font.PLAIN, 14));
            }
        }

        // Thiết lập layout cho JFrame
        this.setLayout(new BorderLayout());
        this.add(inputPanel, BorderLayout.NORTH); // Thêm input panel ở phía Bắc
        this.add(scrollPane, BorderLayout.CENTER); // Thêm bảng ở giữa

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        userTable.setVisible(false); // Ẩn bảng ban đầu
    }

    public void actionPerformed(ActionEvent e) {
        // Xử lý các hành động (nếu cần)
    }

    // Lấy thông tin user từ các trường nhập
    public User getUser() {
        User model = new User(txtUsername.getText(), new String(txtPassword.getPassword()), "", ""); // Cập nhật để lấy đầy đủ thông tin
        return model;
    }

    // Hiển thị thông báo (VD: khi login thành công hoặc thất bại)
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    // Gán sự kiện login cho nút bấm
    public void addLoginListener(ActionListener log) {
        btnLogin.addActionListener(log);
    }

    // Hiển thị danh sách user sau khi đăng nhập thành công
    public void showUserList(ArrayList<User> users) {
        // Xóa dữ liệu cũ trong bảng
        tableModel.setRowCount(0);

        for (User user : users) {
            // Thêm người dùng vào bảng
            Object[] rowData = {user.getUsername(), user.getFullname(), user.getMsv()};
            tableModel.addRow(rowData);
        }

        // Hiện bảng danh sách người dùng
        userTable.setVisible(true); // Hiện bảng
        inputPanel.setVisible(false); // Ẩn form đăng nhập
    }
}
