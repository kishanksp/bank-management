package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// ----------------------------------------------------
// Main Class (launcher)
// ----------------------------------------------------
public class BankManagementSystem {
    public static void main(String[] args) {
        new Login();
    }
}

// ----------------------------------------------------
// LOGIN PAGE
// ----------------------------------------------------
class Login extends JFrame implements ActionListener {

    JTextField userField;
    JPasswordField passField;
    JButton loginBtn, signupBtn;

    Login() {
        setTitle("AUTOMATED TELLER MACHINE");
        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("download.jpg"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon i3 = new ImageIcon(i2);

        JLabel label = new JLabel(i3);
        label.setBounds(30, 30, 100, 100);
        add(label);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(180, 40, 100, 30);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(260, 40, 180, 30);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(180, 80, 100, 30);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(260, 80, 180, 30);
        add(passField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(210, 150, 100, 30);
        loginBtn.addActionListener(this);
        add(loginBtn);

        signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(330, 150, 100, 30);
        signupBtn.addActionListener(this);
        add(signupBtn);

        setSize(600, 300);
        setLocation(350, 200);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == signupBtn) {
            setVisible(false);
            new Signup(); // go to signup
        } else {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (UserDatabase.login(user, pass)) {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                setVisible(false);
                new Dashboard(user);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Credentials");
            }
        }
    }
}

// ----------------------------------------------------
// SIGNUP PAGE
// ----------------------------------------------------
class Signup extends JFrame implements ActionListener {

    JTextField userField, nameField;
    JPasswordField passField;
    JButton createBtn, backBtn;

    Signup() {
        setTitle("Account Registration");
        setLayout(null);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50, 40, 100, 30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 40, 200, 30);
        add(nameField);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 80, 100, 30);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(150, 80, 200, 30);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 120, 100, 30);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(150, 120, 200, 30);
        add(passField);

        createBtn = new JButton("Create Account");
        createBtn.setBounds(80, 180, 150, 30);
        createBtn.addActionListener(this);
        add(createBtn);

        backBtn = new JButton("Back");
        backBtn.setBounds(250, 180, 100, 30);
        backBtn.addActionListener(this);
        add(backBtn);

        setSize(450, 300);
        setLocation(400, 200);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backBtn) {
            setVisible(false);
            new Login();  // FIXED (go back to login)
        } else {
            String name = nameField.getText();
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (user.isEmpty() || pass.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required!");
                return;
            }

            if (UserDatabase.register(user, pass, name)) {
                JOptionPane.showMessageDialog(null, "Account Created Successfully!");
                setVisible(false);
                new Login(); // FIXED (after signup go to login)
            } else {
                JOptionPane.showMessageDialog(null, "Username Already Exists!");
            }
        }
    }
}

// ----------------------------------------------------
// DASHBOARD PAGE
// ----------------------------------------------------
class Dashboard extends JFrame implements ActionListener {

    String username;
    JLabel balanceLabel;
    JButton depositBtn, withdrawBtn, logoutBtn;

    Dashboard(String username) {
        this.username = username;
        setTitle("Welcome " + username);
        setLayout(null);

        balanceLabel = new JLabel("Balance: ₹" + UserDatabase.getBalance(username));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 22));
        balanceLabel.setBounds(50, 30, 300, 30);
        add(balanceLabel);

        depositBtn = new JButton("Deposit");
        depositBtn.setBounds(50, 100, 100, 30);
        depositBtn.addActionListener(this);
        add(depositBtn);

        withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setBounds(170, 100, 100, 30);
        withdrawBtn.addActionListener(this);
        add(withdrawBtn);

        logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(290, 100, 100, 30);
        logoutBtn.addActionListener(this);
        add(logoutBtn);

        setSize(450, 250);
        setLocation(450, 250);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == logoutBtn) {
            setVisible(false);
            new Login(); // back to login
        } else if (ae.getSource() == depositBtn) {
            String amt = JOptionPane.showInputDialog("Enter deposit amount:");
            if (amt != null) {
                double a = Double.parseDouble(amt);
                UserDatabase.deposit(username, a);
                balanceLabel.setText("Balance: ₹" + UserDatabase.getBalance(username));
            }
        } else if (ae.getSource() == withdrawBtn) {
            String amt = JOptionPane.showInputDialog("Enter withdrawal amount:");
            if (amt != null) {
                double a = Double.parseDouble(amt);
                boolean ok = UserDatabase.withdraw(username, a);
                if (!ok)
                    JOptionPane.showMessageDialog(null, "Insufficient Balance!");
                balanceLabel.setText("Balance: ₹" + UserDatabase.getBalance(username));
            }
        }
    }
}

// ----------------------------------------------------
// SIMPLE IN-MEMORY DATABASE
// ----------------------------------------------------
class UserDatabase {

    private static java.util.HashMap<String, String> users = new java.util.HashMap<>();
    private static java.util.HashMap<String, String> names = new java.util.HashMap<>();
    private static java.util.HashMap<String, Double> balances = new java.util.HashMap<>();

    public static boolean register(String username, String password, String fullName) {
        if (users.containsKey(username)) return false;

        users.put(username, password);
        names.put(username, fullName);
        balances.put(username, 0.0);

        return true;
    }

    public static boolean login(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public static double getBalance(String username) {
        return balances.get(username);
    }

    public static void deposit(String username, double amount) {
        balances.put(username, balances.get(username) + amount);
    }

    public static boolean withdraw(String username, double amount) {
        if (balances.get(username) < amount) return false;
        balances.put(username, balances.get(username) - amount);
        return true;
    }
}
