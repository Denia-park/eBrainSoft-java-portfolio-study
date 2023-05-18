<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.io.File" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.util.*" %>
<%@ page import="org.springframework.util.Base64Utils" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    try {
        String id = request.getParameter("id");
        String pw = new String(Base64Utils.decodeFromUrlSafeString(request.getParameter("pw")));

        Connection connection = MySqlConnection.getConnection();
        Statement statement = connection.createStatement();

        String sql = "select * from board where BOARD_ID = " + id;

        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            String dbPassword = resultSet.getString("PASSWORD");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pw.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : md.digest()) {
                sb.append(String.format("%02x", b));
            }

            if (!dbPassword.equals(sb.toString())) {
                response.sendRedirect("edit.jsp?id=" + id + "&status=fail");
                return;
            }
        }

        String directoty = application.getRealPath("/WEB-INF/upload");
        int maxSize = 1024 * 1024 * 10;
        String encoding = "UTF-8";

        MultipartRequest multipartRequest = new MultipartRequest(request, directoty, maxSize, encoding,
                new DefaultFileRenamePolicy());

        String writer = multipartRequest.getParameter("writer");
        String title = multipartRequest.getParameter("title");
        String content = multipartRequest.getParameter("content");
        String[] existFileNames = multipartRequest.getParameterValues("existFileNameList");
        String[] deleteFileNames = multipartRequest.getParameterValues("deleteNameList");

        if (existFileNames == null) {
            existFileNames = new String[0];
        }

        if (deleteFileNames == null) {
            deleteFileNames = new String[0];
        }

        if (title.length() < 3 || 100 < title.length() ||
                writer.length() < 3 || 4 < writer.length() ||
                content.length() < 4 || 2000 < content.length()
        ) {
            Enumeration fileNames = multipartRequest.getFileNames();

            while (fileNames.hasMoreElements()) {
                String parameter = (String) fileNames.nextElement();
                if (parameter == null) continue;

                String fileRealName = multipartRequest.getFilesystemName(parameter);

                File file = new File(directoty, fileRealName);
                file.delete();
            }

            response.sendRedirect("edit.jsp?id=" + id + "&status=fail");
            return;
        }

        Enumeration fileNames = multipartRequest.getFileNames();
        Boolean isFileExist = false;

        List<String> addFileNameList = new ArrayList<>();
        while (fileNames.hasMoreElements()) {
            String parameter = (String) fileNames.nextElement();

            if (parameter == null) continue;

            addFileNameList.add(parameter);
        }

        if (existFileNames.length - deleteFileNames.length + addFileNameList.size() > 0) {
            isFileExist = true;
        }

        sql = "update board set WRITER=?, EDIT_DATETIME=?, TITLE=?,CONTENT=?, FILE_EXIST=? where BOARD_ID = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, writer);
        ps.setString(2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
        ps.setString(3, title);
        ps.setString(4, content);
        ps.setBoolean(5, isFileExist);
        ps.setString(6, id);

        int result = ps.executeUpdate();

        if (result < 0) {
            response.sendRedirect("edit.jsp?id=" + id + "&status=fail");
            return;
        }

        //사라진 애는 삭제
        List<String> deleteFileNameList = List.of(deleteFileNames);
        for (String deleteFileRealName : deleteFileNameList) {
            File file = new File(directoty, deleteFileRealName);
            file.delete();

            sql = "delete from file where BOARD_ID=? and FILE_REAL_NAME=?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, deleteFileRealName);
            result = ps.executeUpdate();
        }

        //추가된 애는 업데이트
        for (String name : addFileNameList) {
            String fileName = multipartRequest.getOriginalFileName(name);
            String fileRealName = multipartRequest.getFilesystemName(name);

            sql = "insert into file (BOARD_ID,FILE_NAME, FILE_REAL_NAME) values (?,?,?)";

            ps = connection.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, fileName);
            ps.setString(3, fileRealName);
            result = ps.executeUpdate();
        }

        if (result > 0) {
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("edit.jsp?id=" + id + "&status=fail");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
