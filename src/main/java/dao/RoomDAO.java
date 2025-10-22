package dao;

import entity.Customer;
import entity.Room;
import entity.RoomType;
import connectDB.ConnectDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class RoomDAO {
    private ConnectDB connectDB;
    private RoomTypeDAO roomTypeDAO;

    public RoomDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();

        this.roomTypeDAO = new RoomTypeDAO();
    }

    // Lấy ra toàn bộ phòng và đưa lên dữ liệu table giao diện
    public ArrayList<Room> getAllRoom(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Room";
        ArrayList<Room> dsPhong = new ArrayList<>();

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String roomID = rs.getString("roomID");
                String description = rs.getString("description");
                boolean isAvailable = rs.getBoolean("isAvailable");
                RoomType roomType = roomTypeDAO.getRoomTypeByID(rs.getString("roomTypeID"));
                String imgRoomSource = rs.getString("imgRoomSource");

                dsPhong.add(new Room(roomID, description, isAvailable, roomType, imgRoomSource));
            }

            return dsPhong;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }

    // Lấy ra danh sách phòng theo loại phòng (Phòng đơn | Phòng đôi) --> Filter phòng theo loại
    public ArrayList<Room> getAllRoomByTypeName(String typeName){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * \n" +
                "from Room r\n" +
                "JOIN RoomType rt \n" +
                "ON r.roomTypeID = rt.roomTypeID \n" +
                "where rt.typeName = ?";
        ArrayList<Room> dsPhong = new ArrayList<>();

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, typeName);
            rs = ps.executeQuery();

            while (rs.next()) {
                String roomID = rs.getString("roomID");
                String description = rs.getString("description");
                boolean isAvailable = rs.getBoolean("isAvailable");
                RoomType roomType = roomTypeDAO.getRoomTypeByID(rs.getString("roomTypeID"));
                String imgRoomSource = rs.getString("imgRoomSource");

                dsPhong.add(new Room(roomID, description, isAvailable, roomType, imgRoomSource));
            }

            return dsPhong;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }

    // Lấy ra danh sách phòng đã được đặt (isAvailable = 0) và status = 'Đặt' --> thuc hien check-in   hoặc  status = 'Check-in' --> Thực hiện check-out
    public ArrayList<Room> getAllRoomByStatus(String status){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select r.roomID, r.description, r.isAvailable, r.roomTypeID, r.imgRoomSource, ordr.status\n" +
                "from Room r\n" +
                "JOIN OrderDetailRoom ordr\n" +
                "ON r.roomID = ordr.roomID and ordr.status = ?";
        ArrayList<Room> dsPhong = new ArrayList<>();

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            rs = ps.executeQuery();

            while (rs.next()) {
                String roomID = rs.getString("roomID");
                String description = rs.getString("description");
                boolean isAvailable = rs.getBoolean("isAvailable");
                RoomType roomType = roomTypeDAO.getRoomTypeByID(rs.getString("roomTypeID"));
                String imgRoomSource = rs.getString("imgRoomSource");

                dsPhong.add(new Room(roomID, description, isAvailable, roomType, imgRoomSource));
            }

            return dsPhong;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }

    // Lấy ra toàn bộ phòng trống (isAvailable = 1)
    public ArrayList<Room> getAllRoomOccupancy(int status){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Room where isAvailable = ?";
        ArrayList<Room> dsPhong = new ArrayList<>();

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            rs = ps.executeQuery();

            while (rs.next()) {
                String roomID = rs.getString("roomID");
                String description = rs.getString("description");
                boolean isAvailable = rs.getBoolean("isAvailable");
                RoomType roomType = roomTypeDAO.getRoomTypeByID(rs.getString("roomTypeID"));
                String imgRoomSource = rs.getString("imgRoomSource");

                dsPhong.add(new Room(roomID, description, isAvailable, roomType, imgRoomSource));
            }

            return dsPhong;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }

    // Lấy ra Room theo roomID
    public Room getRoomByID(String roomID){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Room where roomID = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, roomID);
            rs = ps.executeQuery();

            if(rs.next()){
                String description = rs.getString("description");
                boolean isAvailable = rs.getBoolean("isAvailable");
                RoomType roomType = roomTypeDAO.getRoomTypeByID(rs.getString("roomTypeID"));
                String imgRoomSource = rs.getString("imgRoomSource");

                return new Room(roomID, description, isAvailable, roomType, imgRoomSource);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }

        return null;
    }

    // Thêm phòng mới (sp_AddRoom hoặc insert into)
    public boolean addRoom(Room room){
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO Room (description, isAvailable, roomTypeID, imgRoomSource)\n" +
                "VALUES (?, ?, ?, ?)";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, room.getDescription());
            ps.setBoolean(2, room.isAvailable());
            ps.setString(3, room.getRoomType().getRoomTypeID());
            ps.setString(4, room.getImgRoomSource());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }

    }

    // Cập nhật thông tin phòng (update table) - chỉ cập nhật lại description
    public boolean updateRoomInformation(Room room){
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "update Room set description = ?, imgRoomSource = ? where roomID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, room.getDescription());
            ps.setString(2, room.getImgRoomSource());
            ps.setString(3, room.getRoomID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Cập nhật dịch vụ cho phòng (sp_AddServiceToRoom(@roomID, @serviceName, @quantity)) (chỉ khi phòng đã check-in) --> Tạo button filter theo (All/Check-in) để thêm cho dễ
    public boolean capNhatDichVuChoPhong(String roomID, String serviceName, int quantity){
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_AddServiceToRoom(?, ?, ?)}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, roomID);
            cs.setString(2, serviceName);
            cs.setInt(3, quantity);

            return cs.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(cs, null);
        }
    }

    // Quản lý đặt phòng --> Hieển thị dạng card (giông dịch vụ) và có các nút filter theo loại phòng (Phòng đơn | Phòng đôi), filter theo trạng thái ('Trống', 'Đặt', 'Check-in')
    // Phiá trên ben traái vâẫn là thanh tìm kiếm theo mã phòng
    // Đặt phòng
    public boolean datPhong(Customer customer, String roomID, String employeeID, LocalDateTime bookingDate, LocalDateTime checkInDate, LocalDateTime checkOutDate, String bookingType) {
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_BookRoom(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, customer.getFullName());
            cs.setString(2, customer.getPhone());
            cs.setString(3, customer.getEmail());
            cs.setString(4, customer.getIdCard());
            cs.setString(5, roomID);
            cs.setString(6, employeeID);
            cs.setTimestamp(7, Timestamp.valueOf(bookingDate));
            cs.setTimestamp(8, Timestamp.valueOf(checkInDate));
            cs.setTimestamp(9, Timestamp.valueOf(checkOutDate));
            cs.setString(10, bookingType);

            return cs.executeUpdate() > 0;

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(cs, null);
        }
    }

    // Hủy đặt phòng (call sp_CancelBooking(?)) (chỉ hủy đặt phòng khi status của phòng đó = 'Đặt' tức là chưa check-in) --> giao diện hủy đặt phòng sẽ hieển thị các phòng
    // có trạng thái = 'Đặt', và customer yêu cầu hủy đặt phòng chỉ cần tìm theo mã phòng
    public boolean huyDatPhong(String roomID){
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_CancelBooking(?)}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, roomID);

            return cs.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(cs, null);
        }
    }

    // Check-in ( {call sp_CheckIn(?)} ) --> chuột phải vào phòng --> click check-in (chỉ sử dụng đc khi phòng có status = 'Đặt')
    public boolean checkIn(String roomID){
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_CheckIn(?)}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, roomID);

            return cs.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(cs, null);
        }
    }

    // Check-out ( {call sp_CheckOut(?)} ) --> chuột phải vào phòng --> click check-out (chỉ sử dụng đc khi phòng có status = 'Check-in')
    public boolean checkOut(String roomID){
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_CheckOut(?)}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, roomID);

            return cs.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(cs, null);
        }
    }

    // Gia hạn phòng --> Chuột phải vào phòng sẽ có MenuOption Gia hạn phòng (Chỉ áp dụng đối với phòng đã check-in) sẽ ra FormExtendRoom gồm 3 dòng
    // RoomID | Thời gian check-out cũ | Thời gian check-out mới
    public boolean giaHanPhong(String roomID, LocalDateTime newCheckOutDate){
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_GiaHanPhong(?, ?)}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, roomID);
            cs.setTimestamp(2, Timestamp.valueOf(newCheckOutDate));

            return cs.execute();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(cs, null);
        }
    }

    public LocalDateTime[] getActiveStayTimes(String roomID){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LocalDateTime checkIn = null, checkOut = null;
        String sql = "select TOP 1 checkInDate, checkOutDate " +
                "from OrderDetailRoom where roomID = ? and status = N'Check-in' " +
                "order by orderDetailRoomID desc";
        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, roomID);
            rs = ps.executeQuery();

            if (rs.next()){
                Timestamp checkInDate = rs.getTimestamp("checkInDate");
                Timestamp checkOutDate = rs.getTimestamp("checkOutDate");
                if (checkInDate != null) checkIn  = checkInDate.toLocalDateTime();
                if (checkOutDate != null) checkOut = checkOutDate.toLocalDateTime();
            }

            return new LocalDateTime[]{
                    checkIn, checkOut
            };
        } catch (SQLException e){
            e.printStackTrace();
            return new LocalDateTime[]{null, null};
        } finally {
            connectDB.close(ps, rs);
        }
    }
}
