package bus;

import dao.OrderDAO;
import dao.OrderDetailRoomDAO;
import dao.OrderDetailServiceDAO;
import entity.Order;
import entity.OrderDetailRoom;
import entity.OrderDetailService;
import entity.OrderPay;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class OrderBUS {
    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailRoomDAO odrDAO = new OrderDetailRoomDAO();
    private  OrderDetailServiceDAO odsDAO = new OrderDetailServiceDAO();

    private String lastError;

    public String getLastError() {
        return lastError;
    }

    public ArrayList<Order> getAllOrders() {
        return orderDAO.getAllOrder();
    }

    public ArrayList<Order> getOrdersByStatus(String status) {
        return orderDAO.getAllOrderByStatus(status);
    }

    public Order getByID(String orderID) {
        return orderDAO.getOrderByID(orderID);
    }

    public ArrayList<OrderDetailRoom> getRoomLines(String orderID) {
        return odrDAO.getAllOrderDetailRoomByOrderID(orderID);
    }

    public ArrayList<OrderDetailService> getServiceLines(String orderID) {
        return odsDAO.getAllOrderDetailServiceByOrderID(orderID);
    }

    public ArrayList<Order> searchByCustomerID(String status, String customerIDKeyword) {
        ArrayList<Order> dsHD = (ArrayList<Order>) getOrdersByStatus(status);
        if (customerIDKeyword == null || customerIDKeyword.isBlank()) return dsHD;

        String kw = customerIDKeyword.trim().toLowerCase();
        return (ArrayList<Order>) dsHD.stream()
                .filter(o -> o.getCustomer() != null
                        && o.getCustomer().getCustomerID() != null
                        && o.getCustomer().getCustomerID().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    // Thanh toán: gọi thủ tục sp_PayOrder trong DAO
    public ArrayList<OrderPay> payOrder(String orderID) {
        return orderDAO.thanhToanHoaDon(orderID);
    }
}
