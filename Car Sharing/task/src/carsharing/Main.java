package carsharing;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        boolean exit = false;
        boolean backToLogInMenu = false;
        boolean backToDBCompanyMenu;
        int pointLogInMenu;
        int pointDBCompanyMenu;
        int chosenCompanyID;
        int chosenCustomerID;
        Scanner scanner = new Scanner(System.in);
        CompanyDaoImpl companyDao = new CompanyDaoImpl(args);

        do {
            printLogInMenu();
            pointLogInMenu = scanner.nextInt();

            if (pointLogInMenu == 0) {
                exit = true;

            } else if (pointLogInMenu == 2) {
                //Customer Menu
                if (companyDao.printAllCustomers()) {
                    do {
                        chosenCustomerID = companyDao.chooseCustomer();
                        if (chosenCustomerID == 0) {
                            backToLogInMenu = true;
                        } else {
                            customerMenu(chosenCustomerID, companyDao);
                            backToLogInMenu = true;
                        }
                    } while (!backToLogInMenu);
                    backToLogInMenu = false;
                }

            } else if (pointLogInMenu == 3) {
                //Create customer
                companyDao.createCustomer();
            } else if (pointLogInMenu == 1) {
                //Manager Menu
                printDBCompanyMenu();
                do {
                    pointDBCompanyMenu = scanner.nextInt();
                    if (pointDBCompanyMenu == 1) {
                        if (companyDao.printAllCompanies()) {
                            do {
                                chosenCompanyID = companyDao.chooseCompany();
                                if (chosenCompanyID == 0) {
                                    backToDBCompanyMenu = true;
                                } else {
                                    carsInCompanyMenu(chosenCompanyID, companyDao);
                                    backToDBCompanyMenu = true;
                                }
                            } while (!backToDBCompanyMenu);
                            backToDBCompanyMenu = false;
                            printDBCompanyMenu();
                        } else {
                            printDBCompanyMenu();
                        }
                    } else if (pointDBCompanyMenu == 2) {
                        companyDao.createCompany();
                        printDBCompanyMenu();
                    } else {
                        backToLogInMenu = true;
                    }
                } while (!backToLogInMenu);
                backToLogInMenu = false;
                pointDBCompanyMenu = 0;
            }
        } while (!exit);

        companyDao.closeCompanyDaoImpl();

    }

    private static void customerMenu(int chosenCustomerID, CompanyDaoImpl companyDao) {
        printCustomerMenu();
        boolean backToLoginMenu = false;
        int pointCustomerMenu;
        Scanner customerMenuScanner = new Scanner(System.in);
        do {
            pointCustomerMenu = customerMenuScanner.nextInt();
            if (pointCustomerMenu == 1) {
                //Rent a car
                if (companyDao.printAllCompanies() && companyDao.customerDoNotRentCar(chosenCustomerID, true)) {
                    int companyIDForCarRent = companyDao.chooseCompany();
                    if (companyIDForCarRent == 0) {
                        printCustomerMenu();
                    } else {
                        //choosing car in chosen company
                        companyDao.chooseCarForRent(chosenCustomerID, companyIDForCarRent);
                        printCustomerMenu();
                    }
                } else {
                    printCustomerMenu();
                }
            } else if (pointCustomerMenu == 2) {
                //Return a rented car
                if (companyDao.customerDoNotRentCar(chosenCustomerID, false)) {
                    System.out.println("You didn't rent a car!");
                    printCustomerMenu();
                } else {
                    companyDao.returnRentedCar(chosenCustomerID);
                    printCustomerMenu();
                }
            } else if (pointCustomerMenu == 3) {
                //Show my rented car
                companyDao.showCarRented(chosenCustomerID);
                printCustomerMenu();
            }
            else if (pointCustomerMenu == 0) {
                backToLoginMenu = true;
            }
        } while (!backToLoginMenu);
    }



    private static void carsInCompanyMenu(int chosenCompanyID, CompanyDaoImpl companyDao) {
        String companyName = companyDao.getCompanyName(chosenCompanyID);
        if (!companyName.isEmpty()) {
            printCarsInCompanyMenu(companyName);
            boolean backToDBCompanyMenu = false;
            int pointCarsInCompanyMenu;
            Scanner carsInCompanyMenuScanner = new Scanner(System.in);
            do {
                pointCarsInCompanyMenu = carsInCompanyMenuScanner.nextInt();
                if (pointCarsInCompanyMenu == 1) {
                    companyDao.printAllCarsInCompany(chosenCompanyID);
                    printCarsInCompanyMenu(companyName);
                } else if (pointCarsInCompanyMenu == 2) {
                    companyDao.createCarInCompany(chosenCompanyID);
                    printCarsInCompanyMenu(companyName);
                } else if (pointCarsInCompanyMenu == 0) {
                    backToDBCompanyMenu = true;
                }
            } while (!backToDBCompanyMenu);
        }
    }

    private static void printCustomerMenu() {
        System.out.println("\n1. Rent a car");
        System.out.println("2. Return a rented car");
        System.out.println("3. My rented car");
        System.out.println("0. Back");
    }

    private static void printCarsInCompanyMenu(String companyName) {
        System.out.println("\n'" + companyName + "' company:");
        System.out.println("1. Car list");
        System.out.println("2. Create a car");
        System.out.println("0. Back");
    }

    private static void printDBCompanyMenu() {
        System.out.println("\n1. Company list");
        System.out.println("2. Create a company");
        System.out.println("0. Back");
    }

    private static void printLogInMenu() {
        System.out.println("\n1. Log in as a manager");
        System.out.println("2. Log in as a customer");
        System.out.println("3. Create a customer");
        System.out.println("0. Exit");
    }
}

