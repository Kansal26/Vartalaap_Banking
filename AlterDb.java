import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AlterDb {
    public static void main(String[] args) {
        try {
            // Need to append ;AUTO_SERVER=TRUE so it can connect while app is running if possible? 
            // Better to kill the app first to avoid locks.
            Connection conn = DriverManager.getConnection("jdbc:h2:file:./data/demo", "SA", "");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("ALTER TABLE SYSTEM_NOTIFICATION ALTER COLUMN BODY TEXT");
            System.out.println("Column type altered successfully.");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
