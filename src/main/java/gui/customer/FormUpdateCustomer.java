package gui.customer;

import bus.CustomerBUS;
import entity.Customer;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class FormUpdateCustomer extends JDialog {

    // Color
    private static final Color BG = new Color(0x0B1F33);
    private static final Color CARD_BG = new Color(0x0F2A44);
    private static final Color BORDER = new Color(0x274A6B);
    private static final Color TEXT = new Color(0xE6F1FF);
    private static final Color ACCENT = new Color(0x22D3EE);
    private static final Font  BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private CustomerBUS customerBUS;
    private String customerID;

    private JTextField txtName, txtPhone, txtEmail;

    public FormUpdateCustomer(Window owner, String customerID) {
        super(owner, "Cập nhật khách hàng – " + customerID, ModalityType.APPLICATION_MODAL);
        this.customerBUS = new CustomerBUS();
        this.customerID = customerID;

        setSize(520, 260);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new MigLayout("wrap, insets 12, gap 10", "[grow,fill][grow,fill]", "[][][]push[]"));
        root.setBackground(BG);
        root.setBorder(new LineBorder(BORDER));
        add(root);

        txtName = textField();
        txtPhone = textField();
        txtEmail = textField();

        root.add(label("Họ tên")); root.add(txtName);
        root.add(label("Số điện thoại")); root.add(txtPhone);
        root.add(label("Email")); root.add(txtEmail);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton btnSave = primaryButton("Lưu", true);
        JButton btnCancel = primaryButton("Hủy", false);
        actions.add(btnSave); actions.add(btnCancel);
        root.add(actions, "span 2,growx");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> update());

        loadData();
    }

    // Load data
    private void loadData(){
        Customer c = customerBUS.getById(customerID);
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng!");
            dispose();
            return;
        }
        txtName.setText(c.getFullName());
        txtPhone.setText(c.getPhone());
        txtEmail.setText(c.getEmail());
    }

    // Update customer (lấy thông tin trên form (đã thay đổi -> gọi bus xử lý)
    private void update(){
        String name  = txtName.getText();
        String phone = txtPhone.getText();
        String email = txtEmail.getText();

        try {
            boolean ok = customerBUS.updateCustomer(customerID, name, phone, email);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                dispose();
            }
            else {
                msg("Cập nhật thất bại!");
            }
        } catch (IllegalArgumentException ex) {
            msg(ex.getMessage());
        }
    }

    // style
    private JLabel label(String s){
        JLabel l = new JLabel(s);
        l.setForeground(ACCENT);
        l.setFont(BASE_FONT.deriveFont(Font.BOLD));
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

    private void msg(String s){
        JOptionPane.showMessageDialog(this, s);
    }
}
