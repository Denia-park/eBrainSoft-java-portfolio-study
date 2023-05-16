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
<%@ page import="java.time.LocalDateTime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    LocalDate today = LocalDate.now();
    LocalDate previousDate = today.minusYears(1);
    final String CUSTOM_DATE_FORMAT = "yyyy.MM.dd HH:mm";

    try {
        //목록 조회
        Connection connection = MySqlConnection.getConnection();

        String sql = "select * from board";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<Board> boardList = new ArrayList<>();
        while (resultSet.next()) {
            String regDate = resultSet.getObject("REG_DATETIME", LocalDateTime.class).format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));
            String editDate = "-";
            if (resultSet.getObject("EDIT_DATETIME", LocalDate.class) != null) {
                editDate = resultSet.getObject("EDIT_DATETIME", LocalDateTime.class).format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));
            }
            String fileIcon = null;

            boardList.add(Board.builder().
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

        pageContext.setAttribute("boardList", boardList);

        //전체 게시글 수 조회
        int boardCount = boardList.size();
        pageContext.setAttribute("boardCount", boardCount);

    } catch (Exception e) {
        throw new RuntimeException(e);
    }

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
        <script src="https://kit.fontawesome.com/1b3dd0a9c0.js" crossorigin="anonymous"></script>
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
                    <td>${data.title}</td>
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
