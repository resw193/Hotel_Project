package DAO;

import Entity.Customer;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CustomerDAO {
    private ConnectDB connectDB;

    public CustomerDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public ArrayList<Customer> getAllCustomer(){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Customer";
        ArrayList<Customer> dsKH = new ArrayList();

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()){
                String customerID = rs.getString("customerID");
                String fullName = rs.getString("fullName");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                LocalDateTime regisDate = rs.getTimestamp("regisDate").toLocalDateTime();
                String idCard = rs.getString("idCard");
                int loyaltyPoints = rs.getInt("loyaltyPoints");

                dsKH.add(new Customer(customerID, fullName, phone, email, regisDate, idCard, loyaltyPoints));
            }

            return dsKH;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }

    // Lấy ra khách hàng theo customerID
    public Customer getCustomerByID(String customerID){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Customer where customerID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, customerID);
            rs = ps.executeQuery();

            if(rs.next()){
                String fullName = rs.getString("fullName");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                LocalDateTime regisDate = rs.getTimestamp("regisDate").toLocalDateTime();
                String idCard = rs.getString("idCard");
                int loyaltyPoints = rs.getInt("loyaltyPoints");

                return new Customer(customerID, fullName, phone, email, regisDate, idCard, loyaltyPoints);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }

        return null;
    }

    // Thêm khách hàng
    public boolean addCustomer(Customer customer){
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "insert into Customer values(?,?,?,?,?,?)";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getRegisDate().toString());
            ps.setString(5, customer.getIdCard());
            ps.setInt(6, customer.getLoyaltyPoint());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Cập nhật thông tin khách hàng
    public boolean updateCustomer(Customer customer){
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "update Customer set fullName = ?, phone = ?, email = ? where customerID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getCustomerID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Lấy ra khách hàng theo số CCCD (idCard)
    public Customer getCustomerByCCCD(String idCard){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Customer where idCard = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, idCard);
            rs = ps.executeQuery();

            if(rs.next()){
                String customerID = rs.getString("customerID");
                String fullName = rs.getString("fullName");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                LocalDateTime regisDate = rs.getTimestamp("regisDate").toLocalDateTime();
                int loyaltyPoints = rs.getInt("loyaltyPoints");

                return new Customer(customerID, fullName, phone, email, regisDate, idCard, loyaltyPoints);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }

        return null;
    }

    // Lấy ra khách hàng theo phone
    public Customer getCustomerByPhone(String phone){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Customer where phone = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();

            if(rs.next()){
                String customerID = rs.getString("customerID");
                String fullName = rs.getString("fullName");
                String idCard = rs.getString("idCard");
                String email = rs.getString("email");
                LocalDateTime regisDate = rs.getTimestamp("regisDate").toLocalDateTime();
                int loyaltyPoints = rs.getInt("loyaltyPoints");

                return new Customer(customerID, fullName, phone, email, regisDate, idCard, loyaltyPoints);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }

        return null;
    }
}
