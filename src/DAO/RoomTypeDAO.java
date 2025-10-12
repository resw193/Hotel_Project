package DAO;

import Entity.RoomType;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomTypeDAO {
    private ConnectDB connectDB;

    public RoomTypeDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public RoomType getRoomTypeByID(String roomTypeID){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from RoomType where roomTypeID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, roomTypeID);
            rs = ps.executeQuery();

            if(rs.next()){
                String typeID = rs.getString("roomTypeID");
                String typeName = rs.getString("typeName");
                double pricePerHour = rs.getDouble("pricePerHour");
                double pricePerNight = rs.getDouble("pricePerNight");
                double pricePerDay = rs.getDouble("pricePerDay");
                double lateFeePerHour = rs.getDouble("lateFeePerHour");

                return new RoomType(typeID, typeName, pricePerHour, pricePerNight, pricePerDay, lateFeePerHour);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return null;
    }

    // Lấy ra RoomType theo typeName
    public RoomType getRoomTypeByTypeName(String typeName){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from RoomType where typeName = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, typeName);
            rs = ps.executeQuery();

            if(rs.next()){
                String typeID = rs.getString("roomTypeID");
                double pricePerHour = rs.getDouble("pricePerHour");
                double pricePerNight = rs.getDouble("pricePerNight");
                double pricePerDay = rs.getDouble("pricePerDay");
                double lateFeePerHour = rs.getDouble("lateFeePerHour");

                return new RoomType(typeID, typeName, pricePerHour, pricePerNight, pricePerDay, lateFeePerHour);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

        return null;
    }
}
