package dao;

import entity.Service;
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
                String serviceType = rs.getString("serviceType");
                int quantity = rs.getInt("quantity");
                String imgSource = rs.getString("imgSource");

                dsDichVu.add(new Service(serviceID, serviceName, serviceType, quantity, price, imgSource));
            }

            return dsDichVu;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }
    // Lấy ra sản phẩm theo ID
    public Service getServiceByID(String serviceID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Service where serviceID = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, serviceID);
            rs = ps.executeQuery();

            if(rs.next()) {
                String serviceName = rs.getString("serviceName");
                double price = rs.getDouble("price");
                String serviceType = rs.getString("serviceType");
                int quantity = rs.getInt("quantity");
                String imgSource = rs.getString("imgSource");

                return new Service(serviceID, serviceName, serviceType, quantity, price, imgSource);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }

        return null;
    }

    // Lấy ra sản phẩm theo loại (Food | Drink) --> Lọc theo drink hoặc food
    public ArrayList<Service> getAllServiceByType(String serviceType){
        ArrayList<Service> serviceList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Service where serviceType = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, serviceType);
            rs = ps.executeQuery();

            while(rs.next()){
                String serviceID = rs.getString("serviceID");
                String serviceName = rs.getString("serviceName");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String imageSource = rs.getString("imgSource");

                serviceList.add(new Service(serviceID, serviceName, serviceType, quantity, price, imageSource));
            }

            return serviceList;
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
        String sql = "INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (?, ?, ?, ?, ?)";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, service.getServiceName());
            ps.setDouble(2, service.getPrice());
            ps.setInt(3, service.getQuantity());
            ps.setString(4, service.getServiceType());
            ps.setString(5, service.getImgSource());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Cập nhật số lượng của service (update table) --> hiển thị formUpdateService --> nhập đầy đủ thông tin --> Tạo service mới và gọi hàm updateService (buttonAdd)
    public boolean updateQuantityService(int quantity, String serviceID) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "update Service set quantity = ? where serviceID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setString(2, serviceID);

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Cập nhật lại tên và giá của service (update table) --> hiển thị formUpdateService --> nhập đầy đủ thông tin --> Tạo service mới và gọi hàm updateService
    public boolean updateInformationService(Service service) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "update Service set serviceName = ?, price = ? where serviceID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, service.getServiceName());
            ps.setDouble(2, service.getPrice());
            ps.setString(3, service.getServiceID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Xóa service theo ID
    public boolean removeServiceByID(String serviceID){
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "delete from Service where serviceID = ?";

        try {
            conn = connectDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, serviceID);

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }
} 