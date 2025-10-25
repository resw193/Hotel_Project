package bus;

import dao.BookingTypeRevenueDAO;
import entity.BookingTypeRevenue;

import java.time.LocalDateTime;
import java.util.ArrayList;

//
public class BookingTypeRevenueBUS {
    private BookingTypeRevenueDAO bookingTypeRevenueDAO = new BookingTypeRevenueDAO();

    public ArrayList<BookingTypeRevenue> stats(LocalDateTime start, LocalDateTime end) {
        return bookingTypeRevenueDAO.thongKeTheoKieuDatPhong(start, end);
    }
}
