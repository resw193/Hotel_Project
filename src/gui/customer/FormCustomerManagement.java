package gui.customer;

import DAO.CustomerDAO;
import Entity.Customer;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormCustomerManagement extends JPanel {

    private static final Color BG = new Color(0x0B1F33);
    private static final Color CARD_BG = new Color(0x0F2A44);
    private static final Color BORDER = new Color(0x274A6B);
    private static final Color TEXT = new Color(0xE6F1FF);
    private static final Color ACCENT = new Color(0x22D3EE);
    private static final Font  BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private CustomerDAO customerDAO = new CustomerDAO();
    private CustomerTableModel tableModel = new CustomerTableModel();

    private JTable tableCustomer;
    private JTextField txtSearchName;
    private JComboBox<String> cbxFilter;
    private JButton btnAdd, btnUpdate;

    // CustomerID, Tên, SĐT, Email, Ngày ĐK, CCCD, Điểm thân thiết
    private static final int[] COL_WEIGHTS = {12, 22, 14, 20, 14, 10, 8};

    public FormCustomerManagement() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel top = new JPanel(new MigLayout(
                "insets 10 12 10 12",
                "[grow,fill]push[]8[]8[]",
                "[]"
        ));
        top.setOpaque(true);
        top.setBackground(BG);

        txtSearchName = new JTextField();
        txtSearchName.putClientProperty("JTextField.placeholderText", "Tìm theo tên khách hàng…");
        styleTextField(txtSearchName);
        top.add(txtSearchName, "w 360!");

        cbxFilter = new JComboBox<>(new String[]{"Tất cả", ">= 20", ">= 40"});
        styleCombo(cbxFilter);
        top.add(cbxFilter, "w 140!");

        // btn
        btnAdd = primaryButton("Thêm", true);
        btnUpdate = primaryButton("Cập nhật", false);
        top.add(btnAdd, "w 110!");
        top.add(btnUpdate, "w 110!");

        add(top, BorderLayout.NORTH);

        // table
        tableCustomer = createTable();
        JScrollPane sp = new JScrollPane(tableCustomer);
        sp.getViewport().setBackground(BG);
        sp.setBorder(new LineBorder(BORDER));
        add(sp, BorderLayout.CENTER);

        // fit cột
        fitColumnsToViewport(tableCustomer, COL_WEIGHTS);
        sp.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                fitColumnsToViewport(tableCustomer, COL_WEIGHTS);
            }
        });

        // event
        txtSearchName.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                loadDataToTable();
            }

            @Override public void removeUpdate(DocumentEvent e) {
                loadDataToTable();
            }

            @Override public void changedUpdate(DocumentEvent e) {
                loadDataToTable();
            }
        });
        cbxFilter.addActionListener(e -> loadDataToTable());

        btnAdd.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            new FormAddCustomer(owner).setVisible(true);
            loadDataToTable();
        });

        btnUpdate.addActionListener(e -> updateCustomer());

        // double-click
        tableCustomer.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)){
                    updateCustomer();
                }
            }
        });

        loadDataToTable();
    }

    private JTable createTable() {
        JTable t = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(0x0E253D) : new Color(0x0C2136));
                    c.setForeground(TEXT);
                } else {
                    c.setBackground(new Color(0x10344F));
                    c.setForeground(Color.WHITE);
                }
                if (c instanceof JComponent jc) {
                    jc.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
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

        // Căn giữa tiêu đề cột
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) h.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        int[] centerCols = {0, 1, 2, 3, 4, 6};
        for (int col : centerCols) {
            t.getColumnModel().getColumn(col).setCellRenderer(center);
        }

        return t;
    }

    private void loadDataToTable() {
        String opt = String.valueOf(cbxFilter.getSelectedItem());
        ArrayList<Customer> dsKH;
        if ("Tất cả".equalsIgnoreCase(opt)) dsKH = customerDAO.getAllCustomer();
        else if (">= 40".equals(opt)) dsKH = customerDAO.getAllCustomerByLoyaltyPoint(40);
        else dsKH = customerDAO.getAllCustomerByLoyaltyPoint(20);
        if (dsKH == null) dsKH = new ArrayList<>();

        String kw = txtSearchName.getText().trim().toLowerCase();
        ArrayList<Customer> filtered = kw.isEmpty() ? dsKH :
                (ArrayList<Customer>) dsKH.stream()
                        .filter(c -> c.getFullName() != null && c.getFullName().toLowerCase().contains(kw))
                        .collect(Collectors.toList());

        tableModel.setDsKH(filtered);
        if (!filtered.isEmpty()) tableCustomer.setRowSelectionInterval(0, 0);

        // Fit lại cột sau khi đổi dữ liệu
        fitColumnsToViewport(tableCustomer, COL_WEIGHTS);
    }

    private void updateCustomer() {
        int row = tableCustomer.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng để cập nhật.");
            return;
        }
        Customer c = tableModel.getAt(row);
        if (c == null) return;
        Window owner = SwingUtilities.getWindowAncestor(this);
        new FormUpdateCustomer(owner, customerDAO, c.getCustomerID()).setVisible(true);
        loadDataToTable();
    }

    // style
    private void styleTextField(JTextField f){
        f.setFont(BASE_FONT);
        f.setForeground(TEXT);
        f.setBackground(new Color(0x102A43));
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                BorderFactory.createEmptyBorder(6,8,6,8)
        ));
    }
    private void styleCombo(JComboBox<?> cb){
        cb.setFont(BASE_FONT);
        cb.setForeground(TEXT);
        cb.setBackground(new Color(0x102A43));
        cb.setBorder(new LineBorder(BORDER));
    }

    private JButton primaryButton(String text, boolean solid){
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

        int total = 0;
        for (int w : weights) total += w;
        for (int i = 0; i < cm.getColumnCount() && i < weights.length; i++) {
            int w = (int) Math.round(vw * (weights[i] / (double) total));
            cm.getColumn(i).setPreferredWidth(Math.max(w, 90));
        }
    }
}
