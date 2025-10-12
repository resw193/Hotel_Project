package Entity;

public class OrderStatistics {
    private int soLuongHoaDon;
    private double tongThuNhap;

    public OrderStatistics() {

    }

    public OrderStatistics(int soLuongHoaDon, double tongThuNhap) {
        this.soLuongHoaDon = soLuongHoaDon;
        this.tongThuNhap = tongThuNhap;
    }

    public int getSoLuongHoaDon() {
        return soLuongHoaDon;
    }

    public void setSoLuongHoaDon(int soLuongHoaDon) {
        this.soLuongHoaDon = soLuongHoaDon;
    }

    public double getTongThuNhap() {
        return tongThuNhap;
    }

    public void setTongThuNhap(double tongThuNhap) {
        this.tongThuNhap = tongThuNhap;
    }

    @Override
    public String toString() {
        return "OrderStatistics{" +
                "soLuongHoaDon=" + soLuongHoaDon +
                ", tongThuNhap=" + tongThuNhap +
                '}';
    }
}
