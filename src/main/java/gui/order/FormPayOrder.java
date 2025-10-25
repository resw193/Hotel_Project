package gui.order;

import entity.Order;
import entity.OrderDetailRoom;
import entity.OrderDetailService;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class FormPayOrder extends JDialog {

    private static final Color SHELL_BG   = new Color(0x0B1F33);
    private static final Color SHELL_BRD  = new Color(0x274A6B);

    private static final Color PAPER_BG   = Color.WHITE;
    private static final Color PAPER_TXT  = new Color(0x222222);
    private static final Color PAPER_DIM  = new Color(0x666666);

    private static final Font MONO       = new Font("Consolas", Font.PLAIN, 13);
    private static final Font MONO_BOLD  = MONO.deriveFont(Font.BOLD);
    private static final Font TITLE      = new Font("Consolas", Font.BOLD, 16);

    public FormPayOrder(Window owner, Order order, List<OrderDetailRoom> roomLines, List<OrderDetailService> serviceLines, String qrImagePath) {
        super(owner, "HÓA ĐƠN THANH TOÁN – " + (order == null ? "" : order.getOrderID()), ModalityType.APPLICATION_MODAL);

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(SHELL_BG);

        // giấy hóa đơn
        JComponent paperWrap = buildPaper(order, roomLines, serviceLines, qrImagePath);

        // scrollpane
        JScrollPane sc = new JScrollPane(paperWrap);
        sc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sc.getViewport().setBackground(SHELL_BG);
        sc.setBorder(new LineBorder(SHELL_BRD));

        // tính chiều rộng mong muốn của khung = chiều rộng hóa đơn + viền
        int paperW = Math.max(600, paperWrap.getPreferredSize().width);
        int frameH = 740;
        sc.setPreferredSize(new Dimension(paperW + 8, frameH));

        shell.add(sc, BorderLayout.CENTER);

        // close button
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton close = new JButton("Đóng");
        close.setBackground(new Color(0x2563EB));
        close.setForeground(Color.WHITE);
        close.setFocusPainted(false);
        close.addActionListener(e -> dispose());
        actions.add(close);
        shell.add(actions, BorderLayout.SOUTH);

        setContentPane(shell);

        pack();
        setLocationRelativeTo(owner);
    }

    private JComponent buildPaper(Order order, List<OrderDetailRoom> roomLines, List<OrderDetailService> serviceLines, String qrImagePath) {
        int paperWidth = 640;
        JPanel paper = new JPanel(new MigLayout("wrap, insets 12 16 16 16", "[grow,fill]", ""));
        paper.setBackground(PAPER_BG);
        paper.setBorder(new EmptyBorder(10, 10, 10, 10));
        paper.setPreferredSize(new Dimension(paperWidth, 740));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Header
        JLabel title = new JLabel("HÓA ĐƠN", SwingConstants.CENTER);
        title.setFont(TITLE);
        title.setForeground(PAPER_TXT);
        paper.add(title, "growx");

        // Staff / Order / Customer
        JPanel top = new JPanel(new MigLayout("insets 0, gap 0", "[grow][grow]", "[]"));
        top.setOpaque(false);
        top.add(lbl("Nhân viên", MONO_BOLD, PAPER_DIM));
        top.add(lbl(order != null && order.getEmployee() != null ? order.getEmployee().getFullName() : "", MONO, PAPER_TXT), "wrap");

        top.add(lbl("Khách hàng", MONO_BOLD, PAPER_DIM));
        top.add(lbl(order != null && order.getCustomer() != null ? order.getCustomer().getFullName() : "", MONO, PAPER_TXT), "wrap");

        top.add(lbl("Mã hóa đơn", MONO_BOLD, PAPER_DIM));
        top.add(lbl(order != null ? order.getOrderID() : "", MONO, PAPER_TXT), "wrap");

        top.add(lbl("Ngày lập hóa đơn", MONO_BOLD, PAPER_DIM));
        top.add(lbl(order != null && order.getOrderDate() != null ? formatter.format(order.getOrderDate()) : "", MONO, PAPER_TXT));
        paper.add(top, "growx");
        paper.add(new DashedDivider(), "growx, gaptop 6, gapbottom 6");

        // Tên | Số lượng | Giá | Tổng tiền
        paper.add(rowHeader("Tên", "Số lượng", "Giá", "Tổng tiền"), "growx");

        // Tính tiền
        double roomsSubtotal = 0d;
        double servicesSubtotal = 0d;

        // Phòng và dịch vụ
        if (roomLines != null) {
            for (OrderDetailRoom r : roomLines) {
                String roomName = " " + safe(() -> r.getRoom().getDescription());
                String type = " (" + nullToEmpty(r.getBookingType()) + ")";
                double fee = r.getRoomFee();

                if (fee <= 0) {
                    // fee = 0 -> tính lại
                    fee = calcRoomFeeLikeDB(r);
                }
                roomsSubtotal += fee;

                paper.add(rowItem(roomName + type, "1", money(fee), money(fee)), "growx");

                // Check in -> Check out
                String timeLine = "  " +
                        (r.getCheckInDate()  != null ? formatter.format(r.getCheckInDate())  : "") +
                        "  →  " +
                        (r.getCheckOutDate() != null ? formatter.format(r.getCheckOutDate()) : "");
                paper.add(lbl(timeLine, MONO, PAPER_DIM), "span 4, growx");
            }
        }

        // Dịch vụ
        if (serviceLines != null) {
            // gộp theo tên dịch vụ
            Map<String, ServiceAgg> agg = new LinkedHashMap<>();
            for (OrderDetailService s : serviceLines) {
                String name = (s.getService() == null ? "Dịch vụ" : s.getService().getServiceName());
                int quantity = Math.max(0, s.getQuantity());
                double unit = (s.getService() != null) ? s.getService().getPrice() : 0d;
                ServiceAgg a = agg.computeIfAbsent(name, k -> new ServiceAgg());
                a.qty   += quantity;
                a.unit   = unit;
                a.total += unit * quantity;
            }
            for (Map.Entry<String, ServiceAgg> e : agg.entrySet()) {
                ServiceAgg a = e.getValue();
                servicesSubtotal += a.total;
                paper.add(rowItem(e.getKey(), String.valueOf(a.qty), money(a.unit), money(a.total)), "growx");
            }
        }

        paper.add(new DashedDivider(), "growx, gaptop 4, gapbottom 4");

        // Tổng / VAT / Giảm giá / Thành tiền
        double tong = roomsSubtotal + servicesSubtotal;
        double vat = tong * 0.10;
        double discountRate = (order == null || order.getPromotion() == null) ? 0D
                : order.getPromotion().getDiscount() / 100.0;
        String promoTxt = (order == null || order.getPromotion() == null) ? "0%" :
                ((int) order.getPromotion().getDiscount()) + "%";
        double thanhTien = tong + vat - (tong * discountRate);

        paper.add(rowSum("Tổng", money(tong)), "growx");
        paper.add(rowSum("VAT (10%)", money(vat)), "growx");
        paper.add(rowSum("Discount (" + promoTxt + ")", "-" + money(tong * discountRate)), "growx");
        paper.add(new SolidDivider(), "growx, gapy 2");
        paper.add(rowGrand("THÀNH TIỀN", money(thanhTien)), "growx");

        // QR
        JLabel qrTitle = lbl("Quét mã để thanh toán", MONO_BOLD, PAPER_DIM);
        qrTitle.setHorizontalAlignment(SwingConstants.CENTER);
        paper.add(qrTitle, "gapy 6, growx");

        JLabel qr = new JLabel("", SwingConstants.CENTER);
        qr.setOpaque(false);
        qr.setPreferredSize(new Dimension(240, 240));
        ImageIcon icon = loadIcon(qrImagePath, 240, 240);
        if (icon != null) qr.setIcon(icon); else qr.setText("QR");
        paper.add(qr, "al center, gapbottom 6");

        JLabel thanks = lbl("Cảm ơn quý khách!", MONO_BOLD, PAPER_TXT);
        thanks.setHorizontalAlignment(SwingConstants.CENTER);
        paper.add(thanks, "growx");

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(SHELL_BG);
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(PAPER_BG);
        holder.setBorder(new LineBorder(new Color(0xDDDDDD)));
        holder.add(paper);
        wrap.add(holder);

        return wrap;
    }

    private JLabel lbl(String s, Font f, Color c) {
        JLabel l = new JLabel(s);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    private JPanel rowHeader(String c1, String c2, String c3, String c4) {
        JPanel p = new JPanel(new MigLayout("insets 0, gap 8", "[grow]30[][120!][140!]", "[]"));
        p.setOpaque(false);
        p.add(lbl(c1, MONO_BOLD, PAPER_DIM), "growx");
        p.add(lbl(c2, MONO_BOLD, PAPER_DIM), "al center");
        p.add(lbl(c3, MONO_BOLD, PAPER_DIM), "al right");
        p.add(lbl(c4, MONO_BOLD, PAPER_DIM), "al right");
        return p;
    }

    private JPanel rowItem(String name, String qty, String price, String total) {
        JPanel p = new JPanel(new MigLayout("insets 0, gap 8", "[grow]30[][120!][140!]", "[]"));
        p.setOpaque(false);
        p.add(lbl(name, MONO, PAPER_TXT), "growx");
        p.add(lbl(qty, MONO, PAPER_TXT), "al center");
        p.add(lbl(price, MONO, PAPER_TXT), "al right");
        p.add(lbl(total, MONO, PAPER_TXT), "al right");
        return p;
    }

    private JPanel rowSum(String label, String value) {
        JPanel p = new JPanel(new MigLayout("insets 0, gap 8", "[grow][140!]", "[]"));
        p.setOpaque(false);
        p.add(lbl(label, MONO, PAPER_DIM), "growx");
        p.add(lbl(value, MONO, PAPER_TXT), "al right");
        return p;
    }

    private JPanel rowGrand(String label, String value) {
        JPanel p = new JPanel(new MigLayout("insets 0, gap 8", "[grow][140!]", "[]"));
        p.setOpaque(false);
        p.add(lbl(label, MONO_BOLD, PAPER_TXT), "growx");
        p.add(lbl(value, MONO_BOLD, PAPER_TXT), "al right");
        return p;
    }

    private static class DashedDivider extends JComponent {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0xBBBBBB));
            float[] dash = {4f, 4f};
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
            int y = getHeight() / 2; g2.drawLine(0, y, getWidth(), y); g2.dispose();
        }
        @Override public Dimension getPreferredSize() { return new Dimension(1, 8); }
    }

    private static class SolidDivider extends JComponent {
        @Override protected void paintComponent(Graphics g) {
            g.setColor(new Color(0xCCCCCC)); int y = getHeight()/2; g.drawLine(0, y, getWidth(), y);
        }
        @Override public Dimension getPreferredSize() { return new Dimension(1, 8); }
    }

    private static double calcRoomFeeLikeDB(OrderDetailRoom r) {
        try {
            if (r == null || r.getRoom() == null || r.getRoom().getRoomType() == null) return 0;
            LocalDateTime in  = r.getCheckInDate();
            LocalDateTime out = r.getCheckOutDate();
            if (in == null || out == null) return 0;

            long duration = Math.max(0, Duration.between(in, out).toHours());
            String typeName = nvl(r.getRoom().getRoomType().getTypeName());
            double pricePerHour  = r.getRoom().getRoomType().getPricePerHour();
            double pricePerNight = r.getRoom().getRoomType().getPricePerNight();
            double pricePerDay   = r.getRoom().getRoomType().getPricePerDay();

            double price = 0, lateFee = 0;
            String booking = nvl(r.getBookingType());

            if (booking.equals("Giờ")) {
                double inc = typeName.equals("Phòng đơn") ? 10000d : 20000d;
                price = pricePerHour + Math.max(0, duration - 1) * inc;
            }
            else if (booking.equals("Đêm")) {
                price = pricePerNight;
                if (duration > 13) lateFee = (duration - 13) * 20000d;
                price += lateFee;
            }
            else if (booking.equals("Ngày")) {
                price = pricePerDay;
                if (duration > 24) lateFee = (duration - 24) * (typeName.equals("Phòng đơn") ? 20000d : 30000d);
                price += lateFee;
            }
            else {
                price = r.getRoomFee();
            }

            return price;
        } catch (Exception ex) {
            return Math.max(0, r.getRoomFee());
        }
    }

    // data storage
    private static class ServiceAgg {
        int qty; double unit; double total;
    }

    private static String money(double v) {
        return String.format("%,.0f", v);
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    private static <T> String safe(java.util.concurrent.Callable<T> c) {
        try {
            T t = c.call();
            return t == null ? "" : String.valueOf(t);
        } catch (Exception e) {
            return "";
        }
    }

    private static ImageIcon loadIcon(String path, int w, int h) {
        try {
            java.net.URL url = FormPayOrder.class.getResource(path);
            if (url == null) return null;
            ImageIcon raw = new ImageIcon(url);
            Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            return null;
        }
    }
}
