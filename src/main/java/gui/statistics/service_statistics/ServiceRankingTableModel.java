package gui.statistics.service_statistics;

import entity.ServiceRanking;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ServiceRankingTableModel extends AbstractTableModel {

    private final String[] cols = {"STT", "Tên dịch vụ", "Tổng số lượng", "Tổng thu nhập"};
    private List<ServiceRanking> serviceRankings = new ArrayList<>();

    public void setServiceRankings(List<ServiceRanking> list) {
        this.serviceRankings = (list != null) ? list : new ArrayList<>();
        fireTableDataChanged();
    }

    public ServiceRanking getAt(int row) {
        if (row < 0 || row >= serviceRankings.size()) return null;
        return serviceRankings.get(row);
    }

    @Override public int getRowCount() {
        return serviceRankings.size();
    }

    @Override public int getColumnCount() {
        return cols.length;
    }

    @Override public String getColumnName(int column) {
        return cols[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Integer.class;
            case 2 -> Integer.class;
            case 3 -> Double.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ServiceRanking r = serviceRankings.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rowIndex + 1;
            case 1 -> r.getServiceName();
            case 2 -> r.getTotalQuantity();
            case 3 -> r.getTotalRevenue();
            default -> null;
        };
    }
}
