package gui.statistics.service_statistics;

import bus.ServiceRankingBUS;
import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;
import entity.ServiceRanking;
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

public class FormServiceStatistics extends JPanel {

    private static final Color BG         = new Color(0x0B1F33);
    private static final Color PANEL_TOP  = new Color(0x0E2942);
    private static final Color BORDER     = new Color(0x274A6B);
    private static final Color TEXT       = new Color(0xE6F1FF);
    private static final Color ACCENT     = new Color(0x22D3EE);

    private ServiceRankingBUS serviceRankingBUS = new ServiceRankingBUS();

    private ServiceRankingTableModel tableModel = new ServiceRankingTableModel();
    private JTable table;

    private JDateChooser dcStart, dcEnd;
    private final NumberFormat VND = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // Chart panel
    private TopServiceBarChart chartPanel = new TopServiceBarChart();
    private JLabel lbChartTitle = new JLabel("Top dịch vụ được sử dụng nhiều nhất");

    public FormServiceStatistics() {
        setLayout(new MigLayout("fill, wrap, insets 0", "[grow]", "[grow 0][grow]"));
        setBackground(BG);

        // ====== TOP BAR ======
        JPanel top = new JPanel(new MigLayout("insets 10 16 10 16", "[][180!]16[][180!]push[]", "[]"));
        top.setBackground(PANEL_TOP);

        JLabel lbStart = new JLabel("Bắt đầu");
        lbStart.setForeground(TEXT);
        JLabel lbEnd = new JLabel("Kết thúc");
        lbEnd.setForeground(TEXT);

        dcStart = dateChooser();
        dcEnd = dateChooser();

        JButton btnReload = new JButton("Thống kê");
        btnReload.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#0EA5E9; foreground:#FFFFFF; borderColor:#0B80B0; hoverBackground:#22D3EE;");

        top.add(lbStart);
        top.add(dcStart);
        top.add(lbEnd);
        top.add(dcEnd);
        top.add(btnReload);
        add(top, "growx");

        // table (trái) + chart (phải)
        JPanel body = new JPanel(new MigLayout("insets 0 12 12 12", "[grow][300!]", "[grow]"));
        body.setBackground(BG);
        add(body, "grow");

        // TABLE
        table = new JTable(tableModel) {
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
        table.setRowHeight(30);
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
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer(VND, right));

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(BG);
        sp.setBorder(new LineBorder(BORDER));

        // CHART box (phải)
        JPanel chartBox = new JPanel(new MigLayout("insets 10 10 10 10, fill", "[grow]", "[][grow]"));
        chartBox.setBackground(new Color(0x0E253D));
        chartBox.setBorder(new LineBorder(BORDER));
        lbChartTitle.setForeground(new Color(0xA7F3D0));
        lbChartTitle.setFont(lbChartTitle.getFont().deriveFont(Font.BOLD, 14f));

        chartBox.add(lbChartTitle, "split 2, left, gapbottom 6");
        JLabel lbHint = new JLabel("");
        lbHint.setForeground(new Color(0x7DD3FC));
        lbHint.setFont(lbHint.getFont().deriveFont(Font.PLAIN, 12f));
        chartBox.add(lbHint, "wrap");
        chartBox.add(chartPanel, "grow");

        // add vào body
        body.add(sp, "grow");
        body.add(chartBox, "grow");

        // default range: đầu tháng -> hôm nay
        LocalDate today = LocalDate.now();
        setChooserDate(dcStart, Date.from(today.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        setChooserDate(dcEnd, Date.from(today.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant()));

        // events
        PropertyChangeListener pcl = evt -> { if ("date".equals(evt.getPropertyName())) loadData(); };
        dcStart.getDateEditor().addPropertyChangeListener(pcl);
        dcEnd.getDateEditor().addPropertyChangeListener(pcl);
        btnReload.addActionListener(e -> loadData());

        // first load
        loadData();
    }

    private void loadData() {
        LocalDateTime start = startOfDay(dcStart.getDate());
        LocalDateTime end = endOfDay(dcEnd.getDate());
        if (start == null || end == null) return;

        if (end.isBefore(start)) {
            JOptionPane.showMessageDialog(this, "Khoảng thời gian không hợp lệ (Kết thúc < Bắt đầu).");
            return;
        }

        // bảng
        tableModel.setServiceRankings(serviceRankingBUS.getByRange(start, end));

        // chart (Top 8 theo số lượng)
        ArrayList<ServiceRanking> top = serviceRankingBUS.getTopByRange(start, end, 8);
        chartPanel.setData(top);
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
    private static LocalDateTime startOfDay(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
    }
    private static LocalDateTime endOfDay(Date d) {
        if (d == null) return null;
        LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ld.atTime(LocalTime.of(23, 59, 59));
    }

    // render tiền tệ
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
