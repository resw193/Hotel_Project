package gui.service;

import DAO.ServiceDAO;
import Entity.Service;
import net.miginfocom.swing.MigLayout;
import raven.crazypanel.CrazyPanel;
import raven.toast.Notifications;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FormAddService extends JDialog {
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final FormServiceManagement parent;

    private JTextField txtName, txtPrice, txtQty;
    private JComboBox<String> cboType;
    private JLabel lblPreview, err;

    private File fileChosen;

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

        err = new JLabel(" ");
        err.setForeground(new Color(0xD64545));
        p.add(err, "span, growx");

        JButton btnSave = new JButton("Add");
        btnSave.putClientProperty("JButton.buttonType", "roundRect");
        p.add(btnSave, "span, al trail");

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

        btnSave.addActionListener(e -> {
            if (!validateInput()) return;

            // file path
            String outPath = "images/" + fileChosen.getName();
            try {
                BufferedImage bi = ImageIO.read(fileChosen);
                String ext = outPath.substring(outPath.lastIndexOf('.') + 1);
                ImageIO.write(bi, ext, new File(outPath));
            } catch (Exception ex) { ex.printStackTrace(); }

            Service s = new Service(
                    txtName.getText().trim(),
                    String.valueOf(cboType.getSelectedItem()),
                    Integer.parseInt(txtQty.getText().trim()),
                    Double.parseDouble(txtPrice.getText().trim()),
                    outPath
            );

            if (serviceDAO.addService(s)) {
                Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.BOTTOM_LEFT, "Added");
                dispose();
                parent.reloadData();
            } else {
                err.setText("Failed to add service.");
            }
        });
    }

    private boolean validateInput() {
        err.setText(" ");
        if (txtName.getText().trim().isEmpty()) { err.setText("Name is required."); return false; }
        if (!txtPrice.getText().trim().matches("^[0-9]+(\\.[0-9]+)?$")) { err.setText("Price must be number."); return false; }
        if (!txtQty.getText().trim().matches("^\\d+$")) { err.setText("Quantity must be integer."); return false; }
        if (fileChosen == null) { err.setText("Image is required."); return false; }
        return true;
    }
}
