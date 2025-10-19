package gui.order;

import DAO.OrderDAO;
import DAO.OrderDetailRoomDAO;
import DAO.OrderDetailServiceDAO;
import Entity.*;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormOrderManagement extends JPanel {

    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailRoomDAO odrDAO = new OrderDetailRoomDAO();
    private OrderDetailServiceDAO odsDAO = new OrderDetailServiceDAO();

    // const color
    private static final Color BG = new Color(0x0B1F33);
    private static final Color CARD_BG = new Color(0x0F2A44);
    private static final Color BORDER = new Color(0x274A6B);
    private static final Color TEXT = new Color(0xE6F1FF);
    private static final Color MUTED = new Color(0x9FB6CC);
    private static final Color ACCENT = new Color(0x22D3EE);
    private static final Color ACCENT2 = new Color(0xF59E0B);
    private static final Font  BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private OrderTableModel orderTableModel = new OrderTableModel();
    private JTable tableOrder;
    private JTextField txtSearchCusId;
    private JComboBox<String> cbxStatus;
    private JPanel detailPanel;

    private static final String QR_IMAGE_PATH = "images/qrtui.jpg";
    private static final String TICK_IMAGE_PATH = "images/thanhcong.png"; // ảnh tick xanh bạn sẽ cung cấp

    public FormOrderManagement() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel top = new JPanel(new MigLayout("insets 10 12 10 12", "[grow,fill]push[]", "[]"));
        top.setOpaque(true);
        top.setBackground(BG);

        txtSearchCusId = new JTextField();
        txtSearchCusId.putClientProperty("JTextField.placeholderText", "Tìm theo CustomerID…");
        styleTextField(txtSearchCusId);
        top.add(txtSearchCusId, "w 340!");

        cbxStatus = new JComboBox<>(new String[]{"Tất cả", "Chưa thanh toán", "Thanh toán"});
        styleCombo(cbxStatus);
        top.add(cbxStatus, "w 170!");
        add(top, BorderLayout.NORTH);

        // chia đôi
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.62);
        split.setBorder(null);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);

        tableOrder = createTable();
        JScrollPane sp = new JScrollPane(tableOrder);
        sp.getViewport().setBackground(BG);
        sp.setBorder(new LineBorder(BORDER));
        split.setLeftComponent(sp);

        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setOpaque(false);
        split.setRightComponent(new JScrollPane(detailPanel));

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                split.setDividerLocation(0.62);
            }
        });

        // event nhập doc text
        txtSearchCusId.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                reloadData();
            }

            @Override public void removeUpdate(DocumentEvent e) {
                reloadData();
            }

            @Override public void changedUpdate(DocumentEvent e) {
                reloadData();
            }
        });

        cbxStatus.addActionListener(e -> reloadData());

        tableOrder.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableOrder.getSelectedRow();
                showDetail(orderTableModel.getAt(row));
            }
        });

        reloadData();
    }

    // table
    private JTable createTable() {
        JTable t = new JTable(orderTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(0x0E253D) : new Color(0x0C2136));
                    c.setForeground(TEXT);
                } else {
                    c.setBackground(new Color(0x10344F));
                    c.setForeground(new Color(0xFFFFFF));
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
        ((DefaultTableCellRenderer)h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        int[] widths = {110, 160, 170, 170, 90, 120, 120};
        TableColumnModel cm = t.getColumnModel();
        for (int i = 0; i < cm.getColumnCount() && i < widths.length; i++) {
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }
        return t;
    }

    // LoadDataToTable
    private void reloadData() {
        String status = (String) cbxStatus.getSelectedItem();
        String keySearchID = txtSearchCusId.getText().trim().toLowerCase();

        ArrayList<Order> orders;
        if ("Tất cả".equalsIgnoreCase(status)) orders = orderDAO.getAllOrder();
        else orders = orderDAO.getAllOrderByStatus(status);

        if (orders == null) orders = new ArrayList<>();

        List<Order> filtered = keySearchID.isEmpty() ? orders :
                orders.stream()
                        .filter(o -> o.getCustomer() != null
                                  && o.getCustomer().getCustomerID() != null
                                  && o.getCustomer().getCustomerID().toLowerCase().contains(keySearchID))
                        .collect(Collectors.toList());

        orderTableModel.setDsHoaDon(filtered);

        if (!filtered.isEmpty()) {
            tableOrder.setRowSelectionInterval(0, 0);
            showDetail(filtered.get(0));
        } else {
            detailPanel.removeAll();
            detailPanel.revalidate();
            detailPanel.repaint();
        }
    }

    private void showDetail(Order o) {
        detailPanel.removeAll();
        if (o == null) {
            detailPanel.revalidate(); detailPanel.repaint();
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        JPanel root = new JPanel(new MigLayout("wrap, insets 12, gap 8", "[grow,fill]", ""));
        root.setOpaque(true);
        root.setBackground(BG);

        // thông tin hóa đơn
        JPanel header = titledCard("Thông tin hóa đơn");
        header.add(label("Mã hóa đơn: ", o.getOrderID(), true));
        header.add(label("Ngày lập: ", (o.getOrderDate() == null ? "" : formatter.format(o.getOrderDate())), false));
        header.add(label("Nhân viên: ", (o.getEmployee() == null ? "" : o.getEmployee().getFullName()), false));
        String kh = (o.getCustomer() == null ? "" : o.getCustomer().getFullName());
        String phone = (o.getCustomer() == null ? "" : o.getCustomer().getPhone());
        header.add(label("Khách hàng: ", kh + (isBlank(phone) ? "" :" ("+phone+")"), false));
        root.add(header);

        // Danh sách chi tiết phòng (OrderDetailRoom)
        List<OrderDetailRoom> roomLines = odrDAO.getAllOrderDetailRoomByOrderID(o.getOrderID());
        JPanel pRooms = titledCard("Các phòng đã đặt");
        if (roomLines != null && !roomLines.isEmpty()) {
            for (OrderDetailRoom r : roomLines) {
                String type = (r.getRoom() != null && r.getRoom().getRoomType() != null) ? r.getRoom().getRoomType().getTypeName() : "";

                JPanel card = miniCard();
                card.add(text("• " + (r.getRoom() == null ? "" : r.getRoom().getDescription())));
                card.add(text("Loại phòng: " + type));
                card.add(text("Đặt: " + format(r.getBookingDate(), formatter)
                        + " | Check-in: " + format(r.getCheckInDate(), formatter)
                        + " | Check-out: " + format(r.getCheckOutDate(), formatter)));
                card.add(text("Hình thức: " + nullToEmpty(r.getBookingType())));
                pRooms.add(card, "growx");
            }
        }
        else {
            pRooms.add(text("Không có chi tiết phòng."));
        }
        root.add(pRooms);

        // Dịch vụ đã sử dụng
        List<OrderDetailService> svLines = odsDAO.getAllOrderDetailServiceByOrderID(o.getOrderID());
        JPanel pSvc = titledCard("Dịch vụ đã sử dụng");
        if (svLines != null && !svLines.isEmpty()) {
            Map<String, Integer> agg = svLines.stream()
                    .collect(Collectors.groupingBy(
                            l -> l.getService().getServiceName(),
                            Collectors.summingInt(OrderDetailService::getQuantity)));
            agg.forEach((name, qty) -> pSvc.add(text("• " + name + "  × " + qty)));
        }
        else {
            pSvc.add(text("Không có dịch vụ."));
        }
        root.add(pSvc);

        // tổng, vat, thành tiêền, tính tiền, QR
        JPanel footer = titledCard("Thanh toán");
        footer.setLayout(new MigLayout("insets 8, gap 10", "[grow][280!]", "[][][]push[]"));

        // Tính tiền
        double total = o.getTotal();
        double vat = total * 0.10;
        double discountRate = (o.getPromotion() == null) ? 0D : (o.getPromotion().getDiscount() / 100.0);
        String promoTxt = (o.getPromotion() == null) ? "Không có" : ((int)o.getPromotion().getDiscount()) + "%";
        double amountDue = total + vat - (total * discountRate);
        boolean paidStatus = "Thanh toán".equalsIgnoreCase(o.getOrderStatus());

        // thông tin tiền phải trả
        footer.add(text("Tổng: " + formatVND(total)), "wrap");
        footer.add(text("VAT (10%): " + formatVND(vat)), "wrap");
        footer.add(text("Khuyến mãi: " + promoTxt), "wrap");

        // máy đếm tiền
        JPanel cash = new JPanel(new MigLayout("insets 8, gap 6", "[grow,fill]", ""));
        cash.setOpaque(true);
        cash.setBackground(new Color(0x102E4A));
        cash.setBorder(new LineBorder(BORDER));

        JLabel lblPaid = text("Khách đưa: " + formatVND(0));
        JLabel lblRemain = text("Còn thiếu: " + formatVND(amountDue));
        JLabel lblChange = text("Tiền thối: " + formatVND(0));
        cash.add(lblPaid,   "wrap");
        cash.add(lblRemain, "wrap");
        cash.add(lblChange, "wrap 10");

        JPanel denoms = new JPanel(new GridLayout(2, 3, 6, 6));
        denoms.setOpaque(false);
        int[] vals = {500_000, 200_000, 100_000, 50_000, 20_000, 10_000};
        double[] paid = {0};
        for (int v : vals) {
            JButton b = chipButton("+" + moneyShort(v));
            b.putClientProperty("val", v);
            b.addActionListener(e -> {
                paid[0] += (int) b.getClientProperty("val");
                updateCashUI(paid[0], amountDue, lblPaid, lblRemain, lblChange);
            });
            denoms.add(b);
        }
        cash.add(denoms, "growx, wrap");

        JPanel tools = new JPanel(new GridLayout(1, 2, 6, 6));
        tools.setOpaque(false);

        JButton btnExact = chipButton("Bằng đúng");
        btnExact.addActionListener(e -> {
            paid[0] = amountDue;
            updateCashUI(paid[0], amountDue, lblPaid, lblRemain, lblChange);
        });

        JButton btnClear = chipButton("Xoá");
        btnClear.addActionListener(e -> {
            paid[0] = 0;
            updateCashUI(paid[0], amountDue, lblPaid, lblRemain, lblChange);
        });

        tools.add(btnExact);
        tools.add(btnClear);
        cash.add(tools, "growx, wrap 10");

        JLabel lblFinal = boldText("Thành tiền: " + formatVND(amountDue));
        cash.add(lblFinal);

        footer.add(cash, "grow, wrap");

        // QR + button
        JPanel action = new JPanel(new MigLayout("wrap, insets 0, gap 8", "[grow,fill]", "[]6[]10[]8[]"));
        action.setOpaque(false);

        // Tiêu đề: nếu đã thanh toán thì đổi sang “ĐÃ THANH TOÁN”
        JLabel lblQrTitle = new JLabel(paidStatus ? "ĐÃ THANH TOÁN" : "Phương thức thanh toán bằng mã QR", SwingConstants.CENTER);
        lblQrTitle.setFont(BASE_FONT.deriveFont(Font.BOLD, 16f));
        lblQrTitle.setForeground(ACCENT2);
        action.add(lblQrTitle, "growx");

        // Hình: nếu đã thanh toán thì hiển thị tick xanh, ngược lại hiện QR
        JLabel QR = new JLabel("", SwingConstants.CENTER);
        QR.setOpaque(false);
        QR.setPreferredSize(new Dimension(220, 220));
        ImageIcon icon = loadIcon(paidStatus ? TICK_IMAGE_PATH : QR_IMAGE_PATH, 220, 220);
        if (icon != null) QR.setIcon(icon);
        else QR.setText("<html><div style='text-align:center;padding:80px 6px;color:#9fb6cc;'>no image</div></html>");
        action.add(QR, "growx");

        JButton btnPrint = primaryButton("In hóa đơn", false);
        JButton btnPay = primaryButton("Thanh toán", true);
        action.add(btnPrint, "growx");
        action.add(btnPay, "growx");

        footer.add(action, "cell 1 0, span 1 4, grow");
        root.add(footer, "growx");

        btnPay.setEnabled(!paidStatus);
        btnPrint.setEnabled(paidStatus);

        // Actions
        btnPay.addActionListener(e -> {
            if (paid[0] < amountDue) {
                double missing = amountDue - paid[0];
                int opt = JOptionPane.showConfirmDialog(this,
                        "Khách đưa còn thiếu " + formatVND(missing) + ".\n"
                                + "Xác nhận hoàn tất (QR/Chuyển khoản/khác)?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (opt != JOptionPane.YES_OPTION) return;
            }
            try {
                orderDAO.thanhToanHoaDon(o.getOrderID());
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                reloadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại: " + ex.getMessage());
            }
        });

        btnPrint.addActionListener(e -> {
            if (!"Thanh toán".equalsIgnoreCase(o.getOrderStatus())) {
                JOptionPane.showMessageDialog(this, "Hóa đơn chưa thanh toán, vui lòng thanh toán trước.");
                return;
            }
            ArrayList<OrderDetailRoom> rooms = odrDAO.getAllOrderDetailRoomByOrderID(o.getOrderID());
            ArrayList<OrderDetailService> services = odsDAO.getAllOrderDetailServiceByOrderID(o.getOrderID());

            Window owner = SwingUtilities.getWindowAncestor(this);
            new FormPayOrder(owner, o, rooms, services, QR_IMAGE_PATH).setVisible(true);
        });

        detailPanel.add(root, BorderLayout.CENTER);
        detailPanel.revalidate(); detailPanel.repaint();
    }

    private JPanel titledCard(String title) {
        JPanel p = new JPanel(new MigLayout("wrap, insets 8, gap 8", "[grow,fill]", ""));
        p.setOpaque(true);
        p.setBackground(CARD_BG);
        var tb = BorderFactory.createTitledBorder(
                new LineBorder(BORDER),
                title, 0, 0,
                BASE_FONT.deriveFont(Font.BOLD),
                ACCENT
        );
        p.setBorder(tb);
        return p;
    }
    private JPanel miniCard() {
        JPanel p = new JPanel(new MigLayout("wrap, gap 4", "[grow,fill]", "[]"));
        p.setOpaque(true);
        p.setBackground(new Color(0x102E4A));
        p.setBorder(new LineBorder(BORDER));
        return p;
    }

    private JLabel text(String s){
        JLabel l = new JLabel(s);
        l.setFont(BASE_FONT);
        l.setForeground(TEXT);
        return l;
    }
    private JLabel boldText(String s){
        JLabel l = text(s);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        l.setForeground(new Color(0xFDE68A));
        return l;
    }
    private JPanel label(String head, String value, boolean highlight){
        JPanel p = new JPanel(new MigLayout("insets 0, gapx 6", "[][grow,fill]", "[]"));
        p.setOpaque(false);
        JLabel h = new JLabel(head);
        h.setFont(BASE_FONT.deriveFont(Font.BOLD));
        h.setForeground(highlight ? ACCENT2 : ACCENT);
        JLabel v = new JLabel(value == null ? "" : value);
        v.setFont(BASE_FONT);
        v.setForeground(TEXT);
        p.add(h); p.add(v, "growx");
        return p;
    }

    private static String format(java.time.LocalDateTime dt, DateTimeFormatter f) {
        return dt == null ? "" : f.format(dt);
    }
    private static String formatVND(double v) {
        return String.format("%,.0f VND", v);
    }

    private static String moneyShort(int v)   {
        return String.format("%,d", v);
    }

    private static boolean isBlank(String s){
        return s == null || s.trim().isEmpty();
    }

    private static String nullToEmpty(String s){
        return s == null ? "" : s;
    }

    private static ImageIcon loadIcon(String path, int w, int h) {
        try {
            ImageIcon raw = new ImageIcon(path);
            if (raw.getIconWidth() <= 0 || raw.getIconHeight() <= 0) return null;
            Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            return null;
        }
    }

    private void updateCashUI(double paid, double due, JLabel lblPaid, JLabel lblRemain, JLabel lblChange){
        lblPaid.setText("Khách đưa: " + formatVND(paid));
        double diff = paid - due;
        if (diff >= 0) {
            lblRemain.setText("Còn thiếu: " + formatVND(0));
            lblChange.setText("Tiền thối: " + formatVND(diff));
        } else {
            lblRemain.setText("Còn thiếu: " + formatVND(-diff));
            lblChange.setText("Tiền thối: " + formatVND(0));
        }
    }


    // textfield style
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
    private JButton chipButton(String text){
        JButton b = new JButton(text);
        b.setFont(BASE_FONT.deriveFont(Font.BOLD, 12f));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(0x1F6FEB));
        b.setBorder(new LineBorder(new Color(0x0B3D91)));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
