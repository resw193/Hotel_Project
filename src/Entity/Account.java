package Entity;

import java.util.Objects;

public class Account {
	private String accountID;
	private String username;
	private String password;
	private Employee employee;
	
	public Account() {

	}

	public Account(String username, String password, Employee employee) {
		this.username = username;
		this.password = password;
		this.employee = employee;
	}

	public String getAccountID() {
		return accountID;
	}

	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (username == null || username.trim().length() < 6) {
            throw new IllegalArgumentException("Tên đăng nhập tối thiểu 6 ký tự.");
        }
		this.username = username.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
//		if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
//            throw new IllegalArgumentException(
//                "Mật khẩu tối thiểu 8 ký tự, gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 số.");
//        }
		this.password = password;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public void setEmployee(Employee employee) {
		if (employee == null) {
            throw new IllegalArgumentException("Nhân viên không được để trống.");
        }
		this.employee = employee;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountID);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		return Objects.equals(accountID, other.accountID);
	}
	@Override
	public String toString() {
		return "Account [accountID=" + accountID + ", username=" + username + ", password=" + password + ", employee="
				+ employee + "]";
	}

}
