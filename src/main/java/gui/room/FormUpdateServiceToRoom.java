package gui.room;

import bus.RoomBUS;
import bus.ServiceBUS;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import entity.Service;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class FormUpdateServiceToRoom extends JDialog {

    private static final Color BG   = new Color(0x0B1F33);
    private static final Color TEXT = new Color(0xE9EEF6);
    private static final Color GOLD = new Color(0xF5C452);

    private RoomBUS roomBUS = new RoomBUS();
    private ServiceBUS serviceBUS = new ServiceBUS();
    private String roomID;

    private JComboBox<String> cboService;
    private JSpinner spQty;
    private JButton btnAdd;
    private JLabel lblStock; // hiển thị tồn kho hiện tại

    public FormUpdateServiceToRoom(Component parent, String roomID) {
        this.roomID = roomID;
        setTitle("Thêm dịch vụ cho phòng " + roomID);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new MigLayout("insets 12 16 16 16, wrap 2", "[right]16[300,grow]", "[]6[]0[]20[]"));
        root.setBackground(BG);

        JLabel lblService = new JLabel("Service:");
        lblService.setForeground(TEXT);
        root.add(lblService);

        cboService = new JComboBox<>();
        cboService.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,10,6,10;");
        root.add(cboService, "growx");

        // dòng hiển thị tồn kho
        root.add(new JLabel());
        lblStock = new JLabel("Còn: -");
        lblStock.setForeground(GOLD);
        root.add(lblStock, "growx");

        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setForeground(TEXT);
        root.add(lblQty);

        spQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spQty.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,10,6,10;");
        root.add(spQty, "w 120!");

        btnAdd = new JButton("Thêm", new FlatSVGIcon("gui/icon/svg/check.svg", 0.35f));
        btnAdd.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#F5C452; foreground:#0B1F33; borderColor:#F1B93A; hoverBackground:#FFD36E;");
        root.add(btnAdd, "span 2, al right, w 120!, h 34!");

        setContentPane(root);
        pack();
        setLocationRelativeTo(parent);

        // Load danh sách dịch vụ
        loadServices();

        // khi đổi dịch vụ -> cập nhật tồn kho + giới hạn spinner
        cboService.addActionListener(e -> refreshSelectedServiceInfo());

        // cập nhật lần đầu
        refreshSelectedServiceInfo();

        btnAdd.addActionListener(e -> addServiceToRoom());
    }

    private void loadServices() {
        var ds = serviceBUS.getAll();
        cboService.removeAllItems();
        if (ds != null) {
            for (Service s : ds) {
                if (s != null && s.getServiceName() != null) {
                    cboService.addItem(s.getServiceName());
                }
            }
        }
    }

    // Lấy service hiện chọn, cập nhật “Còn: X” và set max cho spinner
    private void refreshSelectedServiceInfo() {
        String name = (String) cboService.getSelectedItem();
        if (name == null) {
            lblStock.setText("Còn: -");
            return;
        }
        Service s = serviceBUS.getByName(name);
        int stock = (s == null) ? 0 : s.getQuantity();
        lblStock.setText("Còn: " + stock);

        SpinnerNumberModel model = (SpinnerNumberModel) spQty.getModel();
        model.setMaximum(Math.max(1, stock));  // tối thiểu vẫn là 1 để người dùng thấy giới hạn
        if ((int) model.getNumber() > stock) {
            model.setValue(Math.max(1, stock));
        }
        btnAdd.setEnabled(stock > 0); // hết hàng -> không cho thêm
    }

    private void addServiceToRoom() {
        String serviceName = (String) cboService.getSelectedItem();
        int qty = (Integer) spQty.getValue();

        if (serviceName == null || serviceName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ.");
            return;
        }

        // Kiểm tra tồn kho phía giao diệns để cảnh báo
        Service s = serviceBUS.getByName(serviceName);
        int stock = (s == null) ? 0 : s.getQuantity();
        if (qty > stock) {
            JOptionPane.showMessageDialog(this,
                    "Số lượng yêu cầu (" + qty + ") vượt quá tồn kho hiện có (" + stock + ").",
                    "Không đủ tồn kho", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = roomBUS.addServiceToRoom(roomID, serviceName, qty);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã thêm dịch vụ cho " + roomID);

            // Sau khi thêm thành công, trừ tồn kho trên UI
            refreshSelectedServiceInfo();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    roomBUS.getLastError() == null
                            ? "Không thể thêm dịch vụ (chỉ áp dụng đối với phòng đã check-in hoặc không đủ tồn kho)"
                            : roomBUS.getLastError(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
