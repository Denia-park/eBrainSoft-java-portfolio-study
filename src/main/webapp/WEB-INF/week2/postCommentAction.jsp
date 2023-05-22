<%@ page import="ebrainsoft.connection.MySqlConnection" %>
<%@ page import="java.sql.*" %>
<%@ page import="ebrainsoft.week1.util.CommentUtil" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    try {
        String boardId = request.getParameter("id");
        String content = request.getParameter("content");

        Connection con = MySqlConnection.getConnection();

        CommentUtil commentUtil = new CommentUtil();
        commentUtil.queryPostComment(con, boardId, content);

        //덧글 작성 글로 리다이렉트
        response.sendRedirect("detail.jsp?id=" + boardId);

        con.close();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>
