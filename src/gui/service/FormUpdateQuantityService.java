package gui.service;

import DAO.ServiceDAO;
import Entity.Service;
import net.miginfocom.swing.MigLayout;
import raven.crazypanel.CrazyPanel;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormUpdateQuantityService extends JDialog {
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final FormServiceManagement parent;
    private final Service currentService;

    private JTextField txtAdd;
    private JLabel err;


    public FormUpdateQuantityService(FormServiceManagement parent, Service currentService) {
        this.parent = parent;
        this.currentService = currentService;
        initUI();
    }

    private void initUI() {
        setTitle("Add Quantity");
        setSize(420, 220);
        setLocationRelativeTo(null);

        CrazyPanel p = new CrazyPanel();
        p.setBorder(new EmptyBorder(14,16,14,16));
        p.setLayout(new MigLayout("wrap 2, fillx, insets 0, gap 10", "[120::,right]16[fill]"));

        JLabel title = new JLabel("Thêm số lượng cho: " + currentService.getServiceName());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        p.add(title, "span, al center, gapbottom 8");

        txtAdd = new JTextField();
        p.add(new JLabel("Số lượng")); p.add(txtAdd);

        err = new JLabel(" ");
        err.setForeground(new Color(0xD64545));
        p.add(err, "span, growx");

        JButton btnSave = new JButton("Add");
        btnSave.putClientProperty("JButton.buttonType", "roundRect");
        p.add(btnSave, "span, al trail");

        setContentPane(p);

        btnSave.addActionListener(e -> {
            err.setText(" ");
            if (!txtAdd.getText().trim().matches("^\\d+$")) {
                err.setText("Quantity must be a positive integer.");
                return;
            }
            int added = Integer.parseInt(txtAdd.getText().trim());
            int newQty = currentService.getQuantity() + added;

            if (serviceDAO.updateQuantityService(newQty, currentService.getServiceID())) {
                Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.BOTTOM_LEFT, "Quantity updated");
                dispose();
                parent.reloadData();
            } else {
                err.setText("Failed to update quantity.");
            }
        });
    }
}
