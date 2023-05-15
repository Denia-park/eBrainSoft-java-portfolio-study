<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ebrainsoft.week1.model.Board" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    LocalDate today = LocalDate.now();
    LocalDate previousDate = today.minusYears(1);

    try {
        //목록 조회
        Connection connection = MySqlConnection.getConnection();

        String sql = "select * from board";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<Board> boardList = new ArrayList<>();
        while (resultSet.next()) {
            boardList.add(new Board(
                    resultSet.getLong("BOARD_ID"),
                    resultSet.getString("CATEGORY"),
                    resultSet.getObject("REG_DATETIME", LocalDate.class),
                    resultSet.getObject("EDIT_DATETIME", LocalDate.class),
                    resultSet.getInt("VIEWS"),
                    resultSet.getString("WRITER"),
                    resultSet.getString("PASSWORD"),
                    resultSet.getString("TITLE"),
                    resultSet.getString("CONTENT")
            ));
        }

        pageContext.setAttribute("boardList", boardList);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    //목록 뿌리기

    //페이지 처리

    //검색조건을 계속해서 유지해야함 -> 쿠키 사용

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
        <link href="../style.css" rel="stylesheet">
    </head>
    <body>
        <h1>자유 게시판</h1>

        <form action="" class="filter" method="get">
            <div class="reg_date">
                <span id="reg_date_text">
                    등록일
                </span>
                <input class="filter_height text_align_center" id="reg_start_date" name="reg_start_date" type="date"
                       value=<%=previousDate%>>
                -
                <input class="filter_height text_align_center" id="reg_end_date" name="reg_end_date" type="date"
                       value=<%=today%>>
            </div>
            <div class="search">
                <select class="filter_height" id="category" name="category">
                    <option value="all">전체 카테고리</option>
                    <option value="java">Java</option>
                    <option value="javascript">JavaScript</option>
                    <option value="database">DataBase</option>
                </select>
                <input class="filter_height" id="search_box" name="search" placeholder="검색어를 입력해주세요. (제목 + 작성자 + 내용)"
                       type="text">
                <button class="button filter_height" type="submit">검색
                </button>
            </div>
        </form>

        <div id="total_content_num">
            총 10건
        </div>

        <table class="table table-hover text_align_center">
            <thead>
            <tr>
                <th scope="col">카테고리</th>
                <th scope="col"></th>
                <th scope="col">제목</th>
                <th scope="col">작성자</th>
                <th scope="col">조회수</th>
                <th scope="col">등록 일시</th>
                <th scope="col">수정 일시</th>
            </tr>
            </thead>
            <tbody class="table-group-divider">
            <c:forEach var="data" items="${boardList}">
                <tr>
                    <th scope="row">${data.category}</th>
                    <td>file</td>
                    <td>${data.title}</td>
                    <td>${data.writer}</td>
                    <td>${data.views}</td>
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
