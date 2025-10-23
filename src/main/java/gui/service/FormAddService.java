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

    private final FormServiceManagement parent;
    private final JTextField txtName, txtPrice, txtQty;
    private final JComboBox<String> cboType;
    private final JLabel lblPreview, err;
    private File fileChosen;
    private final ServiceBUS serviceBUS = new ServiceBUS();

    private static final Color BG = new Color(0x0B1F33);
    private static final Color CARD_BG = new Color(0x13385A);
    private static final Color TEXT_PRIMARY = new Color(0xE9EEF6);
    private static final Color GOLD_PRIMARY = new Color(0xF5C452);
    private static final Color ERROR_RED = new Color(0xD64545);

    public FormAddService(FormServiceManagement parent) {
        this.parent = parent;
        setTitle("Add New Service");
        setModal(true);
        setSize(620, 660);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        CrazyPanel panel = new CrazyPanel();
        panel.setLayout(new MigLayout(
                "wrap 2, fillx, insets 20 30 20 30, gap 12",
                "[140::,right]20[fill, grow]"
        ));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);

        JLabel lblTitle = new JLabel("Add New Service", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        panel.add(lblTitle, "span, al center, gaptop 4, gapbottom 15");

        lblPreview = new JLabel("No image", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(160, 160));
        lblPreview.setOpaque(true);
        lblPreview.setBackground(new Color(0x102C49));
        lblPreview.setForeground(TEXT_PRIMARY);
        lblPreview.setBorder(BorderFactory.createDashedBorder(new Color(0x355C7D)));

        JButton btnChooseImage = new JButton(" Chọn ảnh");
        styleButton(btnChooseImage, GOLD_PRIMARY, BG);

        panel.add(new JLabel("Hình ảnh:"), "gapbottom 4");
        panel.add(lblPreview, "wrap, h 160!, al center");
        panel.add(new JLabel(""));
        panel.add(btnChooseImage, "wrap, al center");

        txtName = new JTextField();
        txtPrice = new JTextField();
        txtQty = new JTextField();
        cboType = new JComboBox<>(new String[]{"Food", "Drink"});

        styleField(txtName);
        styleField(txtPrice);
        styleField(txtQty);
        styleCombo(cboType);

        addLabeledField(panel, "Tên dịch vụ:", txtName);
        addLabeledField(panel, "Loại dịch vụ:", cboType);
        addLabeledField(panel, "Giá (VND):", txtPrice);
        addLabeledField(panel, "Số lượng:", txtQty);

        // === ERROR LABEL ===
        err = new JLabel(" ");
        err.setForeground(ERROR_RED);
        panel.add(err, "span, growx, gaptop 6");

        // === BUTTONS ===
        JButton btnAdd = new JButton("Thêm dịch vụ");
        JButton btnCancel = new JButton("Hủy");

        styleButton(btnAdd, GOLD_PRIMARY, BG);
        styleButton(btnCancel, ERROR_RED, Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAdd);
        btnPanel.add(btnCancel);

        panel.add(btnPanel, "span, al center, gaptop 15, wrap");

        setContentPane(panel);

        btnChooseImage.addActionListener(e -> chooseImage());
        btnCancel.addActionListener(e -> dispose());
        btnAdd.addActionListener(e -> handleAdd());
    }

    private void addLabeledField(JPanel panel, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(TEXT_PRIMARY);
        panel.add(lbl, "gapbottom 4");
        panel.add(field, "wrap");
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(0x102C49));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(GOLD_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x274A6B)),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(new Color(0x102C49));
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(new Color(0x274A6B)));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    private void chooseImage() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif", "bmp"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileChosen = fc.getSelectedFile();
            Image img = new ImageIcon(fileChosen.getAbsolutePath()).getImage();
            lblPreview.setText("");
            lblPreview.setIcon(new ImageIcon(img.getScaledInstance(160, 160, Image.SCALE_SMOOTH)));
        }
    }

    private void handleAdd() {
        if (!validData()) return;
        try {
            File toSave = copyToResourcesImages(fileChosen);
            boolean ok = serviceBUS.add(
                    txtName.getText().trim(),
                    (String) cboType.getSelectedItem(),
                    Integer.parseInt(txtQty.getText().trim()),
                    Double.parseDouble(txtPrice.getText().trim()),
                    toSave
            );
            if (ok) {
                Notifications.getInstance().show(
                        Notifications.Type.SUCCESS,
                        Notifications.Location.BOTTOM_LEFT,
                        "Thêm dịch vụ thành công!"
                );
                parent.loadData();
                dispose();
            } else {
                err.setText("Không thể thêm dịch vụ. Vui lòng thử lại.");
            }
        } catch (Exception ex) {
            err.setText("Lỗi: " + ex.getMessage());
        }
    }

    private boolean validData() {
        err.setText(" ");
        if (txtName.getText().trim().isEmpty()) {
            err.setText("Tên dịch vụ không được để trống");
            return false;
        }
        if (!txtPrice.getText().trim().matches("^[0-9]+(\\.[0-9]+)?$")) {
            err.setText("Giá phải là số hợp lệ");
            return false;
        }
        if (!txtQty.getText().trim().matches("^\\d+$")) {
            err.setText("Số lượng phải là số nguyên");
            return false;
        }
        if (fileChosen == null) {
            err.setText("Vui lòng chọn ảnh minh họa");
            return false;
        }
        return true;
    }

    private File copyToResourcesImages(File src) {
        try {
            java.net.URL u = getClass().getResource("/images/");
            Path destDir;
            if (u != null && "file".equalsIgnoreCase(u.getProtocol())) {
                destDir = Paths.get(u.toURI());
            } else {
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
