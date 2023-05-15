<%@ page import="java.time.LocalDate" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    LocalDate today = LocalDate.now();
    LocalDate previousDate = today.minusYears(1);
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
            <tr>
                <th scope="row">Java</th>
                <td>Java</td>
                <td>Otto</td>
                <td>@mdo</td>
                <td>Otto</td>
                <td>Mark</td>
                <td>@mdo</td>
            </tr>
            <tr>
                <th scope="row">JavaScript</th>
                <td>JavaScript</td>
                <td>Thornton</td>
                <td>@fat</td>
                <td>Jacob</td>
                <td>Thornton</td>
                <td>@fat</td>
            </tr>
            <tr>
                <th scope="row">DataBase</th>
                <td>DataBase</td>
                <td>DataBase</td>
                <td>@twitter</td>
                <td>@twitter</td>
                <td>@twitter</td>
                <td>@twitter</td>
            </tr>
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
