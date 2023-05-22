package ebrainsoft.week2.repository;

import com.oreilly.servlet.MultipartRequest;
import ebrainsoft.connection.MySqlConnection;
import ebrainsoft.model.FileInfo;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class FileRepository {
    /**
     * 주어진 Enumeration fileNames를 List of String 형태로 변환
     *
     * @return List of String
     */
    public static List<String> convertFileNameList(Enumeration fileNames) {
        List<String> fileNameList = new ArrayList<>();
        while (fileNames.hasMoreElements()) {
            String parameter = (String) fileNames.nextElement();

            if (parameter == null) continue;

            fileNameList.add(parameter);
        }

        return fileNameList;
    }

    /**
     * 주어진 boardId를 기반으로 첨부된 FileInfo의 List를 반환
     *
     * @return List of FileInfo
     * @throws SQLException
     */
    public static List<FileInfo> findAllFileById(String boardId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = MySqlConnection.getConnection();

            String sql = "SELECT * FROM file WHERE board_id = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, boardId);
            rs = ps.executeQuery();

            List<FileInfo> fileInfoList = new ArrayList<>();
            while (rs.next()) {
                String fileRealName = rs.getString("FILE_REAL_NAME");
                String urlEncodedFileRealName = URLEncoder.encode(fileRealName, StandardCharsets.UTF_8);
                fileInfoList.add(new FileInfo(rs.getString("FILE_NAME"), fileRealName, urlEncodedFileRealName));
            }

            return fileInfoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
    }

    /**
     * 잘못저장된 파일들을 삭제
     */
    public static void deleteWrongFiles(MultipartRequest multipartRequest, String directoty) {
        Enumeration fileNames = multipartRequest.getFileNames();

        while (fileNames.hasMoreElements()) {
            String parameter = (String) fileNames.nextElement();
            if (parameter == null) continue;

            String fileRealName = multipartRequest.getFilesystemName(parameter);

            File file = new File(directoty, fileRealName);
            file.delete();
        }
    }

    /**
     * 주어진 deleteFileNameList를 기반으로 DB와 서버에서 파일들을 삭제
     *
     * @return 성공하면 1, 실패하면 0
     * @throws SQLException
     */

    public static int deleteFileList(String boardId, String directoty, List<String> deleteFileNameList) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = MySqlConnection.getConnection();

            String sql = "DELETE FROM file WHERE board_id=? AND file_real_name =?";
            ps = con.prepareStatement(sql);

            for (String deleteFileRealName : deleteFileNameList) {
                File file = new File(directoty, deleteFileRealName);
                file.delete();

                ps.setString(1, boardId);
                ps.setString(2, deleteFileRealName);
                int result = ps.executeUpdate();

                if (result <= 0) {
                    return -1;
                }
            }

            return 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * 주어진 boardId를 기반으로 첨부 파일 저장
     *
     * @return 성공하면 1, 실패하면 0
     * @throws SQLException
     */
    public static int saveFile(MultipartRequest mr, List<String> fileNameList, String boardId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = MySqlConnection.getConnection();

            String sql = "INSERT INTO file (board_id,file_name, file_real_name) values (?,?,?)";

            ps = con.prepareStatement(sql);

            for (String name : fileNameList) {
                String fileName = mr.getOriginalFileName(name);
                String fileRealName = mr.getFilesystemName(name);

                ps.setString(1, boardId);
                ps.setString(2, fileName);
                ps.setString(3, fileRealName);
                int result = ps.executeUpdate();

                if (result <= 0) {
                    return -1;
                }
            }

            return 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) con.close();
            if (ps != null) ps.close();
        }
    }
}
