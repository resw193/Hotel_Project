package dao;

import entity.*;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailServiceDAO {
    private ConnectDB connectDB;
    private OrderDAO orderDAO;
    private RoomDAO roomDAO;
    private ServiceDAO serviceDAO;

    public OrderDetailServiceDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();

        orderDAO = new OrderDAO();
        roomDAO = new RoomDAO();
        serviceDAO = new ServiceDAO();
    }

    // Lấy ra toàn bộ OrderDetailService theo orderID
    public ArrayList<OrderDetailService> getAllOrderDetailServiceByOrderID(String orderID){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from OrderDetailService where orderID = ?";
        ArrayList<OrderDetailService> orderDetailServices = new ArrayList<>();

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, orderID);
            rs = ps.executeQuery();

            while (rs.next()) {
                Order order = orderDAO.getOrderByID(rs.getString("orderID"));
                int quantity = rs.getInt("quantity");
                Service service = serviceDAO.getServiceByID(rs.getString("serviceID"));
                Room room = roomDAO.getRoomByID(rs.getString("roomID"));

                orderDetailServices.add(new OrderDetailService(order, quantity, service, room));
            }

            return orderDetailServices;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
