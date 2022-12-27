package hu.petrik.etlap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtelDB {
    private final Connection connection;
    public static String DB_DRIVER = "mysql";
    public static String DB_HOST = "localhost";
    public static String DB_PORT = "3306";
    public static String DB_NAME = "etlapdb";
    public static String DB_USERNAME = "root";
    public static String DB_PASSWORD = "";

    public EtelDB() throws SQLException {
        String url = String.format("jdbc:%s://%s:%s/%s", DB_DRIVER, DB_HOST, DB_PORT, DB_NAME);
        connection = DriverManager.getConnection(url, DB_USERNAME, DB_PASSWORD);
    }
    public boolean createFood(Etel etel) throws SQLException {
        String sql = "INSERT INTO etlap(nev, leiras, ar, kategoria) VALUES (?, ?, ?, ?)";

        PreparedStatement prepStatement = connection.prepareStatement(sql);
        prepStatement.setString(1, etel.getNev());
        prepStatement.setString(2, etel.getLeiras());
        prepStatement.setInt(3, etel.getAr());
        prepStatement.setString(4, etel.getKategoria());

        return prepStatement.executeUpdate() > 0;
    }
    public List<Etel> readFood() throws SQLException {
        List<Etel> mealsList = new ArrayList<>();
        String sql = "SELECT * FROM etlap";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);

        while(result.next()) {
            int id = result.getInt("id");
            String nev = result.getString("nev");
            String leiras = result.getString("leiras");
            int ar = result.getInt("ar");
            String kategoria = result.getString("kategoria");

            Etel etel = new Etel(id, nev, leiras, ar, kategoria);
            mealsList.add(etel);
        }
        return mealsList;
    }
    public boolean deleteFood(int id) throws SQLException {
        String sql = "DELETE FROM etlap WHERE id = ?";
        PreparedStatement prepStatement = connection.prepareStatement(sql);
        prepStatement.setInt(1, id);

        return prepStatement.executeUpdate() > 0;
    }
    public boolean updatePercentage(Etel etel, double percentage) throws SQLException {
        String sql = "UPDATE etlap SET ar= ? WHERE id = ?";
        PreparedStatement prepStatement = connection.prepareStatement(sql);
        prepStatement.setInt(1, (int) (etel.getAr() * percentage));
        prepStatement.setInt(2, etel.getId());

        return prepStatement.executeUpdate() > 0;
    }
    public boolean updatePrice(Etel etel, int priceUp) throws SQLException {
        String sql = "UPDATE etlap SET ar= ? WHERE id = ?";
        PreparedStatement prepStatement = connection.prepareStatement(sql);
        prepStatement.setInt(1, etel.getAr() + priceUp);
        prepStatement.setInt(2, etel.getId());

        return prepStatement.executeUpdate() > 0;
    }

}
