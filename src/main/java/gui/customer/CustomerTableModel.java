package gui.customer;

import entity.Customer;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CustomerTableModel extends AbstractTableModel {
    private final String[] cols = {
            "CustomerID", "Tên khách hàng", "Số điện thoại", "Email",
            "Ngày đăng ký", "CCCD", "Điểm thân thiết"
    };
    private ArrayList<Customer> dsKH = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setDsKH(ArrayList<Customer> list){
        dsKH.clear();
        if (list != null) dsKH.addAll(list);
        fireTableDataChanged();
    }

    public Customer getAt(int row){
        if (row < 0 || row >= dsKH.size()) return null;
        return dsKH.get(row);
    }

    @Override public int getRowCount() {
        return dsKH.size();
    }

    @Override public int getColumnCount() {
        return cols.length;
    }
    @Override public String getColumnName(int column) {
        return cols[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer c = dsKH.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> c.getCustomerID();
            case 1 -> c.getFullName();
            case 2 -> c.getPhone();
            case 3 -> c.getEmail();
            case 4 -> c.getRegisDate() == null ? "" : c.getRegisDate().format(formatter);
            case 5 -> c.getIdCard();
            case 6 -> c.getLoyaltyPoint();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex){
            case 6 -> Integer.class;
            default -> String.class;
        };
    }
}
