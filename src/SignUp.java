import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class SignUp extends JFrame {
    private static final String USER_DATA_FILE = "D:\\std info\\users.json";
    private JTextField fullNameField, emailField, usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JCheckBox showPasswordCheckBox;
    
    public SignUp() {
        setTitle("Sign Up");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
         setLayout(new GridLayout(6, 2, 10, 10));
        add(new JLabel("Full Name:"));
        fullNameField = new JTextField();
         add(fullNameField);
        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        showPasswordCheckBox = new JCheckBox("Show Password");
        add(showPasswordCheckBox);
        
        JButton signUpButton = new JButton("Sign Up");
        add(signUpButton);

       

        signUpButton.addActionListener(e -> registerUser());
 showPasswordCheckBox.addActionListener(e -> PasswordVisibility());
        setVisible(true);
    }

    private void PasswordVisibility() {
        boolean showPassword = showPasswordCheckBox.isSelected();
        passwordField.setEchoChar(showPassword ? '\u0000' : '*');
        confirmPasswordField.setEchoChar(showPassword ? '\u0000' : '*');
    }

    private void registerUser() {
        
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        JSONObject userData = readJsonFromFile();
        JSONArray users = userData.getJSONArray("users");

        for (int i = 0; i < users.length(); i++) {
            if (users.getJSONObject(i).getString("username").equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
                return;
            }
        }

        JSONObject newUser = new JSONObject();
        newUser.put("fullName", fullName);
        newUser.put("email", email);
        newUser.put("username", username);
        newUser.put("password", password);
        users.put(newUser);

        writeJsonToFile(userData);
        JOptionPane.showMessageDialog(this, "Signup Successful! Redirecting to login.");
        
        Login lgn=new Login();
        lgn.setVisible(true);
        this.dispose();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    private JSONObject readJsonFromFile() {
        File f = new File(USER_DATA_FILE);
        if (!f.exists()) {
            return new JSONObject().put("users", new JSONArray());
        }
        try {
            String cont = new String(Files.readAllBytes(Paths.get(USER_DATA_FILE)));
            return new JSONObject(cont);
        } catch (IOException e) {
            return new JSONObject().put("users", new JSONArray());
        }
    }

    private void writeJsonToFile(JSONObject userData) {
        try (FileWriter w = new FileWriter(USER_DATA_FILE)) {
            w.write(userData.toString(4));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving user data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp());

    }
}