import java.sql.SQLException;

public class Main {
    private static String url = "jdbc:postgresql://pgsql3.mif/studentu";
    private static String username = "";
    private static String password = "";

    public static void main (String[] args) {
        DB db = new DB(url, username, password);

        if(db.isConnected()){
            System.out.println("Succesfully connected to postgres Database");
            db.initPreperedStatemants();
            UI.init(db);
            db.closePreperedStatements();
            try{
                db.closeConnection();
            }catch (SQLException sqle){
                System.out.println(sqle);
            }
        }

    }
}