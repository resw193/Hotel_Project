package dao;

import entity.ServiceRanking;
import connectDB.ConnectDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceRankingDAO {
    private final ConnectDB connectDB;

    public ServiceRankingDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    // Thống kê dịch vụ sử dụng trong khoảng thời gian, sắp xếp giảm dần theo doanh thu
    public List<ServiceRanking> thongKeDichVuTheoThoiGian(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT * FROM fn_ServiceStats(?, ?) ORDER BY TotalRevenue DESC";
        List<ServiceRanking> serviceRankings = new ArrayList<>();

        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(startTime));
            ps.setTimestamp(2, Timestamp.valueOf(endTime));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    serviceRankings.add(new ServiceRanking(
                            rs.getString("serviceName"),
                            rs.getInt("TotalQuantity"),
                            rs.getDouble("TotalRevenue")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return serviceRankings;
    }
}
