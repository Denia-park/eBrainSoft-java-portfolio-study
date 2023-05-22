package ebrainsoft.week2.repository;

import ebrainsoft.connection.MySqlConnection;
import ebrainsoft.model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {
    /**
     * boardId를 기반으로 해당하는 게시글의 모든 Comment 검색 후 반환
     *
     * @return List of Comment
     * @throws SQLException
     */
    public static List<Comment> findAllCommentById(String boardId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = MySqlConnection.getConnection();

            String sql = "SELECT * FROM comment WHERE board_id = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, boardId);
            rs = ps.executeQuery();

            List<Comment> commentList = new ArrayList<>();
            while (rs.next()) {
                commentList.add(
                        Comment.builder().
                                replyId(rs.getLong("REPLY_ID")).
                                boardId(rs.getLong("BOARD_ID")).
                                content(rs.getString("CONTENT")).
                                regDate(rs.getString("REG_DATETIME")).
                                build()
                );
            }

            return commentList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
    }

    /**
     * 주어진 boardId에 해당하는 게시글에 Comment를 추가.
     *
     * @return 추가에 성공하면 1, 실패하면 0
     * @throws SQLException
     */
    public static int saveComment(String boardId, String content) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = MySqlConnection.getConnection();

            String sql = "INSERT INTO comment (board_id, content ,reg_datetime) VALUES (?,?,?)";

            ps = con.prepareStatement(sql);

            ps.setString(1, boardId);
            ps.setString(2, content);
            ps.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
        }
    }
}
