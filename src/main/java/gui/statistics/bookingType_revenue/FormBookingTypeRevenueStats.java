package gui.statistics.bookingType_revenue;

import bus.BookingTypeRevenueBUS;
import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;
import entity.BookingTypeRevenue;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FormBookingTypeRevenueStats extends JPanel {

    private static final Color BG        = new Color(0x0B1F33);
    private static final Color PANEL_TOP = new Color(0x0E2942);
    private static final Color BORDER    = new Color(0x274A6B);
    private static final Color TEXT      = new Color(0xE9EEF6);
    private static final Color ACCENT    = new Color(0x22D3EE);

    private JDateChooser dcStart;
    private JDateChooser dcEnd;
    private JLabel lbSummary = new JLabel(" ");

    private BookingTypeRevenueTableModel tableModel = new BookingTypeRevenueTableModel();
    private JTable table = new JTable(tableModel);

    private BookingTypeRevenueBUS bookingTypeRevenueBUS = new BookingTypeRevenueBUS();

    public FormBookingTypeRevenueStats() {
        setLayout(new MigLayout("fill, wrap, insets 0", "[grow]", "[grow 0][grow]"));
        setBackground(BG);

        JPanel top = new JPanel(new MigLayout("insets 12 16 12 16", "[][180!]16[][180!]push[]", "[]"));
        top.setBackground(PANEL_TOP);

        JLabel lbStart = label("Bắt đầu");
        JLabel lbEnd   = label("Kết thúc");

        dcStart = dateChooser();
        dcEnd   = dateChooser();

        JButton btnReload = new JButton("Thống kê");
        stylePrimary(btnReload);

        top.add(lbStart); top.add(dcStart);
        top.add(lbEnd);  top.add(dcEnd);
        top.add(btnReload);
        add(top, "growx");

        // Table
        table.setRowHeight(30);
        table.setGridColor(new Color(0x13314A));
        table.setBorder(new LineBorder(BORDER));
        table.setFillsViewportHeight(true);

        // Zebra + padding
        table.setDefaultRenderer(Object.class, table.getDefaultRenderer(Object.class));
        JTable zebra = new JTable(tableModel) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(0x0E253D) : new Color(0x0C2136));
                    c.setForeground(TEXT);
                } else {
                    c.setBackground(new Color(0x10344F));
                    c.setForeground(Color.WHITE);
                }
                if (c instanceof JComponent jc)
                    jc.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
                return c;
            }
        };
        // copy settings to zebra instance
        zebra.setRowHeight(table.getRowHeight());
        zebra.setGridColor(table.getGridColor());
        zebra.setBorder(table.getBorder());
        zebra.setFillsViewportHeight(true);

        // Header style + center titles
        JTableHeader h = zebra.getTableHeader();
        h.setBackground(new Color(0x102A43));
        h.setForeground(ACCENT);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Align columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        // STT
        zebra.getColumnModel().getColumn(0).setPreferredWidth(60);
        zebra.getColumnModel().getColumn(0).setCellRenderer(center);

        // Số lượt đặt
        zebra.getColumnModel().getColumn(2).setPreferredWidth(100);
        zebra.getColumnModel().getColumn(2).setCellRenderer(center);

        // Doanh thu
        NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
        zebra.getColumnModel().getColumn(3).setPreferredWidth(160);
        zebra.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer(vnd, right));

        JScrollPane sp = new JScrollPane(zebra);
        sp.getViewport().setBackground(BG);
        sp.setBorder(new LineBorder(BORDER));
        add(sp, "grow");

        lbSummary.putClientProperty(FlatClientProperties.STYLE, "foreground:#E9EEF6");
        add(lbSummary, "gapleft 16, gaptop 6");

        // Default rang
        LocalDate today = LocalDate.now();
        setChooserDate(dcStart, Date.from(today.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        setChooserDate(dcEnd,   Date.from(today.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant()));

        // Events
        PropertyChangeListener pcl = evt -> {
            if ("date".equals(evt.getPropertyName())) load();
        };
        dcStart.getDateEditor().addPropertyChangeListener(pcl);
        dcEnd.getDateEditor().addPropertyChangeListener(pcl);
        btnReload.addActionListener(e -> load());

        // load
        load();
    }

    private JLabel label(String s) {
        JLabel lb = new JLabel(s);
        lb.setForeground(TEXT);
        return lb;
    }

    private void stylePrimary(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#F5C452; foreground:#0B1F33; borderColor:#F1B93A; hoverBackground:#FFD36E;");
    }

    private static JDateChooser dateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy");
        dc.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,10,6,10;");
        return dc;
    }

    private static void setChooserDate(JDateChooser c, Date d) { c.setDate(d); }

    private static LocalDateTime startOfDay(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
    }

    private static LocalDateTime endOfDay(Date d) {
        if (d == null) return null;
        LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ld.atTime(LocalTime.of(23, 59, 59));
    }

    private void load() {
        LocalDateTime start = startOfDay(dcStart.getDate());
        LocalDateTime end   = endOfDay(dcEnd.getDate());
        if (start == null || end == null) return;

        if (end.isBefore(start)) {
            JOptionPane.showMessageDialog(this, "Khoảng thời gian không hợp lệ (Kết thúc < Bắt đầu).");
            return;
        }

        List<BookingTypeRevenue> data = bookingTypeRevenueBUS.stats(start, end);
        tableModel.setData(new ArrayList<>(data));

        int totalCount = data.stream().mapToInt(BookingTypeRevenue::getSoLuot).sum();
        double totalRev = data.stream().mapToDouble(BookingTypeRevenue::getRoomRevenue).sum();
        String f = NumberFormat.getCurrencyInstance(new Locale("vi","VN")).format(totalRev);
        lbSummary.setText("Tổng lượt: " + totalCount + "   |   Doanh thu phòng: " + f);
    }

    // format tiền tệ
    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private final NumberFormat fmt;
        private final DefaultTableCellRenderer base;
        CurrencyRenderer(NumberFormat fmt, DefaultTableCellRenderer base) { this.fmt = fmt; this.base = base; }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) ((JLabel) c).setText(fmt.format(((Number) value).doubleValue()));
            return c;
        }
    }
}
