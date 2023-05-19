package ebrainsoft.week1.connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlConnection {
    static final String DB_URL = "jdbc:mysql://localhost:3306/ebrainsoft_study";
    static final String USER = "ebsoft";
    static final String PASS = "ebsoft";

    public static Connection getConnection() throws Exception {
        Connection conn = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return conn;
    }


    public static List<String> getCategoryList(Connection con) throws Exception {
        List<String> rtList = new ArrayList<>();
        String sql = "select * from category";

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            rtList.add(rs.getString("NAME"));
        }

        st.close();
        rs.close();

        return rtList;
    }
}
