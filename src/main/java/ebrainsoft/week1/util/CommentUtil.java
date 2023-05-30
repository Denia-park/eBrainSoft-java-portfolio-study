package ebrainsoft.week1.util;

import ebrainsoft.model.Comment;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommentUtil {
    public List<Comment> queryCommentList(Connection con, String boardId) throws SQLException {
        String sql = "select * from comment where BOARD_ID = " + boardId;

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        List<Comment> commentList = new ArrayList<>();
        while (rs.next()) {
            commentList.add(
                    Comment.builder().
                            replyId(rs.getLong("REPLY_ID")).
                            boardId(rs.getLong("BOARD_ID")).
                            content(rs.getString("CONTENT")).
                            regDatetime(rs.getString("REG_DATETIME")).
                            build()
            );
        }

        st.close();
        rs.close();

        return commentList;
    }

    public int queryPostComment(Connection con, String boardId, String content) throws SQLException {
        String sql = "insert into comment (BOARD_ID, CONTENT ,REG_DATETIME) values (?,?,?)";

        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, boardId);
        st.setString(2, content);
        st.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

        return st.executeUpdate();
    }
}
