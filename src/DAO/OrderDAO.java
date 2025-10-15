package DAO;

import Entity.Customer;
import Entity.Employee;
import Entity.Order;
import Entity.Promotion;
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
        String sql = "select * from Order";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()) {
                LocalDateTime orderDate = rs.getTimestamp("orderDate").toLocalDateTime();
                Employee employee = employeeDAO.getEmployeeByID(rs.getString("employeeID"));
                Customer customer = customerDAO.getCustomerByID(rs.getString("customerID"));
                Promotion promotion = promotionDAO.getPromotionByID(rs.getString("promotionID"));
                String orderStatus = rs.getString("orderStatus");

                dsHoaDon.add(new Order(orderDate, employee, customer, promotion, orderStatus));
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
        String sql = "select * from Order where orderID = ?";

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

                return new Order(orderDate, employee, customer, promotion, orderStatus);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }

        return null;
    }

    // Lọc hóa đơn (thanh toán | chưa thanh toán) (có filter lọc hóa đơn)
    public ArrayList<Order> getAllOrderByStatus(String orderStatus) {
        ArrayList<Order> dsHoaDon = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Order where orderStatus = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, orderStatus);
            rs = ps.executeQuery();

            while(rs.next()){
                LocalDateTime orderDate = rs.getTimestamp("orderDate").toLocalDateTime();
                Employee employee = employeeDAO.getEmployeeByID(rs.getString("employeeID"));
                Customer customer = customerDAO.getCustomerByID(rs.getString("customerID"));
                Promotion promotion = promotionDAO.getPromotionByID(rs.getString("promotionID"));

                dsHoaDon.add(new Order(orderDate, employee, customer, promotion, orderStatus));
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
    public ArrayList<Object> thanhToanHoaDon(String orderID){
        Connection con = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        String sql = " {call sp_PayOrder(?)} ";
        ArrayList<Object> informations = new ArrayList<>();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        try {
            con = connectDB.getConnection();
            cs = con.prepareCall(sql);
            cs.setString(1, orderID);
            rs = cs.executeQuery();

            if(rs.next()){
                informations.add(rs.getTimestamp("orderDate").toLocalDateTime());
                informations.add(employeeDAO.getEmployeeByID(rs.getString("employeeID")));
                informations.add(customerDAO.getCustomerByID(rs.getString("customerID")));
                informations.add(rs.getDouble("total"));
                informations.add(rs.getString("orderStatus"));
                informations.add(rs.getString("description"));
                informations.add(roomTypeDAO.getRoomTypeByID(rs.getString("roomTypeID")));
                informations.add(rs.getTimestamp("checkInDate").toLocalDateTime());
                informations.add(rs.getTimestamp("checkOutDate").toLocalDateTime());
                informations.add(rs.getString("bookingType"));
            }

            return informations;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
