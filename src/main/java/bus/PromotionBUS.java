package bus;

import dao.PromotionDAO;
import entity.Promotion;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PromotionBUS {

    private PromotionDAO promotionDAO = new PromotionDAO();
    private String lastError;

    public String getLastError() {
        return lastError;
    }

    public ArrayList<Promotion> getAllPromotions() {
        return promotionDAO.getAllPromotions();
    }

    public Promotion getPromotionByID(String promotionID) {
        return promotionDAO.getPromotionByID(promotionID);
    }

    // thêm khuyến mãi
    public boolean addPromotion(Promotion promotion) {
        lastError = null;
        String valid = validateForAdd(promotion);
        if (valid != null) {
            lastError = valid;
            return false;
        }
        boolean ok = promotionDAO.addPromotion(promotion);
        if (!ok) lastError = "Không thể thêm khuyến mãi (lỗi CSDL).";
        return ok;
    }

    // cập nhật thông tin khuyến mãi
    public boolean updatePromotion(Promotion promotion) {
        lastError = null;
        String valid = validateForUpdate(promotion);
        if (valid != null) {
            lastError = valid;
            return false;
        }
        boolean ok = promotionDAO.updatePromotion(promotion);
        if (!ok) lastError = "Không thể cập nhật khuyến mãi (lỗi CSDL).";
        return ok;
    }

    // Xóa khuyến mãi
    public boolean deletePromotion(String promotionID) {
        return promotionDAO.deletePromotion(promotionID);
    }

    // validData
    private String validData(Promotion promotion) {
        if (promotion == null) return "Dữ liệu khuyến mãi trống.";

        // Tên
        if (promotion.getPromotionName() == null || promotion.getPromotionName().trim().length() < 3) {
            return "Tên khuyến mãi phải từ 3 ký tự.";
        }

        // Discount %
        if (Double.isNaN(promotion.getDiscount()) || promotion.getDiscount() <= 0 || promotion.getDiscount() > 100) {
            return "Tỉ lệ giảm giá phải trong (0, 100].";
        }

        LocalDateTime st = promotion.getStartTime();
        LocalDateTime en = promotion.getEndTime();
        if (st == null || en == null) return "Thời gian bắt đầu/kết thúc không được rỗng.";
        if (!en.isAfter(st)) return "Thời gian kết thúc phải sau thời gian bắt đầu.";

        return null;
    }

    private String validateForAdd(Promotion p) {
        String common = validData(p);
        if (common != null) return common;

        // Quantity khi thêm
        if (p.getQuantity() <= 0) return "Số lượng phải > 0.";
        return null;
    }

    private String validateForUpdate(Promotion p) {
        return validData(p);
    }
}
