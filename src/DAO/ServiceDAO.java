package DAO;

import Entity.Service;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServiceDAO {
    private ConnectDB connectDB;

    public ServiceDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    // Lấy ra toàn bộ Service và hiển thi lên giao diện
    public ArrayList<Service> getAllServices() {
        ArrayList<Service> dsDichVu = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Service";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()) {
                String serviceID = rs.getString("serviceID");
                String serviceName = rs.getString("serviceName");
                double price = rs.getDouble("price");
                String description = rs.getString("description");
                int quantity = rs.getInt("quantity");
                String imgSource = rs.getString("imgSource");

                dsDichVu.add(new Service(serviceID, serviceName, description, quantity, price, imgSource));
            }
            return dsDichVu;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }

    // Thêm Service (insert into) (hiển thị formAddService --> Nhập đầy đủ thông tin --> Tạo service và gọi hàm addService(Service service))
    public boolean addService(Service service) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO Service (serviceName, price, quantity) VALUES (?, ?, ?)";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, service.getServiceName());
            ps.setDouble(2, service.getPrice());
            ps.setInt(3, service.getQuantity());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Cập nhật số lượng của service (update table) --> hiển thị formUpdateService --> nhập đầy đủ thông tin --> Tạo service mới và gọi hàm updateService
    public boolean updateService(Service service) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "update Service set price = ?, quantity = ? where serviceID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setDouble(1, service.getPrice());
            ps.setInt(2, service.getQuantity());
            ps.setString(3, service.getServiceID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }
}
