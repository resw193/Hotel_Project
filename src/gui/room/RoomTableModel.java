package gui.room;

import DAO.RoomDAO;
import Entity.Room;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class RoomTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Thông tin phòng", "Trạng thái", "Loại phòng"};
    private ArrayList<Room> rooms = new ArrayList<>();
    private RoomDAO roomDAO = new RoomDAO();

    public RoomTableModel() {
        rooms = roomDAO.getAllRoom();
    }

    @Override
    public int getRowCount() {
        return rooms == null ? 0 : rooms.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Room r = rooms.get(rowIndex);
        switch (columnIndex) {
            case 0: return r.getRoomID();
            case 1: return r.getDescription();
            case 2: return r.isAvailable() ? "Trống" : "Đặt";
            case 3: return (r.getRoomType() == null) ? "" : r.getRoomType().getTypeName();
            default: return null;
        }
    }

    public Room getRoomAt(int row) {
        return rooms.get(row);
    }

    public void setRooms(ArrayList<Room> data) {
        this.rooms = data == null ? new ArrayList<>() : data;
        fireTableDataChanged();
    }
}
