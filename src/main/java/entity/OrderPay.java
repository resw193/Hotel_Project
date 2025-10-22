package entity;

import java.time.LocalDateTime;

public class OrderPay {
    private Order order;
    private String description;
    private RoomType roomType;
    private LocalDateTime bookingDate;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String bookingType;
    private String serviceName;
    private int serviceQuantity;

    public OrderPay() {

    }

    public OrderPay(Order order, String description, RoomType roomType, LocalDateTime bookingDate, LocalDateTime checkInDate, LocalDateTime checkOutDate,
                    String bookingType, String serviceName, int serviceQuantity) {
        this.order = order;
        this.description = description;
        this.roomType = roomType;
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingType = bookingType;
        this.serviceName = serviceName;
        this.serviceQuantity = serviceQuantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServiceQuantity() {
        return serviceQuantity;
    }

    public void setServiceQuantity(int serviceQuantity) {
        this.serviceQuantity = serviceQuantity;
    }

    @Override
    public String toString() {
        return "OrderPay{" +
                "order=" + order +
                ", description='" + description + '\'' +
                ", roomType=" + roomType +
                ", bookingDate=" + bookingDate +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", bookingType='" + bookingType + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceQuantity=" + serviceQuantity +
                '}';
    }
}
