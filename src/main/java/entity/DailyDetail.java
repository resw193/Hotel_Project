package entity;

public class DailyDetail {
    private int soLuongHoaDon;
    private double roomRevenue;
    private double serviceRevenue;
    private double totalRevenue;

    public DailyDetail(int soLuongHoaDon, double roomRevenue, double serviceRevenue, double totalRevenue) {
        this.soLuongHoaDon = soLuongHoaDon;
        this.roomRevenue = roomRevenue;
        this.serviceRevenue = serviceRevenue;
        this.totalRevenue = totalRevenue;
    }

    public int getSoLuongHoaDon() {
        return soLuongHoaDon;
    }

    public void setSoLuongHoaDon(int soLuongHoaDon) {
        this.soLuongHoaDon = soLuongHoaDon;
    }

    public double getRoomRevenue() {
        return roomRevenue;
    }

    public void setRoomRevenue(double roomRevenue) {
        this.roomRevenue = roomRevenue;
    }

    public double getServiceRevenue() {
        return serviceRevenue;
    }

    public void setServiceRevenue(double serviceRevenue) {
        this.serviceRevenue = serviceRevenue;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}