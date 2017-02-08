package database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBManager {

    private static String DBUrl = "jdbc:mysql://localhost:3306/faterun";
    private static String DBCon = "com.mysql.jdbc.Driver";
    private static String DbUserName = "root";
    private static String DbUserPassword = "";

    static {
        try {
            Class.forName(DBCon);
        } catch (Exception xa) {
            System.out.println("Exception in driver loading::one" + xa);
        }
    }
    
    public static boolean getPiChartData() {
        boolean flag = false;
        try {
            Connection con = DriverManager.getConnection(DBUrl, DbUserName, DbUserPassword);
            PreparedStatement ps = con.prepareStatement("select count(id) from faterun.regrun "
            		+ "where buildid=(select max(buildid) from faterun.regrun"
            		+ " where version=(select max(version) from faterun.regrun))");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {                
            }
        } catch (Exception xa) {
            System.out.println("Exception in signUp:::" + xa);
        }
        return flag;
    }

   
}
