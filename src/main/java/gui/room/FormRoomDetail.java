package gui.room;

import entity.Room;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FormRoomDetail extends JDialog {

    public FormRoomDetail(Room room) {
        setTitle("Thông tin phòng");
        setLayout(new MigLayout("wrap 2, insets 20, gap 14", "[180!,right]40[fill,grow]"));
        getContentPane().setBackground(new Color(0x0B1F33));

        Color fg = new Color(0xE9EEF6);
        Color box = new Color(0x102D4A);

        JLabel title = new JLabel("CHI TIẾT PHÒNG");
        title.setForeground(fg);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        ImageIcon icon = null;
        String path = room != null ? room.getImgRoomSource() : null;
        try {
            if (path != null && !path.trim().isEmpty()) {
                File f = new File(path);
                Image img;
                if (f.exists())
                    img = new ImageIcon(f.getAbsolutePath()).getImage();
                else {
                    java.net.URL u = getClass().getResource(path.startsWith("/") ? path : "/" + path); //   /images/...
                    img = (u != null) ? new ImageIcon(u).getImage() : null;
                }
                if (img != null) icon = new ImageIcon(img.getScaledInstance(220, 160, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {
            //
        }
        if (icon == null) {
            java.net.URL nf = getClass().getResource("/images/404-not-found.jpg");
            if (nf != null) {
                Image fallback = new ImageIcon(nf).getImage().getScaledInstance(220, 160, Image.SCALE_SMOOTH);
                icon = new ImageIcon(fallback);
            }
        }
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setBorder(BorderFactory.createLineBorder(new Color(0x153C5B)));

        add(title, "span 2, al center, wrap");
        add(imgLabel, "span 2, al center, wrap");

        add(label("Room ID:")); add(value(room.getRoomID(), fg, box));
        add(label("Mô tả:"));   add(value(room.getDescription(), fg, box));
        add(label("Trạng thái:"));   add(value(room.isAvailable() ? "Trống" : "Đặt", fg, box));
        String typeName = (room.getRoomType() == null) ? "" : room.getRoomType().getTypeName();
        add(label("Loại phòng:"));  add(value(typeName, fg, box));

        pack();
        setSize(700, getHeight());
        setLocationRelativeTo(null);
    }

    private JLabel label(String s) {
        JLabel lb = new JLabel(s);
        lb.setForeground(new Color(0xB8C4D4));
        return lb;
    }
    private JComponent value(String s, Color fg, Color bg) {
        JTextArea ta = new JTextArea(s);
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setEditable(false);
        ta.setOpaque(true);
        ta.setForeground(fg);
        ta.setBackground(bg);
        ta.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        return ta;
    }
}
