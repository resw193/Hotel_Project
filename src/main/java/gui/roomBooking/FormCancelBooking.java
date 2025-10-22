package gui.roomBooking;

import bus.RoomBUS;
import dao.RoomDAO;
import entity.Room;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FormCancelBooking extends JPanel {

    private final Color BG = new Color(0x0B1F33);
    private final Color FG = new Color(0xE9EEF6);
    private final Color PRIMARY = new Color(0xF5C452);
    private final Color HOVER = new Color(0xFFD36E);
    private final Color CARD_BG = new Color(0x102D4A);
    private final Color BORDER = new Color(0x274A6B);
    private final Color MUTED = new Color(0xAAB6C5);

    private RoomBUS roomBUS = new RoomBUS();

    private JTextField txtSearch;
    private JPanel grid;
    private JScrollPane scroll;

    private ArrayList<Room> dsPhong = new ArrayList<>();

    public FormCancelBooking() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel top = new JPanel(new MigLayout(
                "insets 12 16 12 16",
                "[grow,fill]push[]",
                "[]"
        ));
        top.setBackground(BG);
        add(top, BorderLayout.NORTH);

        txtSearch = new JTextField();
        txtSearch.setBackground(CARD_BG);
        txtSearch.setForeground(FG);
        txtSearch.setCaretColor(FG);
        txtSearch.setBorder(BorderFactory.createLineBorder(BORDER, 2));
        txtSearch.putClientProperty("JTextField.placeholderText","Nhập RoomID để tìm nhanh…");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        top.add(txtSearch, "w 300!");

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

        loadData();
    }


    private void buildCards(ArrayList<Room> rooms) {
        grid.removeAll();

        if (rooms.isEmpty()) {
            JLabel empty = new JLabel("Không có phòng nào đang ở trạng thái 'Đặt'.");
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
        JPanel card = new JPanel(new MigLayout("wrap, fillx, gap 8", "[fill]", "[][fill]"));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 2));

        JPanel header = new JPanel(new MigLayout("insets 8 10 0 10, fillx", "[grow]", "[]"));
        header.setBackground(CARD_BG);

        String descText = room.getDescription() ==null ? "(Không có mô tả)" : room.getDescription();
        JLabel desc = new JLabel(descText + "  —  RoomID: " + room.getRoomID());
        desc.setForeground(FG);
        desc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.add(desc, "growx");
        card.add(header);

        JPanel body = new JPanel(new MigLayout(
                "insets 8 10 10 10, fillx, gapx 16",
                "[220!][grow]",
                "[grow]"
        ));
        body.setBackground(CARD_BG);


        // image
        JLabel img = new JLabel();
        ImageIcon ic = null;
        try {
            String p = room.getImgRoomSource();
            if (p != null && !p.isBlank()) {
                File f = new File(p);
                if (f.exists()) {
                    ic = new ImageIcon(f.getAbsolutePath());
                } else {
                    java.net.URL u = getClass().getResource(p.startsWith("/") ? p : "/" + p);
                    if (u != null) ic = new ImageIcon(u);
                }
            }
        } catch (Exception ignored) {}
        if (ic == null) {
            java.net.URL nf = getClass().getResource("/images/404-not-found.jpg");
            if (nf != null) ic = new ImageIcon(nf);
        }
        Image scaled = ic != null ? ic.getImage().getScaledInstance(220, -1, Image.SCALE_SMOOTH) : null;
        img.setIcon(scaled != null ? new ImageIcon(scaled) : null);
        img.setBorder(BorderFactory.createLineBorder(BORDER, 2));
        body.add(img, "cell 0 0, aligny top");

        JPanel right = new JPanel(new MigLayout(
                "insets 0, fill, al center center",
                "[grow]",
                "[grow]"
        ));
        right.setBackground(CARD_BG);

        JButton btnCancel = new JButton("HỦY ĐẶT PHÒNG");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCancel.setPreferredSize(new Dimension(220, 48));
        btnCancel.setBackground(PRIMARY);
        btnCancel.setForeground(new Color(0x0B1F33));
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(0xF1B93A), 2));
        btnCancel.setFocusPainted(false);
        btnCancel.addMouseListener(new Hoverer(btnCancel, PRIMARY, HOVER));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCancel.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Xác nhận hủy đặt phòng " + room.getRoomID() + " ?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (opt == JOptionPane.YES_OPTION) {
                boolean ok = roomBUS.huyDatPhong(room.getRoomID());
                JOptionPane.showMessageDialog(this, ok ? "Đã hủy đặt phòng" :
                        (roomBUS.getLastError() == null ? "Không thể hủy đặt phòng" : roomBUS.getLastError()));
                loadData();
            }
        });

        right.add(btnCancel, "center");
        body.add(right, "cell 1 0, grow");

        card.add(body, "growx");

        return card;
    }

    private void loadData() {
        String kw = txtSearch.getText().trim().toLowerCase();

        // lấy phòng có traạng thái "Đặt"
        dsPhong = roomBUS.getByStatus("Đặt");

        // Lọc theo RoomID
        if (!kw.isEmpty()) {
            dsPhong = dsPhong.stream()
                    .filter(r -> r.getRoomID() != null && r.getRoomID().toLowerCase().contains(kw))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        buildCards(dsPhong);
    }

    // hover -> color change
    private static class Hoverer extends java.awt.event.MouseAdapter {
        private final JButton b;
        private final Color base, hover;
        Hoverer(JButton b, Color base, Color hover){ this.b=b; this.base=base; this.hover=hover; }
        @Override public void mouseEntered(java.awt.event.MouseEvent e){ b.setBackground(hover); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); }
        @Override public void mouseExited (java.awt.event.MouseEvent e){ b.setBackground(base);  b.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); }
    }
}
