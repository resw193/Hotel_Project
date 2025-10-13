package DAO;

import Entity.Employee;
import Entity.EmployeeType;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeTypeDAO {
    private ConnectDB connectDB;

    public EmployeeTypeDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public EmployeeType getEmployeeTypeByID(String employeeTypeID){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from EmployeeType where typeID = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, employeeTypeID);
            rs = ps.executeQuery();

            if(rs.next()){
                String typeID = rs.getString("typeID");
                String typeName = rs.getString("typeName");
                String description = rs.getString("description");

                return new EmployeeType(typeID, typeName, description);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return null;
    }
}
