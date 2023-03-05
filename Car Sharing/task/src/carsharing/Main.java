package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:file:./src/carsharing/db/";

    public static void main(String[] args) {

        Connection conn = null;
        Statement stmt = null;

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

            //conn = DriverManager.getConnection(DbUrlFinal,USER,PASS);
            conn = DriverManager.getConnection(DbUrlFinal);
            conn.setAutoCommit(true);

            //STEP 3: Execute a query
            //Creating table in given database
            stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS COMPANY;" +
                    "CREATE TABLE   COMPANY " +
                    "(ID INTEGER, " +
                    " NAME VARCHAR(255))";
            stmt.executeUpdate(sql);
            //Created table in given database

            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            } // nothing we can do
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se){
                se.printStackTrace();
            } //end finally try
        } //end try
    }
}