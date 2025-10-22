package dao;

import entity.Order;
import entity.OrderDetailRoom;
import entity.Room;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderDetailRoomDAO {
    private ConnectDB connectDB;
    private OrderDAO orderDAO;
    private RoomDAO roomDAO;

    public OrderDetailRoomDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();

        orderDAO = new OrderDAO();
        roomDAO = new RoomDAO();
    }

    // Lấy ra toàn bộ OrderDetailRoom theo orderID
    public ArrayList<OrderDetailRoom> getAllOrderDetailRoomByOrderID(String orderID){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from OrderDetailRoom where orderID = ?";
        ArrayList<OrderDetailRoom> orderDetailRooms = new ArrayList<>();

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, orderID);
            rs = ps.executeQuery();

            while (rs.next()) {
                Order order = orderDAO.getOrderByID(rs.getString("orderID"));
                Room room = roomDAO.getRoomByID(rs.getString("roomID"));
                LocalDateTime bookingDate = rs.getTimestamp("bookingDate").toLocalDateTime();
                LocalDateTime checkInDate = rs.getTimestamp("checkInDate").toLocalDateTime();
                LocalDateTime checkOutDate = rs.getTimestamp("checkOutDate").toLocalDateTime();
                String bookingType = rs.getString("bookingType");
                String status = rs.getString("status");

                orderDetailRooms.add(new OrderDetailRoom(order, room, bookingDate, checkInDate, checkOutDate, bookingType, status));
            }

            return orderDetailRooms;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

}
