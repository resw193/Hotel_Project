package entity;

import java.time.LocalDateTime;

public class OrderDetailRoom {
	private Order order;
	private Room room;
	private double roomFee;
	private LocalDateTime bookingDate;
	private LocalDateTime checkInDate;
	private LocalDateTime checkOutDate;
	private String bookingType;
	private String status;
	
	public OrderDetailRoom() {

	}

	public OrderDetailRoom(Order order, Room room, LocalDateTime bookingDate, LocalDateTime checkInDate,
			LocalDateTime checkOutDate, String bookingType, String status) {
		super();
		this.order = order;
		this.room = room;
		this.bookingDate = bookingDate;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.bookingType = bookingType;
		this.status = status;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		if(order == null) throw new RuntimeException("Hóa đơn không được để trống");
		this.order = order;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		if(room == null) throw new RuntimeException("Phòng không được để trống");
		this.room = room;
	}

	public double getRoomFee() {
		return roomFee;
	}

	public void setRoomFee(double roomFee) {
		this.roomFee = roomFee;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		if(bookingDate.isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Ngày đặt phòng phải từ ngày hiện tại");
		}
		this.bookingDate = bookingDate;
	}

	public LocalDateTime getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(LocalDateTime checkInDate) {
		if(checkInDate.isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Ngay check-in phải từ ngày hiện tại");
		}
		this.checkInDate = checkInDate;
	}

	public LocalDateTime getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(LocalDateTime checkOutDate) {
		if(checkOutDate.isBefore(LocalDateTime.now()) || checkOutDate.isBefore(checkInDate)) {
			throw new RuntimeException("Ngay check-out phải từ ngày hiện tại và sau ngày check-in");
		}
		this.checkOutDate = checkOutDate;
	}

	public String getBookingType() {
		return bookingType;
	}

	public void setBookingType(String bookingType) {
		if(bookingType.equalsIgnoreCase("Giờ") || bookingType.equalsIgnoreCase("Đêm") || bookingType.equalsIgnoreCase("Ngày"))
			this.bookingType = bookingType;
		else 
			throw new RuntimeException("Kiểu đặt phòng sai quy định");
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "OrderDetailRoom [order=" + order + ", lineTotal=" + roomFee + ", bookingDate=" + bookingDate
				+ ", checkInDate=" + checkInDate + ", checkOutDate=" + checkOutDate + ", bookingType=" + bookingType
				+ ", status=" + status + "]";
	}
	
	
	
}
