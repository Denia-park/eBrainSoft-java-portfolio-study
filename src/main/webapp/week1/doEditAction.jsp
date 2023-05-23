<%@ page import="ebrainsoft.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="java.util.*" %>
<%@ page import="org.springframework.util.Base64Utils" %>
<%@ page import="ebrainsoft.week1.util.*" %>
<%@ page import="ebrainsoft.util.Validator" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    try {
        String boardId = request.getParameter("id");
        String userPassword = new String(Base64Utils.decodeFromUrlSafeString(request.getParameter("pw")));

        Connection con = MySqlConnection.getConnection();

        BoardUtil boardUtil = new BoardUtil();
        if (!boardUtil.verifyPassword(con, boardId, userPassword)) {
            response.sendRedirect("edit.jsp?id=" + boardId + "&status=fail");
            return;
        }

        String directoty = application.getRealPath("/WEB-INF/upload");
        int maxSize = 1024 * 1024 * 10;
        String encoding = "UTF-8";

        MultipartRequest mr = new MultipartRequest(request, directoty, maxSize, encoding,
                new DefaultFileRenamePolicy());

        List<String> existFileNames = EditUtil.getFileNames(mr, "existFileNameList");
        List<String> deleteFileNames = EditUtil.getFileNames(mr, "deleteNameList");

        FileUtil fileUtil = new FileUtil();

        if (!Validator.isAllValidOnEdit(mr)) {
            fileUtil.deleteWrongFiles(mr, directoty);

            response.sendRedirect("edit.jsp?id=" + boardId + "&status=fail");
            return;
        }

        List<String> addFileNameList = fileUtil.convertFileNameList(mr.getFileNames());

        boolean isFileExist = EditUtil.checkIsFileExist(existFileNames, deleteFileNames, addFileNameList);

        int result = boardUtil.queryEditBoard(con, mr, boardId, isFileExist);
        if (result < 0) {
            response.sendRedirect("edit.jsp?id=" + boardId + "&status=fail");
            return;
        }

        //사라진 애는 삭제
        fileUtil.queryDeleteFileList(con, boardId, directoty, deleteFileNames);

        //추가된 애는 업데이트
        fileUtil.queryPostFile(con, mr, addFileNameList, boardId);

        con.close();

        if (result > 0) {
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("edit.jsp?id=" + boardId + "&status=fail");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
