package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class CompanyDaoImpl implements CompanyDao {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:file:./src/carsharing/db/";

    static final String USER = "sa";
    static final String PASS = "";

    Connection conn = null;
    Statement stmt = null;

    CompanyDaoImpl(String[] args) {

        int params = args.length;
        String DBname = "carsharing";

        if (params > 1) {
            for (int i = 0; i < params - 1; i++) {
                if ("-databaseFileName".equals(args[i])) {
                    DBname = args[i + 1];
                    break;
                }
            }
        }

        String DbUrlFinal = DB_URL.concat(DBname);

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection

            //uncomment following line when working locally in IDE
            //conn = DriverManager.getConnection(DbUrlFinal,USER,PASS);

            //comment following line when working locally in IDE
            conn = DriverManager.getConnection(DbUrlFinal);
            conn.setAutoCommit(true);

            //STEP 3: Execute a query
            //Creating table COMPANY in given database
            stmt = conn.createStatement();
            String sql = //"DROP TABLE IF EXISTS customer; " +
                    //"DROP TABLE IF EXISTS car; " +
                    //"DROP TABLE IF EXISTS company; " +
                    "CREATE TABLE company " +
                    "(id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " name VARCHAR(255) NOT NULL UNIQUE);";
            stmt.executeUpdate(sql);

            //Creating table CAR in given database
            //"DROP TABLE IF EXISTS car;" +
            String sql2 = "CREATE TABLE car " +
                    "(" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(255) NOT NULL UNIQUE, " +
                    "is_rented BOOLEAN DEFAULT FALSE, " +
                    "company_id INTEGER NOT NULL, " +
                    "CONSTRAINT fk_com_id FOREIGN KEY (company_id) " +
                    "REFERENCES company(id)" +
                    ");";
            stmt.executeUpdate(sql2);

            //Creating table CAR in given database
            String sql3 = "CREATE TABLE customer " +
                    "(" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(255) NOT NULL UNIQUE, " +
                    "rented_car_id INTEGER, " +
                    "CONSTRAINT fk_rc_id FOREIGN KEY (rented_car_id) " +
                    "REFERENCES car(id)" +
                    ");";
            stmt.executeUpdate(sql3);

            //Created tables in given database


        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources

        } //end try
    }

    public void closeCompanyDaoImpl() {
        try {
            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        }  catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            try {
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            } // nothing we can do
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se){
                se.printStackTrace();
            } //end finally try
        }

    }

    @Override
    public void createCompany() {
        System.out.println("Enter the company name:");
        Scanner createScanner = new Scanner(System.in);
        String companyName = createScanner.nextLine();

        String sql = "INSERT INTO company (name) " +
                        "VALUES ('" + companyName + "');";
        try {
            stmt.executeUpdate(sql);
            System.out.println("The company was created!");
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
    }

    @Override
    public void createCarInCompany(int companyID) {
        System.out.println("Enter the car name:");
        Scanner createCarScanner = new Scanner(System.in);
        String carName = createCarScanner.nextLine();

        String sql = "INSERT INTO car (name, company_id) " +
                "VALUES ('" + carName + "', " + companyID +");";
        try {
            stmt.executeUpdate(sql);
            System.out.println("The car was added!");
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
    }

    @Override
    public void createCustomer() {
        System.out.println("\nEnter the customer name:");
        Scanner createCustomerScanner = new Scanner(System.in);
        String customerName = createCustomerScanner.nextLine();

        String sql = "INSERT INTO customer (name, rented_car_id) " +
                "VALUES ('" + customerName + "', NULL);";
        try {
            stmt.executeUpdate(sql);
            System.out.println("The customer was added!");
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
    }

    @Override
    public int chooseCompany() {
        Scanner chooseCompanyScanner = new Scanner(System.in);
        return chooseCompanyScanner.nextInt();
    }

    @Override
    public int chooseCustomer() {
        Scanner chooseCustomerScanner = new Scanner(System.in);
        return chooseCustomerScanner.nextInt();
    }

    @Override
    public void chooseCarForRent(int customerID, int companyID) {
        //result to return - id of car in CAR table. If 0 is returned it means that user chose Back command.
        int resultCarID = 0;

        //Prompt a user to choose a car
        System.out.println("\nChoose a car:");

        //Selecting available for rent cars from chosen company
        String sql = "SELECT * FROM car " +
                "WHERE (company_id = " + companyID +
                ") AND (is_rented = FALSE)" +
                " ORDER BY id;";
        boolean isTableNotEmpty;
        int carNumberInList = 1;
        try {
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            isTableNotEmpty = resultSet.next();
            if (isTableNotEmpty) {
                //System.out.println("Car list:");

                //ArrayList for saving names of cars available for rent
                ArrayList<String> carsAvailable = new ArrayList<>();

                String currentCar;
                do {
                    currentCar = resultSet.getNString("name");
                    System.out.println(carNumberInList + ". " + currentCar);
                    carsAvailable.add(currentCar); //add car to arraylist
                    carNumberInList++;
                } while (resultSet.next());
                System.out.println("0. Back");

                //Accept user choice
                Scanner chooseCarScanner = new Scanner(System.in);

                //index of chosen car in ArrayList (less for 1 then in output list)
                int carIndexInList = chooseCarScanner.nextInt() - 1;

                //getting car id from CAR table using its name of user chose not 0
                if (carIndexInList >= 0) {
                    System.out.println("You rented '" + carsAvailable.get(carIndexInList) + "'");
                    sql = "SELECT * FROM car " +
                            "WHERE name = '" + carsAvailable.get(carIndexInList) + "'";
                    stmt.execute(sql);
                    resultSet = stmt.getResultSet();
                    if (resultSet.next()) {
                        resultCarID = resultSet.getInt("id");
                    } else {
                        System.out.println("Can't find " + carsAvailable.get(carIndexInList));
                    }

                    //////Then CAR and CUSTOMER tables must be UPDATED
                    sql = "UPDATE car " +
                            "SET is_rented = TRUE " +
                            "WHERE id = " + resultCarID;
                    stmt.execute(sql);

                    sql = "UPDATE customer " +
                            "SET rented_car_id = " + resultCarID +
                            " WHERE id = " + customerID;
                    stmt.execute(sql);
                }

            } else {
                System.out.println("No available cars in the '" + getCompanyName(companyID) + "' company");
            }
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
    }

    @Override
    public boolean customerDoNotRentCar(int customerID, boolean withMessage) {
        boolean result = true;
        String sql = "SELECT * FROM customer WHERE id = " + customerID + ";";
        try {
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            resultSet.next();
            result = Objects.isNull(resultSet.getObject("rented_car_id"));
            if (!result && withMessage) {
                System.out.println("You've already rented a car!");
            }
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean printAllCompanies() {
        String sql = "SELECT id, name FROM company ORDER BY id;";
        boolean isTableNotEmpty = false;
        try {
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            isTableNotEmpty = resultSet.next();
            if (isTableNotEmpty) {
                System.out.println("Choose a company:");
                do {
                    System.out.println(resultSet.getInt("id") + ". "
                                + resultSet.getNString("name"));
                    } while (resultSet.next());
                System.out.println("0. Back");
            } else {
                System.out.println("The company list is empty!");
            }
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
        return isTableNotEmpty;
    }

    @Override
    public boolean printAllCustomers() {
        String sql = "SELECT id, name FROM customer ORDER BY id;";
        boolean isTableNotEmpty = false;
        try {
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            isTableNotEmpty = resultSet.next();
            if (isTableNotEmpty) {
                System.out.println("\nCustomer list:");
                do {
                    System.out.println(resultSet.getInt("id") + ". "
                            + resultSet.getNString("name"));
                } while (resultSet.next());
                System.out.println("0. Back");
            } else {
                System.out.println("\nThe customer list is empty!");
            }
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
        return isTableNotEmpty;
    }

    @Override
    public void printAllCarsInCompany(int id) {
        String sql = "SELECT * FROM car " +
                "WHERE company_id = " + id +
                " ORDER BY id;";
        boolean isTableEmpty;
        int carNumberInList = 1;
        try {
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            isTableEmpty = resultSet.next();
            if (isTableEmpty) {
                System.out.println("Car list:");
                do {
                    System.out.println(carNumberInList + ". "
                            + resultSet.getNString("name"));
                    carNumberInList++;
                } while (resultSet.next());
            } else {
                System.out.println("The car list is empty!");
            }
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
    }

    @Override
    public void returnRentedCar(int customerID) {
        String sql = "SELECT * FROM customer " +
                "WHERE id = " + customerID;
        String sql2 = "UPDATE customer " +
                "SET rented_car_id = NULL" +
                " WHERE id = " + customerID;

        try {
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            resultSet.next();
            int returnedCarID = resultSet.getInt("rented_car_id");
            stmt.execute(sql2);

            String sql3 = "UPDATE car " +
                    "SET is_rented = FALSE " +
                    "WHERE id = " + returnedCarID;
            stmt.execute(sql3);
            System.out.println("You've returned a rented car!");

        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
    }

    @Override
    public void showCarRented(int customerID) {
        String sql = "SELECT * FROM customer WHERE id = " + customerID + ";";
        try {
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            resultSet.next();

            if (Objects.isNull(resultSet.getObject("rented_car_id"))) {
                System.out.println("You didn't rent a car!");
            } else {
                int rentedCarID = resultSet.getInt("rented_car_id");
                sql = "SELECT * FROM car WHERE id = " + rentedCarID + ";";
                stmt.execute(sql);
                resultSet = stmt.getResultSet();
                resultSet.next();
                String carName = resultSet.getNString("name");
                int companyID = resultSet.getInt("company_id");
                sql = "SELECT * FROM company WHERE id = " + companyID;
                stmt.execute(sql);
                resultSet = stmt.getResultSet();
                resultSet.next();
                String companyName = resultSet.getNString("name");

                System.out.println("Your rented car:");
                System.out.println(carName);
                System.out.println("Company:");
                System.out.println(companyName);
            }
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
    }

    @Override
    public String getCompanyName(int id) {
        String sql = "SELECT * FROM company WHERE id = " + id + ";";
        String result = "";
        boolean isTableEmpty;
        try {
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            isTableEmpty = resultSet.next();
            if (isTableEmpty) {
                result = resultSet.getNString("name");
            } else {
                System.out.println("Company ID is not valid.");
            }
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }
        return result;
    }

}
