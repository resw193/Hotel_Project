package DAO;

import Entity.Account;
import Entity.Employee;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {
    private ConnectDB connectDB;
    private EmployeeDAO employeeDAO;

    public AccountDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();

        this.employeeDAO = new EmployeeDAO();

    }

    // Check username, password --> Phần login
    public boolean checkAuthAccount(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql =  "select * from Account";

        try {
            conn = connectDB.getConnection();
//            if (conn == null) {
//                System.out.println("Connection is null!");
//                return false;
//            }
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()){
                String us = rs.getString("username");
                String pw = rs.getString("password");

                if (us.equals(username) && pw.equals(password)) {
                    System.out.println("Auth success for user: " + username);
                    return true;
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, rs);
        }

        return false;
    }

    // Lấy ra Account dựa trên username --> Sẽ có được thông tin về employee (vì có employeeID)
    public Account getAccountByUsername(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql =  "select * from Account where username = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if(rs.next()){
                String us = rs.getString("username");
                String pw = rs.getString("password");
                String employeeID = rs.getString("employeeID"); // Lấy ra nhân viên theo employeeID

                Employee employee = employeeDAO.getEmployeeByID(employeeID); // Lấy ra nhân viên theo employeeID
                Account account = new Account(us, pw, employee);
                return account;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
        return null;
    }

    // Khi đăng nhập vào hệ thống thì hệ thống sẽ có được thông của Employee hiện tại (lấy ra đc employeeID) --> Thay đổi password
    // Update (change) password (Phần thông tin cá nhân (thay đổi mật khẩu) --> FormChangePassword)
    public boolean changePassword(String employeeID, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update Account set password = ? where employeeID = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setString(2, employeeID);

            int rowAffected = ps.executeUpdate();
            if(rowAffected > 0){
                System.out.println("Updated password successfully!");
                return true;
            }
            else{
                System.out.println("Failed to update password!");
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Connect Error!");
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // update account bằng username
    public boolean updatePassword(String username, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update Account set password = ? where username = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setString(2, username);

            int rowAffected = ps.executeUpdate();
            if(rowAffected > 0){
                System.out.println("Updated password successfully!");
                return true;
            }
            else{
                System.out.println("Failed to update password!");
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Connect Error!");
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }
}
