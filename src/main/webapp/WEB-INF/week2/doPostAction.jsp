<%@ page import="ebrainsoft.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="ebrainsoft.util.Validator" %>
<%@ page import="ebrainsoft.week1.util.PostUtil" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    try {
        String directoty = application.getRealPath("/WEB-INF/upload");
        int maxSize = 1024 * 1024 * 10;
        String encoding = "UTF-8";

        MultipartRequest multipartRequest = new MultipartRequest(request, directoty, maxSize, encoding,
                new DefaultFileRenamePolicy());

        if (!Validator.isAllValidOnPost(multipartRequest)) {
            response.sendRedirect("post.jsp?status=fail");
            return;
        }

        Connection con = MySqlConnection.getConnection();

        PostUtil postUtil = new PostUtil();

        int result = postUtil.queryPostBoard(con, multipartRequest);

        if (result < 0) {
            response.sendRedirect("post.jsp?status=fail");
            return;
        }

        response.sendRedirect("index.jsp");

        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
