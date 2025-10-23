package gui.statistics.bookingType_revenue;

import entity.BookingTypeRevenue;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Locale;

public class BookingTypeRevenueTableModel extends AbstractTableModel {
    private String[] cols = {"STT", "Kiểu đặt phòng", "Số lượt đặt", "Doanh thu phòng"};
    private ArrayList<BookingTypeRevenue> list = new ArrayList<>();

    public void setData(ArrayList<BookingTypeRevenue> data) {
        list.clear();
        if (data != null) list.addAll(data);
        fireTableDataChanged();
    }

    @Override public int getRowCount() {
        return list.size();
    }

    @Override public int getColumnCount() {
        return cols.length;
    }

    @Override public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Object getValueAt(int r, int c) {
        BookingTypeRevenue x = list.get(r);
        return switch (c) {
            case 0 -> r + 1;
            case 1 -> x.getBookingType();
            case 2 -> x.getSoLuot();
            case 3 -> java.text.NumberFormat.getCurrencyInstance(new Locale("vi","VN")).format(x.getRoomRevenue());
            default -> "";
        };
    }

    @Override public Class<?> getColumnClass(int c) {
        return switch (c) {
            case 0,2 -> Integer.class;
            default -> String.class;
        };
    }
}
