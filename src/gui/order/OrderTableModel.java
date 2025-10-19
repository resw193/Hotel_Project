package gui.order;

import Entity.Order;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderTableModel extends AbstractTableModel {
    private final String[] cols = {
            "Mã hóa đơn", "Ngày lập hóa đơn", "Nhân viên", "Khách hàng", "Mã KM", "Trạng thái", "Tổng tiền"
    };

    private List<Order> dsHoaDon = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setDsHoaDon(List<Order> rows){
        dsHoaDon.clear();
        if (rows != null) dsHoaDon.addAll(rows);
        fireTableDataChanged();
    }

    public Order getAt(int row){
        return (row >= 0 && row < dsHoaDon.size()) ? dsHoaDon.get(row) : null;
    }

    @Override public int getRowCount() {
        return dsHoaDon.size();
    }

    @Override public int getColumnCount() {
        return cols.length;
    }

    @Override public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Object getValueAt(int r, int c) {
        Order o = dsHoaDon.get(r);
        return switch (c) {
            case 0 -> o.getOrderID();
            case 1 -> o.getOrderDate() == null ? "" : formatter.format(o.getOrderDate());
            case 2 -> o.getEmployee() == null ? "" : o.getEmployee().getFullName();
            case 3 -> o.getCustomer() == null ? "" : o.getCustomer().getFullName();
            case 4 -> o.getPromotion() == null ? "" : o.getPromotion().getPromotionID();
            case 5 -> o.getOrderStatus();
            case 6 -> String.format("%,.0f VNĐ", o.getTotal());
            default -> "";
        };
    }
}
