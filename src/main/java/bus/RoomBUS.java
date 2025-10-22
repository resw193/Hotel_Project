package bus;

import dao.RoomDAO;
import dao.RoomTypeDAO;
import entity.Customer;
import entity.Room;
import entity.RoomType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomBUS {
    private final RoomDAO roomDAO = new RoomDAO();
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

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


    public List<Room> getAll() {
       return roomDAO.getAllRoom();
    }

    public ArrayList<Room> getByStatus(String status) {
        return roomDAO.getAllRoomByStatus(status.trim());
    }

    public List<Room> getByTypeName(String typeName) {
        return roomDAO.getAllRoomByTypeName(typeName.trim());
    }

    // Lọc theo tình trạng trống hoặc không trống (1 | 0)
    public ArrayList<Room> getByOccupancy(boolean available) {
        return roomDAO.getAllRoomOccupancy(available ? 1 : 0);
    }

    // Lấy ra phòng theo ID
    public Room getByID(String roomID) {
        return roomDAO.getRoomByID(roomID.trim());
    }

    // lọc theo tình trạng phòng (status) (All/Check-in)
    public List<Room> searchAndFilter(String keyword, String filter) {
        List<Room> rooms = "Check-in".equalsIgnoreCase(String.valueOf(filter))
                ? getByStatus("Check-in")
                : getAll();

        if (keyword == null || keyword.isBlank()) return rooms;

        String kw = keyword.trim().toLowerCase();
        return rooms.stream()
                .filter(r -> r.getRoomID() != null && r.getRoomID().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    // Thêm phòng mới
    public boolean addRoom(String description, String roomTypeName, String imgRoomSource) {
        String v = validateDescription(description);
        if (v != null) {
            setError(v);
            return false;
        }

        RoomType type = roomTypeDAO.getRoomTypeByTypeName(check(roomTypeName));
        if (type == null) {
            setError("Loại phòng không hợp lệ");
            return false;
        }

        Room r = new Room(description.trim(), true, type, check(imgRoomSource));
        boolean ok = roomDAO.addRoom(r);
        if (!ok) setError("Không thể thêm phòng");
        return ok;
    }

    public boolean addRoom(Room room) {
        clearError();
        if (room == null) {
            setError("Thiếu dữ liệu phòng");
            return false;
        }

        String v = validateDescription(room.getDescription());
        if (v != null) {
            setError(v);
            return false;
        }

        if (room.getRoomType() == null) {
            setError("Loại phòng không hợp lệ");
            return false;
        }

        boolean ok = roomDAO.addRoom(room);
        if (!ok) setError("Không thể thêm phòng");
        return ok;
    }

    // Cập nhật thông tin phòng
    public boolean updateRoomInformation(String roomID, String description, String imgRoomSource) {
        clearError();
        if (isBlank(roomID)) {
            setError("Mã phòng không hợp lệ");
            return false;
        }

        String v = validateDescription(description);
        if (v != null) {
            setError(v);
            return false;
        }

        Room current = roomDAO.getRoomByID(roomID.trim());
        if (current == null) {
            setError("Không tìm thấy phòng");
            return false;
        }

        Room newInfo = new Room(roomID.trim(), description.trim(), current.isAvailable(), current.getRoomType(), check(imgRoomSource));

        boolean ok = roomDAO.updateRoomInformation(newInfo);
        if (!ok) setError("Không thể cập nhật phòng");
        return ok;
    }

    // Add service to room
    public boolean addServiceToRoom(String roomID, String serviceName, int quantity) {
        clearError();
        if (isBlank(roomID) || isBlank(serviceName)) {
            setError("Thiếu dữ liệu bắt buộc");
            return false;
        }
        if (quantity <= 0) {
            setError("Số lượng phải > 0");
            return false;
        }

        boolean ok = roomDAO.capNhatDichVuChoPhong(roomID.trim(), serviceName.trim(), quantity);
        if (!ok) setError("Thêm dịch vụ thất bại (chỉ áp dụng cho phòng đang Check-in)");
        return ok;
    }


    // Đặt phòng
    public boolean datPhong(Customer c, String roomID, String employeeID,
                            LocalDateTime bookingDate, LocalDateTime checkInDate,
                            LocalDateTime checkOutDate, String bookingType) {
        clearError();
        if (c == null) {
            setError("Thiếu thông tin khách hàng");
            return false;
        }
        if (isBlank(roomID) || isBlank(employeeID) || isBlank(bookingType)) {
            setError("Thiếu dữ liệu bắt buộc");
            return false;
        }

        String t = validateTimeRange(checkInDate, checkOutDate);
        if (t != null) {
            setError(t);
            return false;
        }

        boolean ok = roomDAO.datPhong(c, roomID.trim(), employeeID.trim(),
                bookingDate == null ? LocalDateTime.now() : bookingDate,
                checkInDate, checkOutDate, bookingType.trim());
        if (!ok) setError("Đặt phòng thất bại");
        return ok;
    }

    public boolean huyDatPhong(String roomID) {
        clearError();
        if (isBlank(roomID)) {
            setError("Mã phòng không hợp lệ");
            return false;
        }

        boolean ok = roomDAO.huyDatPhong(roomID.trim());
        if (!ok) setError("Không thể hủy (chỉ khi trạng thái là 'Đặt')");
        return ok;
    }

    public boolean checkIn(String roomID) {
        clearError();
        if (isBlank(roomID)) {
            setError("Mã phòng không hợp lệ");
            return false;
        }

        boolean ok = roomDAO.checkIn(roomID.trim());
        if (!ok) setError("Check-in thất bại (chỉ khi trạng thái là 'Đặt')");
        return ok;
    }

    public boolean checkOut(String roomID) {
        clearError();
        if (isBlank(roomID)) {
            setError("Mã phòng không hợp lệ");
            return false;
        }

        boolean ok = roomDAO.checkOut(roomID.trim());
        if (!ok) setError("Check-out thất bại (chỉ khi trạng thái là 'Check-in')");
        return ok;
    }

    public boolean giaHanPhong(String roomID, LocalDateTime newCheckOut) {
        clearError();
        if (isBlank(roomID)) {
            setError("Mã phòng không hợp lệ");
            return false;
        }
        if (newCheckOut == null) {
            setError("Chưa chọn thời gian check-out mới");
            return false;
        }

        LocalDateTime[] active = roomDAO.getActiveStayTimes(roomID.trim());
        LocalDateTime curOut = (active != null) ? active[1] : null;

        if (curOut == null) {
            setError("Phòng chưa Check-in, không thể gia hạn");
            return false;
        }
        if (!newCheckOut.isAfter(curOut)) {
            setError("Check-out mới phải sau hiện tại");
            return false;
        }

        boolean ok = roomDAO.giaHanPhong(roomID.trim(), newCheckOut);
        if (!ok) setError("Gia hạn thất bại");
        return ok;
    }

    public LocalDateTime[] getActiveStayTimes(String roomID) {
        if (isBlank(roomID)) return new LocalDateTime[]{null, null};

        return roomDAO.getActiveStayTimes(roomID.trim());
    }

    // validData
    private String validateDescription(String description) {
        if (description == null || description.trim().length() < 15)
            return "Mô tả phòng phải từ 15 ký tự trở lên";
        return null;
    }

    private String validateTimeRange(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkIn == null || checkOut == null) return "Thiếu thời gian checkIn/checkOut";
        if (!checkOut.isAfter(checkIn)) return "checkOut phải sau checkIn";
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String check(String s) {
        return s == null ? "" : s.trim();
    }
}
