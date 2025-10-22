package dao;

import entity.ServiceRanking;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            ps.setString(1, startTime.toString());
            ps.setString(2, endTime.toString());
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
}
