package DAO;

import Entity.Promotion;
import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PromotionDAO {
    private ConnectDB connectDB;

    public PromotionDAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    // Lấy ra toàn bộ khuyến mãi
    public ArrayList<Promotion> getAllPromotions() {
        ArrayList<Promotion> dsKhuyenMai = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Promotion";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String promotionID = rs.getString("promotionID");
                String promotionName = rs.getString("promotionName");
                double discount = rs.getDouble("discount");
                LocalDateTime startTime = rs.getTimestamp("startTime").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("endTime").toLocalDateTime();
                int quantity = rs.getInt("quantity");

                dsKhuyenMai.add(new Promotion(promotionID, promotionName, discount, quantity, startTime, endTime));
            }

            return dsKhuyenMai;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }
    }

    // Lấy ra promotion theo ID
    public Promotion getPromotionByID(String promotionID) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Promotion where promotionID = ?";

        try{
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, promotionID);
            rs = ps.executeQuery();

            if(rs.next()) {
                String promotionName = rs.getString("promotionName");
                double discount = rs.getDouble("discount");
                LocalDateTime startTime = rs.getTimestamp("startTime").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("endTime").toLocalDateTime();
                int quantity = rs.getInt("quantity");

                return new Promotion(promotionID, promotionName, discount, quantity, startTime, endTime);
            }
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            connectDB.close(ps, rs);
        }

        return null;
    }


    // Thêm khuyến mãi
    public boolean addPromotion(Promotion promotion) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "insert into Promotion values(?,?,?,?,?)";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, promotion.getPromotionName());
            ps.setDouble(2, promotion.getDiscount());
            ps.setString(3, promotion.getStartTime().toString());
            ps.setString(4, promotion.getEndTime().toString());
            ps.setInt(5, promotion.getQuantity());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }

    // Cập nhật thông tin khuyến mãi (cập nhật lại promotionName và discount %)
    public boolean updatePromotion(Promotion promotion) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "update Promotion set promotionName = ?, discount = ?, startTime = ?, endTime = ? where promotionID = ?";

        try {
            con = connectDB.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, promotion.getPromotionName());
            ps.setDouble(2, promotion.getDiscount());
            ps.setString(3, promotion.getStartTime().toString());
            ps.setString(4, promotion.getEndTime().toString());
            ps.setString(5, promotion.getPromotionID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            connectDB.close(ps, null);
        }
    }
}