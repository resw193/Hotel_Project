package Entity;

import java.util.Objects;

public class Employee {
	private String employeeID;
	private String fullName;
	private String phone;
	private String email;
	private EmployeeType employeeType;
	private String imgSource;
	private boolean gender;

	public Employee() {

	}
	public Employee(String employeeID, String fullName, String phone, String email, EmployeeType employeeType,
			String imgSource) {
		this.employeeID = employeeID;
		this.fullName = fullName;
		this.phone = phone;
		this.email = email;
		this.employeeType = employeeType;
		this.imgSource = imgSource;
	}

	public Employee(String employeeID, String fullName, String phone, String email, EmployeeType employeeType) {
		this.employeeID = employeeID;
		this.fullName = fullName;
		this.phone = phone;
		this.email = email;
		this.employeeType = employeeType;
	}

	public Employee(String employeeID, String fullName, String phone, String email, EmployeeType employeeType, String imgSource, boolean gioiTinh) {
		this.employeeID = employeeID;
		this.fullName = fullName;
		this.phone = phone;
		this.email = email;
		this.employeeType = employeeType;
		this.imgSource = imgSource;
		this.gender = gioiTinh;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		  if (fullName == null || fullName.trim().isEmpty()) {
	            throw new IllegalArgumentException("Họ tên không được để trống.");
	        }
	        for (String word : fullName.trim().split("\\s+")) {
	            if (!Character.isUpperCase(word.charAt(0))) {
	                throw new IllegalArgumentException(
	                    "Chữ cái đầu mỗi từ phải viết hoa.");
	            }
	        }
		this.fullName = fullName.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		if (phone == null || !phone.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại không đúng định dạng (0xxxxxxxxx).");
        }
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }
		this.email = email.trim();
	}

	public EmployeeType getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(EmployeeType employeeType) {
		if (employeeType == null) {
            throw new IllegalArgumentException("Loại nhân viên không được để trống.");
        }
		this.employeeType = employeeType;
	}

	public String getImgSource() {
		return imgSource;
	}

	public void setImgSource(String imgSource) {
		this.imgSource = imgSource;

	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	@Override
	public int hashCode() {
		return Objects.hash(employeeID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		return Objects.equals(employeeID, other.employeeID);
	}

	@Override
	public String toString() {
		return "Employee{" +
				"employeeID='" + employeeID + '\'' +
				", fullName='" + fullName + '\'' +
				", phone='" + phone + '\'' +
				", email='" + email + '\'' +
				", employeeType=" + employeeType +
				", imgSource='" + imgSource + '\'' +
				'}';
	}
}
