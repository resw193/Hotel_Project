package dao;

import entity.Service;
import connectDB.ConnectDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {
    private ConnectDB connectDB;

    public ServiceDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    // Hàm helper: Lấy danh sách Service theo SQL và tham số
    private List<Service> getServices(String sql, Object... params) {
        List<Service> services = new ArrayList<>();
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    services.add(new Service(
                        rs.getString("serviceID"),
                        rs.getString("serviceName"),
                        rs.getString("serviceType"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("imgSource")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return services;
    }

    // Lấy tất cả dịch vụ
    public List<Service> getAllServices() {
        return getServices("SELECT * FROM Service");
    }

    // Lấy dịch vụ theo ID
    public Service getServiceByID(String serviceID) {
        List<Service> result = getServices("SELECT * FROM Service WHERE serviceID = ?", serviceID);
        return result.isEmpty() ? null : result.get(0);
    }

    // Lấy dịch vụ theo loại
    public List<Service> getAllServiceByType(String serviceType) {
        return getServices("SELECT * FROM Service WHERE serviceType = ?", serviceType);
    }

    // Thêm dịch vụ
    public boolean addService(Service service) {
        String sql = "INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql,
                service.getServiceName(),
                service.getPrice(),
                service.getQuantity(),
                service.getServiceType(),
                service.getImgSource()
        );
    }

    // Cập nhật số lượng
    public boolean updateQuantityService(int quantity, String serviceID) {
        return executeUpdate("UPDATE Service SET quantity = ? WHERE serviceID = ?", quantity, serviceID);
    }

    // Cập nhật tên và giá
    public boolean updateInformationService(Service service) {
        return executeUpdate("UPDATE Service SET serviceName = ?, price = ? WHERE serviceID = ?",
                service.getServiceName(), service.getPrice(), service.getServiceID());
    }

    // Xóa dịch vụ
    public boolean removeServiceByID(String serviceID) {
        return executeUpdate("DELETE FROM Service WHERE serviceID = ?", serviceID);
    }

    // Helper: Thực thi INSERT/UPDATE/DELETE
    private boolean executeUpdate(String sql, Object... params) {
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
