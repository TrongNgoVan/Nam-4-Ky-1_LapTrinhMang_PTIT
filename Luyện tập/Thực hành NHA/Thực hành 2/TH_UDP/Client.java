package UDP.Total;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8889;
    private static final String[] COLUMN_NAMES = {"ID", "Mã SV", "Họ tên", "Năm sinh", "Quê quán", "GPA"};

    private DatagramSocket socket;
    private InetAddress serverAddress;

    private JTextField nameField, minGPAField, maxGPAField;
    private JTextField idField, maSVField, hoTenField, namSinhField, queQuanField, gpaField;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public Client() throws IOException {
        super("Hệ thống Quản lý Sinh viên");
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(SERVER_ADDRESS);

        initComponents(); // Khởi tạo giao diện
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new GridLayout(3, 2));
        nameField = new JTextField(20);
        minGPAField = new JTextField(5);
        maxGPAField = new JTextField(5);
        
        searchPanel.add(new JLabel("Tìm theo tên:"));
        searchPanel.add(nameField);
        searchPanel.add(new JLabel("GPA tối thiểu:"));
        searchPanel.add(minGPAField);
        searchPanel.add(new JLabel("GPA tối đa:"));
        searchPanel.add(maxGPAField);

        // Các nút tìm kiếm
        JButton searchNameButton = new JButton("Tìm theo tên");
        searchNameButton.addActionListener(e -> searchByName());

        JButton searchGPAButton = new JButton("Tìm theo GPA");
        searchGPAButton.addActionListener(e -> searchByGPA());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(searchNameButton);
        buttonPanel.add(searchGPAButton);

        // Bảng kết quả
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = resultTable.getSelectedRow();
                    displayStudentDetails(row);
                }
            }
        });
        JScrollPane tableScrollPane = new JScrollPane(resultTable);

        // Panel cập nhật sinh viên
        JPanel updatePanel = new JPanel(new GridLayout(7, 2));
        idField = new JTextField(5);
        maSVField = new JTextField(10);
        hoTenField = new JTextField(20);
        namSinhField = new JTextField(5);
        queQuanField = new JTextField(20);
        gpaField = new JTextField(5);

        updatePanel.add(new JLabel("ID:"));
        updatePanel.add(idField);
        updatePanel.add(new JLabel("Mã SV:"));
        updatePanel.add(maSVField);
        updatePanel.add(new JLabel("Họ tên:"));
        updatePanel.add(hoTenField);
        updatePanel.add(new JLabel("Năm sinh:"));
        updatePanel.add(namSinhField);
        updatePanel.add(new JLabel("Quê quán:"));
        updatePanel.add(queQuanField);
        updatePanel.add(new JLabel("GPA:"));
        updatePanel.add(gpaField);

        JButton updateButton = new JButton("Cập nhật Sinh viên");
        updateButton.addActionListener(e -> updateStudent());
        updatePanel.add(updateButton);

        // Thêm các component vào frame
        add(searchPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(updatePanel, BorderLayout.EAST);
        add(tableScrollPane, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private void searchByName() {
        String name = nameField.getText();
        if (!name.isEmpty()) {
            sendRequest("searchByName," + name);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên để tìm kiếm.");
        }
    }

    private void searchByGPA() {
        try {
            float minGPA = Float.parseFloat(minGPAField.getText());
            float maxGPA = Float.parseFloat(maxGPAField.getText());
            sendRequest("searchByGPA," + minGPA + "," + maxGPA);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị GPA hợp lệ.");
        }
    }

    private void updateStudent() {
        try {
            int id = Integer.parseInt(idField.getText());
            String maSV = maSVField.getText();
            String hoTen = hoTenField.getText();
            int namSinh = Integer.parseInt(namSinhField.getText());
            String queQuan = queQuanField.getText();
            float gpa = Float.parseFloat(gpaField.getText());

            sendRequestUpdate("update," + id + "," + maSV + "," + hoTen + "," + namSinh + "," + queQuan + "," + gpa);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá trị hợp lệ cho tất cả các trường.");
        }
    }

    private void sendRequestUpdate(String request) {
        try {
            byte[] sendData = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            
           

            if (response.startsWith("error")) {
                JOptionPane.showMessageDialog(this, "Lỗi từ server: " + response);
            } else {
                JOptionPane.showMessageDialog(this, "Thông báo từ Server: " + response);
                updateResultTable(response);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi giao tiếp với server: " + e.getMessage());
        }
    }
    
    private void sendRequest(String request) {
        try {
            byte[] sendData = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            
           

            if (response.startsWith("error")) {
                JOptionPane.showMessageDialog(this, "Lỗi từ server: " + response);
            } else {
                updateResultTable(response);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi giao tiếp với server: " + e.getMessage());
        }
    }

    private void updateResultTable(String response) {
        tableModel.setRowCount(0);
        String[] rows = response.split("\n");
        for (String row : rows) {
            String[] data = row.split(",");
            if (data.length == 6) {
                tableModel.addRow(data);
            }
        }
    }

    private void displayStudentDetails(int row) {
        if (row != -1) {
            idField.setText((String) tableModel.getValueAt(row, 0));
            maSVField.setText((String) tableModel.getValueAt(row, 1));
            hoTenField.setText((String) tableModel.getValueAt(row, 2));
            namSinhField.setText((String) tableModel.getValueAt(row, 3));
            queQuanField.setText((String) tableModel.getValueAt(row, 4));
            gpaField.setText((String) tableModel.getValueAt(row, 5));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Client();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi khởi động client: " + e.getMessage());
            }
        });
    }
}
