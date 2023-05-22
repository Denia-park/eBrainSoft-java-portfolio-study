package ebrainsoft.week2.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryUtil {
    public List<String> queryCategoryList(Connection con) throws Exception {
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
