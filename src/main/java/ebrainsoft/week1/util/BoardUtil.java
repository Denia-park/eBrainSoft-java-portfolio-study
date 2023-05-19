package ebrainsoft.week1.util;

import ebrainsoft.week1.model.Board;
import ebrainsoft.week1.model.BoardInfo;
import ebrainsoft.week1.model.FilterCondition;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BoardUtil {
    public void queryUpdateBoardView(Connection con, Board findBoard) throws SQLException {
        String sql = "UPDATE board SET VIEWS =? where BOARD_ID = ?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, findBoard.getViews() + 1);
        ps.setLong(2, findBoard.getBoardId());
        ps.executeUpdate();

        ps.close();
    }

    public int queryDeleteBoard(Connection con, String boardId) throws SQLException {
        String sql = "delete from board where BOARD_ID = " + boardId;
        Statement st = con.createStatement();

        int result = st.executeUpdate(sql);

        st.close();

        return result;
    }

    public Board querySingleBoard(Connection con, String boardId) throws SQLException {
        Statement st = con.createStatement();

        String sql = "select * from board where BOARD_ID = " + boardId;

        ResultSet rs = st.executeQuery(sql);
        rs.next();

        Board findedBoard = createBoard(rs, true);

        st.close();
        rs.close();

        return findedBoard;
    }

    public List<Board> queryBoardList(Connection con, FilterCondition fc, int needPageNum) throws SQLException {
        String completeSql = getCompleteSql(getSearchConditionSql(fc), needPageNum, BoardInfo.PAGE_SIZE_LIMIT);

        return getBoardList(con, fc, completeSql);
    }

    public int queryTotalCount(Connection con, FilterCondition fc) throws SQLException {
        String countSql = getCountSqlFromSearchSql(getSearchConditionSql(fc));

        return getTotalCount(con, fc, countSql);
    }

    private String getCountSqlFromSearchSql(String searchConditionSql) {
        return "select count(*) as cnt " + searchConditionSql.substring(searchConditionSql.indexOf("from"));
    }

    private String getSearchConditionSql(FilterCondition fc) {
        String categoryFilter = fc.getCategoryFilter();
        String searchTextFilter = fc.getSearchTextFilter();

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


    private String getCustomFormatDateString(String columnName, ResultSet rs) throws SQLException {
        LocalDateTime dateTime = rs.getObject(columnName, LocalDateTime.class);

        if (dateTime == null) {
            return "-";
        }

        return dateTime.format(DateTimeFormatter.ofPattern(BoardInfo.CUSTOM_DATE_FORMAT));
    }

    private List<Board> getBoardList(Connection con, FilterCondition fc, String completeSql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(completeSql);

        ps.setString(1, fc.getFilterStartTime());
        ps.setString(2, fc.getFilterEndTime());

        System.out.println(ps);

        ResultSet rs = ps.executeQuery();

        List<Board> boardList = new ArrayList<>();
        while (rs.next()) {
            boardList.add(createBoard(rs, false));
        }

        ps.close();
        rs.close();

        return boardList;
    }

    private Board createBoard(ResultSet rs, boolean viewsUpdate) throws SQLException {
        String regDate = getCustomFormatDateString("REG_DATETIME", rs);
        String editDate = getCustomFormatDateString("EDIT_DATETIME", rs);
        int views = rs.getInt("VIEWS");
        if (viewsUpdate) views += 1;

        return Board.builder().
                boardId(rs.getLong("BOARD_ID")).
                category(rs.getString("CATEGORY")).
                regDate(regDate).
                editDate(editDate).
                views(views).
                writer(rs.getString("WRITER")).
                password(rs.getString("PASSWORD")).
                title(rs.getString("TITLE")).
                content(rs.getString("CONTENT")).
                fileExist(rs.getBoolean("FILE_EXIST")).
                build();
    }

    private String getCompleteSql(String searchConditionSql, int needPageNum, int pageSizeLimit) {
        int pageOffset = (needPageNum - 1) * pageSizeLimit;

        String addSql = " limit " + pageSizeLimit + " offset " + pageOffset;

        return searchConditionSql + addSql;
    }

    public int getTotalPage(int totalCount) {
        return Math.max(1, (int) Math.ceil(totalCount / 10d));
    }

    private int getTotalCount(Connection con, FilterCondition fc, String countSql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(countSql);

        ps.setString(1, fc.getFilterStartTime());
        ps.setString(2, fc.getFilterEndTime());

        ResultSet rs = ps.executeQuery();
        rs.next();

        int totalCount = rs.getInt("cnt");

        ps.close();
        rs.close();

        return totalCount;
    }

    public boolean verifyPassword(Connection con, String boardId, String userPassword) throws SQLException, NoSuchAlgorithmException {
        Board findBoard = querySingleBoard(con, boardId);

        String dbPassword = findBoard.getPassword();

        String encryptedUserPassword = PostUtil.encryptPassword(userPassword, PostUtil.ALGORITHM_SHA_256);

        return dbPassword.equals(encryptedUserPassword);
    }
}
