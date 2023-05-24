package ebrainsoft.week2.util;

import ebrainsoft.model.FileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class FileUtil {
    final static String DIRECTORY = "C:" + File.separator + "tempStudyFile" + File.separator + "save";

    /**
     * File들을 저장하고 저장된 실제 파일들의 이름들을 FileInfo에 담아서 반환한다.
     *
     * @param request HttpServletRequest
     * @return List of FileInfo FileInfo의 List를 반환하며, FileInfo에는 저장된 File의 이름과 실제 이름이 들어가있다. urlEncodedFileRealName은 null이 들어가 있으니 주의
     * @throws IOException
     * @throws ServletException
     */
    public static List<FileInfo> saveFilesFromRequest(HttpServletRequest request) throws IOException, ServletException {
        List<FileInfo> saveFileInfos = new ArrayList<>();

        for (Part part : request.getParts()) {
            String headerContent = part.getHeader("Content-Disposition");
            if (!headerContent.contains("filename=")) continue;

            saveFileInfos.add(saveFile(part));
        }

        return saveFileInfos;
    }

    /**
     * multipart/form-data 중에서 실제 파일인 부분만 데이터를 가져와서 폴더를 만들고 파일을 저장
     *
     * @param part
     * @return 실제로 저장한 File 에 대한 FileInfo
     */
    private static FileInfo saveFile(Part part) throws IOException {
        String[] splits = part.getHeader("Content-Disposition").split("; ");

        for (String str : splits) {
            if (!str.trim().startsWith("filename=")) continue;

            String filename = str.substring(str.indexOf('"') + 1, str.length() - 1);
            String fileRealName = UUID.randomUUID() + "_" + filename;
            InputStream fis = part.getInputStream();

            saveFileOnServer(fis, fileRealName);
            fis.close();

            return new FileInfo(filename, fileRealName, null);
        }

        return null;
    }

    /**
     * fis 통해서 fileRealName으로 파일을 디렉토리에 저장
     *
     * @param fis
     * @param fileRealName
     */

    private static void saveFileOnServer(InputStream fis, String fileRealName) {
        createFolders();

        try (FileOutputStream fos = new FileOutputStream(DIRECTORY + File.separator + fileRealName)) {
            byte[] buf = new byte[1024];
            int len = 0;

            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 파일을 저장할 경로를 확인하여 없으면 만들고 있으면 무시
     */
    private static void createFolders() {
        File folders = new File(FileUtil.DIRECTORY);

        if (!folders.exists())
            folders.mkdirs();
    }

    public static List<String> extractFileNamesFromRequest(HttpServletRequest request, String paramName) throws ServletException, IOException {
        List<String> fileNames = new ArrayList<>();
        for (Part part : request.getParts()) {
            if (!part.getName().contains(paramName)) continue;

            String fileName = part.getSubmittedFileName();
            if (fileName == null) {
                String tempName = new String(part.getInputStream().readAllBytes());

                if (!tempName.equals("undefined"))
                    fileNames.add(tempName);
            } else {
                fileNames.add(fileName);
            }
        }

        return fileNames;
    }

    public static boolean checkIsFileExist(List<String> existFileNames,
                                           List<String> deleteFileNames,
                                           List<String> addFileNameList) {
        return existFileNames.size() - deleteFileNames.size() + addFileNameList.size() > 0;
    }

    public static void deleteFile(String deleteFileRealName) {
        File file = new File(DIRECTORY, deleteFileRealName);
        file.delete();
    }
}
