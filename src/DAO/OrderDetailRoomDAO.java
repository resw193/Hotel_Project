package DAO;

import connectDB.ConnectDB;

import java.sql.Connection;

public class OrderDetailRoomDAO {
    private ConnectDB connectDB;

    public OrderDetailRoomDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }



}
