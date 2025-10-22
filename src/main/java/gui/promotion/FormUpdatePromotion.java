package gui.promotion;

import bus.PromotionBUS;
import dao.PromotionDAO;
import entity.Promotion;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;

public class FormUpdatePromotion extends JDialog {

    // Color
    private static final Color BG        = new Color(0x0B1F33);
    private static final Color BORDER    = new Color(0x274A6B);
    private static final Color TEXT      = new Color(0xE6F1FF);
    private static final Color ACCENT    = new Color(0x22D3EE);
    private static final Font  BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private PromotionBUS promotionBUS = new PromotionBUS();
    private String promotionID;

    private JTextField txtName = new JTextField();
    private JFormattedTextField txtDiscount = new JFormattedTextField(NumberFormat.getNumberInstance());
    private DateTimePicker dtStart = new DateTimePicker();
    private DateTimePicker dtEnd   = new DateTimePicker();

    public FormUpdatePromotion(Window owner, String promotionID) {
        super(owner, "Cập nhật khuyến mãi", ModalityType.APPLICATION_MODAL);
        this.promotionID = promotionID;

        getContentPane().setBackground(BG);
        setLayout(new MigLayout("insets 14 16 12 16", "[120!][280!]", "[][][][]16[]"));

        styleText(txtName);
        styleField(txtDiscount);

        add(label("Tên khuyến mãi:"), "gapbottom 6");
        add(txtName, "growx, wrap");

        add(label("Giảm giá (%):"), "gaptop 4, gapbottom 6");
        add(txtDiscount, "w 120!, wrap");

        add(label("Bắt đầu:"), "gaptop 4, gapbottom 6");
        add(dtStart, "wrap");

        add(label("Kết thúc:"), "gaptop 4, gapbottom 6");
        add(dtEnd, "wrap");

        JButton btnUpdate = primaryButton("Lưu", true);
        JButton btnCancel = primaryButton("Hủy", false);
        add(btnUpdate, "span 2, split 2, right");
        add(btnCancel, "right");

        btnUpdate.addActionListener(e -> updatePromotion());
        btnCancel.addActionListener(e -> dispose());

        loadData();

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void loadData() {
        Promotion p = promotionBUS.getPromotionByID(promotionID);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi.");
            dispose();
            return;
        }
        txtName.setText(p.getPromotionName());
        txtDiscount.setValue(p.getDiscount());
        LocalDateTime st = p.getStartTime();
        LocalDateTime en = p.getEndTime();
        dtStart.setDateTime(st);
        dtEnd.setDateTime(en);
    }

    private void updatePromotion() {
        try {
            Promotion p = promotionBUS.getPromotionByID(promotionID);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi.");
                return;
            }
            p.setPromotionName(txtName.getText().trim());
            p.setDiscount(parseDouble(txtDiscount.getText()));
            p.setStartTime(dtStart.getDateTime());
            p.setEndTime(dtEnd.getDateTime());

            boolean ok = promotionBUS.updatePromotion(p);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật khuyến mãi.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.");
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
    private void styleField(JFormattedTextField f){ styleText(f); }
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
}
