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
    /**
     * 게시글의 조회수를 1만큼 업데이트
     *
     * @return 성공하면 1, 실패하면 0
     * @throws Exception
     */
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

    /**
     * id에 해당하는 게시글을 삭제
     *
     * @return 성공하면 1, 실패하면 0
     * @throws Exception
     */
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

    /**
     * id에 해당하는 게시글을 검색 후 반환
     *
     * @return Board
     * @throws Exception
     */
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

    /**
     * fc에 주어진 조건을 바탕으로 검색 후 페이지 사이즈만큼 데이터를 가져오고 가져온 데이터들은 SearchedBoard 객체에 담겨서 반환된다.
     *
     * @return SearchedBoard
     * @throws Exception
     */
    public static SearchedBoard findAllBoardWithPageNum(FilterCondition fc, int pageSizeLimit) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = MySqlConnection.getConnection();

            String searchingSql = getSearchingSql(fc);

            int totalSearchedBoardNum = getTotalSearchedBoardCount(fc, searchingSql);

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

    /**
     * fc를 바탕으로 필터링할 내용들이 포함된 SQL을 작성 후 반환
     *
     * @return String
     */
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

    /**
     * fc에 해당하는 전체 게시글 수를 반환한다.
     *
     * @return fc에 해당하는 전체 게시글 수
     * @throws Exception
     */
    private static int getTotalSearchedBoardCount(FilterCondition fc, String searchingSql) throws SQLException {
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

    /**
     * 페이징이 사용되지 않는 PreparedStatement의 parameter를 채우는 메서드
     *
     * @throws Exception
     */
    private static void fillNoPagingPreparedStatement(PreparedStatement ps, FilterCondition fc) throws SQLException {
        fillPagingPreparedStatement(ps, fc, 0);
    }

    /**
     * 페이징이 사용되는 PreparedStatement의 parameter를 채우는 메서드
     *
     * @throws Exception
     */
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

    /**
     * ResultSet을 바탕으로 Board 객체를 만들어서 반환
     * <p>
     * viewsUpdate가 true 이면 조회수가 1만큼 오른 상태로 객체가 생성된다.
     *
     * @return Board
     * @throws Exception
     */
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

    /**
     * LocalDateTime의 포맷을 CUSTOM_DATE_FORMAT에 맞게 변경 후 반환
     *
     * @return CUSTOM_DATE_FORMAT에 맞춰서 포맷팅된 LocalDateTime의 String 반환
     * @throws SQLException
     */
    private static String getCustomFormatDateString(String columnName, ResultSet rs) throws SQLException {
        LocalDateTime dateTime = rs.getObject(columnName, LocalDateTime.class);

        if (dateTime == null) {
            return "-";
        }

        return dateTime.format(DateTimeFormatter.ofPattern(BoardInfo.CUSTOM_DATE_FORMAT));
    }

    /**
     * 주어진 boardId를 기반으로 Board 검색 후 Board의 비밀번호 와 주어진 userPassword가 일치하는지 확인
     *
     * @return 비밀번호의 일치 유무 반환
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     */
    public static boolean verifyPassword(String boardId, String userPassword) throws SQLException, NoSuchAlgorithmException {
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
