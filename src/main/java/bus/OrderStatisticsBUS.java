package bus;

import dao.OrderStatisticsDAO;
import entity.DailyDetail;

import java.time.LocalDate;

public class OrderStatisticsBUS {
    private OrderStatisticsDAO orderStatisticsDAO = new OrderStatisticsDAO();

    public DailyDetail getDailyDetail(LocalDate date) {
        if (date == null) return null;

        return orderStatisticsDAO.thongKeChiTietTheoNgay(date.atStartOfDay());
    }
}
