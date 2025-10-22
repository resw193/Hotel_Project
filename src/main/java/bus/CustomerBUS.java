package bus;

import dao.CustomerDAO;
import entity.Customer;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class CustomerBUS {

    private CustomerDAO customerDAO = new CustomerDAO();

    // method
    public ArrayList<Customer> getAllCus() {
        ArrayList<Customer> r = customerDAO.getAllCustomer();
        return r != null ? r : new ArrayList<>();
    }

    public ArrayList<Customer> getByLoyalty(int loyalty) {
        ArrayList<Customer> r = customerDAO.getAllCustomerByLoyaltyPoint(loyalty);
        return r != null ? r : new ArrayList<>();
    }

    public ArrayList<Customer> searchByName(String keyword) {
        String kw = normalize(keyword);
        if (kw.isEmpty()) return getAllCus();

        ArrayList<Customer> r = customerDAO.getAllCustomerByName(kw);
        return r != null ? r : new ArrayList<>();
    }

    public ArrayList<Customer> filterAndSearch(String keyword, Integer minLoyalty) {
        // loyalty
        ArrayList<Customer> customers = (minLoyalty == null || minLoyalty <= 0) ? getAllCus() : getByLoyalty(minLoyalty);
        if (customers.isEmpty()) return customers;

        // keySearch
        String kw = normalize(keyword);
        if (kw.isEmpty()) return customers;

        ArrayList<Customer> dsKH = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getFullName() != null && c.getFullName().toLowerCase().contains(kw)) {
                dsKH.add(c);
            }
        }
        return dsKH;
    }

    public Customer getById(String customerID) {
        return customerDAO.getCustomerByID(check(customerID));
    }

    public Customer getByPhone(String phone) {
        return customerDAO.getCustomerByPhone(check(phone));
    }

    public Customer getByCCCD(String idCard) {
        return customerDAO.getCustomerByCCCD(check(idCard));
    }

    // thêm khách hàng
    public boolean addCustomer(String fullName, String phone, String email, String idCard) {
        fullName = checkName(fullName);
        phone = checkPhone(phone);
        email = checkEmail(email);
        idCard = checkIdCard(idCard);

        if (customerDAO.getCustomerByPhone(phone) != null)
            throw new IllegalArgumentException("Số điện thoại đã tồn tại.");
        if (!idCard.isEmpty() && customerDAO.getCustomerByCCCD(idCard) != null)
            throw new IllegalArgumentException("CCCD đã tồn tại.");

        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setIdCard(idCard.isEmpty() ? null : idCard);
        customer.setRegisDate(LocalDateTime.now());
        customer.setLoyaltyPoint(0);

        return customerDAO.addCustomer(customer);
    }

    // cập nhật thông tin khách hàng
    public boolean updateCustomer(String customerID, String fullName, String phone, String email) {
        customerID = check(customerID);
        if (customerID.isEmpty())
            throw new IllegalArgumentException("customerID không hợp lệ.");

        fullName = checkName(fullName);
        phone = checkPhone(phone);
        email = checkEmail(email);

        Customer customer = new Customer();
        customer.setCustomerID(customerID);
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail(email);

        return customerDAO.updateCustomer(customer);
    }


    // check data
    private static String check(String s) {
        return s == null ? "" : s.trim();
    }

    private static String normalize(String s) {
        return check(s).toLowerCase();
    }

    private static String checkName(String s) {
        s = check(s);
        if (s.isEmpty()) throw new IllegalArgumentException("Họ tên không được để trống.");
        return s;
    }

    private static String checkPhone(String s) {
        s = check(s);
        if (!s.matches("^0\\d{9}$"))
            throw new IllegalArgumentException("Số điện thoại phải 10 chữ số và bắt đầu bằng 0.");

        return s;
    }

    private static String checkEmail(String s) {
        s = check(s);
        if (s.isEmpty()) return "";
        if (!s.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"))
            throw new IllegalArgumentException("Email không hợp lệ.");

        return s.toLowerCase();
    }

    private static String checkIdCard(String s) {
        s = check(s);
        if (s.isEmpty()) return "";
        if (!s.matches("^\\d{9,12}$"))
            throw new IllegalArgumentException("CCCD phải 9–12 chữ số.");

        return s;
    }
}
