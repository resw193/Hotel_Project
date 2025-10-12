package Entity;

import java.util.Objects;

public class Service {
    private String serviceID;
    private String serviceName;
    private String description;
    private int quantity;
    private double price;
    private String imgSource;

    public Service() {
        
    }

    public Service(String serviceID, String serviceName, String description,
                   int quantity, double price, String imgSource) {
        setServiceID(serviceID); 
        setServiceName(serviceName);
        setPrice(price);
        setDescription(description);
        this.quantity = quantity;
        this.imgSource = imgSource;
    }
    
    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String id) {
    	if(id.trim().isEmpty()) throw new RuntimeException("Mã không được rỗng");
    	this.serviceID = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("TÃªn dá»‹ch vá»¥ khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng.");
        }
        this.serviceName = serviceName.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().length() < 15) {
            throw new IllegalArgumentException("MÃ´ táº£ dá»‹ch vá»¥ pháº£i cÃ³ Ã­t nháº¥t 15 kÃ½ tá»±.");
        }
        this.description = description.trim();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity; // náº¿u muá»‘n rÃ ng buá»™c thÃ¬ cÃ³ thá»ƒ thÃªm: quantity > 0
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("GiÃ¡ dá»‹ch vá»¥ pháº£i lá»›n hÆ¡n 0.");
        }
        this.price = price;
    }

    public String getImgSource() {
        return imgSource;
    }

    public void setImgSource(String imgSource) {
        this.imgSource = imgSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Service other = (Service) obj;
        return Objects.equals(serviceID, other.serviceID);
    }

    public Object[] getObjects() {
        return new Object[] {serviceID, serviceName, description, quantity, price, imgSource};
    }

    @Override
    public String toString() {
        return "Service [serviceID=" + serviceID 
                + ", serviceName=" + serviceName 
                + ", description=" + description
                + ", quantity=" + quantity 
                + ", price=" + price 
                + ", imgSource=" + imgSource + "]";
    }
}
