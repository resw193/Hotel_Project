package gui.profile;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import DAO.AccountDAO;
import Entity.Account;
import Entity.Employee;
import gui.login.main.Application;

public class FormProfileInfo extends JPanel {

    private JPanel panel;
    private JLabel lblTitle;
    private JLabel lblAvatar;

    private JTextField txtEmployeeID;
    private JTextField txtFullName;
    private JTextField txtGender;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtTypeName;

    private AccountDAO accountDAO = new AccountDAO();

    private BufferedImage bgImage;

    public FormProfileInfo() {
        accountDAO = new AccountDAO();
        setLayout(new BorderLayout());
        initComponents();
        loadEmployeeAndBind();
        add(panel, BorderLayout.CENTER);
    }

    private void initComponents() {
        panel = new JPanel(new MigLayout(
                "wrap 2, fillx, insets 22 70 22 70, gap 18 20",
                "[grow 0,trail]20[fill,grow]"
        ));
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:18;background:#0F2A47;foreground:#EAF2FF;");

        // Title
        lblTitle = new JLabel("Personal Information");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +6;foreground:#F2C94C");

        // Avatar (center top)
        lblAvatar = new JLabel();
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(140, 140));

        // Read-only fields
        txtEmployeeID = roField();
        txtFullName   = roField();
        txtGender     = roField();
        txtPhone      = roField();
        txtEmail      = roField();
        txtTypeName   = roField();

        JPanel header = new JPanel(new MigLayout("wrap, insets 0 0 12 0", "[grow]", "[]10[]"));
        header.setOpaque(false);
        header.add(lblTitle, "al left");
        header.add(lblAvatar, "al center");
        panel.add(header, "span 2, growx, wrap");

        // Rows
        panel.add(label("EmployeeID:")); panel.add(txtEmployeeID, "growx");
        panel.add(label("FullName:"));   panel.add(txtFullName,   "growx");
        panel.add(label("Gender:"));     panel.add(txtGender,     "growx");
        panel.add(label("Phone:"));      panel.add(txtPhone,      "growx");
        panel.add(label("Email:"));      panel.add(txtEmail,      "growx");
        panel.add(label("TypeName:"));   panel.add(txtTypeName,   "growx");
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.putClientProperty(FlatClientProperties.STYLE,
                "foreground:#D9E6FF;font:+1");
        return l;
    }

    private JTextField roField() {
        JTextField t = new JTextField();
        t.setEditable(false);
        t.putClientProperty(FlatClientProperties.STYLE,
                "background:#12355A;foreground:#EAF2FF;borderWidth:0;arc:14;padding:10,14,10,14;font:+1");
        return t;
    }

    private String s(String v) { return v == null ? "" : v; }

    private void loadEmployeeAndBind() {
        try {
            Employee e = accountDAO.getAccountByUsername(Application.username).getEmployee();
            txtEmployeeID.setText(s(e.getEmployeeID()));
            txtFullName.setText(s(e.getFullName()));
            txtGender.setText(e.isGender() ? "Nam" : "Nữ");
            txtPhone.setText(s(e.getPhone()));
            txtEmail.setText(s(e.getEmail()));
            txtTypeName.setText(e.getEmployeeType() != null ? s(e.getEmployeeType().getTypeName()) : "");

            displayImage(e.getImgSource());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                lblAvatar.setIcon(null);
                return;
            }
            BufferedImage img = ImageIO.read(new File(imagePath));
            Image scaled = img.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            lblAvatar.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            lblAvatar.setIcon(null);
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(10, 34, 55),
                    getWidth(), getHeight(), new Color(20, 60, 100));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}
