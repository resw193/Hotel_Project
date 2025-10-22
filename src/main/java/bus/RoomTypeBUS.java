package bus;

import dao.RoomTypeDAO;
import entity.RoomType;

import java.util.ArrayList;
import java.util.List;

public class RoomTypeBUS {
    private RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    private String lastError;

    public String getLastError() {
        return lastError;
    }

    private void setError(String m) {
        lastError = m;
    }

    private void clearError() {
        lastError = null;
    }

    //
    public List<RoomType> getAll() {
       return roomTypeDAO.getAllRoomTypes();
    }

    public RoomType getByID(String roomTypeID) {
        return roomTypeDAO.getRoomTypeByID(roomTypeID.trim());
    }

    public RoomType getByTypeName(String typeName) {
        return roomTypeDAO.getRoomTypeByTypeName(typeName.trim());
    }

    public boolean checkExist(String typeName) {
        return getByTypeName(typeName) != null;
    }
}
