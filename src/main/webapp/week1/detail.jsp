<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ebrainsoft.model.Board" %>
<%@ page import="java.util.List" %>
<%@ page import="ebrainsoft.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="ebrainsoft.model.Comment" %>
<%@ page import="ebrainsoft.week1.util.FileUtil" %>
<%@ page import="ebrainsoft.model.BoardInfo" %>
<%@ page import="ebrainsoft.model.FileInfo" %>
<%@ page import="ebrainsoft.week1.util.CommentUtil" %>
<%@ page import="ebrainsoft.week1.util.BoardUtil" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String boardId = request.getParameter("id");

    try {
        Connection con = MySqlConnection.getConnection();

        BoardUtil boardUtil = new BoardUtil();
        Board findBoard = boardUtil.querySingleBoard(con, boardId);

        FileUtil fileUtil = new FileUtil();
        List<FileInfo> fileInfoList = fileUtil.queryFileList(con, boardId);

        CommentUtil commentUtil = new CommentUtil();
        List<Comment> commentList = commentUtil.queryCommentList(con, boardId);

        boardUtil.queryUpdateBoardView(con, findBoard);

        pageContext.setAttribute("files", fileInfoList);
        pageContext.setAttribute("board", findBoard);
        pageContext.setAttribute("commentList", commentList);
        pageContext.setAttribute("status", request.getParameter("status"));
        pageContext.setAttribute("type", request.getParameter("type"));

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
        <link href="../style.css" rel="stylesheet">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
        <script src="https://kit.fontawesome.com/1b3dd0a9c0.js" crossorigin="anonymous"></script>
        <script src="../urlsafe-base64.js"></script>
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
            <c:forEach var="file" items="${files}">
                <div class="file">
                    <i class="fa-solid fa-download"></i> <a
                        href="downloadAction.jsp?file=${file.urlEncodedFileRealName}">${file.fileName}</a>
                </div>
            </c:forEach>

        </div>
        <div class="detail_comment_box">
            <div class="comment">
                <c:forEach var="comment" items="${commentList}">
                    <div id="reg_date">${comment.regDate}</div>
                    <div id="content">${comment.content}</div>
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

                <div class="popup_bot_button_box">
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
                    "&pw=" + encode(btoa($('#password').val())) + "&type=" + actionType;
            }
        </script>

        <script crossorigin="anonymous"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
