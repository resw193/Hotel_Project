package gui.dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import gui.login.main.Application;
import dao.DashboardDAO;
import dao.DashboardDAO.UpcomingBooking;
import dao.OrderStatisticsDAO;
import entity.OrderStatistics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Path2D;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FormDashboard extends JPanel {

    // Table recent
    private JTable tblUpcoming;

    // Revenue card widgets
    private JLabel lblRevenueBig, lblRevenueDelta;
    private RevenueChartPanel revenueChart;

    // Goals & Alerts
    private JTextArea txtGoals, txtAlerts;

    // KPI donut
    private OccDonutPanel occDonut;            // Tỉ lệ lấp đầy
    private SplitDonutPanel bookedFreeDonut;   // Đã đặt / Trống
    private JLabel lbBookedLegend, lbFreeLegend;

    // Today bookings
    private JLabel lblTodayBookings;

    public FormDashboard() {
        setLayout(new MigLayout("wrap,fill,insets 18 22 22 22", "[grow]"));
        setBackground(Color.decode("#0E2237"));

        add(createTopRow(), "growx, gapbottom 12");
        add(createRevenueSection(), "growx, gapbottom 10");
        add(createBottomRow(), "grow, push");

        reloadAll();
    }

    // KPI
    private JComponent createTopRow() {
        JPanel row = new JPanel(new MigLayout("insets 0,gap 12", "[grow][grow][grow]"));
        row.setOpaque(false);

        // Card 1: Tỉ lệ lấp đầy (donut progress)
        occDonut = new OccDonutPanel();
        row.add(cardDonut("Tỉ lệ lấp đầy", occDonut, null), "growx");

        // Card 2: Đã đặt / Trống (donut split + legend)
        bookedFreeDonut = new SplitDonutPanel();
        JPanel legend = new JPanel(new MigLayout("insets 0,gap 12", "[]12[]"));
        legend.setOpaque(false);
        lbBookedLegend = legendLabel("Đã đặt (0)", new Color(0x3498DB));
        lbFreeLegend   = legendLabel("Trống (0)",    new Color(0x7F8C8D));
        legend.add(lbBookedLegend);
        legend.add(lbFreeLegend);
        row.add(cardDonut("Đã đặt / Trống", bookedFreeDonut, legend), "growx");

        // Card 3: Đặt phòng hôm nay (số lớn)
        lblTodayBookings = metricValueLabel();
        row.add(kpiCard("Đặt phòng hôm nay", lblTodayBookings), "growx");

        return row;
    }

    private JPanel cardDonut(String title, JComponent chart, JComponent footer) {
        JPanel card = new JPanel(new MigLayout("insets 14 16 12 16", "[grow]", "[]8[grow]8[]"));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel l1 = new JLabel(title);
        l1.putClientProperty(FlatClientProperties.STYLE, "foreground:#BFD7FF;font:bold +1");
        card.add(l1, "wrap");
        card.add(chart, "w 100%, h 150!");
        if (footer != null) card.add(footer, "growx");
        return card;
    }

    private JLabel legendLabel(String text, Color c) {
        JLabel lb = new JLabel(text);
        lb.setIcon(new LegendIcon(c));
        lb.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF");
        return lb;
    }

    private JPanel kpiCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new MigLayout("insets 14 16 14 16", "[grow]", "[]12[]"));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel l1 = new JLabel(title);
        l1.putClientProperty(FlatClientProperties.STYLE, "foreground:#BFD7FF");
        valueLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +8;foreground:#EAF2FF");
        card.add(l1, "wrap");
        card.add(valueLabel);
        return card;
    }

    private JLabel metricValueLabel() {
        JLabel l = new JLabel("--");
        l.putClientProperty(FlatClientProperties.STYLE, "font:bold +6;foreground:#BFD7FF");
        return l;
    }

    // Revenue chart
    private JComponent createRevenueSection() {
        JPanel card = new JPanel(new MigLayout("insets 14 16 10 16", "[grow]push[]", "[]6[]0[grow]"));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");

        JLabel title = new JLabel("Doanh thu tháng này");
        title.putClientProperty(FlatClientProperties.STYLE, "foreground:#BFD7FF;font:bold +2");

        lblRevenueBig = new JLabel("--");
        lblRevenueBig.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF;font:bold +8");

        lblRevenueDelta = new JLabel("");
        lblRevenueDelta.putClientProperty(FlatClientProperties.STYLE, "foreground:#9AE6B4;font:+0");

        revenueChart = new RevenueChartPanel();
        revenueChart.setPreferredSize(new Dimension(100, 180));

        card.add(title, "split 2");
        card.add(lblRevenueDelta, "al right, wrap");
        card.add(lblRevenueBig, "wrap");
        card.add(revenueChart, "span, growx, h 180!");

        return card;
    }

    // Bottom row
    private JComponent createBottomRow() {
        JPanel row = new JPanel(new MigLayout("insets 0,gap 12", "[grow][grow][grow]"));
        row.setOpaque(false);

        // Tác vụ nhanh
        JPanel quick = new JPanel(new MigLayout("wrap 2,insets 14 16 16 16", "[grow][grow]", "[]12[]12[]12[]"));
        quick.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel tt = new JLabel("Tác vụ nhanh");
        tt.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF;font:bold +2");
        quick.add(tt, "span, wrap");
        quick.add(actionButton("Trang chủ", e -> Application.showForm(new FormTrangChu())), "growx");
        quick.add(actionButton("Quản lý phòng", e -> Application.setSelectedMenu(3, 0)), "growx");
        quick.add(actionButton("Quản lý đặt phòng", e -> Application.setSelectedMenu(4, 0)), "growx");
        quick.add(actionButton("Hủy đặt phòng", e -> Application.setSelectedMenu(5, 0)), "growx");
        quick.add(actionButton("Quản lý hóa đơn", e -> Application.setSelectedMenu(6, 0)), "growx");
        quick.add(actionButton("Khách hàng", e -> Application.setSelectedMenu(7, 0)), "growx");
        quick.add(actionButton("Dịch vụ", e -> Application.setSelectedMenu(8, 0)), "growx");
        quick.add(actionButton("Thông tin cá nhân", e -> Application.setSelectedMenu(9, 0)), "growx");
        quick.add(actionButton("Đăng xuất", e -> Application.logout()), "growx");
        row.add(quick, "grow");

        // --- Recent ---
        JPanel recent = new JPanel(new MigLayout("insets 14 16 16 16", "[grow]", "[]8[grow]"));
        recent.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel lb = new JLabel("Hoạt động gần đây (check-in/check-out sắp tới)");
        lb.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF;font:bold +2");
        recent.add(lb, "wrap");
        tblUpcoming = new JTable(new DefaultTableModel(
                new Object[][]{}, new String[]{"Mã đơn", "Khách", "Phòng", "Check-in", "Check-out", "Trạng thái"}) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        recent.add(new JScrollPane(tblUpcoming), "grow");
        row.add(recent, "grow");

        // Goals + Alerts
        JPanel right = new JPanel(new MigLayout("insets 0, gap 12 0", "[grow]", "[grow 40][grow 60]"));
        right.setOpaque(false);

        JPanel goals = new JPanel(new MigLayout("insets 14 16 16 16", "[grow]", "[]8[grow]"));
        goals.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel gTitle = new JLabel("Mục tiêu & kế hoạch tháng tới");
        gTitle.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF;font:bold +2");
        goals.add(gTitle, "wrap");
        if (txtGoals == null) txtGoals = area();
        goals.add(new JScrollPane(txtGoals), "grow, push");
        right.add(goals, "grow, pushy, hmin 160");

        JPanel alerts = new JPanel(new MigLayout("insets 14 16 16 16", "[grow]", "[]8[grow]"));
        alerts.putClientProperty(FlatClientProperties.STYLE, "arc:16;background:#0F2A47");
        JLabel aTitle = new JLabel("Cảnh báo & Khuyến nghị");
        aTitle.putClientProperty(FlatClientProperties.STYLE, "foreground:#EAF2FF;font:bold +2");
        alerts.add(aTitle, "wrap");
        if (txtAlerts == null) txtAlerts = area();
        alerts.add(new JScrollPane(txtAlerts), "grow, push");
        right.add(alerts, "grow, pushy, hmin 220");

        row.add(right, "grow");

        return row;
    }

    private JTextArea area() {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.putClientProperty(FlatClientProperties.STYLE,
                "background:#12355A;foreground:#EAF2FF;borderWidth:0;arc:12;font:+0");
        return a;
    }

    private JButton actionButton(String text, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.putClientProperty(FlatClientProperties.STYLE,
                "background:#F2C94C;foreground:#0B1F33;arc:14;borderWidth:0;focusWidth:0;innerFocusWidth:0;margin:6,12,6,12");
        b.addActionListener(al);
        return b;
    }


    // Load data
    private void reloadAll() {
        DashboardDAO dashboardDAO = new DashboardDAO();
        int total = dashboardDAO.totalRooms();
        int busy = dashboardDAO.occupiedNow();
        int free = Math.max(total - busy, 0);
        int today = dashboardDAO.bookingsToday();

        // KPIs — donuts + today number
        occDonut.setPercent(total > 0 ? (busy * 100.0 / total) : 0);
        bookedFreeDonut.setValues(busy, free);
        lbBookedLegend.setText("Đã đặt (" + busy + ")");
        lbFreeLegend.setText("Trống (" + free + ")");
        lblTodayBookings.setText(String.valueOf(today));

        // Recent table
        List<UpcomingBooking> list = dashboardDAO.upcomingBookings(7);
        DefaultTableModel m = (DefaultTableModel) tblUpcoming.getModel();
        m.setRowCount(0);
        for (UpcomingBooking u : list) {
            m.addRow(new Object[]{ u.orderCode, u.customer, u.room, u.checkIn, u.checkOut, u.status });
        }

        refreshRevenueAndInsights(total, busy);
    }

    private void refreshRevenueAndInsights(int totalRooms, int busyRooms) {
        // 1) Doanh thu theo ngày trong tháng hiện tại
        OrderStatisticsDAO statsDAO = new OrderStatisticsDAO();
        YearMonth ym = YearMonth.now();
        LocalDate first = ym.atDay(1);
        LocalDate last  = ym.atEndOfMonth();

        List<Double> daily = new ArrayList<>();
        double sumThisMonth = 0;
        for (LocalDate d = first; !d.isAfter(last); d = d.plusDays(1)) {
            OrderStatistics s = statsDAO.thongKeSoLuongHoaDonTheoNgay(d.atStartOfDay());
            double val = (s != null) ? s.getTongThuNhap() : 0;
            daily.add(val);
            sumThisMonth += val;
        }
        revenueChart.setValues(daily);

        // 2) So với tháng trước
        YearMonth prev = ym.minusMonths(1);
        LocalDateTime pStart = prev.atDay(1).atStartOfDay();
        LocalDateTime pEnd   = prev.atEndOfMonth().atTime(23,59,59);
        double sumPrevMonth = statsDAO.thongKeDoanhThuTheoThoiGian(pStart, pEnd);
        double delta = (sumPrevMonth > 0) ? ((sumThisMonth - sumPrevMonth) / sumPrevMonth) * 100.0 : 0;

        lblRevenueBig.setText(formatVN(sumThisMonth));
        lblRevenueDelta.setText(String.format(Locale.US, "%s %.2f%% so với tháng trước",
                (delta >= 0 ? "▲" : "▼"), Math.abs(delta)));
        lblRevenueDelta.putClientProperty(FlatClientProperties.STYLE,
                (delta >= 0 ? "foreground:#9AE6B4" : "foreground:#FF8787"));

        // 3) Goals (tháng tới) & alerts
        double targetRevenue = (sumThisMonth > 0 ? sumThisMonth * 1.10 : sumPrevMonth * 1.15); // mục tiêu +10%
        int targetOcc = 75; // %
        double occNow = (totalRooms > 0) ? (busyRooms * 100.0 / totalRooms) : 0;

        String goals = "• Mục tiêu doanh thu tháng tới: %s→ Còn thiếu: %s để đạt mốc. • Mục tiêu tỉ lệ lấp đầy phòng: %d%% (hiện tại: %.0f%%). • Hành động gợi ý:– Chạy Flash Sale cuối tuần cho phòng trống > 2 đêm.– Gói combo “Phòng + đồ uống” tăng cross-sell dịch vụ.– Remarketing khách hàng có ≥ 10 điểm loyalty.".formatted(formatVN(targetRevenue),formatVN(Math.max(0, targetRevenue - sumThisMonth)),targetOcc, occNow);
        txtGoals.setText(goals);

        List<String> alerts = new ArrayList<>();
        if (occNow < 60) alerts.add("Công suất < 60% hôm nay → cân nhắc giảm giá theo giờ sau 18:00.");
        if (delta < 0) alerts.add("Doanh thu tháng đang thấp hơn tháng trước " + String.format(Locale.US, "%.1f%%.", Math.abs(delta)));
        if (daily.size() >= 7) {
            double last3 = daily.subList(Math.max(0, daily.size() - 3), daily.size()).stream().mapToDouble(Double::doubleValue).sum();
            double prev3 = daily.subList(Math.max(0, daily.size() - 6), Math.max(0, daily.size() - 3)).stream().mapToDouble(Double::doubleValue).sum();
            if (prev3 > 0 && last3 < prev3 * 0.85)
                alerts.add("Doanh thu 3 ngày gần nhất giảm >15% so với 3 ngày trước đó.");
        }
        if (alerts.isEmpty()) alerts.add("Không có cảnh báo nào đáng chú ý. Tiếp tục duy trì chất lượng phục vụ 👍");
        txtAlerts.setText("• " + String.join("\n• ", alerts));
    }

    private String formatVN(double v) {
        java.text.NumberFormat vn = java.text.NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return vn.format(Math.max(0, v));
    }

    private static class RevenueChartPanel extends JPanel {
        private List<Double> values = new ArrayList<>();
        public void setValues(List<Double> v) { this.values = (v == null) ? new ArrayList<>() : v; repaint(); }
        @Override public Dimension getMinimumSize() { return new Dimension(200, 120); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            // background (match cards)
            g2.setColor(new Color(0x0F,0x2A,0x47));
            g2.fillRoundRect(0,0,w,h,16,16);

            if (values == null || values.size() < 2) {
                g2.dispose(); return;
            }

            int left = 8, right = 8, top = 8, bottom = 10;
            int cw = w - left - right, ch = h - top - bottom;

            double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(1);
            double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            if (max == min) max = min + 1;

            // path
            Path2D path = new Path2D.Double();
            int n = values.size();
            for (int i = 0; i < n; i++) {
                double x = left + (i * (cw * 1.0 / (n - 1)));
                double norm = (values.get(i) - min) / (max - min);
                double y = top + ch - norm * ch;
                if (i == 0) path.moveTo(x, y);
                else path.lineTo(x, y);
            }

            // area fill
            Path2D area = (Path2D) path.clone();
            area.lineTo(left + cw, top + ch);
            area.lineTo(left, top + ch);
            area.closePath();

            GradientPaint gp = new GradientPaint(0, top, new Color(26, 188, 156, 130),
                    0, top + ch, new Color(26, 188, 156, 20));
            g2.setPaint(gp);
            g2.fill(area);

            // line
            g2.setColor(new Color(26, 188, 156));
            g2.setStroke(new BasicStroke(2.2f));
            g2.draw(path);

            // last point dot
            double lastX = left + cw;
            double lastNorm = (values.get(n - 1) - min) / (max - min);
            double lastY = top + ch - lastNorm * ch;
            g2.fillOval((int) lastX - 3, (int) lastY - 3, 6, 6);

            g2.dispose();
        }
    }

    private static class OccDonutPanel extends JPanel {
        private double percent = 0;  // 0..100
        public void setPercent(double p){ percent = Math.max(0, Math.min(100, p)); repaint(); }
        @Override public Dimension getPreferredSize(){ return new Dimension(260,150); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w=getWidth(), h=getHeight();
            int size=Math.min(w, h)-20;
            int cx=(w-size)/2, cy=(h-size)/2;

            // track
            g2.setColor(new Color(0x12355A));
            g2.setStroke(new BasicStroke(18f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(cx, cy, size, size);

            // progress
            g2.setColor(new Color(0x1ABC9C));
            g2.drawArc(cx, cy, size, size, 90, (int) (-360*percent/100.0));

            // inner circle (tạo donut)
            int inner=size-36;
            g2.setColor(new Color(0x0F2A47));
            g2.fillOval(cx+18, cy+18, inner, inner);

            // text
            String t = String.format(Locale.US, "%.0f%%", percent);
            g2.setColor(new Color(0xEAF2FF));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
            FontMetrics fm=g2.getFontMetrics();
            int tw=fm.stringWidth(t), th=fm.getAscent();
            g2.drawString(t, (w-tw)/2, (h+th/2)/2);
            g2.dispose();
        }
    }

    private static class SplitDonutPanel extends JPanel {
        private int booked=0, free=0;
        public void setValues(int booked, int free){ this.booked=booked; this.free=free; repaint(); }
        @Override public Dimension getPreferredSize(){ return new Dimension(260,150); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = Math.max(1, booked+free);
            double a1 = 360.0*booked/total;
            double a2 = 360.0*free/total;

            int w=getWidth(), h=getHeight();
            int size=Math.min(w, h)-20;
            int cx=(w-size)/2;
            int cy=(h-size)/2 - 6;

            // sector 1
            g2.setColor(new Color(0x3498DB));
            g2.fillArc(cx, cy, size, size, 90, (int)-a1);

            // sector 2
            g2.setColor(new Color(0x7F8C8D));
            g2.fillArc(cx, cy, size, size, 90-(int)a1, (int)-a2);

            // inner to make donut
            g2.setColor(new Color(0x0F2A47));
            g2.fillOval(cx+18, cy+18, size-36, size-36);

            // text in center: tổng
            g2.setColor(new Color(0xEAF2FF));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            String t = booked + "/" + (booked+free);
            FontMetrics fm=g2.getFontMetrics();
            g2.drawString(t, (w-fm.stringWidth(t))/2, (h+fm.getAscent()/2)/2 - 6);
            g2.dispose();
        }
    }

    private static class LegendIcon implements Icon {
        private final Color color;
        LegendIcon(Color c){ this.color=c; }
        public int getIconWidth(){ return 10; }
        public int getIconHeight(){ return 10; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, 10, 10, 4, 4);
            g2.dispose();
        }
    }
}
