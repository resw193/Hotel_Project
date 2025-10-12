package DAO;

import Entity.Employee;
import Entity.EmployeeType;
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

    public Employee getEmployeeByID(String employeeTypeID){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Employee where employeeID = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, employeeTypeID);
            rs = ps.executeQuery();

            if(rs.next()){
                String id = rs.getString("employeeID");
                String fullName = rs.getString("fullName");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                EmployeeType empType = employeeTypeDAO.getEmployeeTypeByID(rs.getString("employeeTypeID"));

                return new Employee(id, fullName, phone, email, empType);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return null;
    }
}
