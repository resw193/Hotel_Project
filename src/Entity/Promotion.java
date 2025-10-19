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
        if (promotionName == null || promotionName.trim().isEmpty()) {
            throw new IllegalArgumentException("TÃªn khuyáº¿n mÃ£i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
        }
        this.promotionName = promotionName.trim();
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        if (discount <= 0) {
            throw new IllegalArgumentException("Khuyáº¿n mÃ£i pháº£i > 0");
        }
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Sá»‘ lÆ°á»£ng khuyáº¿n mÃ£i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
        }
        this.quantity = quantity;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Ä�á»‹nh dáº¡ng khÃ´ng há»£p lá»‡");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thá»�i gian khÃ´ng há»£p lá»‡ (pháº£i tá»« hiá»‡n táº¡i trá»Ÿ Ä‘i)");
        }
        if (this.endTime != null && startTime.isAfter(this.endTime)) {
            throw new IllegalArgumentException("Thá»�i gian khÃ´ng há»£p lá»‡ (báº¯t Ä‘áº§u pháº£i trÆ°á»›c ngÃ y káº¿t thÃºc)");
        }
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null) {
            throw new IllegalArgumentException("Ä�á»‹nh dáº¡ng khÃ´ng há»£p lá»‡");
        }
        if (endTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thá»�i gian khÃ´ng há»£p lá»‡ (pháº£i tá»« hiá»‡n táº¡i trá»Ÿ Ä‘i)");
        }
        if (this.startTime != null && endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("Thá»�i gian khÃ´ng há»£p lá»‡ (káº¿t thÃºc pháº£i sau ngÃ y báº¯t Ä‘áº§u)");
        }
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
