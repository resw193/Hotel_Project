package bus;

import dao.ServiceRankingDAO;
import entity.ServiceRanking;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ServiceRankingBUS {
    private ServiceRankingDAO serviceRankingDAO = new ServiceRankingDAO();

    public ArrayList<ServiceRanking> getByRange(LocalDateTime start, LocalDateTime end) {
        return serviceRankingDAO.thongKeDichVuTheoThoiGian(start, end);
    }
}
