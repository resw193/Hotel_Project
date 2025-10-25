package gui.service;

import bus.ServiceBUS;
import entity.Service;
import net.miginfocom.swing.MigLayout;
import raven.crazypanel.CrazyPanel;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormUpdateQuantityService extends JDialog {
    private final Service currentService;
    private JTextField txtAdd;
    private JLabel err;
    private final ServiceBUS serviceBUS = new ServiceBUS();

    public FormUpdateQuantityService(Service currentService) {
        this.currentService = currentService;
        initUI();
    }

    private void initUI() {
        setTitle("Thêm số lượng");
        setSize(420, 240);
        setResizable(false);
        setLocationRelativeTo(null);

        // Panel chính
        CrazyPanel mainPanel = new CrazyPanel();
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.GRAY); // nền sáng

        // Tiêu đề
        JLabel title = new JLabel("Thêm số lượng cho: " + currentService.getServiceName(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0x333333));
        mainPanel.add(title, BorderLayout.NORTH);

        // Panel form
        JPanel formPanel = new JPanel(new MigLayout("wrap 2, fillx, insets 0", "[right][grow]", "[]10[]"));
        formPanel.setBackground(Color.WHITE);

        JLabel lblQuantity = new JLabel("Số lượng:");
        lblQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAdd = new JTextField();
        txtAdd.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        formPanel.add(lblQuantity);
        formPanel.add(txtAdd, "growx");

        err = new JLabel(" ");
        err.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        err.setForeground(new Color(0xD64545));
        formPanel.add(err, "span, growx");

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel nút
        JButton btnSave = new JButton("Thêm");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setBackground(new Color(0x4CAF50));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setOpaque(true);
        btnSave.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnSave);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Action nút
        btnSave.addActionListener(e -> {
            err.setText(" ");
            String input = txtAdd.getText().trim();
            if (!input.matches("^\\d+$")) {
                err.setText("Số lượng thêm phải là số");
                return;
            }
            int added = Integer.parseInt(input);

            try {
                if (serviceBUS.increaseQuantity(currentService.getServiceID(), added)) {
                    Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.BOTTOM_LEFT, "Quantity updated");
                    dispose();
                } else {
                    err.setText("Cập nhật thất bại.");
                }
            } catch (Exception ex) {
                err.setText(ex.getMessage());
            }
        });
    }
}