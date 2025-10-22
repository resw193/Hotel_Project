package gui.room;

import bus.RoomBUS;
import bus.RoomTypeBUS;
import com.formdev.flatlaf.FlatClientProperties;
import dao.RoomDAO;
import dao.RoomTypeDAO;
import entity.Room;
import entity.RoomType;
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
import java.util.ArrayList;
import java.util.List;

public class FormAddRoom extends JDialog {

    private RoomBUS roomBUS = new RoomBUS();
    private RoomTypeBUS roomTypeBUS = new RoomTypeBUS();
    private final FormRoomManagement parent;

    private JTextField txtDescription;
    private JCheckBox chkAvailable;
    private JComboBox<String> cbxRoomType;
    private List<RoomType> roomTypes = new ArrayList<>();
    private JLabel lblPreview;
    private String imgPath = "";

    public FormAddRoom(FormRoomManagement parent) {
        this.parent = parent;
        setTitle("Thêm phòng mới");
        setLayout(new MigLayout("wrap 2, insets 16, gap 10", "[right][350!, grow]"));
        getContentPane().setBackground(new Color(0x0B1F33));

        JLabel lb1 = label("Mô tả:");
        txtDescription = field();

        JLabel lb2 = label("Tình trạng:");
        chkAvailable = new JCheckBox("Trống");
        chkAvailable.setOpaque(false);
        chkAvailable.setForeground(new Color(0xE9EEF6));
        chkAvailable.setSelected(true);     // luôn Trống
        chkAvailable.setEnabled(false);     // không cho sửa

        JLabel lb3 = label("Loại phòng:");
        cbxRoomType = new JComboBox<>();
        cbxRoomType.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B;");

        JLabel lb4 = label("Ảnh phòng:");
        JButton btnChoose = new JButton("Chọn ảnh");
        stylePrimary(btnChoose);
        lblPreview = new JLabel();
        lblPreview.setPreferredSize(new Dimension(160,120));

        JButton btnAddRoom = new JButton("Thêm");
        stylePrimary(btnAddRoom);

        add(lb1); add(txtDescription, "growx");
        add(lb2); add(chkAvailable, "left");
        add(lb3); add(cbxRoomType, "w 220!");
        add(lb4); add(btnChoose, "split 2"); add(lblPreview, "wrap");
        add(new JLabel()); add(btnAddRoom, "right");

        // Lấy ra tooàn bộ roomType add zo combobox
        roomTypes = roomTypeBUS.getAll();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (RoomType t : roomTypes)
            model.addElement(t.getTypeName());
        cbxRoomType.setModel(model);

        btnChoose.addActionListener(e -> chooseImage());
        btnAddRoom.addActionListener(e -> addRoom());

        pack();
        setSize(560, getHeight());
        setLocationRelativeTo(parent);
    }

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

    private void chooseImage() {
        JFileChooser ch = new JFileChooser();
        ch.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif", "bmp"));
        if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = ch.getSelectedFile();
            File copied = copyToResourcesImages(f);
            imgPath = "/images/" + copied.getName();  // lưu path trong resources
            Image img = new ImageIcon(copied.getAbsolutePath()).getImage().getScaledInstance(160,120, Image.SCALE_SMOOTH);
            lblPreview.setIcon(new ImageIcon(img));
        }
    }

    private void addRoom() {
        String des = txtDescription.getText().trim();
        int idx = cbxRoomType.getSelectedIndex();
        RoomType type = (idx >= 0 && idx < roomTypes.size()) ? roomTypes.get(idx) : null;
        boolean avail = true;

        if (des.length() < 15) {
            JOptionPane.showMessageDialog(this, "Mô tả phải từ 15 kí tự.");
            return;
        }
        if (type == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn loại phòng.");
            return;
        }

        boolean ok = roomBUS.addRoom(des, type.getTypeName(), imgPath);
        if (ok) {
            Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.BOTTOM_LEFT, "Thêm phòng thành công");
            dispose();
            parent.searchAndFilter();
        } else {
            JOptionPane.showMessageDialog(this,
                    roomBUS.getLastError() == null ? "Không thể thêm phòng" : roomBUS.getLastError(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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
