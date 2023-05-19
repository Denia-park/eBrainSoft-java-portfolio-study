package ebrainsoft.week1.model;

import lombok.Getter;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BoardInfo {
    public static final int PAGE_SIZE_LIMIT = 10;
    public static final int PAGE_NUM_WIDTH_LIMIT = 5;
    public static final String REG_START_DATE = "reg_start_date";
    public static final String REG_END_DATE = "reg_end_date";
    public static final String SEARCH_CATEGORY = "searchCategory";
    public static final String SEARCH_TEXT = "searchText";
    public static final String PAGE = "page";
    public static final String CUR_PAGE = "curPage";
    public static final String CUSTOM_DATE_FORMAT = "yyyy.MM.dd HH:mm";

    public static final String ALL_CATEGORY_VALUE = "all";
    public static final String EMPTY_STRING = "";

    public static final int DEFAULT_YEAR_GAP = 1;
    public static final int DEFAULT_START_PAGE_NUM = 1;


    private int totalCount;
    private int totalPage;
    private int needPageNum;
    private List<Board> boardList;
    private List<String> categoryList;

    private String getCustomFormatDateString(String columnName, ResultSet rs) throws SQLException {
        LocalDateTime dateTime = rs.getObject(columnName, LocalDateTime.class);

        if (dateTime == null) {
            return "-";
        }

        return dateTime.format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));
    }

    private List<Board> getBoardList(Connection con, FilterCondition fc, String completeSql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(completeSql);

        ps.setString(1, fc.getFilterStartTime());
        ps.setString(2, fc.getFilterEndTime());

        System.out.println(ps);

        ResultSet rs = ps.executeQuery();

        List<Board> boardList = new ArrayList<>();
        while (rs.next()) {
            boardList.add(createBoard(rs, false));
        }

        ps.close();
        rs.close();

        return boardList;
    }

    private Board createBoard(ResultSet rs, boolean viewsUpdate) throws SQLException {
        String regDate = getCustomFormatDateString("REG_DATETIME", rs);
        String editDate = getCustomFormatDateString("EDIT_DATETIME", rs);
        int views = rs.getInt("VIEWS");
        if (viewsUpdate) views += 1;

        return Board.builder().
                boardId(rs.getLong("BOARD_ID")).
                category(rs.getString("CATEGORY")).
                regDate(regDate).
                editDate(editDate).
                views(views).
                writer(rs.getString("WRITER")).
                password(rs.getString("PASSWORD")).
                title(rs.getString("TITLE")).
                content(rs.getString("CONTENT")).
                fileExist(rs.getBoolean("FILE_EXIST")).
                build();
    }

    private String getCompleteSql(String searchConditionSql, int needPageNum, int pageSizeLimit) {
        int pageOffset = (needPageNum - 1) * pageSizeLimit;

        String addSql = " limit " + pageSizeLimit + " offset " + pageOffset;

        return searchConditionSql + addSql;
    }

    private int getTotalPage(int totalCount) {
        return Math.max(1, (int) Math.ceil(totalCount / 10d));
    }

    private int getTotalCount(Connection con, FilterCondition fc, String countSql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(countSql);

        ps.setString(1, fc.getFilterStartTime());
        ps.setString(2, fc.getFilterEndTime());

        ResultSet rs = ps.executeQuery();
        rs.next();

        int totalCount = rs.getInt("cnt");

        ps.close();
        rs.close();

        return totalCount;
    }

    private String getCountSqlFromSearchSql(String searchConditionSql) {
        return "select count(*) as cnt " + searchConditionSql.substring(searchConditionSql.indexOf("from"));
    }

    private String getSearchConditionSql(FilterCondition fc) {
        String categoryFilter = fc.getCategoryFilter();
        String searchTextFilter = fc.getSearchTextFilter();

        String sql = "select * from board where REG_DATETIME between ? and ?";
        if (!categoryFilter.equals("all")) {
            sql += " and CATEGORY = '" + categoryFilter + "'";
        }
        if (!searchTextFilter.isEmpty()) {
            sql += " and (TITLE like '%" + searchTextFilter + "%'";
            sql += " or WRITER like '%" + searchTextFilter + "%'";
            sql += " or CONTENT like '%" + searchTextFilter + "%')";
        }

        sql += " order by REG_DATETIME desc";

        return sql;
    }

    public Board querySingleBoard(Connection con, String boardId) throws SQLException {
        Statement st = con.createStatement();

        String sql = "select * from board where BOARD_ID = " + boardId;

        ResultSet rs = st.executeQuery(sql);
        rs.next();

        Board findedBoard = createBoard(rs, true);

        st.close();
        rs.close();

        return findedBoard;
    }

    public void queryBoardList(Connection con, FilterCondition fc) throws SQLException {
        String searchConditionSql = getSearchConditionSql(fc);
        String countSql = getCountSqlFromSearchSql(searchConditionSql);

        int tempTotalCount = getTotalCount(con, fc, countSql);

        int tempTotalPage = getTotalPage(tempTotalCount);
        int tempNeedPageNum = fc.getNeedPageNum();

        if (tempNeedPageNum > tempTotalPage) {
            tempNeedPageNum = tempTotalPage;
        }

        String completeSql = getCompleteSql(searchConditionSql, tempNeedPageNum, PAGE_SIZE_LIMIT);
        List<Board> tempBoardList = getBoardList(con, fc, completeSql);

        updateData(tempTotalCount, tempTotalPage, tempNeedPageNum, tempBoardList);
    }

    private void updateData(int totalCount, int totalPage, int needPageNum, List<Board> boardList) {
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.needPageNum = needPageNum;
        this.boardList = boardList;
    }

    public void queryCategoryList(Connection con) throws Exception {
        List<String> rtList = new ArrayList<>();
        String sql = "select * from category";

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            rtList.add(rs.getString("NAME"));
        }

        st.close();
        rs.close();

        this.categoryList = rtList;
    }

    public int getNeedPageNum() {
        return needPageNum;
    }

    public int getPrevPage() {
        return this.needPageNum == DEFAULT_START_PAGE_NUM ? DEFAULT_START_PAGE_NUM : this.needPageNum - 1;
    }

    public int getNextPage() {
        return this.needPageNum == this.totalPage ? this.totalPage : this.needPageNum + 1;
    }

    public int getPageLimitStart() {
        return 1 + (((this.needPageNum - 1) / PAGE_NUM_WIDTH_LIMIT) * PAGE_NUM_WIDTH_LIMIT);
    }

    public int getPageLimitEnd(int pageLimitStart) {
        return Math.min(pageLimitStart + (PAGE_NUM_WIDTH_LIMIT - DEFAULT_START_PAGE_NUM), this.totalPage);
    }
}
