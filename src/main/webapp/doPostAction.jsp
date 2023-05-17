<%@ page import="java.sql.ResultSet" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    try {
        String category = request.getParameter("category");
        String writer = request.getParameter("writer");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String password = request.getParameter("password");

        Connection connection = MySqlConnection.getConnection();

        String sql = "insert into board (CATEGORY, REG_DATETIME, VIEWS, WRITER, PASSWORD, TITLE,CONTENT, FILE_EXIST) values (?,?,?,?,?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, category);
        statement.setString(2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
        statement.setString(3, "0");
        statement.setString(4, writer);
        statement.setString(5, password);
        statement.setString(6, title);
        statement.setString(7, content);
        statement.setInt(8, 0);


        int result = statement.executeUpdate();

        if (result > 0) {
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("index.jsp?status=fail");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
