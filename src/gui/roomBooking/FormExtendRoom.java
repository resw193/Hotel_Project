package gui.roomBooking;

import DAO.RoomDAO;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormExtendRoom extends JDialog {

    private final Color BG = new Color(0x0B1F33);
    private final Color FG = new Color(0xE9EEF6);
    private final Color CARD_BG = new Color(0x102D4A);
    private final Color BORDER = new Color(0x274A6B);
    private final Color GOLD = new Color(0xF5C452);
    private final Color GOLD_HV = new Color(0xFFD36E);

    private final RoomDAO roomDAO = new RoomDAO();
    private final String roomID;
    private final FormRoomBookingManagement parent;

    private JTextField txtRoomID, txtOldOut, txtNewOut;
    private JButton btnApply, btnCancel;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public FormExtendRoom(String roomID, FormRoomBookingManagement parent) {
        super((Frame) null, "Gia hạn phòng", true);
        this.roomID = roomID;
        this.parent = parent;

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new MigLayout("wrap 2, fillx, insets 16 20 16 20, gap 10",
                "[right]15[grow, 360!]"));
        root.setBackground(BG);

        txtRoomID = roText(roomID);

        LocalDateTime[] times = roomDAO.getActiveStayTimes(roomID);
        LocalDateTime checkIn  = times[0];
        LocalDateTime oldCheckOut   = times[1];

        txtOldOut = roText(oldCheckOut == null ? "(không có)" : oldCheckOut.format(fmt));

        txtNewOut = text();
        txtNewOut.setToolTipText("Nhập theo định dạng: dd-MM-yyyy HH:mm"); // rê chuột vào de hien thi
        if (oldCheckOut != null) txtNewOut.setText(oldCheckOut.plusHours(1).format(fmt));  // gợi ý mặc định

        root.add(label("Room ID:"));            root.add(txtRoomID, "growx");
        root.add(label("Check-out hiện tại:")); root.add(txtOldOut, "growx");
        root.add(label("Check-out mới:"));      root.add(txtNewOut, "growx");

        btnApply  = primaryButton("GIA HẠN");
        btnCancel = secondaryButton("HỦY");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(BG);
        actions.add(btnCancel); actions.add(btnApply);
        root.add(actions, "span 2, growx");

        setContentPane(root);
        pack();
        setSize(560, getPreferredSize().height + 20);
        setLocationRelativeTo(null);

        btnCancel.addActionListener(e -> dispose());
        btnApply.addActionListener(e -> onApply(checkIn, oldCheckOut));
    }

    private void onApply(LocalDateTime checkIn, LocalDateTime oldOut) {
        String sNew = txtNewOut.getText().trim();
        if (sNew.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập thời gian check-out mới."); return; }

        LocalDateTime newCheckOutDate;
        try { newCheckOutDate = LocalDateTime.parse(sNew, fmt); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Định dạng phải là dd-MM-yyyy HH:mm"); return; }

        if (checkIn != null && !newCheckOutDate.isAfter(checkIn)) {
            JOptionPane.showMessageDialog(this, "Giờ trả mới phải > giờ nhận phòng.");
            return;
        }
        if (oldOut != null && newCheckOutDate.isBefore(oldOut)) {
            JOptionPane.showMessageDialog(this, "Giờ trả mới không được sớm hơn giờ trả hiện tại.");
            return;
        }

        boolean ok = roomDAO.giaHanPhong(roomID, newCheckOutDate);
        if (ok) {
            dispose();
            if (parent != null) parent.reload();
        } else {
            JOptionPane.showMessageDialog(this, "Gia hạn thành công!");
        }
    }

    // color
    private JLabel label(String s){
        JLabel l = new JLabel(s);
        l.setForeground(FG);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return l;
    }
    private JTextField text(){
        JTextField t = new JTextField();
        t.setBackground(CARD_BG);
        t.setForeground(FG);
        t.setCaretColor(FG);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setBorder(BorderFactory.createLineBorder(BORDER,1));
        return t;
    }
    private JTextField roText(String v){
        JTextField t = text();
        t.setText(v);
        t.setEditable(false);
        t.setFocusable(false);
        t.setBackground(new Color(0x0F3556));
        return t;
    }
    private JButton primaryButton(String text){
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(GOLD);
        b.setForeground(new Color(0x0B1F33));
        b.setBorder(BorderFactory.createLineBorder(new Color(0xF1B93A),1));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e){ b.setBackground(GOLD_HV); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e){ b.setBackground(GOLD);    b.setCursor(Cursor.getDefaultCursor()); }
        });
        return b;
    }
    private JButton secondaryButton(String text){
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(CARD_BG);
        b.setForeground(FG);
        b.setBorder(BorderFactory.createLineBorder(BORDER,1));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e){ b.setBackground(new Color(0x153C5B)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e){ b.setBackground(CARD_BG);                 b.setCursor(Cursor.getDefaultCursor()); }
        });
        return b;
    }
}
