package gui.statistics.order_statistics;

import dao.OrderStatisticsDAO;
import entity.DailyDetail;

import javax.swing.table.AbstractTableModel;

public class OrderStatisticsTableModel extends AbstractTableModel {

    private String[] cols = {
            "Số lượng hóa đơn", "Thu nhập phòng", "Thu nhập dịch vụ", "Tổng thu nhập"
    };
    private DailyDetail data = new DailyDetail(0,0,0,0);

    public void setData(DailyDetail d) {
        this.data = (d == null) ? new DailyDetail(0,0,0,0) : d;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class;
            default -> Double.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return switch (columnIndex) {
            case 0 -> data.getSoLuongHoaDon();
            case 1 -> data.getRoomRevenue();
            case 2 -> data.getServiceRevenue();
            case 3 -> data.getTotalRevenue();
            default -> null;
        };
    }
}
