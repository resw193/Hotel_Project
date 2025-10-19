package gui.promotion;

import Entity.Promotion;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PromotionTableModel extends AbstractTableModel {

    private final String[] cols = {
            "PromotionID", "Tên khuyến mãi", "Giảm giá", "Bắt đầu", "Kết thúc", "Số lượng"
    };
    private ArrayList<Promotion> dsKM = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setDsKM(ArrayList<Promotion> list) {
        dsKM.clear();
        if (list != null) dsKM.addAll(list);
        fireTableDataChanged();
    }

    public Promotion getAt(int row) {
        if (row < 0 || row >= dsKM.size()) return null;
        return dsKM.get(row);
    }

    @Override public int getRowCount() {
        return dsKM.size();
    }

    @Override public int getColumnCount() {
        return cols.length;
    }

    @Override public String getColumnName(int column) {
        return cols[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Promotion p = dsKM.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> p.getPromotionID();
            case 1 -> p.getPromotionName();
            case 2 -> (int) Math.round(p.getDiscount()) + " %";
            case 3 -> p.getStartTime() == null ? "" : p.getStartTime().format(formatter);
            case 4 -> p.getEndTime() == null ? "" : p.getEndTime().format(formatter);
            case 5 -> p.getQuantity();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 5 -> Integer.class;
            default -> String.class;
        };
    }
}
