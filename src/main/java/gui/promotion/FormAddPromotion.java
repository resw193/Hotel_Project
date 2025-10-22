package gui.promotion;

import bus.PromotionBUS;
import entity.Promotion;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;

public class FormAddPromotion extends JDialog {

    // Color
    private static final Color BG = new Color(0x0B1F33);
    private static final Color BORDER = new Color(0x274A6B);
    private static final Color TEXT = new Color(0xE6F1FF);
    private static final Color ACCENT = new Color(0x22D3EE);
    private static final Font  BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private PromotionBUS promotionBUS = new PromotionBUS();

    private JTextField txtName = new JTextField();
    private JFormattedTextField txtDiscount = new JFormattedTextField(NumberFormat.getNumberInstance());
    private JFormattedTextField txtQuantity = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private DateTimePicker dtStart = new DateTimePicker();
    private DateTimePicker dtEnd   = new DateTimePicker();

    public FormAddPromotion(Window owner) {
        super(owner, "Thêm khuyến mãi", ModalityType.APPLICATION_MODAL);

        getContentPane().setBackground(BG);
        setLayout(new MigLayout("insets 14 16 12 16", "[120!][280!]", "[][][][][]16[]"));

        styleText(txtName);
        styleField(txtDiscount);
        styleField(txtQuantity);

        LocalDateTime now = LocalDateTime.now().plusMinutes(5);
        dtStart.setDateTime(now);
        dtEnd.setDateTime(now.plusDays(1));

        add(label("Tên khuyến mãi:"), "gapbottom 6");
        add(txtName, "growx, wrap");

        add(label("Giảm giá (%):"), "gaptop 4, gapbottom 6");
        add(txtDiscount, "w 120!, wrap");

        add(label("Bắt đầu:"), "gaptop 4, gapbottom 6");
        add(dtStart, "wrap");

        add(label("Kết thúc:"), "gaptop 4, gapbottom 6");
        add(dtEnd, "wrap");

        add(label("Số lượng:"), "gaptop 4");
        add(txtQuantity, "w 120!, wrap");

        JButton btnAdd = primaryButton("Lưu", true);
        JButton btnCancel = primaryButton("Hủy", false);
        add(btnAdd, "span 2, split 2, right");
        add(btnCancel, "right");

        btnAdd.addActionListener(e -> addCustomer());
        btnCancel.addActionListener(e -> dispose());

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void addCustomer() {
        try {
            String name = txtName.getText().trim();
            double discount = parseDouble(txtDiscount.getText());
            int quantity = parseInt(txtQuantity.getText());
            LocalDateTime start = dtStart.getDateTime();
            LocalDateTime end = dtEnd.getDateTime();

            Promotion p = new Promotion();
            p.setPromotionName(name);
            p.setDiscount(discount);
            p.setQuantity(quantity);
            p.setStartTime(start);
            p.setEndTime(end);

            boolean ok = promotionBUS.addPromotion(p);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã thêm khuyến mãi.");
                dispose();
            }
            else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage());
        }
    }

    // style
    private JLabel label(String s){
        JLabel l = new JLabel(s);
        l.setFont(BASE_FONT.deriveFont(Font.BOLD));
        l.setForeground(ACCENT);
        return l;
    }
    private void styleText(JTextField f){
        f.setFont(BASE_FONT);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBackground(new Color(0x102A43));
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER), BorderFactory.createEmptyBorder(6,8,6,8)));
    }
    private void styleField(JFormattedTextField f){
        styleText(f);
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

    private static double parseDouble(String s){
        return Double.parseDouble(s.replace(",", "").trim());
    }

    private static int parseInt(String s){
        return Integer.parseInt(s.replace(",", "").trim());
    }
}
