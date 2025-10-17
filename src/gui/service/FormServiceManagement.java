package gui.service;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import DAO.ServiceDAO;
import Entity.Service;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;

public class FormServiceManagement extends JPanel {

    private static final Color BG            = new Color(0x0B1F33);
    private static final Color PANEL_TOP     = new Color(0x0E2942);
    private static final Color CARD_BG       = new Color(0x102D4A);
    private static final Color TEXT_PRIMARY  = new Color(0xE9EEF6);
    private static final Color TEXT_MUTED    = new Color(0xB8C4D4);
    private static final Color GOLD_PRIMARY  = new Color(0xF5C452);
    private static final Color SUCCESS_BG    = new Color(0x1E9E8F);
    private static final Color DANGER_BG     = new Color(0xD64545);

    private final ServiceDAO serviceDAO = new ServiceDAO();

    private JComboBox<String> cboFilter;
    private JButton btnAddNew;
    private JPanel grid;
    private JScrollPane scroll;

    public FormServiceManagement() {
        setLayout(new MigLayout("fill, wrap, insets 0", "[grow]", "[grow 0][grow]"));
        setBackground(BG);

        // Top bar
        JPanel top = new JPanel(new MigLayout("insets 12 16 12 16", "[grow]push[][]", "[]"));
        top.setBackground(PANEL_TOP);
        top.setBorder(new EmptyBorder(0,0,0,0));
        add(top, "growx");

        JLabel title = new JLabel("Dịch vụ | Services");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        top.add(title, "left");

        cboFilter = new JComboBox<>(new String[]{"All", "Food", "Drink"});
        cboFilter.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,12,6,12");
        top.add(cboFilter, "w 150!");

        FlatSVGIcon.ColorFilter goldFilter = new FlatSVGIcon.ColorFilter() {
            @Override public Color filter(Color c) { return GOLD_PRIMARY; }
        };
        FlatSVGIcon addTopIcon = new FlatSVGIcon("gui/icon/svg/add.svg", 0.35f);
        addTopIcon.setColorFilter(goldFilter);
        btnAddNew = new JButton("Add New", addTopIcon);
        stylePrimary(btnAddNew);
        top.add(btnAddNew, "w 120!");

        // Grid 5 cột
        grid = new JPanel(new MigLayout("wrap 5, insets 16, gap 16",
                "[grow,fill] [grow,fill] [grow,fill] [grow,fill] [grow,fill]", ""));
        grid.setBackground(BG);
        scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "width:10; background:#0B1F33; track:#0B1F33; thumb:#274A6B; trackArc:999");
        add(scroll, "grow");

        // Events
        cboFilter.addActionListener(e -> reloadData());
        btnAddNew.addActionListener(e -> {
            FormAddService FormAddService = new FormAddService(FormServiceManagement.this);
            FormAddService.setModal(true);
            FormAddService.setLocationRelativeTo(this);
            FormAddService.setVisible(true);
            reloadData();
        });

