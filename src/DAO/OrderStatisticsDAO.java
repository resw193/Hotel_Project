package DAO;

import Entity.OrderStatistics;
import connectDB.ConnectDB;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderStatisticsDAO {
    private ConnectDB connectDB;

    public OrderStatisticsDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    // Thống kê số lượng hóa đơn và tổng doanh thu theo ngày (sp_DailyOrderStats(?)) --> Trả về [soLuongHoaDon | TotalRevenue]
    public OrderStatistics thongKeSoLuongHoaDonTheoNgay(LocalDateTime ngayThongKe){
        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        String sql = " {call sp_DailyOrderStats(?)} ";

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setTimestamp(1, Timestamp.valueOf(ngayThongKe));
            rs = cs.executeQuery();

            if(rs.next()){
                int soLuongHoaDon = rs.getInt("soLuongHoaDon");
                double totalRevenue = rs.getDouble("totalRevenue");

                return new OrderStatistics(soLuongHoaDon, totalRevenue);
            }

        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(cs, rs);
        }
         return null;
    }

    // Thống kê doanh thu theo thời gian (fn_RevenueStats(@startDate, @endDate)
    public double thongKeDoanhThuTheoThoiGian(LocalDateTime startTime, LocalDateTime endTime){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from fn_RevenueStats(?, ?)";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(startTime));
            ps.setTimestamp(2, Timestamp.valueOf(endTime));

            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getDouble("totalRevenue");
            }
        } catch (SQLException e){
            e.printStackTrace();
            return -1;
        }

        return -1;
    }
}
