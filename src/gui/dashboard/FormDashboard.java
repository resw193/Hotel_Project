package gui.dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import other.QRCodeUtil;
import gui.login.main.Application;
import DAO.DashboardDAO;
import DAO.DashboardDAO.UpcomingBooking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.net.URI;


public class FormDashboard extends JPanel {

    // labels để cập nhật số liệu
    private JLabel lblOcc, lblBusyFree, lblRevenue, lblTodayBookings;
    private JTable tblUpcoming;

    public FormDashboard() {
        setLayout(new MigLayout("wrap,fill,insets 18 22 22 22", "[grow]"));
        setBackground(Color.decode("#0E2237"));
        add(createBanner(), "growx, gapbottom 14");
        add(createTopRow(), "growx, gapbottom 10");
        add(createBottomRow(), "grow, push");
        reloadStats();
    }

    private JComponent createBanner() {
        JPanel p = new JPanel(new MigLayout("insets 18 20 18 20", "[grow]push[]", "[]6[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "arc:18;background:#102C49");

        JLabel title = new JLabel("MIMOSA HOTEL – Booking & Management");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +8;foreground:#F2C94C");
        JLabel sub = new JLabel("Quản lý đặt phòng nhanh, chính xác & tiện lợi.     Hôm nay: " + LocalDate.now());
        sub.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF");

        // Khối Store + QR
        JPanel store = new JPanel(new MigLayout("wrap, insets 6 6 6 6", "[grow]", "[]8[]8[]"));
        store.setOpaque(false);

        // QR tới trang app chính thức (tự điều hướng OS)
        JLabel qr = new JLabel();
        try {
            BufferedImage img = QRCodeUtil.generate("https://www.booking.com/apps.html", 120);
            qr.setIcon(new ImageIcon(img));
            qr.setToolTipText("Quét để mở trang tải ứng dụng Booking.com");
        } catch (Exception ignored) {}

        // Giữ link cũ, chỉ thêm icon
        JButton appStore = storeButton("App Store", "/other/appstore.jpg",
                e -> openURL("https://apps.apple.com/app/booking-com-hotel-reservations/id367003839"));
        JButton playStore = storeButton("Google Play", "/other/googleplay.jpg",
                e -> openURL("https://play.google.com/store/apps/details?id=com.booking"));

        store.add(new JLabel("Quét để mở App"), "center, wrap");
        store.add(qr, "center, wrap");
        store.add(appStore, "growx");
        store.add(playStore, "growx");

        p.add(title, "wrap");
        p.add(sub, "wrap");
        p.add(new JLabel(), "growx, push");
        p.add(store, "gapleft 10");

        return p;
    }

    private JButton storeButton(String text, String iconPath, java.awt.event.ActionListener l) {
        JButton b = new JButton(text, loadIcon(iconPath));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.putClientProperty(FlatClientProperties.STYLE,
                "background:#F2C94C;foreground:#0B1F33;arc:14;borderWidth:0;focusWidth:0;innerFocusWidth:0;margin:4,10,4,10;iconTextGap:8");
        b.addActionListener(l);
        return b;
    }

    private ImageIcon loadIcon(String path) {
        java.net.URL u = getClass().getResource(path);
        return (u != null) ? new ImageIcon(u) : null;
    }

    private JComponent createTopRow() {
        JPanel row = new JPanel(new MigLayout("insets 0,gap 12", "[grow][grow][grow][grow]"));
        row.setOpaque(false);

        lblOcc = metricValueLabel();
        row.add(kpiCard("Công suất phòng", lblOcc), "growx");

        lblBusyFree = metricValueLabel();
        row.add(kpiCard("Phòng bận / trống", lblBusyFree), "growx");

        lblRevenue = metricValueLabel();
        row.add(kpiCard("Doanh thu tháng", lblRevenue), "growx");

        lblTodayBookings = metricValueLabel();
        row.add(kpiCard("Đặt phòng hôm nay", lblTodayBookings), "growx");

        return row;
    }

    private JPanel kpiCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new MigLayout("insets 14 16 14 16", "[grow]", "[]12[]"));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel l1 = new JLabel(title);
        l1.putClientProperty(FlatClientProperties.STYLE, "foreground:#BFD7FF");
        valueLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +8;foreground:#1ABC9C");
        card.add(l1, "wrap");
        card.add(valueLabel);
        return card;
    }

