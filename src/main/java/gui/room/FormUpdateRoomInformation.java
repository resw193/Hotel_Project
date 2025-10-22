package gui.room;

import bus.RoomBUS;
import com.formdev.flatlaf.FlatClientProperties;
import entity.Room;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FormUpdateRoomInformation extends JDialog {

    private RoomBUS roomBUS = new RoomBUS();
    private FormRoomManagement parent;
    private Room room;

    private JTextField txtDescription;
    private JLabel lblPreview;
    private String imgPath;

    public FormUpdateRoomInformation(FormRoomManagement parent, Room room) {
        this.parent = parent;
        this.room = room;

        setTitle("Cập nhật thông tin phòng");
        setLayout(new MigLayout("wrap 2, insets 16, gap 10", "[right][350!, grow]"));
        getContentPane().setBackground(new Color(0x0B1F33));

        JLabel lb1 = label("Mã phòng:");
        JTextField txtID = field(); txtID.setText(room.getRoomID()); txtID.setEditable(false);
        txtID.setEditable(false);

        JLabel lb2 = label("Mô tả:");
        txtDescription = field(); txtDescription.setText(room.getDescription());

        JLabel lb3 = label("Ảnh phòng:");
        JButton btnChoose = new JButton("Chọn ảnh");
        stylePrimary(btnChoose);

        lblPreview = new JLabel();
        lblPreview.setPreferredSize(new Dimension(160,120));

        if (room.getImgRoomSource() != null && !room.getImgRoomSource().trim().isEmpty()) {
            String p = room.getImgRoomSource();
            Image img = null;
            File f = new File(p);
            if (f.exists()) {
                img = new ImageIcon(f.getAbsolutePath()).getImage();
            } else {
                java.net.URL u = getClass().getResource(p.startsWith("/") ? p : "/" + p);
                if (u != null) img = new ImageIcon(u).getImage();
            }
            if (img != null)
                lblPreview.setIcon(new ImageIcon(img.getScaledInstance(160,120, Image.SCALE_SMOOTH)));
            imgPath = p;
        }

        JButton btnSave = new JButton("Lưu");
        stylePrimary(btnSave);

        add(lb1); add(txtID, "growx");
        add(lb2); add(txtDescription, "growx");
        add(lb3); add(btnChoose, "split 2"); add(lblPreview, "wrap");
        add(new JLabel()); add(btnSave, "right");

        btnChoose.addActionListener(e -> chooseImage());
        btnSave.addActionListener(e -> updateInformation());

        pack();
        setSize(560, getHeight());
        setLocationRelativeTo(parent);
    }
    private void chooseImage() {
        JFileChooser ch = new JFileChooser();
        ch.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif", "bmp"));
        if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = ch.getSelectedFile();
            File copied = copyToResourcesImages(f);
            imgPath = "/images/" + copied.getName();
            Image img = new ImageIcon(copied.getAbsolutePath()).getImage().getScaledInstance(160,120, Image.SCALE_SMOOTH);
            lblPreview.setIcon(new ImageIcon(img));
        }
    }

    private void updateInformation() {
        String des = txtDescription.getText().trim();
        if (des.length() < 15) {
            JOptionPane.showMessageDialog(this, "Mô tả phải từ 15 kí tự.");
            return;
        }
        boolean ok = roomBUS.updateRoomInformation(room.getRoomID(), des, imgPath);
        if (ok) {
            Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.BOTTOM_LEFT, "Cập nhật thành công");
            dispose();
            parent.searchAndFilter();
        }
        else {
            JOptionPane.showMessageDialog(this,
                    roomBUS.getLastError() == null ? "Không thể cập nhật" : roomBUS.getLastError(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // style
    private JLabel label(String s) {
        JLabel lb = new JLabel(s);
        lb.setForeground(new Color(0xE9EEF6));
        return lb;
    }
    private JTextField field() {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,10,6,10;");
        return tf;
    }
    private void stylePrimary(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#F5C452; foreground:#0B1F33; borderColor:#F1B93A; hoverBackground:#FFD36E;");
    }

    private File copyToResourcesImages(File src) {
        try {
            java.net.URL u = getClass().getResource("/images/");
            Path destDir = (u != null && "file".equalsIgnoreCase(u.getProtocol()))
                    ? Paths.get(u.toURI())
                    : Paths.get("src", "main", "resources", "images");
            Files.createDirectories(destDir);
            Path dest = destDir.resolve(src.getName());
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest.toFile();
        } catch (Exception ex) {
            return src;
        }
    }
}
