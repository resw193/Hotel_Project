package gui.service;

import DAO.ServiceDAO;
import Entity.Service;
import net.miginfocom.swing.MigLayout;
import raven.crazypanel.CrazyPanel;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormUpdateService extends JDialog {
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final FormServiceManagement parent;
    private final Service currentService;

    private JTextField txtName, txtPrice;
    private JLabel err;


    public FormUpdateService(FormServiceManagement parent, Service currentService) {
        this.parent = parent;
        this.currentService = currentService;
        initUI();
    }

    private void initUI() {
        setTitle("Update Service");
        setSize(520, 300);
        setLocationRelativeTo(null);

        CrazyPanel p = new CrazyPanel();
        p.setBorder(new EmptyBorder(14,16,14,16));
        p.setLayout(new MigLayout("wrap 2, fillx, insets 0, gap 10", "[120::,right]16[fill]"));

        JLabel title = new JLabel("Update Information");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        p.add(title, "span, al center, gapbottom 8");

        txtName = new JTextField(currentService.getServiceName());
        txtPrice = new JTextField(String.valueOf(currentService.getPrice()));
        p.add(new JLabel("Name")); p.add(txtName);
        p.add(new JLabel("Price")); p.add(txtPrice);

        err = new JLabel(" ");
        err.setForeground(new Color(0xD64545));
        p.add(err, "span, growx");

        JButton btnUpdate = new JButton("Update");
        btnUpdate.putClientProperty("JButton.buttonType", "roundRect");
        p.add(btnUpdate, "span, al trail");

        setContentPane(p);

        btnUpdate.addActionListener(e -> {
            err.setText(" ");
            if (txtName.getText().trim().isEmpty()) { err.setText("Name is required."); return; }
            if (!txtPrice.getText().trim().matches("^[0-9]+(\\.[0-9]+)?$")) { err.setText("Price must be number."); return; }

            Service s = new Service(
                    currentService.getServiceID(),
                    txtName.getText().trim(),
                    currentService.getServiceType(),
                    currentService.getQuantity(),
                    Double.parseDouble(txtPrice.getText().trim()),
                    currentService.getImgSource()
            );

            if (serviceDAO.updateInformationService(s)) {
                Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.BOTTOM_LEFT, "Updated");
                dispose();
                parent.reload();
            } else {
                err.setText("Failed to update.");
            }
        });
    }
}
