package carsharing;

import java.util.List;

public interface CompanyDao {
    public void createCompany();
    public void createCarInCompany(int companyID);
    public void createCustomer();
    public int chooseCompany();
    public int chooseCustomer();
    public void chooseCarForRent(int customerID, int companyID);
    public boolean customerDoNotRentCar(int customerID, boolean withMessage);
    public boolean printAllCompanies();
    public boolean printAllCustomers();
    public void printAllCarsInCompany(int id);
    public void returnRentedCar(int customerID);
    public void showCarRented(int customerID);
    public String getCompanyName(int id);
}

