package bus;

import com.beust.ah.A;
import dao.ServiceRankingDAO;
import entity.ServiceRanking;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ServiceRankingBUS {
    private ServiceRankingDAO serviceRankingDAO = new ServiceRankingDAO();

    public ArrayList<ServiceRanking> getByRange(LocalDateTime start, LocalDateTime end) {
        return serviceRankingDAO.thongKeDichVuTheoThoiGian(start, end);
    }

    public ArrayList<ServiceRanking> getTopByRange(LocalDateTime start, LocalDateTime end, int topN) {
        return serviceRankingDAO.getTopByRange(start, end, topN);
    }
}