    private JLabel metricValueLabel() {
        JLabel l = new JLabel("--");
        l.putClientProperty(FlatClientProperties.STYLE, "font:bold +6;foreground:#BFD7FF");
        return l;
    }

    private JComponent createBottomRow() {
        JPanel row = new JPanel(new MigLayout("insets 0,gap 12", "[grow][grow]"));
        row.setOpaque(false);

        // Quick Actions
        JPanel quick = new JPanel(new MigLayout(
                "wrap 2,insets 14 16 16 16", "[grow][grow]", "[]12[]12[]12[]"));
        quick.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel tt = new JLabel("Tác vụ nhanh");
        tt.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF;font:bold +2");
        quick.add(tt, "span, wrap");

        // Hàng 1
        quick.add(actionButton("Trang chủ", e -> Application.showForm(new FormDashboard())), "growx");
        quick.add(actionButton("Quản lý phòng", e -> Application.setSelectedMenu(0, 0)), "growx");
        // Hàng 2
        quick.add(actionButton("Quản lý đặt phòng", e -> Application.setSelectedMenu(1, 0)), "growx");
        quick.add(actionButton("Quản lý hóa đơn", e -> Application.setSelectedMenu(2, 0)), "growx");
        // Hàng 3
        quick.add(actionButton("Khách hàng", e -> Application.setSelectedMenu(3, 1)), "growx"); // subIndex=1 theo MainForm
        quick.add(actionButton("Dịch vụ", e -> Application.setSelectedMenu(4, 0)), "growx");
        // Hàng 4
        quick.add(actionButton("Thông tin cá nhân", e -> Application.setSelectedMenu(5, 0)), "growx");
        quick.add(actionButton("Đăng xuất", e -> Application.logout()), "growx");

        row.add(quick, "grow");

        // Hoạt động gần đây
        JPanel recent = new JPanel(new MigLayout("insets 14 16 16 16", "[grow]", "[]8[grow]"));
        recent.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel lb = new JLabel("Hoạt động gần đây (check-in/checkout sắp tới)");
        lb.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF;font:bold +2");
        recent.add(lb, "wrap");

        tblUpcoming = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Mã đơn", "Khách", "Phòng", "Check-in", "Check-out", "Trạng thái"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        JScrollPane sp = new JScrollPane(tblUpcoming);
        recent.add(sp, "grow");

        row.add(recent, "grow");
        return row;
    }

    private JButton actionButton(String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.putClientProperty(FlatClientProperties.STYLE,
                "background:#F2C94C;foreground:#0B1F33;arc:14;borderWidth:0;focusWidth:0;innerFocusWidth:0;margin:6,12,6,12");
        b.addActionListener(al);
        return b;
    }

    private void openURL(String url) {
        try { Desktop.getDesktop().browse(new URI(url)); } catch (Exception e) { e.printStackTrace(); }
    }

    private void reloadStats() {
        DashboardDAO dao = new DashboardDAO();
        int total = dao.totalRooms();
        int busy = dao.occupiedNow();
        int free = Math.max(total - busy, 0);
        BigDecimal revenue = dao.revenueThisMonth();
        int today = dao.bookingsToday();

        // Công suất = busy / total
        String occ = (total > 0) ? String.format(Locale.US, "%.0f%%", (busy * 100.0 / total)) : "0%";
        lblOcc.setText(occ);
        lblBusyFree.setText(busy + " / " + free);

        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        lblRevenue.setText(vn.format(revenue));
        lblTodayBookings.setText(String.valueOf(today));

        // Bảng hoạt động
        List<UpcomingBooking> list = dao.upcomingBookings(7);
        DefaultTableModel m = (DefaultTableModel) tblUpcoming.getModel();
        m.setRowCount(0);
        for (UpcomingBooking u : list) {
            m.addRow(new Object[]{
                    u.orderCode, u.customer, u.room, u.checkIn, u.checkOut, u.status
            });
        }
    }
}
