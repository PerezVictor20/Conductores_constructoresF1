package Interface;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class Formula_1 {
    private Connection conn;
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboBox;

    public Formula_1() {
        // Establecer conexión a la base de datos PostgreSQL
        connectDB();

        // Crear la interfaz gráfica
        createGUI();
    }

    private void connectDB() {
        try {
            String url = "jdbc:postgresql://localhost:5433/formula1";
            String user = "postgres";
            String password = "1234";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión establecida con PostgreSQL.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Centrar el título de la ventana
        centerTitle(frame, "Tabla de constructores y conductores de fórmula 1");

        // Panel para el combo box y el texto
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel label = new JLabel("Seleccione una tabla");
        comboBox = new JComboBox<>(new String[]{"", "Constructores", "Conductores", "Conductores y constructores"}); // opción "Ambos" añadida
        comboBox.setPreferredSize(new Dimension(150, 25));
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTable = (String) comboBox.getSelectedItem();
                if (!selectedTable.isEmpty()) { // solo actualizar si no es la opción vacía
                    updateTable(selectedTable);
                } else {
                    clearTable();
                }
            }
        });

        topPanel.add(label);
        topPanel.add(comboBox);

        // Tabla para mostrar los datos
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Centrar el contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void updateTable(String selectedTable) {
        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        try {
            String query;
            if ("Constructores".equals(selectedTable)) {
                query = "SELECT constructor_id, constructor_ref, name, nationality, url FROM constructors";
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();

                columnNames.add("Constructor ID");
                columnNames.add("Constructor Ref");
                columnNames.add("Name");
                columnNames.add("Nationality");
                columnNames.add("URL");

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("constructor_id"));
                    row.add(rs.getString("constructor_ref"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("nationality"));
                    row.add(rs.getString("url"));
                    data.add(row);
                }

                rs.close();
                pstmt.close();
            } else if ("Conductores".equals(selectedTable)) {
                query = "SELECT driver_id, driver_ref, number, code, forename, surname, dob, nationality, url FROM drivers";
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();

                columnNames.add("Driver ID");
                columnNames.add("Driver Ref");
                columnNames.add("Number");
                columnNames.add("Code");
                columnNames.add("Forename");
                columnNames.add("Surname");
                columnNames.add("Date of Birth");
                columnNames.add("Nationality");
                columnNames.add("URL");

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("driver_id"));
                    row.add(rs.getString("driver_ref"));
                    row.add(rs.getInt("number"));
                    row.add(rs.getString("code"));
                    row.add(rs.getString("forename"));
                    row.add(rs.getString("surname"));
                    row.add(rs.getDate("dob"));
                    row.add(rs.getString("nationality"));
                    row.add(rs.getString("url"));
                    data.add(row);
                }

                rs.close();
                pstmt.close();
            } else if ("Conductores y constructores".equals(selectedTable)) {
                query = "SELECT d.driver_id, d.driver_ref, d.number, d.code, d.forename, d.surname, d.dob, d.nationality AS driver_nationality, d.url AS driver_url, " +
                        "c.constructor_id, c.constructor_ref, c.name AS constructor_name, c.nationality AS constructor_nationality, c.url AS constructor_url " +
                        "FROM drivers d " +
                        "LEFT JOIN constructors c ON d.driver_id = c.constructor_id"; // Adjust this JOIN condition as per your actual database schema
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();

                columnNames.add("Driver ID");
                columnNames.add("Driver Ref");
                columnNames.add("Number");
                columnNames.add("Code");
                columnNames.add("Forename");
                columnNames.add("Surname");
                columnNames.add("Date of Birth");
                columnNames.add("Driver Nationality");
                columnNames.add("Driver URL");
                columnNames.add("Constructor ID");
                columnNames.add("Constructor Ref");
                columnNames.add("Constructor Name");
                columnNames.add("Constructor Nationality");
                columnNames.add("Constructor URL");

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("driver_id"));
                    row.add(rs.getString("driver_ref"));
                    row.add(rs.getInt("number"));
                    row.add(rs.getString("code"));
                    row.add(rs.getString("forename"));
                    row.add(rs.getString("surname"));
                    row.add(rs.getDate("dob"));
                    row.add(rs.getString("driver_nationality"));
                    row.add(rs.getString("driver_url"));
                    row.add(rs.getInt("constructor_id"));
                    row.add(rs.getString("constructor_ref"));
                    row.add(rs.getString("constructor_name"));
                    row.add(rs.getString("constructor_nationality"));
                    row.add(rs.getString("constructor_url"));
                    data.add(row);
                }

                rs.close();
                pstmt.close();
            }

            // Actualizar modelo de la tabla
            tableModel.setDataVector(data, columnNames);

            // Centrar el contenido de las celdas
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.setDefaultRenderer(Object.class, centerRenderer);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearTable() {
        tableModel.setDataVector(new Vector<>(), new Vector<>()); // vaciar la tabla
    }

    private void centerTitle(JFrame frame, String title) {
        Font font = new Font("Serif", Font.BOLD, 14);
        FontMetrics metrics = frame.getFontMetrics(font);
        int frameWidth = frame.getWidth();
        int titleWidth = metrics.stringWidth(title);
        int padding = (frameWidth - titleWidth) / 2;
        StringBuilder paddedTitle = new StringBuilder(title);
        for (int i = 0; i < padding / 8; i++) {
            paddedTitle.insert(0, " ");
        }
        frame.setTitle(paddedTitle.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Formula_1::new);
    }
}
