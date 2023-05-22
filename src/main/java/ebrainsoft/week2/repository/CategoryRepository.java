package ebrainsoft.week2.repository;

import ebrainsoft.connection.MySqlConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    /**
     * DB에 존재하는 모든 Category를 검색해서 가져온다.
     *
     * @return List of String
     * @throws Exception
     */
    public static List<String> findAllCategory() throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = MySqlConnection.getConnection();

            List<String> rtList = new ArrayList<>();
            String sql = "SELECT * FROM category";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                rtList.add(rs.getString("NAME"));
            }

            return rtList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
    }
}
