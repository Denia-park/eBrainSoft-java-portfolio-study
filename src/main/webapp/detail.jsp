<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="ebrainsoft.week1.model.Board" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    //검색 조건 확인
    String boardId = request.getParameter("id");

    try {
        //목록 조회
        Connection connection = MySqlConnection.getConnection();
        Statement statement = connection.createStatement();

        String sql = "select * from board where BOARD_ID = " + boardId;

        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();

        final String CUSTOM_DATE_FORMAT = "yyyy.MM.dd HH:mm";

        LocalDateTime regDatetime = resultSet.getObject("REG_DATETIME", LocalDateTime.class);
        String regDate = regDatetime.format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));

        String editDate = "-";
        LocalDateTime editDatetime = resultSet.getObject("EDIT_DATETIME", LocalDateTime.class);
        if (editDatetime != null) {
            editDate = editDatetime.format(DateTimeFormatter.ofPattern(CUSTOM_DATE_FORMAT));
        }

        Board findBoard = Board.builder().
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
                build();


        pageContext.setAttribute("board", findBoard);

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
        <link href="style.css" rel="stylesheet">
    </head>
    <body>
        <div class="detail_info_box">
            <div>
                ${board.writer}
            </div>

            <div>
                <span>등록일시</span>
                <span>${board.regDate}</span>

                <span id="detail_edit_date">수정일시</span>
                <span>${board.editDate}</span>
            </div>
        </div>

        <div class="detail_title_box">
            <div id="title">
                <span id="category">
                    [${board.category.toUpperCase()}]
                </span>

                <span>
                    ${board.title}
                </span>
            </div>

            <div id="views">
                조회수: ${board.views}
            </div>
        </div>

        <hr id="title_box_bot_line"/>

        <div class="detail_content_box">
            <div class="content">${board.content}</div>

        </div>
        <div class="detail_file_box">
            <div class="file">
                <a href="http://">첨부파일1.hwp</a>
            </div>
            <div class="file">
                <a href="http://">첨부파일1.hwp</a>
            </div>
        </div>
        <div class="detail_reply_box">
            <div class="reply">
                <div id="reg_date">2020.03.09 16:32</div>
                <div id="content">댓글이 출력됩니다.</div>
                <hr id="reply_bot_line">
            </div>
            <div class="reply_input_box">
                <input id="input_box" placeholder="댓글을 입력해주세요" type="text">
                <button id="submit_btn">등록</button>
            </div>
        </div>

        <hr id="reply_box_bot_line"/>

        <div class="detail_bot_button_box">
            <button id="list_btn">목록</button>
            <button id="edit_btn">수정</button>
            <button id="delete_btn">삭제</button>
        </div>

        <script crossorigin="anonymous"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
