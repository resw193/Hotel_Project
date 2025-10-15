package gui.login.forms;

import DAO.AccountDAO;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import gui.login.main.Application;
import net.miginfocom.swing.MigLayout;
import other.EmailService;
import other.OtpService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.security.SecureRandom;

public class Login extends JPanel {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton cmdLogin;
    private JButton cmdForgot;   // <-- NEW
    private AccountDAO accountDAO;

    public static String email;

    public Login() {
        accountDAO = new AccountDAO();
        init();
        txtUsername.addKeyListener(new EnterKeyListener());
        txtPassword.addKeyListener(new EnterKeyListener());
    }

    private void init() {
        setOpaque(false);
        setLayout(new MigLayout("wrap,fillx,insets 45 45 50 45", "[fill]"));

        JLabel title = new JLabel("Login to your account", SwingConstants.CENTER);
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        JCheckBox chRememberMe = new JCheckBox("Remember me");
        cmdForgot = new JButton("Forgot password?");     // <-- NEW
        cmdLogin = new JButton("Login");

        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");

        txtUsername.putClientProperty(FlatClientProperties.STYLE,
                "margin:5,10,5,10;focusWidth:1;innerFocusWidth:0");
        txtPassword.putClientProperty(FlatClientProperties.STYLE,
                "margin:5,10,5,10;focusWidth:1;innerFocusWidth:0;showRevealButton:true");

        cmdLogin.putClientProperty(FlatClientProperties.STYLE,
                "background:$Component.accentColor;borderWidth:0;focusWidth:0;innerFocusWidth:0");

        cmdForgot.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth:0;focusWidth:0;innerFocusWidth:0;foreground:$Component.accentColor");

        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        add(title);
        add(new JLabel("Username"), "gapy 20");
        add(txtUsername);
        add(new JLabel("Password"), "gapy 10");
        add(txtPassword);

        // Hàng Remember (trái) - Forgot (phải)
        JPanel rowOptions = new JPanel(new MigLayout("insets 0, fillx", "[left]push[right]", "[]"));
        rowOptions.setOpaque(false);
        rowOptions.add(chRememberMe, "left");
        rowOptions.add(cmdForgot, "right");
        add(rowOptions);

        add(cmdLogin, "gapy 25");

        cmdLogin.addActionListener(this::cmdLoginActionPerformed);
        cmdForgot.addActionListener(this::cmdForgotActionPerformed);  // <-- NEW
    }

    private void cmdLoginActionPerformed(ActionEvent evt) {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username and password cannot be empty");
            return;
        }

        if (accountDAO.checkAuthAccount(user, pass)) {
            //JOptionPane.showMessageDialog(null, "Login successful");
            Application.login(user, pass);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Forgot password flow (OTP email) ---
    private void cmdForgotActionPerformed(ActionEvent evt) {
        String username = JOptionPane.showInputDialog(null,
                "Nhập username để nhận OTP qua email:", "Quên mật khẩu", JOptionPane.QUESTION_MESSAGE);

        if (username == null)
            return;
        username = username.trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username không được trống");
            return;
        }

        // Lấy email từ username (Account -> Employee -> getEmail())
        email = accountDAO.getAccountByUsername(username).getEmployee().getEmail();
        if (email == null || email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy email của tài khoản này!");
            return;
        }

        // Tạo OTP 6 chữ số & lưu tạm 5 phút
        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        OtpService.put(username, otp, 5 * 60);

        // Gửi email OTP
        try {
            EmailService.sendOtp(email, otp);
            JOptionPane.showMessageDialog(null,
                    "Đã gửi mã OTP đến email: " + email + "\nVui lòng kiểm tra hộp thư và nhập OTP để đặt lại mật khẩu.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Gửi OTP thất bại: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Dialog nhập OTP + mật khẩu mới
        JTextField txtOtp = new JTextField();
        JPasswordField txtNew = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        txtOtp.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập OTP 6 số");
        txtNew.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mật khẩu mới");
        txtConfirm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Xác nhận mật khẩu");

        JPanel panel = new JPanel(new MigLayout("wrap 2,insets 10 10 5 10", "[][250!]"));
        panel.add(new JLabel("OTP:")); panel.add(txtOtp, "growx");
        panel.add(new JLabel("Mật khẩu mới:")); panel.add(txtNew, "growx");
        panel.add(new JLabel("Xác nhận:")); panel.add(txtConfirm, "growx");

        int opt = JOptionPane.showConfirmDialog(null, panel, "Xác thực OTP & đặt mật khẩu mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) return;

        String enteredOtp = txtOtp.getText().trim();
        String newPass = new String(txtNew.getPassword()).trim();
        String confirm = new String(txtConfirm.getPassword()).trim();

        if (!OtpService.verify(username, enteredOtp)) {
            JOptionPane.showMessageDialog(null, "OTP không đúng hoặc đã hết hạn!");
            return;
        }
        if (newPass.length() < 8) {
            JOptionPane.showMessageDialog(null, "Mật khẩu phải tối thiểu 8 ký tự");
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(null, "Xác nhận mật khẩu không khớp");
            return;
        }

        boolean ok = accountDAO.updatePassword(username, newPass);
        if (ok) {
            JOptionPane.showMessageDialog(null, "Đổi mật khẩu thành công. Vui lòng đăng nhập lại!");
        } else {
            JOptionPane.showMessageDialog(null, "Đổi mật khẩu thất bại", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Nhấn enter để login
    private class EnterKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                cmdLoginActionPerformed(null);
            }
        }
    }

    // Vẽ màu
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = UIScale.scale(20);
        g2.setColor(getBackground());
        g2.setComposite(AlphaComposite.SrcOver.derive(0.6f));
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2.dispose();
        super.paintComponent(g);
    }
}
