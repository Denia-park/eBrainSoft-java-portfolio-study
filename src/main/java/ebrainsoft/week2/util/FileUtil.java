package ebrainsoft.week2.util;

import com.oreilly.servlet.MultipartRequest;
import ebrainsoft.week2.model.FileInfo;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class FileUtil {
    public List<String> convertFileNameList(Enumeration fileNames) {
        List<String> fileNameList = new ArrayList<>();
        while (fileNames.hasMoreElements()) {
            String parameter = (String) fileNames.nextElement();

            if (parameter == null) continue;

            fileNameList.add(parameter);
        }

        return fileNameList;
    }

    public List<FileInfo> queryFileList(Connection con, String boardId) throws SQLException {
        String sql = "select * from file where BOARD_ID = " + boardId;

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        List<FileInfo> fileInfoList = new ArrayList<>();
        while (rs.next()) {
            String fileRealName = rs.getString("FILE_REAL_NAME");
            String urlEncodedFileRealName = URLEncoder.encode(fileRealName, StandardCharsets.UTF_8);
            fileInfoList.add(new FileInfo(rs.getString("FILE_NAME"), fileRealName, urlEncodedFileRealName));
        }

        st.close();
        rs.close();

        return fileInfoList;
    }

    public void deleteWrongFiles(MultipartRequest multipartRequest, String directoty) {
        Enumeration fileNames = multipartRequest.getFileNames();

        while (fileNames.hasMoreElements()) {
            String parameter = (String) fileNames.nextElement();
            if (parameter == null) continue;

            String fileRealName = multipartRequest.getFilesystemName(parameter);

            File file = new File(directoty, fileRealName);
            file.delete();
        }
    }

    public void queryDeleteFileList(Connection con, String boardId, String directoty, List<String> deleteFileNameList) throws SQLException {
        for (String deleteFileRealName : deleteFileNameList) {
            File file = new File(directoty, deleteFileRealName);
            file.delete();

            String sql = "delete from file where BOARD_ID=? and FILE_REAL_NAME=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, boardId);
            ps.setString(2, deleteFileRealName);
            ps.executeUpdate();
        }
    }

    public int queryPostFile(Connection con, MultipartRequest mr, List<String> fileNameList, String boardId) throws SQLException {
        for (String name : fileNameList) {
            String fileName = mr.getOriginalFileName(name);
            String fileRealName = mr.getFilesystemName(name);

            String sql = "insert into file (BOARD_ID,FILE_NAME, FILE_REAL_NAME) values (?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, boardId);
            ps.setString(2, fileName);
            ps.setString(3, fileRealName);
            int result = ps.executeUpdate();

            if (result <= 0) {
                ps.close();
                return -1;
            }

            ps.close();
        }

        return 1;
    }
}
