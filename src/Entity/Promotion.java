package Entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Promotion {   
    private String promotionID;        
    private String promotionName;       
    private double discount;            
    private int quantity;              
    private LocalDateTime startTime;   
    private LocalDateTime endTime;      

    public Promotion() {
        
    }

    public Promotion(String promotionID, String promotionName, double discount, int quantity, LocalDateTime startTime,
			LocalDateTime endTime) {
		super();
		this.promotionID = promotionID;
		this.promotionName = promotionName;
		this.discount = discount;
		this.quantity = quantity;
		this.startTime = startTime;
		this.endTime = endTime;
	}



	public String getPromotionID() {
        return promotionID;
    }

    public void setPromotionID(String promotionID) {
        this.promotionID = promotionID;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName.trim();
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {

        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Promotion [promotionID=" + promotionID + ", promotionName=" + promotionName
                + ", discount=" + discount + ", quantity=" + quantity
                + ", startTime=" + (startTime != null ? startTime.format(fmt) : null)
                + ", endTime=" + (endTime != null ? endTime.format(fmt) : null);
    }
}
