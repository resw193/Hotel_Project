package dao;

import connectDB.ConnectDB;
import entity.BookingTypeRevenue;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BookingTypeRevenueDAO {
    private ConnectDB connectDB;

    public BookingTypeRevenueDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public ArrayList<BookingTypeRevenue> thongKeTheoKieuDatPhong(LocalDateTime start, LocalDateTime end) {
        ArrayList<BookingTypeRevenue> list = new ArrayList<>();
        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        String sql = "{CALL sp_BookingTypeRevenueStats(?,?)}";

        try {
            conn = connectDB.getConnection();
            cs = conn.prepareCall(sql);
            cs.setTimestamp(1, Timestamp.valueOf(start));
            cs.setTimestamp(2, Timestamp.valueOf(end));
            rs = cs.executeQuery();

            while(rs.next()){
                list.add(new BookingTypeRevenue(
                        rs.getString("bookingType"),
                        rs.getInt("SoLuot"),
                        rs.getDouble("RoomRevenue")
                ));
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
