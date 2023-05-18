<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.security.MessageDigest" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    try {
        String directoty = application.getRealPath("/WEB-INF/upload");
        int maxSize = 1024 * 1024 * 10;
        String encoding = "UTF-8";

        MultipartRequest multipartRequest = new MultipartRequest(request, directoty, maxSize, encoding,
                new DefaultFileRenamePolicy());

        String category = multipartRequest.getParameter("category");
        String writer = multipartRequest.getParameter("writer");
        String title = multipartRequest.getParameter("title");
        String content = multipartRequest.getParameter("content");
        String password = multipartRequest.getParameter("password");

        String regex = "[a-zA-Z0-9{}\\[\\]/?.,;:|()*~`!^\\-_+<>@#$%&='\"]{4,15}";
        if (category.equals("all") ||
                title.length() < 3 || 100 < title.length() ||
                writer.length() < 3 || 4 < writer.length() ||
                content.length() < 4 || 2000 < content.length() ||
                !password.matches(regex)
        ) {
            response.sendRedirect("post.jsp?status=fail");
            return;
        }

        Connection connection = MySqlConnection.getConnection();

        Enumeration fileNames = multipartRequest.getFileNames();
        Boolean isFileExist = false;

        List<String> fileNameList = new ArrayList<>();
        while (fileNames.hasMoreElements()) {
            String parameter = (String) fileNames.nextElement();

            if (parameter == null) continue;

            isFileExist = true;
            fileNameList.add(parameter);
        }

        String sql = "insert into board (CATEGORY, REG_DATETIME, VIEWS, WRITER, PASSWORD, TITLE,CONTENT, FILE_EXIST) values (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, category);
        ps.setString(2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
        ps.setString(3, "0");
        ps.setString(4, writer);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : md.digest()) {
            sb.append(String.format("%02x", b));
        }
        ps.setString(5, sb.toString());
        ps.setString(6, title);
        ps.setString(7, content);
        ps.setBoolean(8, isFileExist);

        int result = ps.executeUpdate();

        if (result < 0) {
            response.sendRedirect("post.jsp?status=fail");
            return;
        }

        sql = "select LAST_INSERT_ID() as boardId";
        Statement st = connection.createStatement();
        ResultSet resultSet = st.executeQuery(sql);
        resultSet.next();

        String boardId = resultSet.getString("boardId");

        for (String name : fileNameList) {
            String fileName = multipartRequest.getOriginalFileName(name);
            String fileRealName = multipartRequest.getFilesystemName(name);

            sql = "insert into file (BOARD_ID,FILE_NAME, FILE_REAL_NAME) values (?,?,?)";

            ps = connection.prepareStatement(sql);
            ps.setString(1, boardId);
            ps.setString(2, fileName);
            ps.setString(3, fileRealName);
            result = ps.executeUpdate();
        }

        if (result > 0) {
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("post.jsp?status=fail");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
