<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.sql.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    try {
        String boardId = request.getParameter("id");
        String content = request.getParameter("content");

        Connection connection = MySqlConnection.getConnection();

        String sql = "insert into comment (BOARD_ID, CONTENT ,REG_DATETIME) values (?,?,?)";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, boardId);
        statement.setString(2, content);
        statement.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

        statement.executeUpdate();

        //덧글 작성 글로 리다이렉트
        response.sendRedirect("detail.jsp?id=" + boardId);

        connection.close();
        statement.close();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>
