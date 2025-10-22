package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Customer {
    private String customerID;       
    private String fullName;          
    private String phone;             
    private String email;            
    private LocalDateTime regisDate;  
    private String idCard;            
    private int loyaltyPoint;        

    public Customer() {

    }

    public Customer(String customerID, String fullName, String phone, String email, LocalDateTime regisDate, String idCard, int loyaltyPoint) {
        this.customerID = customerID;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.regisDate = regisDate;
        this.idCard = idCard;
        this.loyaltyPoint = loyaltyPoint;
    }

    public Customer(String fullName, String phone, String email, String idCard) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.idCard = idCard;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRegisDate() {
        return regisDate;
    }

    public void setRegisDate(LocalDateTime regisDate) {
        this.regisDate = regisDate;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public int getLoyaltyPoint() {
        return loyaltyPoint;
    }

    public void setLoyaltyPoint(int loyaltyPoint) {
        this.loyaltyPoint = loyaltyPoint;
    }

    @Override
    public int hashCode() {
        return customerID == null ? 0 : customerID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Customer)) return false;
        Customer other = (Customer) obj;
        return customerID != null && customerID.equals(other.customerID);
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Customer [customerID=" + customerID + ", fullName=" + fullName + ", phone=" + phone 
                + ", email=" + email + ", regisDate=" + (regisDate != null ? regisDate.format(fmt) : null) 
                + ", idCard=" + idCard + ", loyaltyPoint=" + loyaltyPoint + "]";
    }
}
