package ebrainsoft.week1.model;

import ebrainsoft.week1.model.searchfilter.FilterCondition;

import javax.servlet.jsp.PageContext;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BoardInfo {
    private final int PAGE_SIZE_LIMIT = 10;
    private final int PAGE_WIDTH_NUM_LIMIT = 5;
    private int totalCount;
    private int totalPage;
    private int needPageNum;
    private List<Board> boardList;
    private List<String> categoryList;

    private List<Board> getBoardList(Connection con, FilterCondition fc, String completeSql) throws SQLException {
        PreparedStatement ps = con.prepareStatement(completeSql);

        ps.setString(1, fc.getFilterStartTime());
        ps.setString(2, fc.getFilterEndTime());

        System.out.println(ps);

        ResultSet rs = ps.executeQuery();

        List<Board> boardList = new ArrayList<>();
        while (rs.next()) {
            boardList.add(createBoard(rs));
        }

        ps.close();
        rs.close();

        return boardList;
    }

    private Board createBoard(ResultSet rs) throws SQLException {
        String regDate = getCustomFormatDateString("REG_DATETIME", rs);
        String editDate = getCustomFormatDateString("EDIT_DATETIME", rs);

        return Board.builder().
                boardId(rs.getLong("BOARD_ID")).
                category(rs.getString("CATEGORY")).
                regDate(regDate).
                editDate(editDate).
                views(rs.getInt("VIEWS")).
                writer(rs.getString("WRITER")).
                password(rs.getString("PASSWORD")).
                title(rs.getString("TITLE")).
                content(rs.getString("CONTENT")).
                fileExist(rs.getBoolean("FILE_EXIST")).
                build();
    }

    private String getCustomFormatDateString(String columnName, ResultSet rs) throws SQLException {
        LocalDateTime dateTime = rs.getObject(columnName, LocalDateTime.class);

        if (dateTime == null) {
            return "-";
        }

        final String CUSTOM_DATE_FORMAT = "yyyy.MM.dd HH:mm";

        return dateTime.format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));
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

    public void queryBoardData(Connection con, FilterCondition fc) throws SQLException {
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

    public void updateAllAttribute(PageContext pageContext, FilterCondition fc) {
        int curPage = this.needPageNum;

        int prevPage = curPage == 1 ? 1 : curPage - 1;
        int nextPage = curPage == this.totalPage ? this.totalPage : curPage + 1;

        int pageLimitStart = 1 + (((curPage - 1) / PAGE_WIDTH_NUM_LIMIT) * PAGE_WIDTH_NUM_LIMIT);
        int pageLimitEnd = Math.min(pageLimitStart + (PAGE_WIDTH_NUM_LIMIT - 1), this.totalPage);

        pageContext.setAttribute("categoryList", this.categoryList);

        pageContext.setAttribute("category", fc.getCategoryFilter());
        pageContext.setAttribute("searchText", fc.getSearchTextFilter());

        pageContext.setAttribute("boardList", this.boardList);
        pageContext.setAttribute("totalCount", this.totalCount);

        pageContext.setAttribute("curPage", curPage);
        pageContext.setAttribute("totalPage", this.totalPage);
        pageContext.setAttribute("prevPage", prevPage);
        pageContext.setAttribute("nextPage", nextPage);

        pageContext.setAttribute("pageLimitStart", pageLimitStart);
        pageContext.setAttribute("pageLimitEnd", pageLimitEnd);

//        request.getSession().setAttribute("curPage", curPage);
    }
}
