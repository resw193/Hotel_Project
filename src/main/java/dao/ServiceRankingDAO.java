package dao;

import entity.ServiceRanking;
import connectDB.ConnectDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ServiceRankingDAO {
    private ConnectDB connectDB;

    public ServiceRankingDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    // Sử dụng JDateChooser
    // Thống kê dịch vụ sử dụng trong khoảng thời gian (start,end) --> trả về (serviceName, TotalQuantity, TotalRevenue)
    // Trong giao diện thống kê sẽ có 2 lịch (lịch trái chọn startTime, lịch phải chọn endTime) và cho lắng nghe sự kiện sau khi chọn xong thì table dạng cột
    // table dạng cột sẽ hiển thị các cột : ServiceName | TotalQuantity | TotalRevenue
    public ArrayList<ServiceRanking> thongKeDichVuTheoThoiGian(LocalDateTime startTime, LocalDateTime endTime) {
        ArrayList<ServiceRanking> serviceRankings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from fn_ServiceStats(?, ?) order by TotalRevenue desc"; // sắp xếp giảm dần để quan sát

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(startTime));
            ps.setTimestamp(2, Timestamp.valueOf(endTime));
            rs = ps.executeQuery();

            while(rs.next()) {
                String serviceName = rs.getString("serviceName");
                int totalQuantity = rs.getInt("TotalQuantity");
                double totalRevenue = rs.getDouble("TotalRevenue");

                serviceRankings.add(new ServiceRanking(serviceName, totalQuantity, totalRevenue));
            }

            return serviceRankings;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Top các dịch vụ được sử dụng nhiều nhất trong khoảng thời gian start -> end
    public ArrayList<ServiceRanking> getTopByRange(LocalDateTime start, LocalDateTime end, int topN) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<ServiceRanking> listTopService = new ArrayList<>();
        String sql =
                "SELECT serviceName, TotalQuantity, TotalRevenue " +
                        "FROM (" +
                        "   SELECT serviceName, TotalQuantity, TotalRevenue, " +
                        "          ROW_NUMBER() OVER (ORDER BY TotalQuantity DESC) rn " +
                        "   FROM fn_ServiceStats(?, ?)" +
                        ") x WHERE rn <= ? " +
                        "ORDER BY TotalQuantity DESC";
        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setInt(3, topN);
            rs = ps.executeQuery();

            while (rs.next()) {
                listTopService.add(new ServiceRanking(
                        rs.getString("serviceName"),
                        rs.getInt("TotalQuantity"),
                        rs.getDouble("TotalRevenue")));
            }

            return listTopService;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}  