package entity;


// Entity này dùng để thống kê dịch vụ đã sử dụng từ startTime -> endTime
public class ServiceRanking {
    private String serviceName;
    private double totalQuantity;
    private double totalRevenue;

    public ServiceRanking(String serviceName, double totalQuantity, double totalRevenue) {
        this.serviceName = serviceName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @Override
    public String toString() {
        return "ServiceRanking{" +
                "serviceName='" + serviceName + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
}
