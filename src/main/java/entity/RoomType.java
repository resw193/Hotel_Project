package entity;

import java.util.Objects;

public class RoomType {
    private String roomTypeID;
    private String typeName;
    private double pricePerHour;
    private double pricePerNight;
    private double pricePerDay;
    private double lateFeePerHour;

    public RoomType() {}

    public RoomType(String roomTypeID, String typeName, double pricePerHour,
                    double pricePerNight, double pricePerDay, double lateFeePerHour) {
        setRoomTypeID(roomTypeID);
        setTypeName(typeName);
        setPricePerHour(pricePerHour);
        setPricePerNight(pricePerNight);
        setPricePerDay(pricePerDay);
        setLateFeePerHour(lateFeePerHour);
    }


    public String getRoomTypeID() {
        return roomTypeID;
    }

    public void setRoomTypeID(String roomTypeID) {
    	if(roomTypeID.trim().isEmpty())
    		throw new RuntimeException("Mã không được rỗng!");
    	this.roomTypeID = roomTypeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        if (typeName == null || typeName.trim().length() < 3) {
            throw new IllegalArgumentException("TÃªn loáº¡i phÃ²ng pháº£i cÃ³ Ã­t nháº¥t 3 kÃ½ tá»±.");
        }
        this.typeName = typeName.trim();
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        if (pricePerHour <= 0) {
            throw new IllegalArgumentException("GiÃ¡ thuÃª theo giá»� pháº£i lá»›n hÆ¡n 0.");
        }
        this.pricePerHour = pricePerHour;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("GiÃ¡ thuÃª theo Ä‘Ãªm pháº£i lá»›n hÆ¡n 0.");
        }
        this.pricePerNight = pricePerNight;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        if (pricePerDay <= 0) {
            throw new IllegalArgumentException("GiÃ¡ thuÃª theo ngÃ y pháº£i lá»›n hÆ¡n 0.");
        }
        this.pricePerDay = pricePerDay;
    }

    public double getLateFeePerHour() {
        return lateFeePerHour;
    }

    public void setLateFeePerHour(double lateFeePerHour) {
        if (lateFeePerHour <= 0) {
            throw new IllegalArgumentException("PhÃ­ tráº£ phÃ²ng trá»… theo giá»� pháº£i lá»›n hÆ¡n 0.");
        }
        this.lateFeePerHour = lateFeePerHour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomTypeID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RoomType other = (RoomType) obj;
        return Objects.equals(roomTypeID, other.roomTypeID);
    }

    public Object[] getObjects() {
        return new Object[] {roomTypeID, typeName, pricePerHour, pricePerNight, pricePerDay, lateFeePerHour};
    }

    @Override
    public String toString() {
        return "RoomType [roomTypeID=" + roomTypeID 
                + ", typeName=" + typeName 
                + ", pricePerHour=" + pricePerHour
                + ", pricePerNight=" + pricePerNight 
                + ", pricePerDay=" + pricePerDay 
                + ", lateFeePerHour=" + lateFeePerHour + "]";
    }
}
