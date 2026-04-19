import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class ItemDbDAO implements IDAO {
    private static Connection connection;

    @Override
    public boolean save(Hashtable<String, String> data) {
        String sql = "INSERT INTO items (itemName, type, power, weight) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, data.get("itemName"));
            pstmt.setString(2, data.get("type"));
            pstmt.setInt(3, Integer.parseInt(data.get("power")));
            pstmt.setBigDecimal(4, BigDecimal.valueOf(Double.parseDouble(data.get("weight"))));

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteAll()
    {
        String sql = "DELETE FROM items";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int affectedRows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(String itemName) {
        String sql = "DELETE FROM items WHERE itemName = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, itemName);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Hashtable<String, String> load(String itemName) {
        String sql = "SELECT * FROM items WHERE itemName = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, itemName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Hashtable<String, String> item = new Hashtable<>();
                item.put("itemName", rs.getString("itemName"));
                item.put("type", rs.getString("type"));
                item.put("power", String.valueOf(rs.getInt("power")));
                item.put("weight", String.valueOf(rs.getInt("weight")));
                return item;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Hashtable<String, String>> load() {
        String sql = "SELECT * FROM items";
        ArrayList<Hashtable<String, String>> items = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Hashtable<String, String> item = new Hashtable<>();
                item.put("itemName", rs.getString("itemName"));
                item.put("type", rs.getString("type"));
                item.put("power", String.valueOf(rs.getInt("power")));
                item.put("weight", String.valueOf(rs.getInt("weight")));
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:sqlserver://<HOST_NAME>\\<INSTANCE_NAME>;"
                    + "databaseName=<DATABASE_NAME>;"
                    + "integratedSecurity=true;"  // Add this for Windows authentication
                    + "encrypt=true;"
                    + "trustServerCertificate=true";
            connection = DriverManager.getConnection(url);
            System.out.println("Successfully Connected to Database");
        }
        return connection;
    }

    // New method to get most powerful items by type
    public Hashtable<String, String> getMostPowerfulItemByType(String type) {
        String sql = "SELECT TOP 1 * FROM items WHERE type = ? ORDER BY power DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Hashtable<String, String> item = new Hashtable<>();
                item.put("itemName", rs.getString("itemName"));
                item.put("type", rs.getString("type"));
                item.put("power", String.valueOf(rs.getInt("power")));
                item.put("weight", String.valueOf(rs.getInt("weight")));
                return item;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}