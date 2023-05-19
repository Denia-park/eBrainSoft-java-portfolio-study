<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="org.springframework.util.Base64Utils" %>
<%@ page import="ebrainsoft.week1.util.BoardUtil" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    try {
        String boardId = request.getParameter("id");
        String userPassword = new String(Base64Utils.decodeFromUrlSafeString(request.getParameter("pw")));
        String type = request.getParameter("type");

        Connection con = MySqlConnection.getConnection();

        BoardUtil boardUtil = new BoardUtil();
        if (!boardUtil.verifyPassword(con, boardId, userPassword)) {
            response.sendRedirect("detail.jsp?id=" + boardId + "&type=" + type + "&status=fail");
        }

        if (type.equals("edit")) {
            response.sendRedirect("edit.jsp?id=" + boardId);
        } else if (type.equals("delete")) {
            if (boardUtil.queryDeleteBoard(con, boardId) > 0) {
                response.sendRedirect("index.jsp");
                return;
            }

            response.sendRedirect("index.jsp?status=fail");
        }

        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
