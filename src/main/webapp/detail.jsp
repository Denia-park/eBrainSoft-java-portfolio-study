<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<%@ page import="ebrainsoft.week1.model.Comment" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    //검색 조건 확인
    String boardId = request.getParameter("id");
    String status = request.getParameter("status");
    String type = request.getParameter("type");
    pageContext.setAttribute("status", status);
    pageContext.setAttribute("type", type);

    try {
        Connection connection = MySqlConnection.getConnection();

        //내용 조회
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

        //미리 조회수 1 올린다
        int updateViews = resultSet.getInt("VIEWS") + 1;

        Board findBoard = Board.builder().
                boardId(resultSet.getLong("BOARD_ID")).
                category(resultSet.getString("CATEGORY")).
                regDate(regDate).
                editDate(editDate).
                views(updateViews).
                writer(resultSet.getString("WRITER")).
                password(resultSet.getString("PASSWORD")).
                title(resultSet.getString("TITLE")).
                content(resultSet.getString("CONTENT")).
                fileExist(resultSet.getBoolean("FILE_EXIST")).
                build();

        sql = "select * from comment where BOARD_ID = " + boardId;

        resultSet = statement.executeQuery(sql);

        List<Comment> commentList = new ArrayList<>();
        while (resultSet.next()) {
            commentList.add(
                    Comment.builder().
                            replyId(resultSet.getLong("REPLY_ID")).
                            boardId(resultSet.getLong("BOARD_ID")).
                            content(resultSet.getString("CONTENT")).
                            regDate(resultSet.getString("REG_DATETIME")).
                            build()
            );
        }

        sql = "UPDATE board SET VIEWS =? where BOARD_ID = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, updateViews);
        preparedStatement.setLong(2, findBoard.getBoardId());
        preparedStatement.executeUpdate();

        pageContext.setAttribute("board", findBoard);
        pageContext.setAttribute("commentList", commentList);

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
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
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
        <div class="detail_comment_box">
            <div class="comment">
                <c:forEach var="data" items="${commentList}">
                    <div id="reg_date">${data.regDate}</div>
                    <div id="content">${data.content}</div>
                    <hr id="comment_bot_line">
                </c:forEach>
            </div>
            <form action="postCommentAction.jsp?id=${board.boardId}" method="post"
                  class="comment_input_box">
                <input id="input_box" name="content" placeholder="댓글을 입력해주세요" type="text">
                <button type="submit" id="submit_btn">등록</button>
            </form>
        </div>

        <hr id="comment_box_bot_line"/>

        <div class="detail_bot_button_box">
            <button id="list_btn" onclick="location.href='index.jsp'">목록</button>
            <button id="edit_btn" onclick="openPopup('edit')">수정</button>
            <button id="delete_btn" onclick="openPopup('delete')">삭제</button>
        </div>

        <div id="layer_bg" <c:if test="${empty status}">style="display: none"</c:if>>
            <div id="popup">
                <div>
                    <table class="post_table">
                        <tbody id="tbody">
                        <tr>
                            <th scope="row">비밀번호<span class="t_red">*</span></th>
                            <td><input class="tbox01" id="password" placeholder="비밀번호" value=""/></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div style="
                <c:if test="${empty status}">display: none;</c:if>
                        text-align: center; color: red">
                    <span>비밀번호가 틀렸습니다.</span>
                </div>

                <div class="post_bot_button_box">
                    <button id="cancel_btn" onclick="closePopup()">취소</button>
                    <button class="check_btn"
                            <c:if test="${!type.equals('edit')}">style="display: none"</c:if>
                            id="check_edit_btn" onclick="doAction('edit')">확인
                    </button>
                    <button class="check_btn"
                            <c:if test="${!type.equals('delete')}">style="display: none"</c:if>
                            id="check_delete_btn" onclick="doAction('delete')">확인
                    </button>
                </div>
            </div>
        </div>

        <div class="password_check_form">

        </div>

        <script>
            function openPopup(action) {
                $('#layer_bg').show();
                if (action === 'edit') {
                    $('#check_edit_btn').show();
                    $('#check_delete_btn').hide();
                } else if (action === 'delete') {
                    $('#check_edit_btn').hide();
                    $('#check_delete_btn').show();
                }

            }

            function closePopup() {
                $('#layer_bg').hide();
            }

            function doAction(actionType) {
                location.href = "doPasswordCheckAction.jsp?id=" + ${board.boardId} +
                    "&pw=" + $('#password').val() + "&type=" + actionType;
            }
        </script>

        <script crossorigin="anonymous"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
