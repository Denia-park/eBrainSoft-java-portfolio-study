<%@ page import="java.io.File" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String fileName = request.getParameter("file");

    String directory = application.getRealPath("WEB-INF/upload");
    File file = new File(directory, fileName);

    String mimeType = application.getMimeType(file.toString());
    if (mimeType == null) {
        response.setContentType("application/octet-stream");
    }

    String downloadName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

    response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\";");

    FileInputStream fis = new FileInputStream(file);

    ServletOutputStream servletOutputStream = response.getOutputStream();

    byte[] b = new byte[1024];
    int len = 0;

    while ((len = fis.read(b, 0, b.length)) != -1) {
        servletOutputStream.write(b, 0, len);
    }

    servletOutputStream.flush();
    fis.close();
    servletOutputStream.close();
%>
