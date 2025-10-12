package DAO;

import connectDB.ConnectDB;

public class OrderDetailServiceDAO {
    private ConnectDB connectDB;

    public OrderDetailServiceDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }


}
