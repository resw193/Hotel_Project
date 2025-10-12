package Entity;

import java.util.Objects;

public class EmployeeType {
	private String typeID;      
    private String typeName;    
    private String description;

    public EmployeeType() {

	}

	public EmployeeType(String typeID, String typeName, String description) {
		this.typeID = typeID;
		this.typeName = typeName;
		this.description = description;
	}

	public String getTypeID() {
		return typeID;
	}

	public void setTypeID(String typeID) {
//		 if (typeID == null || !typeID.matches("^ET\\d{2}$")) {
//	            throw new IllegalArgumentException("Mã loại nhân viên không hợp lệ (ETXX).");
//	        }
		this.typeID = typeID;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		if (typeName == null || typeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại nhân viên không được để trống.");
        }
		this.typeName = typeName.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(typeID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeeType other = (EmployeeType) obj;
		return Objects.equals(typeID, other.typeID);
	}

	@Override
	public String toString() {
		return "EmployeeType [typeID=" + typeID + ", typeName=" + typeName + ", description=" + description + "]";
	}
	
}

