<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ebrainsoft.week1.model.Board" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.sql.*" %>
<%@ page import="ebrainsoft.week1.model.searchfilter.SearchUtil" %>
<%@ page import="ebrainsoft.week1.model.searchfilter.FilterCondition" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    FilterCondition sfc = SearchUtil.updateFilterConditionIfSearchConditionExist(request);

    String startDayFilter = sfc.getStartDayFilter();
    String endDayFilter = sfc.getEndDayFilter();
    String categoryFilter = sfc.getCategoryFilter();
    String searchTextFilter = sfc.getSearchTextFilter();

    try {
        //목록 조회
        Connection con = MySqlConnection.getConnection();

        List<String> categoryList = MySqlConnection.getCategoryList(con);

        String sql = "select * from board where REG_DATETIME between ? and ?";
        if (!categoryFilter.equals("all")) {
            sql += " and CATEGORY = '" + categoryFilter + "'";
        }
        if (!searchTextFilter.isEmpty()) {
            sql += " and (TITLE like '%" + searchTextFilter + "%'";
            sql += " or WRITER like '%" + searchTextFilter + "%'";
            sql += " or CONTENT like '%" + searchTextFilter + "%')";
        }

        String countSql = "select count(*) as cnt " + sql.substring(9);

        PreparedStatement countStatement = con.prepareStatement(countSql);
        //시간 추가 -> 해야지만 검색이 가능함
        countStatement.setString(1, startDayFilter + " 00:00:00");
        countStatement.setString(2, endDayFilter + " 23:59:59");

        ResultSet resultSet = countStatement.executeQuery();
        resultSet.next();

        //전체 게시글 수 조회
        int boardCount = resultSet.getInt("cnt");
        //가장 작아도 1페이지는 항상 나와야 하므로 추가.
        int totalPage = Math.max(1, (int) Math.ceil(boardCount / 10d));

        int curPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                int tempPage = Integer.parseInt(pageParam);
                if (0 < tempPage && tempPage <= 1000_000) {
                    curPage = tempPage;
                }
            } catch (NumberFormatException ignored) {
            }
        } else {
            Object tempPageParam = request.getSession().getAttribute("curPage");
            if (tempPageParam != null) {
                curPage = (Integer) tempPageParam;
            }
        }

        if (curPage > totalPage) {
            curPage = totalPage;
        }

        int pageSizeLimit = 10;
        sql += " order by REG_DATETIME desc limit " + pageSizeLimit;

        if (curPage != 1) {
            int pageOffset = (curPage - 1) * pageSizeLimit;

            sql += " offset " + pageOffset;
        }

        PreparedStatement searchStatement = con.prepareStatement(sql);
        //시간 추가 -> 해야지만 검색이 가능함
        searchStatement.setString(1, startDayFilter + " 00:00:00");
        searchStatement.setString(2, endDayFilter + " 23:59:59");

        System.out.println(searchStatement);

        resultSet = searchStatement.executeQuery();

        final String CUSTOM_DATE_FORMAT = "yyyy.MM.dd HH:mm";

        List<Board> boardList = new ArrayList<>();
        while (resultSet.next()) {
            LocalDateTime regDatetime = resultSet.getObject("REG_DATETIME", LocalDateTime.class);
            String regDate = regDatetime.format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));

            String editDate = "-";
            LocalDateTime editDatetime = resultSet.getObject("EDIT_DATETIME", LocalDateTime.class);
            if (editDatetime != null) {
                editDate = editDatetime.format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));
            }

            boardList.add(
                    Board.builder().
                            boardId(resultSet.getLong("BOARD_ID")).
                            category(resultSet.getString("CATEGORY")).
                            regDate(regDate).
                            editDate(editDate).
                            views(resultSet.getInt("VIEWS")).
                            writer(resultSet.getString("WRITER")).
                            password(resultSet.getString("PASSWORD")).
                            title(resultSet.getString("TITLE")).
                            content(resultSet.getString("CONTENT")).
                            fileExist(resultSet.getBoolean("FILE_EXIST")).
                            build());
        }

        int prevPage = curPage == 1 ? 1 : curPage - 1;
        int nextPage = curPage == totalPage ? totalPage : curPage + 1;

        int div = (curPage - 1) / 5;
        int pageLimitStart = 1 + (div * 5);
        int pageLimitEnd = Math.min(pageLimitStart + 4, totalPage);

        pageContext.setAttribute("category", categoryFilter);
        pageContext.setAttribute("searchText", searchTextFilter);
        pageContext.setAttribute("categoryList", categoryList);
        pageContext.setAttribute("boardList", boardList);
        pageContext.setAttribute("boardCount", boardCount);
        pageContext.setAttribute("curPage", curPage);
        request.getSession().setAttribute("curPage", curPage);
        pageContext.setAttribute("totalPage", totalPage);
        pageContext.setAttribute("prevPage", prevPage);
        pageContext.setAttribute("nextPage", nextPage);
        pageContext.setAttribute("pageLimitStart", pageLimitStart);
        pageContext.setAttribute("pageLimitEnd", pageLimitEnd);

        con.close();
        resultSet.close();
        countStatement.close();
        searchStatement.close();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
