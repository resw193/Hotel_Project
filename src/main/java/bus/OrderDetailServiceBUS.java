package bus;

import dao.OrderDetailServiceDAO;
import entity.OrderDetailService;

import java.util.ArrayList;

public class OrderDetailServiceBUS {
    private OrderDetailServiceDAO orderDetailServiceDAO = new OrderDetailServiceDAO();

    public ArrayList<OrderDetailService> getbyOrderID(String orderID){
        return orderDetailServiceDAO.getAllOrderDetailServiceByOrderID(orderID);
    }


}
