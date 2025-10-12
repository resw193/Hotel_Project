package DAO;

import connectDB.ConnectDB;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DashboardDAO {
    private final ConnectDB db = ConnectDB.getInstance();

    private Connection con() throws SQLException {
        return db.connect();
    }

    /* ================= KPIs ================= */
    public int totalRooms() {
        final String sql = "SELECT COUNT(*) FROM Room";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Phòng trống
    public int occupiedNow() {
        final String sql = "SELECT COUNT(*) FROM Room WHERE isAvailable = 0";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int freeNow() {
        int total = totalRooms();
        int occ = occupiedNow();
        return Math.max(0, total - occ);
    }

    public double occupancyPercentNow() {
        int total = totalRooms();
        if (total <= 0) return 0d;
        return occupiedNow() * 100.0 / total;
    }

    // Doanh thu theo tháng hiện tại
    public BigDecimal revenueThisMonth() {
        final String sql =
                "SELECT COALESCE(SUM(CAST(total AS DECIMAL(19,2))),0) " +
                        "FROM [Order] " +
                        "WHERE YEAR(orderDate)=YEAR(GETDATE()) AND MONTH(orderDate)=MONTH(GETDATE())";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    // Số đơn đặt trong hôm nay
    public int bookingsToday() {
        final String sql =
                "SELECT COUNT(*) FROM [Order] " +
                        "WHERE CAST(orderDate AS DATE) = CAST(GETDATE() AS DATE)";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /* ============== Bảng hoạt động sắp tới ============== */
    public List<UpcomingBooking> upcomingBookings(int daysAhead) {
        final String sql =
                "SELECT odr.orderID, c.fullName, odr.roomID, odr.checkInDate, odr.checkOutDate, odr.status " +
                        "FROM OrderDetailRoom odr " +
                        "JOIN [Order] o ON o.orderID = odr.orderID " +
                        "JOIN Customer c ON c.customerID = o.customerID " +
                        "WHERE CAST(odr.checkInDate AS DATE) >= CAST(GETDATE() AS DATE) " +
                        "  AND CAST(odr.checkInDate AS DATE) < CAST(DATEADD(DAY, ?, GETDATE()) AS DATE) " +
                        "ORDER BY odr.checkInDate ASC";
        List<UpcomingBooking> list = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, daysAhead);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UpcomingBooking ub = new UpcomingBooking();
                    ub.orderCode = rs.getString(1);
                    ub.customer  = rs.getString(2);
                    ub.room      = rs.getString(3);
                    Timestamp ci = rs.getTimestamp(4);
                    Timestamp co = rs.getTimestamp(5);
                    ub.checkIn   = (ci != null) ? ci.toLocalDateTime().toLocalDate() : null;
                    ub.checkOut  = (co != null) ? co.toLocalDateTime().toLocalDate() : null;
                    ub.status    = rs.getString(6);
                    list.add(ub);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // tmp class để hiển thị ban đầu
    public static class UpcomingBooking {
        public String orderCode;
        public String customer;
        public String room;
        public LocalDate checkIn;
        public LocalDate checkOut;
        public String status;
    }
}
