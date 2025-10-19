package gui.promotion;

import DAO.PromotionDAO;
import Entity.Promotion;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

public class FormPromotionManagement extends JPanel {

    // Color
    private static final Color BG = new Color(0x0B1F33);
    private static final Color BORDER = new Color(0x274A6B);
    private static final Color TEXT = new Color(0xE6F1FF);
    private static final Color ACCENT = new Color(0x22D3EE);
    private static final Font  BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private PromotionDAO promotionDAO = new PromotionDAO();
    private PromotionTableModel tableModel = new PromotionTableModel();

    private JTable tablePromotion;
    private JButton btnAdd, btnUpdate;

    // ID, Name, Discount, Start, End, Qty
    private static final int[] COL_WEIGHTS = {10, 26, 10, 22, 22, 10};

    public FormPromotionManagement() {
        setLayout(new BorderLayout());
        setBackground(BG);

        // top bar: đẩy nút về bên phải
        JPanel top = new JPanel(new MigLayout(
                "insets 10 12 10 12",
                "[grow,fill]push[]8[]",
                "[]"
        ));
        top.setBackground(BG);

        btnAdd = primaryButton("Thêm khuyến mãi", true);
        btnUpdate = primaryButton("Cập nhật", false);
        top.add(btnAdd, "w 150!, align right");
        top.add(btnUpdate, "w 120!, align right");
        add(top, BorderLayout.NORTH);

        // table
        tablePromotion = createTable();
        JScrollPane sp = new JScrollPane(tablePromotion);
        sp.getViewport().setBackground(BG);
        sp.setBorder(new LineBorder(BORDER));
        add(sp, BorderLayout.CENTER);

        fitColumnsToViewport(tablePromotion, COL_WEIGHTS);
        sp.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                fitColumnsToViewport(tablePromotion, COL_WEIGHTS);
            }
        });

        // events
        btnAdd.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            new FormAddPromotion(owner).setVisible(true);
            loadDataToTable();
        });

        btnUpdate.addActionListener(e -> update());

        // load
        loadDataToTable();
    }

    private JTable createTable() {
        JTable t = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(0x0E253D) : new Color(0x0C2136));
                    c.setForeground(TEXT);
                } else {
                    c.setBackground(new Color(0x10344F));
                    c.setForeground(Color.WHITE);
                }
                if (c instanceof JComponent jc) {
                    jc.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
                }
                return c;
            }
        };
        t.setRowHeight(30);
        t.setFont(BASE_FONT);
        t.setForeground(TEXT);
        t.setBackground(BG);
        t.setGridColor(new Color(0x13314A));
        t.setFillsViewportHeight(true);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(0x102A43));
        h.setForeground(ACCENT);
        h.setFont(BASE_FONT.deriveFont(Font.BOLD, 13f));
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Căn giữa
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(2).setCellRenderer(center);
        t.getColumnModel().getColumn(5).setCellRenderer(center);

        return t;
    }

    private void loadDataToTable() {
        ArrayList<Promotion> dsKM = promotionDAO.getAllPromotions();
        if (dsKM == null) dsKM = new ArrayList<>();
        tableModel.setDsKM(dsKM);

        if (!dsKM.isEmpty()) tablePromotion.setRowSelectionInterval(0,0);
        fitColumnsToViewport(tablePromotion, COL_WEIGHTS);
    }

    private void update() {
        int row = tablePromotion.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để cập nhật.");
            return;
        }
        Promotion promotion = tableModel.getAt(row);
        if (promotion == null) return;

        Window owner = SwingUtilities.getWindowAncestor(this);
        new FormUpdatePromotion(owner, promotion.getPromotionID()).setVisible(true);
        loadDataToTable();
    }

    // style
    private JButton primaryButton(String text, boolean solid) {
        JButton b = new JButton(text);
        b.setFont(BASE_FONT.deriveFont(Font.BOLD));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(new Color(0x1B4F72)));
        b.setBackground(solid ? new Color(0x2563EB) : new Color(0x0EA5E9));
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void fitColumnsToViewport(JTable table, int[] weights) {
        Component p = table.getParent();
        if (!(p instanceof JViewport vp)) return;
        int vw = vp.getWidth();
        javax.swing.table.TableColumnModel cm = table.getColumnModel();

        int total = 0; for (int w : weights) total += w;
        for (int i = 0; i < cm.getColumnCount() && i < weights.length; i++) {
            int w = (int) Math.round(vw * (weights[i] / (double) total));
            cm.getColumn(i).setPreferredWidth(Math.max(w, 90));
        }
    }
}
