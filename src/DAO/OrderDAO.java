package DAO;

import Entity.*;
import connectDB.ConnectDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderDAO {
    private ConnectDB connectDB;
    private EmployeeDAO employeeDAO;
    private CustomerDAO customerDAO;
    private PromotionDAO promotionDAO;

    public OrderDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();

        employeeDAO = new EmployeeDAO();
        customerDAO = new CustomerDAO();
        promotionDAO = new PromotionDAO();
    }

    // Hiển thị toàn bộ Order (hóa đơn) hiện tại đang có trong csdl lên table giao diện để xử lý
    public ArrayList<Order> getAllOrder(){
        ArrayList<Order> dsHoaDon = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT orderID, orderDate, total, employeeID, customerID, promotionID, orderStatus FROM [dbo].[Order]";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()) {
                String orderID = rs.getString("orderID");
                LocalDateTime orderDate = rs.getTimestamp("orderDate").toLocalDateTime();
                Employee employee = employeeDAO.getEmployeeByID(rs.getString("employeeID"));
                Customer customer = customerDAO.getCustomerByID(rs.getString("customerID"));
                Promotion promotion = promotionDAO.getPromotionByID(rs.getString("promotionID"));
                String orderStatus = rs.getString("orderStatus");

                Order order = new Order(orderID, orderDate, employee, customer, promotion, orderStatus);
                order.setTotal(rs.getDouble("total"));
                dsHoaDon.add(order);
            }

            return dsHoaDon;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Lấy ra Order theo ID --> để khi nhấn nút thanh toán --> theo table ta sẽ lấy ra được OrderID --> tạo Order --> Lấy ra được Total
    public Order getOrderByID(String orderID) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from [dbo].[Order] where orderID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, orderID);
            rs = ps.executeQuery();

            while(rs.next()){
                LocalDateTime orderDate = rs.getTimestamp("orderDate").toLocalDateTime();
                Employee employee = employeeDAO.getEmployeeByID(rs.getString("employeeID"));
                Customer customer = customerDAO.getCustomerByID(rs.getString("customerID"));
                Promotion promotion = promotionDAO.getPromotionByID(rs.getString("promotionID"));
                String orderStatus = rs.getString("orderStatus");

                Order order = new Order(orderID, orderDate, employee, customer, promotion, orderStatus);
                order.setTotal(rs.getDouble("total"));
                return order;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }

        return null;
    }

    // Lọc hóa đơn (thanh toán | Chưa thanh toán) (có filter lọc hóa đơn)
    public ArrayList<Order> getAllOrderByStatus(String orderStatus) {
        if ("Tất cả".equalsIgnoreCase(orderStatus)) {
            return getAllOrder();
        }

        ArrayList<Order> dsHoaDon = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT orderID, orderDate, total, employeeID, customerID, promotionID, orderStatus "
                + "FROM [dbo].[Order] WHERE orderStatus = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, orderStatus);
            rs = ps.executeQuery();

            while(rs.next()){
                String orderID = rs.getString("orderID");
                LocalDateTime orderDate = rs.getTimestamp("orderDate").toLocalDateTime();
                Employee employee = employeeDAO.getEmployeeByID(rs.getString("employeeID"));
                Customer customer = customerDAO.getCustomerByID(rs.getString("customerID"));
                Promotion promotion = promotionDAO.getPromotionByID(rs.getString("promotionID"));

                Order order = new Order(orderID, orderDate, employee, customer, promotion, orderStatus);
                order.setTotal(rs.getDouble("total"));
                dsHoaDon.add(order);
            }

            return dsHoaDon;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }

    // Thanh toán hóa đơn (sp_PayOrder(?)) --> khi người dùng click chuột phải vào dòng hóa đơn chưa thanh toán sẽ có nút thanh toán
    public ArrayList<OrderPay> thanhToanHoaDon(String orderID){
        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        String sql = " {call sp_PayOrder(?)} ";
        ArrayList<OrderPay> informations = new ArrayList<>();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, orderID);
            rs = cs.executeQuery();

            while(rs.next()){
                Order order = getOrderByID(orderID);
                String description = rs.getString("description");
                RoomType roomType = roomTypeDAO.getRoomTypeByID(rs.getString("roomTypeID"));
                LocalDateTime bookingDate = rs.getTimestamp("bookingDate").toLocalDateTime();
                LocalDateTime checkInDate = rs.getTimestamp("checkInDate").toLocalDateTime();
                LocalDateTime checkOutDate = rs.getTimestamp("checkOutDate").toLocalDateTime();
                String bookingType = rs.getString("bookingType");
                String serviceName = rs.getString("serviceName");
                int quantity = rs.getInt("serviceQuantity");

                informations.add(new OrderPay(order, description, roomType, bookingDate, checkInDate, checkOutDate, bookingType, serviceName, quantity)) ;
            }

            return informations;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
