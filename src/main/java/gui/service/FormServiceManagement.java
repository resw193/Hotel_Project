package gui.service;

import bus.ServiceBUS;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import entity.Service;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class FormServiceManagement extends JPanel {

    private static final Color BG            = new Color(0x0B1F33);
    private static final Color PANEL_TOP     = new Color(0x0E2942);
    private static final Color CARD_BG       = new Color(0x102D4A);
    private static final Color TEXT_PRIMARY  = new Color(0xE9EEF6);
    private static final Color TEXT_MUTED    = new Color(0xB8C4D4);
    private static final Color GOLD_PRIMARY  = new Color(0xF5C452);

    private final ServiceBUS serviceBUS = new ServiceBUS();

    private JComboBox<String> cboFilter;
    private JButton btnAddNew;
    private JPanel grid;
    private JScrollPane scroll;

    public FormServiceManagement() {
        setLayout(new MigLayout("fill, insets 0, wrap 1", "[grow]", "[grow 0][grow]"));
        setBackground(BG);

        // --- TOP BAR ---
        JPanel top = new JPanel(new MigLayout("insets 12 16 12 16", "[grow]push[][]", "[]"));
        top.setBackground(PANEL_TOP);
        top.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(top, "growx");

        JLabel title = new JLabel("Dịch vụ | Services");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        top.add(title, "left");

        cboFilter = new JComboBox<>(new String[]{"All", "Food", "Drink", "Laundry"});
        cboFilter.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,12,6,12");
        top.add(cboFilter, "w 150!");

        FlatSVGIcon addTopIcon = new FlatSVGIcon("icon/svg/add.svg", 0.35f);
        addTopIcon.setColorFilter(new FlatSVGIcon.ColorFilter() {
            @Override
            public Color filter(Color c) {
                return GOLD_PRIMARY;
            }
        });

        btnAddNew = new JButton("ADD NEW", addTopIcon);
        stylePrimary(btnAddNew);
        top.add(btnAddNew, "w 130!");

        // --- GRID AREA ---
        grid = new JPanel();
        grid.setBackground(BG);

        // Responsive layout: tự co số cột theo chiều rộng
        grid.setLayout(new MigLayout(
                "wrap 5, insets 20, gapx 20, gapy 20",
                "[grow,fill] [grow,fill] [grow,fill] [grow,fill] [grow,fill]",
                "[]"));

        scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "width:10; background:#0B1F33; track:#0B1F33; thumb:#274A6B; trackArc:999");
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        add(scroll, "grow");

        // --- EVENTS ---
        cboFilter.addActionListener(e -> loadData());
        btnAddNew.addActionListener(e -> {
            FormAddService formAddService = new FormAddService(FormServiceManagement.this);
            formAddService.setModal(true);
            formAddService.setLocationRelativeTo(this);
            formAddService.setVisible(true);
            loadData();
        });

        // Click chuột lấy lại focus để cuộn bằng phím
        scroll.addMouseWheelListener(e -> scroll.requestFocusInWindow());
        grid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                scroll.requestFocusInWindow();
            }
        });

        loadData();
    }

    void loadData() {
        String selected = String.valueOf(cboFilter.getSelectedItem());
        List<Service> data = switch (selected) {
            case "Food" -> serviceBUS.getByType("Food");
            case "Drink" -> serviceBUS.getByType("Drink");
            case "Laundry" -> serviceBUS.getByType("Laundry");
            default -> serviceBUS.getAll();
        };
        LoadDataToDisplay(data);
    }

    private void LoadDataToDisplay(List<Service> services) {
        grid.removeAll();

        FlatSVGIcon.ColorFilter whiteFilter = new FlatSVGIcon.ColorFilter() {
            @Override
            public Color filter(Color c) {
                return Color.WHITE;
            }
        };

        FlatSVGIcon.ColorFilter mutedFilter = new FlatSVGIcon.ColorFilter() {
            @Override
            public Color filter(Color c) {
                return TEXT_MUTED;
            }
        };

        for (Service service : services) {
            JPanel card = new JPanel(new MigLayout("wrap 1, insets 10, gapy 6", "[grow,fill]", "[]"));
            card.setBackground(CARD_BG);
            card.putClientProperty(FlatClientProperties.STYLE, "arc:16; borderColor:#153C5B");

            JLabel imgService = new JLabel("", SwingConstants.CENTER);
            imgService.setPreferredSize(new Dimension(140, 120));

            ImageIcon icon = loadImageFromResourcesOrFile(service.getImgSource(), 120, 120);
            if (icon != null) {
                imgService.setIcon(icon);
            } else {
                FlatSVGIcon ph = new FlatSVGIcon("icon/svg/error.svg", 1f);
                ph.setColorFilter(mutedFilter);
                imgService.setIcon(ph);
            }

            JLabel name = new JLabel(service.getServiceName(), SwingConstants.CENTER);
            name.setForeground(TEXT_PRIMARY);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 14f));

            JLabel qty = new JLabel("Số lượng: " + service.getQuantity(), SwingConstants.CENTER);
            qty.setForeground(TEXT_MUTED);

            JLabel price = new JLabel(String.format("Giá: %,.2f VND", service.getPrice()), SwingConstants.CENTER);
            price.setForeground(GOLD_PRIMARY);
            price.setFont(price.getFont().deriveFont(Font.BOLD, 13f));

            JPanel btnPanel = new JPanel(new MigLayout("insets 0, gapx 6", "[grow,fill][grow,fill][grow,fill]"));
            btnPanel.setOpaque(false);

            JButton btnDelete = new JButton("Delete", new FlatSVGIcon("icon/svg/delete.svg", 0.35f));
            ((FlatSVGIcon) btnDelete.getIcon()).setColorFilter(whiteFilter);
            styleDanger(btnDelete);

            JButton btnUpdate = new JButton("Update", new FlatSVGIcon("icon/svg/edit.svg", 0.35f));
            ((FlatSVGIcon) btnUpdate.getIcon()).setColorFilter(whiteFilter);
            styleSuccess(btnUpdate);

            JButton btnAddQty = new JButton("Add", new FlatSVGIcon("icon/svg/add.svg", 0.35f));
            ((FlatSVGIcon) btnAddQty.getIcon()).setColorFilter(whiteFilter);
            stylePrimary(btnAddQty);

            btnPanel.add(btnDelete);
            btnPanel.add(btnUpdate);
            btnPanel.add(btnAddQty);

            card.add(imgService, "growx");
            card.add(name, "growx");
            card.add(qty, "growx");
            card.add(price, "growx");
            card.add(btnPanel, "growx");

            grid.add(card);
        }

        grid.revalidate();
        grid.repaint();
    }

    private void stylePrimary(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#F5C452; foreground:#0B1F33; borderColor:#F1B93A;" +
                        "hoverBackground:#FFD36E; focusWidth:1; innerFocusWidth:0; margin:6,4,6,4");
    }

    private void styleSuccess(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#1E9E8F; foreground:#FFFFFF; borderColor:#178C7F;" +
                        "hoverBackground:#1A8A7E; focusWidth:1; innerFocusWidth:0; margin:6,4,6,4");
    }

    private void styleDanger(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#D64545; foreground:#FFFFFF; borderColor:#BF3E3E;" +
                        "hoverBackground:#B73A3A; focusWidth:1; innerFocusWidth:0; margin:6,4,6,4");
    }

    private ImageIcon loadImageFromResourcesOrFile(String path, int w, int h) {
        try {
            Image img = null;
            if (path != null && !path.isBlank()) {
                File f = new File(path);
                if (f.exists()) img = new ImageIcon(f.getAbsolutePath()).getImage();
                else {
                    java.net.URL u = getClass().getResource(path.startsWith("/") ? path : "/" + path);
                    if (u != null) img = new ImageIcon(u).getImage();
                }
            }
            if (img == null) return null;
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception ex) {
            return null;
        }
    }
}