        reloadData();
    }

    public void reloadData() {
        String selected = String.valueOf(cboFilter.getSelectedItem());

        List<Service> data = switch(selected)
        {
            case "Food"  -> serviceDAO.getAllServiceByType("Food");
            case "Drink" -> serviceDAO.getAllServiceByType("Drink");
            default      -> serviceDAO.getAllServices();
        };

        LoadDataToDisplay(data);
    }

    private void LoadDataToDisplay(List<Service> services) {
        grid.removeAll();

        FlatSVGIcon.ColorFilter whiteFilter = new FlatSVGIcon.ColorFilter() {
            @Override public Color filter(Color c) { return Color.WHITE; }
        };

        FlatSVGIcon.ColorFilter mutedFilter = new FlatSVGIcon.ColorFilter() {
            @Override public Color filter(Color c) { return TEXT_MUTED; }
        };

        for (Service service : services) {
            JPanel card = new JPanel(new MigLayout("wrap 6, insets 14, gapy 6", "[grow]", "[]"));
            card.setBackground(CARD_BG);
            card.putClientProperty(FlatClientProperties.STYLE, "arc:16; borderColor:#153C5B");

            JLabel imgService = new JLabel("", SwingConstants.CENTER);
            imgService.setPreferredSize(new Dimension(140, 180));

            String path = service.getImgSource();
            if (path != null && !path.isBlank() && new File(path).exists()) {
                Image img = new ImageIcon(path).getImage();
                imgService.setIcon(new ImageIcon(img.getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
            } else {
                FlatSVGIcon ph = new FlatSVGIcon("gui/icon/svg/error.svg", 1f);
                ph.setColorFilter(mutedFilter);
                imgService.setIcon(ph);
            }

            JLabel name = new JLabel(service.getServiceName(), SwingConstants.CENTER);
            name.setForeground(TEXT_PRIMARY);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 14f));
            name.putClientProperty(FlatClientProperties.STYLE, "border:6,0,6,0");

            JLabel qty = new JLabel("Quantity: " + service.getQuantity());
            qty.setForeground(TEXT_MUTED);

            JLabel price = new JLabel(String.format("Price: %,.2f VND", service.getPrice()), SwingConstants.RIGHT);
            price.setForeground(TEXT_MUTED);

            // FlatSVGIcon cho các button
            FlatSVGIcon delI = new FlatSVGIcon("gui/icon/svg/delete.svg", 0.35f); delI.setColorFilter(whiteFilter);
            JButton btnDelete = new JButton("Delete", delI);
            FlatSVGIcon editI = new FlatSVGIcon("gui/icon/svg/edit.svg", 0.35f);   editI.setColorFilter(whiteFilter);
            JButton btnUpdate = new JButton("Update", editI);
            FlatSVGIcon addI  = new FlatSVGIcon("gui/icon/svg/add.svg", 0.35f);    addI.setColorFilter(whiteFilter);
            JButton btnAddQty = new JButton("Add", addI);

            styleDanger(btnDelete);
            styleSuccess(btnUpdate);
            stylePrimary(btnAddQty);

            card.add(imgService,    "growx, span 6");
            card.add(name,      "growx, span 6");
            card.add(qty,       "growx, span 3");
            card.add(price,     "growx, span 3");
            card.add(btnDelete, "growx, span 2");
            card.add(btnUpdate, "growx, span 2");
            card.add(btnAddQty, "growx, span 2");

            btnDelete.addActionListener(e -> {
                int opt = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa dịch vụ này không?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    if (serviceDAO.removeServiceByID(service.getServiceID())) {
                        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.BOTTOM_LEFT, "Deleted");
                        reloadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Cannot delete service", "Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnUpdate.addActionListener(e -> {
                FormUpdateService formUpdateService = new FormUpdateService(FormServiceManagement.this, service);
                formUpdateService.setModal(true);
                formUpdateService.setLocationRelativeTo(this);
                formUpdateService.setVisible(true);
                reloadData();
            });

            btnAddQty.addActionListener(e -> {
                FormUpdateQuantityService formUpdateQuantityService = new FormUpdateQuantityService(FormServiceManagement.this, service);
                formUpdateQuantityService.setModal(true);
                formUpdateQuantityService.setLocationRelativeTo(this);
                formUpdateQuantityService.setVisible(true);
                reloadData();
            });

            grid.add(card, "grow");
        }

        grid.revalidate();
        grid.repaint();
    }

    private void stylePrimary(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#F5C452; foreground:#0B1F33; borderColor:#F1B93A; " +
                        "hoverBackground:#FFD36E; focusWidth:1; innerFocusWidth:0; margin:6,10,6,10");
    }

    private void styleSuccess(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#1E9E8F; foreground:#FFFFFF; borderColor:#178C7F; " +
                        "hoverBackground:#1A8A7E; focusWidth:1; innerFocusWidth:0; margin:6,10,6,10");
    }

    private void styleDanger(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#D64545; foreground:#FFFFFF; borderColor:#BF3E3E; " +
                        "hoverBackground:#B73A3A; focusWidth:1; innerFocusWidth:0; margin:6,10,6,10");
    }
}
