<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ebrainsoft.week1.model.Board" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.sql.*" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    //검색 조건 확인
    String startDayParam = request.getParameter("reg_start_date");
    String endDayParam = request.getParameter("reg_end_date");
    String categoryParam = request.getParameter("category");
    String searchTextParam = request.getParameter("searchText");
    String startDayFilter;
    String endDayFilter;
    String categoryFilter = "all";
    String searchTextFilter = "";

    //검색 버튼을 누른 경우
    if (startDayParam != null && endDayParam != null && categoryParam != null && searchTextParam != null) {
        request.getSession().setAttribute("reg_start_date", startDayParam);
        request.getSession().setAttribute("reg_end_date", endDayParam);
        request.getSession().setAttribute("category", categoryParam);
        request.getSession().setAttribute("searchText", searchTextParam);
        startDayFilter = startDayParam;
        endDayFilter = endDayParam;
        if (!categoryParam.equals("all")) {
            categoryFilter = categoryParam;
        }
        if (!searchTextParam.isEmpty()) {
            searchTextFilter = searchTextParam;
        }
    }
    //검색 버튼 안누르고 조회하는 경우
    else {
        String tempStartDay = (String) request.getSession().getAttribute("reg_start_date");
        String tempEndDay = (String) request.getSession().getAttribute("reg_end_date");
        String tempCategory = (String) request.getSession().getAttribute("category");
        String tempSearchText = (String) request.getSession().getAttribute("searchText");

        //한번도 검색을 안 누른 경우 -> 새로 조회, 현재 날짜로 조회
        if (tempStartDay == null || tempEndDay == null || tempCategory == null || tempSearchText == null) {
            startDayFilter = String.valueOf(LocalDate.now().minusYears(1));
            endDayFilter = String.valueOf(LocalDate.now());
        }
        //이미 한번 조회를 해서 데이터가 들어가 있는 경우 -> Session에서 확인한다.
        else {
            startDayFilter = tempStartDay;
            endDayFilter = tempEndDay;
            if (!tempCategory.equals("all")) {
                categoryFilter = tempCategory;
            }
            if (!tempSearchText.isEmpty()) {
                searchTextFilter = tempSearchText;
            }
        }
    }

    try {
        //목록 조회
        Connection connection = MySqlConnection.getConnection();
        Statement statement = connection.createStatement();

        List<String> categoryList = new ArrayList<>();
        String sql = "select * from category";
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            categoryList.add(resultSet.getString("NAME"));
        }

        sql = "select * from board where REG_DATETIME between ? and ?";
        if (!categoryFilter.equals("all")) {
            sql += " and CATEGORY = '" + categoryFilter + "'";
        }
        if (!searchTextFilter.isEmpty()) {
            sql += " and TITLE like '%" + searchTextFilter + "%'";
            sql += " or WRITER like '%" + searchTextFilter + "%'";
            sql += " or CONTENT like '%" + searchTextFilter + "%'";
        }
        sql += " order by REG_DATETIME desc";

        PreparedStatement searchStatement = connection.prepareStatement(sql);
        //시간 추가 -> 해야지만 검색이 가능함
        searchStatement.setString(1, startDayFilter + " 00:00:00");
        searchStatement.setString(2, endDayFilter + " 23:59:59");
        resultSet = searchStatement.executeQuery();

        System.out.println(searchStatement);

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

        //전체 게시글 수 조회
        int boardCount = boardList.size();

        pageContext.setAttribute("category", categoryFilter);
        pageContext.setAttribute("searchText", searchTextFilter);
        pageContext.setAttribute("categoryList", categoryList);
        pageContext.setAttribute("boardList", boardList);
        pageContext.setAttribute("boardCount", boardCount);

    } catch (Exception e) {
        throw new RuntimeException(e);
    }

    //페이지 처리
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

        <div class="align_center">
            <nav aria-label="Page navigation example">
                <ul class="pagination">
                    <li class="page-item">
                        <a aria-label="Previous" class="page-link" href="#">
                            <span aria-hidden="true">&laquo;</span>
                        </a>
                    </li>
                    <li class="page-item"><a class="page-link" href="#">1</a></li>
                    <li class="page-item"><a class="page-link" href="#">2</a></li>
                    <li class="page-item"><a class="page-link" href="#">3</a></li>
                    <li class="page-item">
                        <a aria-label="Next" class="page-link" href="#">
                            <span aria-hidden="true">&raquo;</span>
                        </a>
                    </li>
                </ul>
            </nav>
        </div>

        <button class="button filter_height align_left" type="button">등록</button>

        <script crossorigin="anonymous"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
