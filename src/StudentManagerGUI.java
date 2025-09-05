
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Arrays;
import com.itextpdf.text.*;

import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class StudentManagerGUI extends JFrame {

    private static final String JSON_FILE_PATH = "D:\\std info\\Data.json";
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentManagerGUI() {
        setTitle("Student Manager");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createJsonFileIfNotExists();
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchStudents(searchField.getText()));

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        tableModel = new DefaultTableModel(new String[]{
            "Name", "Roll", "Institute", "Courses", "Address", "Blood Group", "Phone", "Gender"
        }, 0);
        studentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        loadStudentsIntoTable();

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton modifyButton = new JButton("Modify Student");
        JButton refreshButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export to PDF");

        addButton.addActionListener(e -> addOrModifyStudent(null));
        deleteButton.addActionListener(e -> deleteStudent());
        modifyButton.addActionListener(e -> {
            int sRow = studentTable.getSelectedRow();
            if (sRow >= 0) {
                JSONObject student = getStudentFromTable(sRow);
                addOrModifyStudent(student);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to modify.");
            }
        });
        refreshButton.addActionListener(e -> loadStudentsIntoTable());
        exportButton.addActionListener(e -> exportToPDF());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void exportToPDF() {
        try {

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("StudentData.pdf"));
            document.open();

            document.add(new Paragraph("Student Data Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generated on: " + java.time.LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(8);
            pdfTable.setWidths(new float[]{2, 1, 2, 3, 3, 2, 2, 2});
            pdfTable.addCell("Name");
            pdfTable.addCell("Roll");
            pdfTable.addCell("Institute");
            pdfTable.addCell("Courses");
            pdfTable.addCell("Address");
            pdfTable.addCell("Blood Group");
            pdfTable.addCell("Phone");
            pdfTable.addCell("Gender");

            JSONObject studentData = readJsonFromFile();
            JSONArray students = studentData.getJSONArray("students");

            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);
                pdfTable.addCell(student.getString("name"));
                pdfTable.addCell(String.valueOf(student.getInt("roll")));
                pdfTable.addCell(student.getString("institute"));
                pdfTable.addCell(Arrays.toString(getArrayAsList(student.getJSONArray("courses"))));
                pdfTable.addCell(student.getString("address"));
                pdfTable.addCell(student.getString("bloodGroup"));
                pdfTable.addCell(student.getString("phone"));
                pdfTable.addCell(student.getString("gender"));
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this, "Student data exported to PDF successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting to PDF: " + e.getMessage());
        }
    }

    private void addOrModifyStudent(JSONObject studentToModify) {

        JTextField nameField = new JTextField(studentToModify != null ? studentToModify.getString("name") : "");
        JTextField rollField = new JTextField(studentToModify != null ? String.valueOf(studentToModify.getInt("roll")) : "");
        JTextField instituteField = new JTextField(studentToModify != null ? studentToModify.getString("institute") : "");
        JTextField addressField = new JTextField(studentToModify != null ? studentToModify.getString("address") : "");
        JTextField phoneField = new JTextField(studentToModify != null ? studentToModify.getString("phone") : "");

        Dimension textFieldSize = new Dimension(200, 25);
        nameField.setPreferredSize(textFieldSize);
        rollField.setPreferredSize(textFieldSize);
        instituteField.setPreferredSize(textFieldSize);
        addressField.setPreferredSize(textFieldSize);
        phoneField.setPreferredSize(textFieldSize);

        String[] bloodGroups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        JComboBox<String> bloodGroupComboBox = new JComboBox<>(bloodGroups);
        if (studentToModify != null) {
            bloodGroupComboBox.setSelectedItem(studentToModify.getString("bloodGroup"));
        }

        String[] allCourses = {"EDC", "OOP", "DM", "LA", "BGS", "EDC LAB", "OOP LAB", "PROJECT VIVA"};
        JList<String> coursesList = new JList<>(allCourses);
        coursesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (studentToModify != null) {
            String[] selectedCourses = getArrayAsList(studentToModify.getJSONArray("courses"));
            for (int i = 0; i < allCourses.length; i++) {
                if (Arrays.asList(selectedCourses).contains(allCourses[i])) {
                    coursesList.addSelectionInterval(i, i);
                }
            }
        }
        JScrollPane coursesScrollPane = new JScrollPane(coursesList);

        String[] genderOptions = {"Male", "Female"};
        JComboBox<String> genderComboBox = new JComboBox<>(genderOptions);

        JTextField customGenderField = new JTextField();
        customGenderField.setPreferredSize(textFieldSize);
        customGenderField.setVisible(false);

        genderComboBox.addActionListener(e -> {

        });

        if (studentToModify != null) {
            String gender = studentToModify.getString("gender");
            if (Arrays.asList(genderOptions).contains(gender)) {
                genderComboBox.setSelectedItem(gender);
            } else {

                genderComboBox.setSelectedItem("Male");
            }
        }

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Roll:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(rollField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Institute:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(instituteField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Courses:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(coursesScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(bloodGroupComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        inputPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        inputPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(genderComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                studentToModify == null ? "Add Student" : "Modify Student",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int roll = Integer.parseInt(rollField.getText());
                String[] selectedCourses = coursesList.getSelectedValuesList().toArray(new String[0]);
                String gender = genderComboBox.getSelectedItem().equals("Custom") ? customGenderField.getText() : (String) genderComboBox.getSelectedItem();

                if (studentToModify == null) {
                    addNewStudent(nameField.getText(), roll, instituteField.getText(), selectedCourses, addressField.getText(),
                            bloodGroupComboBox.getSelectedItem().toString(), phoneField.getText(), gender);
                } else {
                    modifyExistingStudent(studentToModify, nameField.getText(), roll, instituteField.getText(), selectedCourses,
                            addressField.getText(), bloodGroupComboBox.getSelectedItem().toString(), phoneField.getText(), gender);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid roll number.");
            }
        }
    }

    private void addNewStudent(String name, int roll, String institute, String[] courses, String address, String bloodGroup, String phone, String gender) {
        try {
            JSONObject studentData = readJsonFromFile();
            JSONArray students = studentData.getJSONArray("students");

            JSONObject newStudent = new JSONObject();
            newStudent.put("name", name);
            newStudent.put("roll", roll);
            newStudent.put("institute", institute);
            newStudent.put("courses", new JSONArray(Arrays.asList(courses)));
            newStudent.put("address", address);
            newStudent.put("bloodGroup", bloodGroup);
            newStudent.put("phone", phone);
            newStudent.put("gender", gender);

            students.put(newStudent);
            writeJsonToFile(studentData);
            loadStudentsIntoTable();

            JOptionPane.showMessageDialog(this, "Student added successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void modifyExistingStudent(JSONObject studentToModify, String name, int roll, String institute, String[] courses, String address, String bloodGroup, String phone, String gender) {
        try {
            JSONObject studentData = readJsonFromFile();
            JSONArray students = studentData.getJSONArray("students");

            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);
                if (studentToModify.getInt("roll") == student.getInt("roll")) {
                    student.put("name", name);
                    student.put("roll", roll);
                    student.put("institute", institute);
                    student.put("courses", new JSONArray(Arrays.asList(courses)));
                    student.put("address", address);
                    student.put("bloodGroup", bloodGroup);
                    student.put("phone", phone);
                    student.put("gender", gender);

                    writeJsonToFile(studentData);
                    loadStudentsIntoTable();

                    JOptionPane.showMessageDialog(this, "Student modified successfully.");
                    return;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                JSONObject studentData = readJsonFromFile();
                JSONArray students = studentData.getJSONArray("students");

                students.remove(selectedRow);
                writeJsonToFile(studentData);
                loadStudentsIntoTable();

                JOptionPane.showMessageDialog(this, "Student deleted successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void searchStudents(String query) {
        try {
            JSONObject studentData = readJsonFromFile();
            JSONArray students = studentData.getJSONArray("students");

            tableModel.setRowCount(0);
            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);
                if (student.getString("name").toLowerCase().contains(query.toLowerCase())
                        || String.valueOf(student.getInt("roll")).contains(query)
                        || student.getString("institute").toLowerCase().contains(query.toLowerCase())) {
                    tableModel.addRow(new Object[]{
                        student.getString("name"),
                        student.getInt("roll"),
                        student.getString("institute"),
                        Arrays.toString(getArrayAsList(student.getJSONArray("courses"))),
                        student.getString("address"),
                        student.getString("bloodGroup"),
                        student.getString("phone"),
                        student.getString("gender")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadStudentsIntoTable() {
        try {
            JSONObject studentData = readJsonFromFile();
            JSONArray students = studentData.getJSONArray("students");

            tableModel.setRowCount(0);
            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);
                tableModel.addRow(new Object[]{
                    student.getString("name"),
                    student.getInt("roll"),
                    student.getString("institute"),
                    Arrays.toString(getArrayAsList(student.getJSONArray("courses"))),
                    student.getString("address"),
                    student.getString("bloodGroup"),
                    student.getString("phone"),
                    student.getString("gender")
                }
                );

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private JSONObject readJsonFromFile() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            return new JSONObject(content);
        } catch (IOException e) {
            return new JSONObject().put("students", new JSONArray());
        }
    }

    private void writeJsonToFile(JSONObject studentData) throws IOException {
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            writer.write(studentData.toString(4));
        }
    }

    private void createJsonFileIfNotExists() {
        File file = new File(JSON_FILE_PATH);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                JSONObject studentData = new JSONObject().put("students", new JSONArray());
                writer.write(studentData.toString(4));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error creating JSON file: " + e.getMessage());
            }
        }
    }

    private String[] getArrayAsList(JSONArray jsonArray) {
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }

    private JSONObject getStudentFromTable(int row) {
        try {
            JSONObject student = new JSONObject();
            student.put("name", tableModel.getValueAt(row, 0));

            student.put("roll", Integer.parseInt(tableModel.getValueAt(row, 1).toString()));
            student.put("institute", tableModel.getValueAt(row, 2));
            student.put("courses", new JSONArray(Arrays.asList(tableModel.getValueAt(row, 3).toString().replace("[", "").replace("]", "").split(", "))));
            student.put("address", tableModel.getValueAt(row, 4));
            student.put("bloodGroup", tableModel.getValueAt(row, 5));
            student.put("phone", tableModel.getValueAt(row, 6));
            student.put("gender", tableModel.getValueAt(row, 7));
            return student;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error retrieving student data: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagerGUI().setVisible(true));
    }

}
