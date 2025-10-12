package DAO;

import Entity.Room;
import Entity.RoomType;
import connectDB.ConnectDB;

import java.sql.*;
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
        String sql = "INSERT INTO Room (description, roomTypeID, imgRoomSource)\n" +
                "VALUES (?, ?, ?)";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, room.getDescription());
            ps.setString(2, room.getRoomType().getRoomTypeID());
            ps.setString(3, room.getImgRoomSource());

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

    // Cập nhật dịch vụ cho phòng (sp_AddServiceToRoom(@roomID, @serviceName, @quantity))
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
        }
    }

    // Hủy đặt phòng (call sp_CancelBooking(?)) (nếu hủy đặt phòng thì đầu tiên phải kiểm tra checkIn = null)
    public boolean huyDatPhong(String roomID){
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_CancelBooking(?}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, roomID);

            return cs.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    // Check-in ( {call sp_CheckIn(?)} )
    public boolean checkIn(String roomID){
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_CheckIn(?}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, roomID);

            return cs.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    // Check-out ( {call sp_CheckOut(?)} )
    public boolean checkOut(String roomID){
        Connection con = null;
        CallableStatement cs = null;
        String sql = "{call sp_CheckOut(?}";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, roomID);

            return cs.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    // Thống kê tỷ lệ lấp đầy phòng theo ngày ? (fn_OccupancyRate(@date))     (? chưa rõ ràng)
}
