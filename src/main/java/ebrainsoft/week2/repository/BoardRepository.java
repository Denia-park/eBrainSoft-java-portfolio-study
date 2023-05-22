package ebrainsoft.week2.repository;

import ebrainsoft.connection.MySqlConnection;
import ebrainsoft.model.Board;
import ebrainsoft.model.BoardInfo;
import ebrainsoft.model.FilterCondition;
import ebrainsoft.model.SearchedBoard;
import ebrainsoft.week2.util.PostUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BoardRepository {
    public static int updateBoardView(Board findBoard) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = MySqlConnection.getConnection();

            String sql = "UPDATE board SET views =? WHERE board_id = ?";

            ps = con.prepareStatement(sql);
            ps.setInt(1, findBoard.getViews() + 1);
            ps.setLong(2, findBoard.getBoardId());
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
        }
    }

    public static int deleteBoard(String boardId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = MySqlConnection.getConnection();

            String sql = "DELETE FROM board WHERE board_id = ?" + boardId;

            ps = con.prepareStatement(sql);
            ps.setString(1, boardId);

            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
        }
    }

    public static Board findBoardById(String boardId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = MySqlConnection.getConnection();

            String sql = "SELECT * FROM board WHERE board_id = ?";

            ps = con.prepareStatement(sql);

            ps.setString(1, boardId);

            rs = ps.executeQuery();
            rs.next();

            return createBoard(rs, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
        }
    }

    public static SearchedBoard findAllBoardWithPageNum(FilterCondition fc, int pageSizeLimit) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = MySqlConnection.getConnection();

            String searchingSql = getSearchingSql(fc);

            int totalSearchedBoardNum = getTotalCount(fc, searchingSql);

            String completeSql = searchingSql + " ORDER BY reg_datetime DESC LIMIT ? OFFSET ?"; // pageSizeLimit, pageOffset;
            ps = con.prepareStatement(completeSql);
            fillPagingPreparedStatement(ps, fc, pageSizeLimit);

            rs = ps.executeQuery();

            List<Board> boardList = new ArrayList<>();
            while (rs.next()) {
                boardList.add(createBoard(rs, false));
            }

            return new SearchedBoard(totalSearchedBoardNum, pageSizeLimit, boardList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
    }

    private static String getSearchingSql(FilterCondition fc) {
        String categoryFilter = fc.getCategoryFilter();
        String searchTextFilter = fc.getSearchTextFilter();

        String sql = "SELECT * FROM board WHERE reg_datetime BETWEEN ? AND ?";

        if (!categoryFilter.equals("all")) {
            sql += " AND category = ?"; //categoryFilter
        }

        if (!searchTextFilter.isEmpty()) {
            sql += " AND (title LIKE %?% OR writer LIKE %?% OR content LIKE %?%"; //searchTextFilter
        }

        return sql;
    }

    private static int getTotalCount(FilterCondition fc, String searchingSql) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = MySqlConnection.getConnection();

            String countSql = "SELECT COUNT(*) AS cnt " + searchingSql.substring(searchingSql.indexOf("FROM"));

            ps = con.prepareStatement(countSql);
            fillNoPagingPreparedStatement(ps, fc);
            System.out.println(ps);

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt("cnt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
    }

    private static void fillNoPagingPreparedStatement(PreparedStatement ps, FilterCondition fc) throws SQLException {
        fillPagingPreparedStatement(ps, fc, 0);
    }

    private static void fillPagingPreparedStatement(PreparedStatement ps, FilterCondition fc, int pageSizeLimit) throws SQLException {
        String categoryFilter = fc.getCategoryFilter();
        String searchTextFilter = fc.getSearchTextFilter();
        int needPageNum = fc.getNeedPageNum();

        int paramIdx = 1;

        ps.setString(paramIdx++, fc.getFilterStartTime());
        ps.setString(paramIdx++, fc.getFilterEndTime());
        if (!categoryFilter.equals("all")) {
            ps.setString(paramIdx++, categoryFilter);
        }
        if (!searchTextFilter.isEmpty()) {
            ps.setString(paramIdx++, searchTextFilter);
            ps.setString(paramIdx++, searchTextFilter);
            ps.setString(paramIdx++, searchTextFilter);
        }

        if (pageSizeLimit > 0) {
            int pageOffset = (needPageNum - 1) * pageSizeLimit;
            ps.setInt(paramIdx++, pageSizeLimit);
            ps.setInt(paramIdx, pageOffset);
        }
    }

    private static Board createBoard(ResultSet rs, boolean viewsUpdate) throws SQLException {
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

    private static String getCustomFormatDateString(String columnName, ResultSet rs) throws SQLException {
        LocalDateTime dateTime = rs.getObject(columnName, LocalDateTime.class);

        if (dateTime == null) {
            return "-";
        }

        return dateTime.format(DateTimeFormatter.ofPattern(BoardInfo.CUSTOM_DATE_FORMAT));
    }

    public boolean verifyPassword(String boardId, String userPassword) throws SQLException, NoSuchAlgorithmException {
        Board findBoard = findBoardById(boardId);

        String dbPassword = findBoard.getPassword();

        String encryptedUserPassword = PostUtil.encryptPassword(userPassword, PostUtil.ALGORITHM_SHA_256);

        return dbPassword.equals(encryptedUserPassword);
    }

//    public int updateBoard(MultipartRequest mr, String boardId, boolean isFileExist) throws SQLException {
//        String writer = mr.getParameter("writer");
//        String title = mr.getParameter("title");
//        String content = mr.getParameter("content");
//
//        String sql = "UPDATE board SET writer=?, edit_datetime=?, title=?,content=?, file_exist=? where board_id = ?";
//        PreparedStatement ps = con.prepareStatement(sql);
//        ps.setString(1, writer);
//        ps.setString(2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
//        ps.setString(3, title);
//        ps.setString(4, content);
//        ps.setBoolean(5, isFileExist);
//        ps.setString(6, boardId);
//
//        int result = ps.executeUpdate();
//
//        ps.close();
//
//        return result;
//    }
}
