import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

public class Login extends JFrame {
    private static final String USER_DATA_FILE = "D:\\std info\\users.json";
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Login");
        
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 0));

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");
       

        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);

        loginButton.addActionListener(e -> loginUser());
        signupButton.addActionListener(e -> signupUser());
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        JSONObject userData = readJsonFromFile();
        JSONArray users = userData.getJSONArray("users");

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("username").equals(username) && user.getString("password").equals(password)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                new StudentManagerGUI().setVisible(true);
                this.dispose();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Invalid Username or Password.");
    }

    private void signupUser() {
        String b,c="admin1234";
b=JOptionPane.showInputDialog(null,"Enter Admin Pin :",null);
if(b.equals(c)){
    JOptionPane.showMessageDialog(null, "You can Sign-Up now!");
    SignUp su= new SignUp();
    su.setVisible(true);
    this.dispose();
}
else{
    JOptionPane.showMessageDialog(null, "Invalid Pin!");
    Login l= new Login();
    l.setVisible(true);
    this.dispose();
}
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



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
