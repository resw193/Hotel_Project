package gui.customer;

import DAO.CustomerDAO;
import Entity.Customer;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormAddCustomer extends JDialog {

    private static final Color BG = new Color(0x0B1F33);
    private static final Color CARD_BG = new Color(0x0F2A44);
    private static final Color BORDER = new Color(0x274A6B);
    private static final Color TEXT = new Color(0xE6F1FF);
    private static final Color ACCENT = new Color(0x22D3EE);
    private static final Font  BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private CustomerDAO customerDAO;

    private JTextField txtName, txtPhone, txtEmail, txtCCCD;
    private JLabel lblRegisDate, lblLoyalty;

    public FormAddCustomer(Window owner) {
        super(owner, "Thêm khách hàng", ModalityType.APPLICATION_MODAL);
        this.customerDAO = new CustomerDAO();
        setSize(520, 360);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new MigLayout("wrap, insets 12, gap 10", "[grow,fill][grow,fill]", "[][][][][]push[]"));
        root.setBackground(BG);
        root.setBorder(new LineBorder(BORDER));
        add(root);

        // txtField
        txtName  = textField();
        txtPhone = textField();
        txtEmail = textField();
        txtCCCD  = textField();

        lblRegisDate = labelValue(nowStr());
        lblLoyalty = labelValue("0");

        root.add(label("Họ tên"));            root.add(txtName);
        root.add(label("Số điện thoại"));     root.add(txtPhone);
        root.add(label("Email"));             root.add(txtEmail);
        root.add(label("CCCD"));              root.add(txtCCCD);
        root.add(label("Ngày đăng ký"));      root.add(lblRegisDate);
        root.add(label("Điểm thân thiết"));   root.add(lblLoyalty);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton btnSave = primaryButton("Lưu", true);
        JButton btnCancel = primaryButton("Hủy", false);
        actions.add(btnSave); actions.add(btnCancel);
        root.add(actions, "span 2,growx");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> addCustomer());
    }

    private void addCustomer(){
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String idCard = txtCCCD.getText().trim();

        if(!validData()) return;

        Customer c = new Customer();
        c.setFullName(name);
        c.setPhone(phone);
        c.setEmail(email);
        c.setIdCard(idCard);
        c.setRegisDate(LocalDateTime.now());
        c.setLoyaltyPoint(0);

        boolean ok = customerDAO.addCustomer(c);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            dispose();
        } else {
            msg("Thêm khách hàng thất bại! (đã bị trùng SĐT hoặc CCCD)");
        }
    }

    public boolean validData(){
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String idCard = txtCCCD.getText().trim();

        if (name.isEmpty()) {
            msg("Họ tên không được để trống.");
            return false;
        }
        if (!phone.matches("^0\\d{9}$")) {
            msg("Số điện thoại phải 10 chữ số và bắt đầu bằng 0.");
            return false;
        }

        if (!email.isEmpty() && !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            msg("Email không hợp lệ.");
            return false;
        }
        if (!idCard.isEmpty() && !idCard.matches("^\\d{9,12}$")) {
            msg("CCCD phải 9–12 chữ số.");
            return false;
        }

        return true;
    }

    // style
    private JLabel label(String s){
        JLabel l = new JLabel(s);
        l.setForeground(ACCENT);
        l.setFont(BASE_FONT.deriveFont(Font.BOLD));
        return l;
    }

    private JLabel labelValue(String s){
        JLabel l = new JLabel(s);
        l.setForeground(TEXT);
        l.setFont(BASE_FONT);
        return l;
    }

    private JTextField textField(){
        JTextField f = new JTextField();
        f.setFont(BASE_FONT);
        f.setForeground(TEXT);
        f.setBackground(new Color(0x102A43));
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                BorderFactory.createEmptyBorder(6,8,6,8)));
        return f;
    }
    private JButton primaryButton(String text, boolean solid){
        JButton b = new JButton(text);
        b.setFont(BASE_FONT.deriveFont(Font.BOLD));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(new Color(0x1B4F72)));
        b.setBackground(solid ? new Color(0x2563EB) : new Color(0x0EA5E9));
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
    private static String nowStr(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void msg(String s){
        JOptionPane.showMessageDialog(this, s);
    }
}
