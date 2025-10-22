package bus;

import dao.EmployeeDAO;
import entity.Account;
import entity.Employee;

public class EmployeeBUS {

    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private AccountBUS accountBUS = new AccountBUS();

    public Employee getByID(String employeeID){
        return employeeDAO.getEmployeeByID(employeeID);
    }

    public Employee getByUsername(String username) {
        username = normalize(username);
        if (username.isEmpty()) return null;

        Account acc = accountBUS.getAccountByUsername(username);
        return acc != null ? acc.getEmployee() : null;
    }

    // cập nhật thông tin nhân vien6
    public boolean updateProfile(Employee e) {
        if (e == null || isBlank(e.getEmployeeID()))
            throw new IllegalArgumentException("Thiếu mã nhân viên.");

        return employeeDAO.updateProfile(e);
    }

    // thay đổi ảnh đại diện nhân viên
    public boolean updateAvatar(String employeeID, String imgSource) {
        if (isBlank(employeeID))
            throw new IllegalArgumentException("Thiếu mã nhân viên.");

        return employeeDAO.updateAvatar(employeeID, imgSource == null ? "" : imgSource.trim());
    }

    private static boolean isBlank(String s){
        return s == null || s.trim().isEmpty();
    }

    private static String check(String s) {
        return s == null ? "" : s.trim();
    }

    private static String normalize(String s) {
        return check(s).toLowerCase();
    }
}
