<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.*" %>
<%@ page import="ebrainsoft.week1.util.SearchUtil" %>
<%@ page import="ebrainsoft.week1.model.FilterCondition" %>
<%@ page import="ebrainsoft.week1.model.BoardInfo" %>
<%@ page import="ebrainsoft.week1.util.CategoryUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="ebrainsoft.week1.util.BoardUtil" %>
<%@ page import="ebrainsoft.week1.model.Board" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    FilterCondition fc = SearchUtil.updateFilterConditionIfSearchConditionExist(request);

    try {
        Connection con = MySqlConnection.getConnection();

        CategoryUtil categoryUtil = new CategoryUtil();
        List<String> categoryList = categoryUtil.queryCategoryList(con);

        BoardUtil boardUtil = new BoardUtil();
        int totalCount = boardUtil.queryTotalCount(con, fc);
        int totalPage = boardUtil.getTotalPage(totalCount);
        int needPageNum = fc.getNeedPageNum(totalPage);
        List<Board> boardList = boardUtil.queryBoardList(con, fc, needPageNum);

        BoardInfo boardInfo = new BoardInfo(totalCount, totalPage, needPageNum, boardList);

        request.getSession().setAttribute("curPage", boardInfo.getNeedPageNum());

        pageContext.setAttribute("categoryList", categoryList);
        pageContext.setAttribute("searchCategory", fc.getCategoryFilter());
        pageContext.setAttribute("searchText", fc.getSearchTextFilter());
        pageContext.setAttribute("boardList", boardInfo.getBoardList());
        pageContext.setAttribute("totalCount", boardInfo.getTotalCount());

        pageContext.setAttribute("curPage", boardInfo.getNeedPageNum());
        pageContext.setAttribute("totalPage", boardInfo.getTotalPage());
        pageContext.setAttribute("prevPage", boardInfo.getPrevPage());
        pageContext.setAttribute("nextPage", boardInfo.getNextPage());

        pageContext.setAttribute("pageLimitStart", boardInfo.getPageLimitStart());
        pageContext.setAttribute("pageLimitEnd", boardInfo.getPageLimitEnd(boardInfo.getPageLimitStart()));

        con.close();
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
                       value=<%=fc.getStartDayFilter()%>>
                -
                <input class="filter_height text_align_center" id="reg_end_date" name="reg_end_date" type="date"
                       value=<%=fc.getEndDayFilter()%>>
            </div>
            <div class="search">
                <select class="filter_height" id="category" name="searchCategory">
                    <option value="all">전체 카테고리</option>
                    <c:forEach var="data" items="${categoryList}">
                        <option
                                <c:if test='${searchCategory.equals(data)}'>selected</c:if>
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
            총 ${totalCount} 건
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
