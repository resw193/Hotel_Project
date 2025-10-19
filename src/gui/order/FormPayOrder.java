package gui.order;

import Entity.Order;
import Entity.OrderDetailRoom;
import Entity.OrderDetailService;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormPayOrder extends JDialog {

    private static final Color BG = new Color(0x0B1F33);
    private static final Color CARD_BG = new Color(0x0F2A44);
    private static final Color BORDER = new Color(0x274A6B);
    private static final Color TEXT = new Color(0xE6F1FF);
    private static final Color ACCENT = new Color(0x22D3EE);
    private static final Color ACCENT2 = new Color(0x6D28D9);
    private static final Font  BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public FormPayOrder(Window owner, Order order, List<OrderDetailRoom> roomLines, List<OrderDetailService> serviceLines, String qrImagePath) {
        super(owner, "Phiếu xác nhận đặt phòng – " + (order == null ? "": order.getOrderID()), ModalityType.APPLICATION_MODAL);
        setSize(820, 760);
        setLocationRelativeTo(owner);

        JPanel body = new JPanel(new MigLayout("wrap, insets 12, gap 8", "[grow,fill]", ""));
        body.setBackground(BG);
        JScrollPane sc = new JScrollPane(body);
        sc.getViewport().setBackground(BG);
        sc.setBorder(new LineBorder(BORDER));
        add(sc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        JLabel title = new JLabel("PHIẾU XÁC NHẬN ĐẶT PHÒNG", SwingConstants.CENTER);
        title.setForeground(ACCENT);
        title.setFont(BASE_FONT.deriveFont(Font.BOLD, 18f));
        body.add(title, "gapy 6");

        if (order != null) {
            JPanel info = card("Thông tin hóa đơn");
            info.add(row("Mã hóa đơn: ", order.getOrderID(), true));
            info.add(row("Ngày lập: ", order.getOrderDate() == null ? "" : formatter.format(order.getOrderDate()), false));
            info.add(row("Nhân viên: ", order.getEmployee() == null ? "" : order.getEmployee().getFullName(), false));
            info.add(row("Khách hàng: ", order.getCustomer() == null ? "" : order.getCustomer().getFullName(), false));
            info.add(row("Trạng thái: ", order.getOrderStatus(), false));
            body.add(info);
        }

        JPanel detail = card("Chi tiết");

        // <RoomID, <ServiceName, ServiceQuantity>>
        Map<String, Map<String, Integer>> svcByRoom = serviceLines == null ? Map.of()
                : serviceLines.stream().collect(Collectors.groupingBy(
                s -> s.getRoom() == null ? "" : s.getRoom().getRoomID(),
                Collectors.groupingBy(s -> s.getService().getServiceName(),
                        Collectors.summingInt(OrderDetailService::getQuantity))
        ));

        if (roomLines != null && !roomLines.isEmpty()) {
            for (OrderDetailRoom r : roomLines) {
                JPanel card = new JPanel(new MigLayout("wrap, gap 3", "[grow,fill]"));
                card.setOpaque(true);
                card.setBackground(new Color(0x102E4A));
                card.setBorder(new LineBorder(BORDER));

                String type = (r.getRoom() != null && r.getRoom().getRoomType() != null) ? r.getRoom().getRoomType().getTypeName() : "";

                card.add(t("• " + (r.getRoom() == null ? "" : r.getRoom().getDescription())));
                card.add(t("Loại phòng: " + type));
                card.add(t("Đặt: " + format(r.getBookingDate(), formatter)
                        + " | Check-in: " + format(r.getCheckInDate(), formatter)
                        + " | Check-out: " + format(r.getCheckOutDate(), formatter)));
                card.add(t("Hình thức: " + check(r.getBookingType())));

                String roomId = (r.getRoom() == null ? "" : r.getRoom().getRoomID());
                Map<String, Integer> svc = svcByRoom.get(roomId);
                if (svc != null && !svc.isEmpty()) {
                    card.add(t("Dịch vụ:"));
                    for (Map.Entry<String, Integer> e : svc.entrySet()) {
                        card.add(t("   - " + e.getKey() + " × " + e.getValue()));
                    }
                }
                detail.add(card);
            }
        }
        else {
            detail.add(t("Không có chi tiết."));
        }
        body.add(detail);

        // thành tiền, qr
        JPanel summary = card("Thanh toán");
        summary.setLayout(new MigLayout("insets 8, gap 8", "[grow][260!]", "[][][]push[]"));

        double total = order == null ? 0 : order.getTotal();
        double vat = total * 0.10;
        double discountRate = (order == null || order.getPromotion() == null) ? 0D : order.getPromotion().getDiscount() / 100.0;
        String promoTxt = (order == null || order.getPromotion() == null) ? "Không có" : ((int)order.getPromotion().getDiscount()) + "%";
        double finalAmount = total + vat - (total * discountRate);

        summary.add(t("Tổng: " + money(total)), "wrap");
        summary.add(t("VAT (10%): " + money(vat)), "wrap");
        summary.add(t("Khuyến mãi: " + promoTxt), "wrap");
        JLabel lFinal = t("Thành tiền: " + money(finalAmount));
        lFinal.setFont(lFinal.getFont().deriveFont(Font.BOLD));
        lFinal.setForeground(new Color(0xFDE68A));
        summary.add(lFinal, "wrap");

        JPanel qrPanel = new JPanel(new MigLayout("wrap, insets 0, gap 6", "[grow,fill]", "[]6[]"));
        qrPanel.setOpaque(false);
        JLabel lbl = t("Phương thức thanh toán bằng mã QR");
        lbl.setForeground(ACCENT2);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        qrPanel.add(lbl, "growx");

        JLabel qr = new JLabel("", SwingConstants.CENTER);
        qr.setOpaque(false);
        qr.setPreferredSize(new Dimension(220, 220));
        ImageIcon icon = loadIcon(qrImagePath, 220, 220);
        if (icon != null)
            qr.setIcon(icon);
        else
            qr.setText("<html><div style='text-align:center;padding:80px 6px;color:#9fb6cc;'>QR<br>no image</div></html>");

        qrPanel.add(qr, "growx");
        summary.add(qrPanel, "cell 1 0, span 1 4, grow");
        body.add(summary, "growx");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton close = new JButton("Đóng");
        close.setBackground(new Color(0x2563EB));
        close.setForeground(Color.WHITE);
        close.setFocusPainted(false);
        actions.add(close);
        close.addActionListener(e -> dispose());
        body.add(actions);
    }

    private JPanel card(String title) {
        JPanel p = new JPanel(new MigLayout("wrap, insets 8, gap 8", "[grow,fill]", ""));
        p.setOpaque(true);
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createTitledBorder(new LineBorder(BORDER), title, 0, 0,
                new Font("Segoe UI", Font.BOLD, 13), ACCENT));
        return p;
    }
    private JPanel row(String head, String val, boolean hl){
        JPanel p = new JPanel(new MigLayout("insets 0, gapx 6", "[][grow,fill]", "[]"));
        p.setOpaque(false);
        JLabel h = new JLabel(head);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setForeground(hl ? ACCENT2 : ACCENT);
        JLabel v = t(val==null?"":val);
        p.add(h); p.add(v, "growx");
        return p;
    }
    private JLabel t(String s){
        JLabel l = new JLabel(s);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(TEXT);
        return l;
    }

    private static String format(java.time.LocalDateTime dt, DateTimeFormatter f) {
        return dt == null ? "" : f.format(dt);
    }

    private static String check(String s){
        return s == null ? "" : s;
    }

    private static String money(double v) {
        return String.format("%,.0f", v);
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
}
