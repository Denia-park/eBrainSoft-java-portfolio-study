package ebrainsoft.week1.util;

import com.oreilly.servlet.MultipartRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostUtil {
    public static String ALGORITHM_SHA_256 = "SHA-256";

    public static String encryptPassword(String password, String algorithmType) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithmType);

        md.update(password.getBytes());

        StringBuffer sb = new StringBuffer();

        for (byte b : md.digest()) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public boolean checkFileExistFromFileNameList(List<String> fileNameList) {
        return fileNameList.size() > 0;
    }

    public int postBoard(Connection con, MultipartRequest mr, List<String> fileNameList) throws SQLException, NoSuchAlgorithmException {
        boolean isFileExist = checkFileExistFromFileNameList(fileNameList);

        int views = 0;

        String sql = "insert into board (CATEGORY, REG_DATETIME, VIEWS, WRITER, PASSWORD, TITLE,CONTENT, FILE_EXIST) " +
                "values (?,?,?,?,?,?,?,?)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, mr.getParameter("category"));
        ps.setString(2, getNowDateTime());
        ps.setString(3, String.valueOf(views));
        ps.setString(4, mr.getParameter("writer"));
        ps.setString(5, encryptPassword(mr.getParameter("password"), ALGORITHM_SHA_256));
        ps.setString(6, mr.getParameter("title"));
        ps.setString(7, mr.getParameter("content"));
        ps.setBoolean(8, isFileExist);

        int result = ps.executeUpdate();

        ps.close();

        return result;
    }

    private String getNowDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    }

    public String getBoardId(Connection con) throws SQLException {
        String sql = "select LAST_INSERT_ID() as boardId";

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        rs.next();

        String boardId = rs.getString("boardId");

        st.close();
        rs.close();

        return boardId;
    }

    public int queryPostBoard(Connection con, MultipartRequest mr) throws SQLException, NoSuchAlgorithmException {
        FileUtil fileUtil = new FileUtil();
        List<String> fileNameList = fileUtil.convertFileNameList(mr.getFileNames());

        int result = postBoard(con, mr, fileNameList);

        if (result <= 0) {
            return -1;
        }

        result = fileUtil.queryPostFile(con, mr, fileNameList, getBoardId(con));

        if (result <= 0) {
            return -1;
        }

        return result;
    }
}
