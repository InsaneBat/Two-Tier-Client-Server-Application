/*
Name: Axel Alvarado
Course: CNT 4714 Spring 2024
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 10, 2024
Class: Enterprise Computing
*/
package theAccountant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main extends JFrame {

    private JLabel propertiesFileLabel;
    private JLabel userPropertiesFileLabel;
    private JLabel dataBaseURLLabel;

    private JTextField jtfUsername;
    private JPasswordField jpfPassword;

    private JTextArea jtaSqlCommand;

    private JButton jbtConnectToDB;
    private JButton jbtDisconnectFromDB;
    private JButton jbtClearSQLCommand;
    private JButton jbtExecuteSQLCommand;
    private JButton jbtClearResultWindow;

    private ResultSetTableModel tableModel = null;
    private JTable table;

    private Connection connection;
    private boolean connectedToDatabase = false;

    private JLabel jlbConnectionStatus;

    public Main() throws ClassNotFoundException, SQLException, IOException {
        createInstanceGUIComponents();
        setupEventListeners();
        setupGUI();
    }

    private void createInstanceGUIComponents() throws ClassNotFoundException, SQLException, IOException {
        propertiesFileLabel = new JLabel("operationslog.properties");
        userPropertiesFileLabel = new JLabel("theaccountant.properties");
        dataBaseURLLabel = new JLabel("jdbc:mysql://localhost:3312/project3");

        jtfUsername = new JTextField();
        jpfPassword = new JPasswordField();

        jtaSqlCommand = new JTextArea(3, 75);
        jtaSqlCommand.setWrapStyleWord(true);
        jtaSqlCommand.setLineWrap(true);

        jbtConnectToDB = new JButton("Connect to Database");
        jbtDisconnectFromDB = new JButton("Disconnect From Database");
        jbtClearSQLCommand = new JButton("Clear SQL Command");
        jbtExecuteSQLCommand = new JButton("Execute SQL Command");
        jbtClearResultWindow = new JButton("Clear Result Window");
        jlbConnectionStatus = new JLabel("No Connection Established");
        jlbConnectionStatus.setForeground(Color.RED);

        table = new JTable();
    }

    private void setupEventListeners() {
        jbtConnectToDB.addActionListener(e -> handleConnectToDB());
        jbtDisconnectFromDB.addActionListener(e -> handleDisconnectFromDB());
        jbtClearSQLCommand.addActionListener(e -> jtaSqlCommand.setText(""));
        jbtExecuteSQLCommand.addActionListener(e -> executeSQLCommand());
        jbtClearResultWindow.addActionListener(e -> clearResultWindow());

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent event) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    private void setupGUI() {
        JPanel topPanel = new JPanel(new GridLayout(4, 2));
        topPanel.add(new JLabel("DB URL Properties"));
        topPanel.add(propertiesFileLabel);
        topPanel.add(new JLabel("User Properties"));
        topPanel.add(userPropertiesFileLabel);
        topPanel.add(new JLabel("Username"));
        topPanel.add(jtfUsername);
        topPanel.add(new JLabel("Password"));
        topPanel.add(jpfPassword);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.add(new JLabel("Enter An SQL Command"), BorderLayout.NORTH);
        sqlPanel.add(new JScrollPane(jtaSqlCommand), BorderLayout.CENTER);

        // Add the JLabel for "SQL Execution Result Window" above the JTable
        JLabel resultLabel = new JLabel("SQL Execution Result Window", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel resultLabelPanel = new JPanel(new BorderLayout());
        resultLabelPanel.add(resultLabel, BorderLayout.NORTH);
        resultLabelPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel sqlButtonPanel = new JPanel(new FlowLayout());
        sqlButtonPanel.add(jbtClearSQLCommand);
        sqlButtonPanel.add(jbtExecuteSQLCommand);
        sqlPanel.add(sqlButtonPanel, BorderLayout.SOUTH);
        sqlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel connectionPanel = new JPanel(new GridLayout(3, 2));
        connectionPanel.add(jbtConnectToDB);
        connectionPanel.add(jbtDisconnectFromDB);
        connectionPanel.add(new JLabel("Connection Status:"));
        connectionPanel.add(jlbConnectionStatus);
        connectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultLabelPanel, BorderLayout.CENTER);
        JPanel resultButtonPanel = new JPanel(new FlowLayout());
        resultButtonPanel.add(jbtClearResultWindow);
        bottomPanel.add(resultButtonPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sqlPanel, BorderLayout.CENTER);
        mainPanel.add(connectionPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setTitle("SPECIALIZED ACCOUNTANT APPLICATION  - (AXA - CNT 4714 - Spring 2024 - Project 3)");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void handleConnectToDB() {
        try {
            Properties properties = new Properties();
            String selectedPropertiesFile = propertiesFileLabel.getText();
            properties.load(new FileInputStream(selectedPropertiesFile));

            Properties userProperties = new Properties();
            String selectedUserPropertiesFile = userPropertiesFileLabel.getText();
            userProperties.load(new FileInputStream(selectedUserPropertiesFile));

            Class.forName(properties.getProperty("MYSQL_DB_DRIVER_CLASS"));
            String dbUrl = properties.getProperty("MYSQL_DB_URL");

            if (connectedToDatabase) {
                connection.close();
                updateConnectionStatus("No Connection Now", Color.RED);
                connectedToDatabase = false;
                clearTable();
            }

            String enteredUsername = jtfUsername.getText().trim();
            String enteredPassword = new String(jpfPassword.getPassword());

            String correctUsername = userProperties.getProperty("MYSQL_DB_USERNAME");
            String correctPassword = userProperties.getProperty("MYSQL_DB_PASSWORD");

            if (enteredUsername.equals(correctUsername) && enteredPassword.equals(correctPassword)) {
                connection = DriverManager.getConnection(dbUrl, userProperties.getProperty("MYSQL_DB_USERNAME"), userProperties.getProperty("MYSQL_DB_PASSWORD"));
                updateConnectionStatus("Connected to " + dbUrl, Color.GREEN);
                connectedToDatabase = true;
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            handleConnectionError(e);
        }
    }

    private void handleDisconnectFromDB() {
        try {
            if (connectedToDatabase) {
                connection.close();
                updateConnectionStatus("No Connection Now", Color.RED);
                connectedToDatabase = false;
                clearTable();
            }
        } catch (SQLException e) {
            handleConnectionError(e);
        }
    }

    private void executeSQLCommand() {
        if (connectedToDatabase && tableModel == null) {
            try {
                tableModel = new ResultSetTableModel(connection, jtaSqlCommand.getText());
                table.setModel(tableModel);
            } catch (ClassNotFoundException | SQLException e) {
                handleSQLError(e);
            }
        } else if (connectedToDatabase && tableModel != null) {
            String query = jtaSqlCommand.getText();
            if (query.toLowerCase().contains("select")) {
                try {
                    tableModel.setQuery(query);
                } catch (IllegalStateException | SQLException e) {
                    handleSQLError(e);
                }
            } else {
                try {
                    tableModel.setUpdate(query);
                    clearTable();
                } catch (IllegalStateException | SQLException e) {
                    handleSQLError(e);
                }
            }
        }
    }

    private void clearResultWindow() {
        clearTable();
    }

    private void clearTable() {
        table.setModel(new DefaultTableModel());
        tableModel = null;
    }

    private void updateConnectionStatus(String status, Color color) {
        jlbConnectionStatus.setText(status);
        jlbConnectionStatus.setForeground(color);
    }

    private void handleConnectionError(Exception e) {
        updateConnectionStatus("No Connection Now", Color.RED);
        clearTable();
        e.printStackTrace();
    }

    private void handleSQLError(Exception e) {
        updateConnectionStatus("No Connection Now", Color.RED);
        clearTable();
        JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Main().setVisible(true);
            } catch (ClassNotFoundException | SQLException | IOException e) {
                e.printStackTrace();
            }
        });
    }
}
