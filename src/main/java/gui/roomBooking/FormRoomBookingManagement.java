package gui.roomBooking;

import bus.AccountBUS;
import bus.RoomBUS;
import dao.RoomDAO;
import dao.AccountDAO;
import entity.Room;
import gui.login.main.Application;
import gui.room.FormRoomDetail;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormRoomBookingManagement extends JPanel {

    private final Color BG        = new Color(0x0B1F33);
    private final Color FG        = new Color(0xE9EEF6);
    private final Color PRIMARY   = new Color(0xF5C452);
    private final Color HOVER     = new Color(0xFFD36E);
    private final Color CARD_BG   = new Color(0x102D4A);
    private final Color BORDER    = new Color(0x274A6B);
    private final Color MUTED     = new Color(0xAAB6C5);

    private RoomBUS roomBUS = new RoomBUS();
    private AccountBUS accountBUS = new AccountBUS();

    private JTextField txtSearch;
    private JComboBox<String> cbxType;    // Phòng đơn | Phòng đôi
    private JComboBox<String> cbxStatus;  // Trống | Đặt | Check-in
    private JPanel grid;                  // card grid
    private JScrollPane scroll;

    // disable menu item trong option
    private String currentStatusFilter = "Trống";

    public FormRoomBookingManagement() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel top = new JPanel(new MigLayout(
                "insets 12 16 12 16",
                "[grow,fill]push[][]",
                "[]"
        ));
        top.setBackground(BG);
        add(top, BorderLayout.NORTH);

        txtSearch = new JTextField();
        txtSearch.setBackground(CARD_BG);
        txtSearch.setForeground(FG);
        txtSearch.setCaretColor(FG);
        txtSearch.setBorder(BorderFactory.createLineBorder(BORDER, 2));
        txtSearch.putClientProperty("JTextField.placeholderText","Tìm theo RoomID…");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        top.add(txtSearch, "w 280!");

        // Loại phòng
        cbxType = new JComboBox<>(new String[]{"All", "Phòng đơn", "Phòng đôi"});
        styleCombo(cbxType);
        top.add(cbxType, "gapx 10 10, w 140!");

        // Trạng thái phòng
        cbxStatus = new JComboBox<>(new String[]{"Trống", "Đặt", "Check-in"});
        styleCombo(cbxStatus);
        top.add(cbxStatus, "w 140!");

        // grid card
        grid = new JPanel(new MigLayout("wrap 2, gap 16, insets 16, fillx, aligny top", "[grow][grow]", ""));
        grid.setBackground(BG);

        scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 2));
        scroll.getVerticalScrollBar().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // events
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                loadData();
            }

            @Override public void removeUpdate(DocumentEvent e) {
                loadData();
            }

            @Override public void changedUpdate(DocumentEvent e) {
                loadData();
            }
        });
        cbxType.addActionListener(e -> loadData());

        cbxStatus.addActionListener(e -> {
            currentStatusFilter = (String) cbxStatus.getSelectedItem();
            loadData();
        });

        // mặc định hiển thị ds các phòng "Trống"
        cbxStatus.setSelectedItem("Trống");
        loadData();
    }


    //
    private void loadData() {
        String roomType = String.valueOf(cbxType.getSelectedItem());
        String status = String.valueOf(cbxStatus.getSelectedItem());
        String kwRoomID = txtSearch.getText().trim().toLowerCase();

        ArrayList<Room> dsPhong = switch (status) {
            case "Trống" -> roomBUS.getByOccupancy(true);
            case "Đặt"   -> roomBUS.getByStatus("Đặt");
            case "Check-in" -> roomBUS.getByStatus("Check-in");
            default -> roomBUS.getByOccupancy(true);
        };

        // lọc theo loại phòng
        if (!"All".equals(roomType)) {
            dsPhong = dsPhong.stream()
                    .filter(r -> r.getRoomType() != null && roomType.equals(r.getRoomType().getTypeName()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        // search theo RoomID
        if (!kwRoomID.isEmpty()) {
            dsPhong = dsPhong.stream()
                    .filter(r -> r.getRoomID() != null && r.getRoomID().toLowerCase().contains(kwRoomID))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        buildCards(dsPhong);
    }

    private void buildCards(ArrayList<Room> rooms) {
        grid.removeAll();
        if (rooms == null || rooms.isEmpty()) {
            JLabel empty = new JLabel("Không có phòng phù hợp.");
            empty.setForeground(FG);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            grid.add(empty, "span 2");
        } else {
            for (Room r : rooms) {
                grid.add(createRoomCard(r), "growx");
            }
        }
        grid.revalidate();
        grid.repaint();
    }

    private JComponent createRoomCard(Room room) {
        JPanel card = new JPanel(new MigLayout("wrap, fill, gap 8", "[fill]", "[grow 0][fill]"));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 2));

        // header
        JPanel header = new JPanel(new MigLayout("insets 8 10 0 10, fill", "[grow]push[]", "[]"));
        header.setBackground(CARD_BG);

        JLabel desc = new JLabel(room.getDescription()==null ? "(Không có mô tả)" : room.getDescription());
        desc.setForeground(FG);
        desc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.add(desc);

        JButton btnDetail = new JButton("XEM CHI TIẾT");
        btnDetail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDetail.setBackground(PRIMARY);
        btnDetail.setForeground(new Color(0x0B1F33));
        btnDetail.setBorder(BorderFactory.createLineBorder(new Color(0xF1B93A), 1));
        btnDetail.addMouseListener(new Hoverer(btnDetail, PRIMARY, HOVER)); // zzzzz
        btnDetail.addActionListener(e -> {
            FormRoomDetail formRoomDetail = new FormRoomDetail(room);
            formRoomDetail.setModal(true);
            formRoomDetail.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
            formRoomDetail.setVisible(true);
        });
        header.add(btnDetail);
        card.add(header);

        // center (luu ảnh)
        JPanel body = new JPanel(new MigLayout("insets 8 10 10 10", "[left]", "[]"));
        body.setBackground(CARD_BG);

        JLabel img = new JLabel();
        ImageIcon ic = null;
        try {
            String path = room.getImgRoomSource();
            if (path != null && !path.isBlank()) {
                File f = new File(path);
                if (f.exists()) {
                    ic = new ImageIcon(f.getAbsolutePath());
                } else {
                    java.net.URL u = getClass().getResource(path.startsWith("/") ? path : "/" + path);
                    if (u != null) ic = new ImageIcon(u);
                }
            }
        } catch (Exception e) {
            // 
        }
        if (ic == null) {
            java.net.URL nf = getClass().getResource("/images/404-not-found.jpg");
            if (nf != null) ic = new ImageIcon(nf);
        }
        Image scaled = ic != null ? ic.getImage().getScaledInstance(180, -1, Image.SCALE_SMOOTH) : null;
        img.setIcon(scaled != null ? new ImageIcon(scaled) : null);
        img.setBorder(BorderFactory.createLineBorder(BORDER, 2));
        body.add(img, "gapbottom 2");
        card.add(body, "growx");

        // popup menu
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miBookRoom   = new JMenuItem("Đặt phòng");
        JMenuItem miCheckIn = new JMenuItem("Check-in");
        JMenuItem miCheckOut =new JMenuItem("Check-out");
        JMenuItem miExtend = new JMenuItem("Gia hạn phòng");
        menu.add(miBookRoom); menu.add(miCheckIn); menu.add(miCheckOut); menu.add(miExtend);

        // enable/disable tùy filter hiện tại
        switch (currentStatusFilter) {
            case "Trống" -> {
                miBookRoom.setEnabled(true);
                miCheckIn.setEnabled(false);
                miCheckOut.setEnabled(false);
                miExtend.setEnabled(false);
            }
            case "Đặt" -> {
                miBookRoom.setEnabled(false);
                miCheckIn.setEnabled(true);
                miCheckOut.setEnabled(false);
                miExtend.setEnabled(false);
            }
            case "Check-in" -> {
                miBookRoom.setEnabled(false);
                miCheckIn.setEnabled(false);
                miCheckOut.setEnabled(true);
                miExtend.setEnabled(true);
            }
            default -> {}
        }

        // nếu phòng đang trống thì chỉ cho đặt
        if (room.isAvailable()) {
            miBookRoom.setEnabled(true);
            miCheckIn.setEnabled(false);
            miCheckOut.setEnabled(false);
            miExtend.setEnabled(false);
        }

        // Event
        miBookRoom.addActionListener(e -> {
            String empID = accountBUS.getAccountByUsername(Application.username).getEmployee().getEmployeeID();
            FormBookRoom formBookRoom = new FormBookRoom(room.getRoomID(), empID, this);
            formBookRoom.setModal(true);
            formBookRoom.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
            formBookRoom.setVisible(true);
        });

        miCheckIn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Xác nhận Check-in phòng " + room.getRoomID() + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                boolean ok = roomBUS.checkIn(room.getRoomID());
                JOptionPane.showMessageDialog(this, ok ? "Đã Check-in" : (roomBUS.getLastError() == null ? "Không thể Check-in" : roomBUS.getLastError()));
                loadData();
            }
        });

        miCheckOut.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "Xác nhận Check-out phòng " + room.getRoomID() + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                boolean ok = roomBUS.checkOut(room.getRoomID());
                JOptionPane.showMessageDialog(this, ok ? "Đã Check-out" : (roomBUS.getLastError() == null ? "Không thể Check-out" : roomBUS.getLastError()));
                loadData();
            }
        });

        miExtend.addActionListener(e -> {
            FormExtendRoom formExtendRoom = new FormExtendRoom(room.getRoomID(), this);
            formExtendRoom.setModal(true);
            formExtendRoom.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
            formExtendRoom.setVisible(true);
        });

        MouseAdapter showPopup = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  {
                if (e.isPopupTrigger()) menu.show(e.getComponent(), e.getX(), e.getY());
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) menu.show(e.getComponent(), e.getX(), e.getY());
            }
        };
        card.addMouseListener(showPopup);
        header.addMouseListener(showPopup);
        body.addMouseListener(showPopup);
        img.addMouseListener(showPopup);

        return card;
    }

//    private JLabel labelMuted(String s){
//        JLabel l = new JLabel(s);
//        l.setForeground(MUTED);
//        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        return l;
//    }

    private void styleCombo(JComboBox<?> c){
        c.setBackground(CARD_BG);
        c.setForeground(FG);
        c.setBorder(BorderFactory.createLineBorder(BORDER, 2));
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private static class Hoverer extends MouseAdapter {
        private final JButton b;
        private final Color base, hover;

        Hoverer(JButton b, Color base, Color hover){
            this.b=b; this.base=base; this.hover=hover;
        }
        @Override public void mouseEntered(MouseEvent e){
            b.setBackground(hover); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override public void mouseExited(MouseEvent e){
            b.setBackground(base);  b.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void reload() {
        loadData();
    }
}
