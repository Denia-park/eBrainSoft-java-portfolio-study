package ebrainsoft.week1.util;

import ebrainsoft.week1.model.FileInfo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
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
}
