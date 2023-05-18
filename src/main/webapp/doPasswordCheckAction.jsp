<%@ page import="java.sql.ResultSet" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    try {
        String boardId = request.getParameter("id");
        String userPassword = request.getParameter("pw");
        String type = request.getParameter("type");

        Connection connection = MySqlConnection.getConnection();

        //내용 조회
        Statement statement = connection.createStatement();

        String sql = "select PASSWORD from board where BOARD_ID = " + boardId;
        ResultSet resultSet = statement.executeQuery(sql);

        if (resultSet.next()) {
            String dbPassword = resultSet.getString("PASSWORD");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(userPassword.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : md.digest()) {
                sb.append(String.format("%02x", b));
            }

            userPassword = sb.toString();

            if (!dbPassword.equals(userPassword)) {
                response.sendRedirect("detail.jsp?id=" + boardId + "&type=" + type + "&status=fail");
                return;
            }
        }

        if (type.equals("edit")) {
            response.sendRedirect("edit.jsp?id=" + boardId);
        } else if (type.equals("delete")) {
            sql = "delete from board where BOARD_ID = " + boardId;

            int result = statement.executeUpdate(sql);

            if (result > 0) {
                response.sendRedirect("index.jsp");
            } else {
                response.sendRedirect("index.jsp?status=fail");
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
