package gui.room;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import DAO.RoomDAO;
import DAO.ServiceDAO;
import Entity.Service;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FormUpdateServiceToRoom extends JDialog {

    private static final Color BG = new Color(0x0B1F33);
    private static final Color PANEL = new Color(0x0E2942);
    private static final Color TEXT = new Color(0xE9EEF6);
    private static final Color GOLD = new Color(0xF5C452);

    private final RoomDAO roomDAO = new RoomDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final String roomID;

    private JComboBox<String> cboService;
    private JSpinner spQty;
    private JButton btnAdd;

    public FormUpdateServiceToRoom(Component parent, String roomID) {
        this.roomID = roomID;
        setTitle("Thêm dịch vụ cho phòng " + roomID);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new MigLayout("insets 12 16 16 16, wrap 2", "[right]16[300,grow]", "[]10[]20[]"));
        root.setBackground(BG);

        JLabel lblService = new JLabel("Service:");
        lblService.setForeground(TEXT);
        root.add(lblService);

        cboService = new JComboBox<>();
        cboService.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,10,6,10;");
        root.add(cboService, "growx");

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

        // Load toàn bộ service lên combobox để chọn
        loadServices();

        btnAdd.addActionListener(e -> addServiceToRoom());
    }

    private void loadServices() {
        List<Service> dsDichVu = serviceDAO.getAllServices();
        cboService.removeAllItems();
        if (dsDichVu != null) {
            for (Service s : dsDichVu) {
                if (s != null && s.getServiceName() != null) {
                    cboService.addItem(s.getServiceName());
                }
            }
        }
    }

    private void addServiceToRoom() {
        String serviceName = (String) cboService.getSelectedItem();
        int qty = (Integer) spQty.getValue();

        if (serviceName == null || serviceName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ.");
            return;
        }
        boolean ok = roomDAO.capNhatDichVuChoPhong(roomID, serviceName, qty);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã thêm dịch vụ cho " + roomID);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm dịch vụ (chỉ áp dụng đối với phòng đã check-in", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
