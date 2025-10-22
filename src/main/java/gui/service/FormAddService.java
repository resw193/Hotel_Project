package gui.service;

import bus.ServiceBUS;
import net.miginfocom.swing.MigLayout;
import raven.crazypanel.CrazyPanel;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FormAddService extends JDialog {
    private FormServiceManagement parent;

    private JTextField txtName, txtPrice, txtQty;
    private JComboBox<String> cboType;
    private JLabel lblPreview, err;

    private File fileChosen;

    private ServiceBUS serviceBUS = new ServiceBUS();

    public FormAddService(FormServiceManagement parent) {
        this.parent = parent;

        initGUI();
    }

    private void initGUI() {
        setTitle("Add Service");
        setSize(560, 520);
        setLocationRelativeTo(null);

        CrazyPanel p = new CrazyPanel();
        p.setBorder(new EmptyBorder(14,16,14,16));
        p.setLayout(new MigLayout("wrap 2, fillx, insets 0, gap 10", "[120::,right]16[fill]"));

        JLabel title = new JLabel("Add New Service");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        p.add(title, "span, al center, gapbottom 8");

        lblPreview = new JLabel();
        lblPreview.setPreferredSize(new Dimension(140,140));
        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreview.setBorder(BorderFactory.createDashedBorder(new Color(0x274A6B)));
        JButton btnChooseImage = new JButton("Chọn ảnh");
        p.add(new JLabel("Hình ảnh"), "gapbottom 6");
        p.add(lblPreview, "wrap, h 140!");
        p.add(new JLabel(""));
        p.add(btnChooseImage, "wrap");

        txtName = new JTextField();
        cboType = new JComboBox<>(new String[]{"Food","Drink"});
        txtPrice = new JTextField();
        txtQty = new JTextField();

        p.add(new JLabel("Tên dịch vụ"));   p.add(txtName);
        p.add(new JLabel("Loại dịch vụ"));   p.add(cboType);
        p.add(new JLabel("Giá"));  p.add(txtPrice);
        p.add(new JLabel("Số lượng")); p.add(txtQty);

        err = new JLabel(" ");
        err.setForeground(new Color(0xD64545));
        p.add(err, "span, growx");

        JButton btnAdd = new JButton("Add");
        btnAdd.putClientProperty("JButton.buttonType", "roundRect");
        p.add(btnAdd, "span, al trail");

        setContentPane(p);

        btnChooseImage.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Images","jpg","png","jpeg","gif","bmp"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fileChosen = fc.getSelectedFile();
                Image img = new ImageIcon(fileChosen.getAbsolutePath()).getImage();
                lblPreview.setIcon(new ImageIcon(img.getScaledInstance(140, 140, Image.SCALE_SMOOTH)));
            }
        });

        btnAdd.addActionListener(e -> {
            if (!validData()) return;
            try {
                File toSave = copyToResourcesImages(fileChosen);
                boolean ok = serviceBUS.add(txtName.getText().trim(),
                        String.valueOf(cboType.getSelectedItem()),
                        Integer.parseInt(txtQty.getText().trim()),
                        Double.parseDouble(txtPrice.getText().trim()),
                        toSave
                );

                if (ok) {
                    Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.BOTTOM_LEFT, "Added");
                    dispose();
                    parent.loadData();
                }
                else {
                    err.setText("Failed to add service.");
                }
            } catch (Exception ex) {
                err.setText(ex.getMessage());
            }
        });
    }

    private boolean validData() {
        err.setText(" ");
        if (txtName.getText().trim().isEmpty()) {
            err.setText("Tên service không được để trống");
            return false;
        }

        if (!txtPrice.getText().trim().matches("^[0-9]+(\\.[0-9]+)?$")) {
            err.setText("Giá phải là số");
            return false;
        }

        if (!txtQty.getText().trim().matches("^\\d+$")) {
            err.setText("Số lượng phải là số");
            return false;
        }

        if (fileChosen == null) {
            err.setText("Ảnh không đc trống");
            return false;
        }

        return true;
    }

    // Copy ảnh vào thư mục resources/images (hoặc target/classes/images khi chạy)
    private File copyToResourcesImages(File src) {
        try {
            java.net.URL u = getClass().getResource("/images/");
            Path destDir;
            if (u != null && "file".equalsIgnoreCase(u.getProtocol())) {
                destDir = Paths.get(u.toURI());
            } else {
                // /src/main/resources/images
                destDir = Paths.get("src", "main", "resources", "images");
            }
            Files.createDirectories(destDir);
            Path dest = destDir.resolve(src.getName());
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest.toFile();
        } catch (Exception ex) {
            return src;
        }
    }
}
