package dao;

import entity.Employee;
import entity.EmployeeType;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDAO {
    private ConnectDB connectDB;
    private EmployeeTypeDAO employeeTypeDAO;

    public EmployeeDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();

        this.employeeTypeDAO = new EmployeeTypeDAO();
    }

    // Lấy ra employee theo ID
    public Employee getEmployeeByID(String employeeID){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Employee where employeeID = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, employeeID);
            rs = ps.executeQuery();

            if(rs.next()){
                String fullName = rs.getString("fullName");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                EmployeeType empType = employeeTypeDAO.getEmployeeTypeByID(rs.getString("employeeTypeID"));
                String imgSource = rs.getString("imgSource");
                boolean gioiTinh = rs.getBoolean("gender");

                return new Employee(employeeID, fullName, phone, email, empType, imgSource, gioiTinh);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return null;
    }

    // cập nhât thông tin nhân viên
    public boolean updateProfile(Employee e) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = """
                update Employee 
                set fullName = ?, phone = ?, email = ?, gender = ?, employeeTypeID = ?, imgSource = ?
                where employeeID = ?
                """;
        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, e.getFullName());
            ps.setString(2, e.getPhone());
            ps.setString(3, e.getEmail());
            ps.setBoolean(4, e.isGender());
            ps.setString(5, e.getEmployeeType() != null ? e.getEmployeeType().getTypeID() : null);
            ps.setString(6, e.getImgSource());
            ps.setString(7, e.getEmployeeID());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // cập nhật lại ảnh đại diện của nhân viên
    public boolean updateAvatar(String employeeID, String imgSource) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update Employee set imgSource = ? where employeeID = ?";
        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, imgSource);
            ps.setString(2, employeeID);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }
}
