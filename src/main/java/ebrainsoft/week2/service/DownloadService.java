package ebrainsoft.week2.service;

import ebrainsoft.week2.util.FileUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DownloadService implements Service {

    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) {
        try {
            downloadFile(request, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletOutputStream servletOutputStream = null;
        FileInputStream fis = null;
        try {
            String fileName = request.getParameter("file");

            File file = new File(FileUtil.DIRECTORY, fileName);

            String mimeType = request.getContentType();
            if (mimeType == null) {
                response.setContentType("application/octet-stream");
            }

            String downloadName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

            response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\";");

            fis = new FileInputStream(file);

            servletOutputStream = response.getOutputStream();

            byte[] b = new byte[1024];
            int len = 0;

            while ((len = fis.read(b, 0, b.length)) != -1) {
                servletOutputStream.write(b, 0, len);
            }

            servletOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) fis.close();
            if (servletOutputStream != null) servletOutputStream.close();
        }
    }
}
