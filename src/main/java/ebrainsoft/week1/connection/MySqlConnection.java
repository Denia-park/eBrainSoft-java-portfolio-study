package ebrainsoft.week1.connection;

import ebrainsoft.week1.model.Board;
import ebrainsoft.week1.model.BoardInfo;
import ebrainsoft.week1.model.searchfilter.FilterCondition;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static BoardInfo getBoardInfo(Connection con, FilterCondition sfc, int pageSizeLimit) throws SQLException {
        String searchConditionSql = getSearchConditionSql(sfc);
        String countSql = getCountSqlFromSearchSql(searchConditionSql);

        int totalCount = getTotalCount(con, sfc, countSql);

        int totalPage = getTotalPage(totalCount);
        int needPageNum = sfc.getNeedPageNum();

        if (needPageNum > totalPage) {
            needPageNum = totalPage;
        }

        String completeSql = getCompleteSql(searchConditionSql, needPageNum, pageSizeLimit);
        List<Board> boardList = getBoardList(con, sfc, completeSql);

        return new BoardInfo(totalCount, totalPage, needPageNum, boardList);
    }

    private static List<Board> getBoardList(Connection con, FilterCondition sfc, String completeSql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(completeSql);

        ps.setString(1, sfc.getFilterStartTime());
        ps.setString(2, sfc.getFilterEndTime());

        System.out.println(ps);

        ResultSet rs = ps.executeQuery();

        List<Board> boardList = new ArrayList<>();
        while (rs.next()) {
            boardList.add(createBoard(rs));
        }

        ps.close();
        rs.close();

        return boardList;
    }

    private static Board createBoard(ResultSet rs) throws SQLException {
        String regDate = getCustomFormatDateString("REG_DATETIME", rs);
        String editDate = getCustomFormatDateString("EDIT_DATETIME", rs);

        return Board.builder().
                boardId(rs.getLong("BOARD_ID")).
                category(rs.getString("CATEGORY")).
                regDate(regDate).
                editDate(editDate).
                views(rs.getInt("VIEWS")).
                writer(rs.getString("WRITER")).
                password(rs.getString("PASSWORD")).
                title(rs.getString("TITLE")).
                content(rs.getString("CONTENT")).
                fileExist(rs.getBoolean("FILE_EXIST")).
                build();
    }

    private static String getCustomFormatDateString(String columnName, ResultSet rs) throws SQLException {
        LocalDateTime dateTime = rs.getObject(columnName, LocalDateTime.class);

        if (dateTime == null) {
            return "-";
        }

        final String CUSTOM_DATE_FORMAT = "yyyy.MM.dd HH:mm";

        return dateTime.format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));
    }

    private static String getCompleteSql(String searchConditionSql, int needPageNum, int pageSizeLimit) {
        int pageOffset = (needPageNum - 1) * pageSizeLimit;

        String addSql = " limit " + pageSizeLimit + " offset " + pageOffset;

        return searchConditionSql + addSql;
    }

    private static int getTotalPage(int totalCount) {
        return Math.max(1, (int) Math.ceil(totalCount / 10d));
    }

    private static int getTotalCount(Connection con, FilterCondition sfc, String countSql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(countSql);

        ps.setString(1, sfc.getFilterStartTime());
        ps.setString(2, sfc.getFilterEndTime());

        ResultSet rs = ps.executeQuery();
        rs.next();

        int totalCount = rs.getInt("cnt");

        ps.close();
        rs.close();

        return totalCount;
    }

    private static String getCountSqlFromSearchSql(String searchConditionSql) {
        return "select count(*) as cnt " + searchConditionSql.substring(searchConditionSql.indexOf("from"));
    }

    private static String getSearchConditionSql(FilterCondition sfc) {
        String categoryFilter = sfc.getCategoryFilter();
        String searchTextFilter = sfc.getSearchTextFilter();

        String sql = "select * from board where REG_DATETIME between ? and ?";
        if (!categoryFilter.equals("all")) {
            sql += " and CATEGORY = '" + categoryFilter + "'";
        }
        if (!searchTextFilter.isEmpty()) {
            sql += " and (TITLE like '%" + searchTextFilter + "%'";
            sql += " or WRITER like '%" + searchTextFilter + "%'";
            sql += " or CONTENT like '%" + searchTextFilter + "%')";
        }

        sql += " order by REG_DATETIME desc";

        return sql;
    }
}