%>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta content="width=device-width, initial-scale=1" name="viewport">
        <title>자유 게시판</title>
        <link crossorigin="anonymous"
              href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ"
              rel="stylesheet">
        <script src="https://kit.fontawesome.com/1b3dd0a9c0.js" crossorigin="anonymous"></script>
        <link href="style.css" rel="stylesheet">
    </head>
    <body>
        <h1><a class="title_link" href="index.jsp">자유 게시판</a></h1>

        <form action="" class="filter" method="get">
            <div class="reg_date">
                <span id="reg_date_text">
                    등록일
                </span>
                <input class="filter_height text_align_center" id="reg_start_date" name="reg_start_date" type="date"
                       value=<%=startDayFilter%>>
                -
                <input class="filter_height text_align_center" id="reg_end_date" name="reg_end_date" type="date"
                       value=<%=endDayFilter%>>
            </div>
            <div class="search">
                <select class="filter_height" id="category" name="category">
                    <option value="all">전체 카테고리</option>
                    <c:forEach var="data" items="${categoryList}">
                        <option
                                <c:if test='${category.equals(data)}'>selected</c:if>
                                value="${data}">${data}
                        </option>
                    </c:forEach>
                </select>
                <input class="filter_height" id="search_box" name="searchText"
                       placeholder="검색어를 입력해주세요. (제목 + 작성자 + 내용)"
                       type="text" value="${searchText}">
                <button class="button filter_height" type="submit">검색
                </button>
            </div>
        </form>

        <div id="total_content_num">
            총 ${boardCount} 건
        </div>

        <table class="table table-hover text_align_center">
            <thead>
            <tr>
                <th style="width: 12%;" scope="col">카테고리</th>
                <th style="width: 2%;" scope="col"></th>
                <th style="width: 40%;" scope="col">제목</th>
                <th style="width: 10%;" scope="col">작성자</th>
                <th style="width: 10%;" scope="col">조회수</th>
                <th style="width: 13%;" scope="col">등록 일시</th>
                <th style="width: 13%;" scope="col">수정 일시</th>
            </tr>
            </thead>
            <tbody class="table-group-divider">
            <c:forEach var="data" items="${boardList}">
                <tr>
                    <th scope="row">${data.category}</th>
                    <td>${data.fileExist ? "<i class='fa-solid fa-paperclip'></i>" : null}</td>
                    <c:choose>
                        <c:when test="${data.title.length() > 80}">
                            <td><a class="title_link"
                                   href="detail.jsp?id=${data.boardId}">${(data.title.substring(0,81))}...</a></td>
                        </c:when>
                        <c:otherwise>
                            <td><a class="title_link" href="detail.jsp?id=${data.boardId}">${data.title}</a></td>
                        </c:otherwise>
                    </c:choose>
                    <td>${data.writer}</td>
                    <td><fmt:formatNumber value="${data.views}" pattern="#,###"/></td>
                    <td>${data.regDate}</td>
                    <td>${data.editDate}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <div class="align_center page_div">
            <nav aria-label="Page navigation example">
                <ul class="pagination">
                    <li class="page-item">
                        <a aria-label="Previous" class="page-link" href="index.jsp?page=1">
                            <span aria-hidden="true">&laquo;</span>
                        </a>
                    </li>
                    <li class="page-item">
                        <a aria-label="Previous" class="page-link" href="index.jsp?page=${prevPage}">
                            <span aria-hidden="true"><</span>
                        </a>
                    </li>
                    <c:forEach var="num" begin="${pageLimitStart}" end="${pageLimitEnd}" step="1">
                        <li class="page-item">
                            <a class="page-link" <c:if test="${num == curPage}"> id="page_select"</c:if>
                               href="index.jsp?page=${num}">${num}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item">
                        <a aria-label="Next" class="page-link" href="index.jsp?page=${nextPage}">
                            <span aria-hidden="true">></span>
                        </a>
                    </li>
                    <li class="page-item">
                        <a aria-label="Next" class="page-link" href="index.jsp?page=${totalPage}">
                            <span aria-hidden="true">&raquo;</span>
                        </a>
                    </li>
                </ul>
            </nav>
        </div>

        <button class="button filter_height align_left" type="button" onclick="location.href='post.jsp'">등록</button>

        <script crossorigin="anonymous"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
