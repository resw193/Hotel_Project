package gui.roomBooking;

import DAO.RoomDAO;
import Entity.Customer;
import com.raven.datechooser.DateChooser;
import com.raven.datechooser.SelectedAction;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormBookRoom extends JDialog {

    // Theme đồng bộ navy–gold
    private final Color BG      = new Color(0x0B1F33);
    private final Color FG      = new Color(0xE9EEF6);
    private final Color CARD_BG = new Color(0x102D4A);
    private final Color BORDER  = new Color(0x274A6B);
    private final Color GOLD    = new Color(0xF5C452);
    private final Color GOLD_HV = new Color(0xFFD36E);

    private RoomDAO roomDAO = new RoomDAO();
    private String roomID;
    private String employeeID;
    private FormRoomBookingManagement parent;

    private JTextField txtRoomID, txtEmployeeID, txtFullName, txtPhone, txtEmail, txtIdCard;
    private JTextField txtBookingDate, txtCheckIn, txtCheckOut;
    private JComboBox<String> cbxBookingType;
    private JButton btnBookRoom, btnCancel;

    private DateChooser chBooking, chCheckIn, chCheckOut;

    public FormBookRoom(String roomID, String employeeID, FormRoomBookingManagement parent) {
        super((Frame)null, "Đặt phòng", true);
        this.roomID = roomID;
        this.employeeID = employeeID;
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new MigLayout(
                "wrap 2, fillx, insets 16 20 16 20, gap 10",
                "[right]15[grow, 360!]"
        ));
        root.setBackground(BG);

        // ---- fields ----
        txtRoomID = roText(roomID);
        txtEmployeeID = roText(employeeID);
        txtFullName = text();
        txtPhone = text();
        txtEmail = text();
        txtIdCard = text();
        txtBookingDate = dateField();
        txtCheckIn = dateField();
        txtCheckOut = dateField();

        cbxBookingType = new JComboBox<>(new String[]{"Giờ","Ngày","Đêm"});
        cbxBookingType.setBackground(CARD_BG);
        cbxBookingType.setForeground(FG);
        cbxBookingType.setBorder(BorderFactory.createLineBorder(BORDER,1));
        cbxBookingType.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // ---- date choosers ----
        chBooking = makeChooser(txtBookingDate);
        chCheckIn = makeChooser(txtCheckIn);
        chCheckOut = makeChooser(txtCheckOut);

        // layout
        root.add(label("Room ID:"));        root.add(txtRoomID, "growx");
        root.add(label("Employee ID:"));    root.add(txtEmployeeID, "growx");
        root.add(label("Full name:"));      root.add(txtFullName, "growx");
        root.add(label("Phone:"));          root.add(txtPhone, "growx");
        root.add(label("Email:"));          root.add(txtEmail, "growx");
        root.add(label("ID Card:"));        root.add(txtIdCard, "growx");

        root.add(label("Booking date:"));
        root.add(rowWithPicker(txtBookingDate, btnCalendar(chBooking)), "growx");

        root.add(label("Check-in date:"));
        root.add(rowWithPicker(txtCheckIn, btnCalendar(chCheckIn)), "growx");

        root.add(label("Check-out date:"));
        root.add(rowWithPicker(txtCheckOut, btnCalendar(chCheckOut)), "growx");

        root.add(label("Booking type:"));   root.add(cbxBookingType, "growx");

        btnBookRoom = primaryButton("ĐẶT PHÒNG");
        btnCancel = secondaryButton("HỦY");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(BG);
        actions.add(btnCancel); actions.add(btnBookRoom);
        root.add(actions, "span 2, growx");

        setContentPane(root);
        pack();
        setSize(640, getPreferredSize().height + 20);
        setLocationRelativeTo(null);

        // events
        btnCancel.addActionListener(e -> dispose());
        btnBookRoom.addActionListener(e -> bookRoom());
    }

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
    private JTextField dateField(){
        JTextField t = text();
        t.setEditable(false);
        return t;
    }
    private JButton btnCalendar(DateChooser dc){
        JButton b = new JButton("📅");
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setBackground(CARD_BG);
        b.setForeground(FG);
        b.setBorder(BorderFactory.createLineBorder(BORDER,1));
        b.addActionListener(e -> dc.showPopup());
        return b;
    }
    private JPanel rowWithPicker(JTextField tf, JButton pick){
        JPanel p = new JPanel(new MigLayout("insets 0, fillx", "[grow]8[]", "[]"));
        p.setBackground(BG);
        p.add(tf, "growx");
        p.add(pick, "w 44!, h 28!");
        return p;
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
    private DateChooser makeChooser(JTextField ref){
        DateChooser dc = new DateChooser();
        dc.setTextRefernce(ref);
        dc.addEventDateChooser((action, date) -> {
            if (action.getAction()==SelectedAction.DAY_SELECTED) dc.hidePopup();
        });
        return dc;
    }

    private void bookRoom() {
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String idCard = txtIdCard.getText().trim();
        String sBook = txtBookingDate.getText().trim();
        String in = txtCheckIn.getText().trim();
        String out = txtCheckOut.getText().trim();
        String type = String.valueOf(cbxBookingType.getSelectedItem());

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || idCard.isEmpty()
                || sBook.isEmpty() || in.isEmpty() || out.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!phone.matches("\\d{9,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ.");
            txtPhone.requestFocus(); return;
        }
      
        // Formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate bookingDate = parse(sBook, formatter);
        LocalDate checkInDate   = parse(in, formatter);
        LocalDate checkOutDate  = parse(out, formatter);

        if (bookingDate == null || checkInDate == null || checkOutDate == null) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày phải là dd-MM-yyyy.");
            return;
        }
        if (checkInDate.isBefore(bookingDate)) {
            JOptionPane.showMessageDialog(this, "Check-in phải sau hoặc bằng ngày đặt.");
            return;
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            JOptionPane.showMessageDialog(this, "Check-out phải sau Check-in.");
            return;
        }

        // customer
        Customer customer = new Customer(fullName, phone, email, idCard);

        LocalDateTime ldtBook = bookingDate.atStartOfDay();
        LocalDateTime ldtIn   = checkInDate.atStartOfDay();
        LocalDateTime ldtOut  = checkOutDate.atStartOfDay();

        boolean ok = roomDAO.datPhong(customer, roomID, employeeID, ldtBook, ldtIn, ldtOut, type);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đặt phòng thành công!");
            dispose();
            if (parent != null) parent.reload();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể đặt phòng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate parse(String s, DateTimeFormatter f){
        try { return LocalDate.parse(s, f); } catch(Exception ex){ return null; }
    }
}
