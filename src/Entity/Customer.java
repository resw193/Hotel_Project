package Entity;

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

    public Customer(String customerID, String fullName, String phone, String email, LocalDateTime regisDate,
            String idCard, int loyaltyPoint) {
        setCustomerID(customerID);
        setFullName(fullName);
        setPhone(phone);
        setEmail(email);
        setRegisDate(regisDate);
        setIdCard(idCard);
        setLoyaltyPoint(loyaltyPoint);
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
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được rỗng");
        }
        this.fullName = fullName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("^\\d{10,11}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        this.email = email;
    }

    public LocalDateTime getRegisDate() {
        return regisDate;
    }

    public void setRegisDate(LocalDateTime regisDate) {
        if (regisDate == null || regisDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày đăng ký phải từ hiện tại trở đi");
        }
        this.regisDate = regisDate;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        if (idCard == null || !idCard.matches("^\\d{12}$")) {
            throw new IllegalArgumentException("CMND/CCCD không hợp lệ");
        }
        this.idCard = idCard;
    }

    public int getLoyaltyPoint() {
        return loyaltyPoint;
    }

    public void setLoyaltyPoint(int loyaltyPoint) {
        if (loyaltyPoint < 0) {
            throw new IllegalArgumentException("Điểm tích lũy không hợp lệ");
        }
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
