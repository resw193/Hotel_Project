package bus;

import dao.EmployeeTypeDAO;
import entity.EmployeeType;

public class EmployeeTypeBUS {

    private EmployeeTypeDAO employeeTypeDAO = new EmployeeTypeDAO();

    public EmployeeType getByID(String employeeTypeID) {
        return employeeTypeDAO.getEmployeeTypeByID(employeeTypeID);
    }
}
