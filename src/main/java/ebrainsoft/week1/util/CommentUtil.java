package ebrainsoft.week1.util;

import ebrainsoft.week1.model.Comment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                            regDate(rs.getString("REG_DATETIME")).
                            build()
            );
        }

        st.close();
        rs.close();

        return commentList;
    }
}
