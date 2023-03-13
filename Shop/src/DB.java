import java.sql.*;

public class DB {
    private Connection con = null;

    private PreparedStatement stmt_insertPreke;
    private PreparedStatement stmt_updatePreke;
    private PreparedStatement stmt_deletePreke;
    private PreparedStatement stmt_selectPreke;
    private PreparedStatement stmt_selectConcretePreke;

    private PreparedStatement stmt_selectKlientas;

    private PreparedStatement stmt_insertUzsakymas;
    private PreparedStatement stmt_updateUzsakymas;


    private PreparedStatement stmt_selectUzsakymoEl;
    private PreparedStatement stmt_insertUzsakymoEl;

    public DB(String url, String user, String pass) {
        try {
            loadDriver();
            getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void loadDriver() throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe) {
            throw new Exception("Unable to load the driver class!");
        }
    }

    private Connection getConnection(String url, String user, String pass) throws Exception {
        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException sqle) {
            con = null;
            throw new Exception("Couldn't get connection!");
        }
        return con;
    }

    public boolean isConnected() {
        if (con != null) {
            return true;
        }
        return false;
    }


    public void closeConnection() throws SQLException {
        con.close();
    }

    public void initPreperedStatemants() {
        try {
            //Klientas table statements
            this.stmt_selectKlientas = this.con.prepareStatement("SELECT kliento_id, vardas, pavarde FROM Klientas");

            //Uzsakymas table statements
            this.stmt_insertUzsakymas = this.con.prepareStatement("INSERT INTO Uzsakymas (kliento_id) VALUES (?)");
            this.stmt_updateUzsakymas = this.con.prepareStatement("UPDATE Uzsakymas SET apmokejimas = ? WHERE nr = ?");

            //Preke table statements
            this.stmt_insertPreke = this.con.prepareStatement("INSERT INTO Preke VALUES (?,?,?,?,?,?,?,?)");
            this.stmt_updatePreke = this.con.prepareStatement("UPDATE Preke SET likutis = ? WHERE kodas = ?");
            this.stmt_deletePreke = this.con.prepareStatement("DELETE FROM Preke WHERE kodas = ?");
            this.stmt_selectPreke = this.con.prepareStatement("SELECT * FROM Preke WHERE tipas = ?");
            this.stmt_selectConcretePreke = this.con.prepareStatement("SELECT * FROM Preke WHERE kodas = ?");

            //Uzsakymo_elementas table statements
            this.stmt_insertUzsakymoEl = this.con.prepareStatement("INSERT INTO Uzsakymo_elementas(uzsakymo_nr, prekes_kodas, kiekis) VALUES (?, ?, ?)");
            this.stmt_selectUzsakymoEl = this.con.prepareStatement("SELECT uzsakymo_nr, pavadinimas, tipas, kiekis FROM Uzsakymo_elementas, Preke WHERE uzsakymo_nr = ? AND Uzsakymo_elementas.prekes_kodas = Preke.kodas");
        } catch (SQLException sqle) {
            System.out.println(sqle);
        }

    }

    public void closePreperedStatements() {
        try {
            this.stmt_selectKlientas.close();
            this.stmt_insertUzsakymas.close();;
            this.stmt_updateUzsakymas.close();
            this.stmt_insertPreke.close();
            this.stmt_updatePreke.close();
            this.stmt_deletePreke.close();
            this.stmt_selectPreke.close();
            this.stmt_selectConcretePreke.close();
            this.stmt_insertUzsakymoEl.close();
            this.stmt_selectUzsakymoEl.close();

        } catch (SQLException sqle) {
            System.out.println(sqle);
        }
    }

    public ResultSet selectAllKlientas() throws SQLException {
        ResultSet result = this.stmt_selectKlientas.executeQuery();
        if (result.isBeforeFirst())
            return result;
        else
            return null;
    }

    public ResultSet selectKlientasUzsakymas() throws SQLException {
        String query = "SELECT vardas, pavarde, nr AS \"Uzasakymo nr\", apmokejimas From Klientas Join Uzsakymas ON Klientas.Kliento_id = Uzsakymas.kliento_id";
        ResultSet result = con.createStatement().executeQuery(query);

        if (result.isBeforeFirst())
            return result;
        else
            return null;
    }

    public void updateUzsakymoStatusas(boolean status, int nr) throws SQLException {
        this.stmt_updateUzsakymas.setBoolean(1, status);
        this.stmt_updateUzsakymas.setInt(2, nr);
        this.stmt_updateUzsakymas.executeUpdate();
    }

    public void insertUzsakymas(int KlientoId) throws SQLException {
        this.stmt_insertUzsakymas.setInt(1, KlientoId);
        this.stmt_insertUzsakymas.executeUpdate();
    }

    public ResultSet selectAllPrekes() throws SQLException {
        String query = "SELECT kodas, pavadinimas, tipas, kaina, aukstis, plotis, skersmuo, likutis FROM Preke";
        ResultSet result = con.createStatement().executeQuery(query);

        if (result.isBeforeFirst())
            return result;
        else
            return null;
    }

    public void insertPreke(int kodas, String pavadinimas, String tipas, float kaina, int aukstis, int plotis, int skersmuo, int likutis) throws SQLException {
        this.stmt_insertPreke.setInt(1, kodas);
        this.stmt_insertPreke.setString(2, pavadinimas);
        this.stmt_insertPreke.setString(3, tipas);
        this.stmt_insertPreke.setFloat(4, kaina);
        this.stmt_insertPreke.setInt(5, aukstis);
        this.stmt_insertPreke.setInt(6, plotis);
        this.stmt_insertPreke.setInt(7, skersmuo);
        this.stmt_insertPreke.setInt(8, likutis);
        this.stmt_insertPreke.executeUpdate();
    }

    public void updatePrekesLikutis(int likutis, int kodas) throws SQLException {
        this.stmt_updatePreke.setInt(1, likutis);
        this.stmt_updatePreke.setInt(2, kodas);
        this.stmt_updatePreke.executeUpdate();
    }

    public void deletePreke(int kodas) throws SQLException {
        this.stmt_deletePreke.setInt(1, kodas);
        this.stmt_deletePreke.executeUpdate();
    }

    public ResultSet selectPrekeTipas(String tipas) throws SQLException {
        this.stmt_selectPreke.setString(1, tipas);
        ResultSet result = this.stmt_selectPreke.executeQuery();
        if (result.isBeforeFirst())
            return result;
        else
            return null;
    }

    public void insertUzsakymoEl(int uzsakymo_nr, int prekes_kodas, int kiekis) throws SQLException {
        this.stmt_insertUzsakymoEl.setInt(1, uzsakymo_nr);
        this.stmt_insertUzsakymoEl.setInt(2, prekes_kodas);
        this.stmt_insertUzsakymoEl.setInt(3, kiekis);
        con.setAutoCommit(false);

        // 1
        this.stmt_insertUzsakymoEl.executeUpdate();

        //2
        stmt_selectConcretePreke.setInt(1, prekes_kodas);
        ResultSet result = stmt_selectConcretePreke.executeQuery();
        if (result.next()) {
            try {
                int oldQuantity = result.getInt("likutis");
                if(oldQuantity - kiekis >= 0) {
                    stmt_updatePreke.setInt(1, oldQuantity - kiekis);
                    stmt_updatePreke.setInt(2, prekes_kodas);
                    stmt_updatePreke.executeUpdate();
                    con.commit();
                } else {
                    con.rollback();
                    System.out.println("Nepakankamas prekes likutis");
                }

            } catch (SQLException sqle) {
                con.rollback();
                System.out.println(sqle);
            }

        } else {
            con.rollback();
            con.setAutoCommit(true);
            throw new SQLException("Tokios prekes nera!");
        }
        con.setAutoCommit(true);


    }

    public ResultSet selectUzsakymoEl(int uzsakymo_nr) throws SQLException {
        this.stmt_selectUzsakymoEl.setInt(1, uzsakymo_nr);
        ResultSet result = this.stmt_selectUzsakymoEl.executeQuery();
        if (result.isBeforeFirst())
            return result;
        else
            return null;
    }

}

