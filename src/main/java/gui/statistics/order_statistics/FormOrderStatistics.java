package gui.statistics.order_statistics;

import bus.OrderStatisticsBUS;
import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;
import entity.DailyDetail;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.*;
import java.util.Date;
import java.util.Locale;

public class FormOrderStatistics extends JPanel {

    private static final Color BG         = new Color(0x0B1F33);
    private static final Color PANEL_TOP  = new Color(0x0E2942);
    private static final Color BORDER     = new Color(0x274A6B);
    private static final Color TEXT       = new Color(0xE6F1FF);
    private static final Color ACCENT     = new Color(0x22D3EE);

    private OrderStatisticsBUS orderStatisticsBUS = new OrderStatisticsBUS();

    private OrderStatisticsTableModel orderStatisticsTableModel = new OrderStatisticsTableModel();
    private JTable table;

    private JDateChooser dcDate;
    private final NumberFormat VND = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

    public FormOrderStatistics() {
        setLayout(new MigLayout("fill, wrap, insets 0", "[grow]", "[grow 0][grow]"));
        setBackground(BG);

        JPanel top = new JPanel(new MigLayout("insets 10 16 10 16", "push[][180!]16[]", "[]"));
        top.setBackground(PANEL_TOP);

        JLabel lbDate = new JLabel("Ngày thống kê");
        lbDate.setForeground(TEXT);

        dcDate = dateChooser();

        JButton btn = new JButton("Thống kê");
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#0EA5E9; foreground:#FFFFFF; borderColor:#0B80B0; hoverBackground:#22D3EE;");

        top.add(lbDate);
        top.add(dcDate);
        top.add(btn);
        add(top, "growx");

        // TABLE
        table = new JTable(orderStatisticsTableModel) {
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
        table.setRowHeight(34);
        table.setGridColor(new Color(0x13314A));
        table.setBorder(new LineBorder(BORDER));
        table.setFillsViewportHeight(true);

        // header
        JTableHeader h = table.getTableHeader();
        h.setBackground(new Color(0x102A43));
        h.setForeground(ACCENT);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // align & format
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(0).setCellRenderer(center); // Số lượng
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setCellRenderer(new CurrencyRenderer(VND, right)); // Thu nhập phòng
        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setCellRenderer(new CurrencyRenderer(VND, right)); // Thu nhập dịch vụ
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer(VND, right)); // Tổng thu nhập

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(BG);
        sp.setBorder(new LineBorder(BORDER));
        add(sp, "grow");

        // default: hôm nay
        LocalDate today = LocalDate.now();
        setChooserDate(dcDate, Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // events
        PropertyChangeListener pcl = evt -> { if ("date".equals(evt.getPropertyName())) loadData(); };
        dcDate.getDateEditor().addPropertyChangeListener(pcl);
        btn.addActionListener(e -> loadData());

        // loadData
        loadData();
    }

    private void loadData() {
        LocalDate date = toLocalDate(dcDate.getDate());
        if (date == null) return;

        DailyDetail dailyDetail = orderStatisticsBUS.getDailyDetail(date);
        orderStatisticsTableModel.setData(dailyDetail);
    }

    private static JDateChooser dateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy");
        dc.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,10,6,10;");
        return dc;
    }

    private static void setChooserDate(JDateChooser c, Date d) {
        c.setDate(d);
    }

    private static LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        private final NumberFormat fmt;
        private final DefaultTableCellRenderer base;

        CurrencyRenderer(NumberFormat fmt, DefaultTableCellRenderer base) {
            this.fmt = fmt; this.base = base;
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) {
                ((JLabel) c).setText(fmt.format(((Number) value).doubleValue()));
            }
            return c;
        }
    }
}
