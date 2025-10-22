package bus;

import dao.AccountDAO;
import entity.Account;
import other.EmailService;
import other.OtpService;

import java.security.SecureRandom;

public class AccountBUS {

    private AccountDAO accountDAO = new AccountDAO();

    // Xác thực đăng nhập
    public boolean authentication(String username, String password) {
        username = normalize(username);
        password = check(password);
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username và password không được trống.");
        }

        return accountDAO.checkAuthAccount(username, password);
    }

    // lấy ra Account theo username
    public Account getAccountByUsername(String username) {
        username = normalize(username);
        if (username.isEmpty()) return null;

        return accountDAO.getAccountByUsername(username);
    }

    // Gửi OTP đặt lại mật khẩu và trả về email đã gửi (để hiển thị)
    public String sendOtpForReset(String username) throws Exception {
        String key = normalize(username);

        Account acc = getAccountByUsername(key);
        if (acc == null || acc.getEmployee() == null)
            throw new IllegalArgumentException("Không tìm thấy tài khoản.");
        String email = check(acc.getEmployee().getEmail());
        if (email.isEmpty())
            throw new IllegalArgumentException("Tài khoản chưa có email.");

        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        OtpService.put(key, otp, 5 * 60);

        EmailService.sendOtp(email, otp);
        return email;
    }

    // Xác thực OTP và đổi mật khẩu (chức năng quên mật khẩu)
    public boolean resetPasswordWithOtp(String username, String otp, String newPassword, String confirmPassword) {
        String key = normalize(username);

        if (!OtpService.verify(key, check(otp)))
            throw new IllegalArgumentException("OTP không đúng hoặc đã hết hạn.");

        validatePassword(newPassword);
        if (!newPassword.equals(confirmPassword))
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp.");

        return accountDAO.updatePassword(key, newPassword);
    }

    // Đổi mật khẩu theo employeeID
    public boolean changePasswordByEmployeeID(String employeeID, String newPassword, String confirmPassword) {
        validatePassword(newPassword);
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp.");
        }
        return accountDAO.changePassword(employeeID, newPassword);
    }


    // check password
    private static void validatePassword(String pw) {
        if (pw == null || pw.trim().length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải tối thiểu 8 ký tự.");
        }
    }

    private static String check(String s) {
        return s == null ? "" : s.trim();
    }

    private static String normalize(String s) {
        return check(s).toLowerCase();
    }
}
