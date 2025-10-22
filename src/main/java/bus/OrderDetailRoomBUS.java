package bus;

import dao.OrderDetailRoomDAO;
import dao.OrderDetailServiceDAO;
import entity.OrderDetailRoom;
import entity.OrderDetailService;

import java.util.ArrayList;

public class OrderDetailRoomBUS {
    private OrderDetailRoomDAO orderDetailRoomDAO = new OrderDetailRoomDAO();

    public ArrayList<OrderDetailRoom> getbyOrderID(String orderID){
        return orderDetailRoomDAO.getAllOrderDetailRoomByOrderID(orderID);
    }
}
