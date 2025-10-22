package entity;

import java.time.LocalDateTime;

public class Order {
	private String orderID;
	private LocalDateTime orderDate;
	private double total;
	private Employee employee;
	private Customer customer;
	private Promotion promotion;
	private String orderStatus;

	public Order() {
		
	}

	public Order(String orderID, LocalDateTime orderDate, Employee employee, Customer customer, Promotion promotion, String orderStatus) {
		this.orderID = orderID;
		this.orderDate = orderDate;
		this.employee = employee;
		this.customer = customer;
		this.promotion = promotion;
		this.orderStatus = orderStatus;
	}

	public Order(LocalDateTime orderDate, Employee employee, Customer customer, Promotion promotion, String orderStatus) {
		this.orderDate = orderDate;
		this.employee = employee;
		this.customer = customer;
		this.promotion = promotion;
		this.orderStatus = orderStatus;
	}

	// -----------------------------------------------------------------------------------------------------------------------------
	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}



	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString() {
		return "Order [orderID=" + orderID + ", orderDate=" + orderDate + ", total=" + total + ", employee=" + employee
				+ "]";
	}
	
	
}
