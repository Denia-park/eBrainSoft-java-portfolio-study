<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String status = request.getParameter("status");
    pageContext.setAttribute("status", status);

    //목록 조회
    Connection connection = MySqlConnection.getConnection();
    Statement statement = connection.createStatement();

    List<String> categoryList = new ArrayList<>();
    String sql = "select * from category";
    ResultSet resultSet = statement.executeQuery(sql);
    while (resultSet.next()) {
        categoryList.add(resultSet.getString("NAME"));
    }

    pageContext.setAttribute("categoryList", categoryList);

%>
<!doctype html>
<html>
    <head>
        <meta charset="utf-8">
        <meta content="width=device-width, initial-scale=1" name="viewport">
        <title>자유 게시판</title>
        <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
        <!-- 공통 CSS -->
        <link href="style.css" rel="stylesheet">
    </head>
    <body>
        <h2>게시글 작성</h2>
        <div>
            <div class="caption">
                <span class="t_red">*</span> 표시는 필수입력 항목입니다.
            </div>
            <table class="post_table">
                <tbody id="tbody">
                <tr>
                    <th scope="row">카테고리<span class="t_red">*</span></th>
                    <td>
                        <select class="tbox01" id="category" name="category">
                            <option value="all">카테고리 선택</option>
                            <c:forEach var="data" items="${categoryList}">
                                <option value="${data}">${data} </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th scope="row">작성자<span class="t_red">*</span></th>
                    <td><input class="tbox01" id="board_writer" name="board_subject" value=""/></td>
                </tr>
                <tr>
                    <th scope="row">비밀번호<span class="t_red">*</span></th>
                    <td><input class="tbox01" id="password_first" placeholder="비밀번호" value=""/>
                        <input class="tbox01" id="password_second" placeholder="비밀번호 확인" value=""/></td>
                </tr>
                <tr>
                    <th scope="row">제목<span class="t_red">*</span></th>
                    <td><input class="tbox01" id="board_subject" name="board_writer" value=""/></td>
                </tr>
                <tr>
                    <th scope="row">내용<span class="t_red">*</span></th>
                    <td><textarea class="textarea01" id="board_content" name="board_content"></textarea></td>
                </tr>
                <tr>
                    <th scope="row">파일첨부</th>
                    <td>
                        <div class="file-upload">
                            <input class="upload-name" disabled="disabled" type="text" value="파일선택">

                            <label for="ex_filename_1">파일 찾기</label>
                            <input class="upload-hidden" id="ex_filename_1" type="file">
                        </div>
                        <div class="file-upload">
                            <input class="upload-name" disabled="disabled" type="text" value="파일선택">

                            <label for="ex_filename_2">파일 찾기</label>
                            <input class="upload-hidden" id="ex_filename_2" type="file">
                        </div>
                        <div class="file-upload">
                            <input class="upload-name" disabled="disabled" type="text" value="파일선택">

                            <label for="ex_filename_3">파일 찾기</label>
                            <input class="upload-hidden" id="ex_filename_3" type="file">
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="post_bot_button_box">
            <button id="cancel_btn" onclick="doCancel()">취소</button>
            <button class="check_btn" id="save_btn" onclick="doVerify()">저장</button>
        </div>
    </body>

    <script>
        $(document).ready(function () {
            const fileTarget = $('.file-upload .upload-hidden');

            fileTarget.on('change', function () {  // 값이 변경되면
                let filename;

                if (window.FileReader) {  // modern browser
                    filename = $(this)[0].files[0].name;
                }

                // 추출한 파일명 삽입
                $(this).siblings('.upload-name').val(filename);
            });

            if ('${status}' === "fail") {
                alert("게시글 등록을 실패하였습니다.");
            }
        });

        /** 게시판 - 목록 페이지 이동 */
        function doCancel() {
            location.href = "index.jsp";
        }

        /** 게시판 - 작성  */
        function insertBoard() {

            var boardSubject = $("#board_subject").val();
            var boardContent = $("#board_content").val();

            if (boardSubject == "") {
                alert("제목을 입력해주세요.");
                $("#board_subject").focus();
                return;
            }

            if (boardContent == "") {
                alert("내용을 입력해주세요.");
                $("#board_content").focus();
                return;
            }

        }

        /** 게시판 - 작성 콜백 함수 */
        function insertBoardCallback(obj) {
            if (obj != null) {

                var result = obj.result;

                if (result == "SUCCESS") {
                    alert("게시글 등록을 성공하였습니다.");
                    doCancel();
                } else {
                    alert("게시글 등록을 실패하였습니다.");
                    return;
                }
            }
        }

        function doVerify() {
            let category = $("#category").val();
            let writer = $("#board_writer").val();
            let passwordFirst = $("#password_first").val();
            let passwordSecond = $("#password_second").val();
            let title = $("#board_subject").val();
            let content = $("#board_content").val();

            if (category === "all") {
                alert("카테고리를 선택해주세요.");
                $("#category").focus();
                return;
            }
            if (writer.length < 3 || 4 < writer.length) {
                alert("작성자는 3글자 이상, 5글자 미만입니다.");
                $("#board_writer").focus();
                return;
            }
            const regex = /[a-zA-Z0-9{}\[\]\/?.,;:|()*~`!^\-_+<>@#$%&\\='"]{4,15}/;
            if (!regex.test(passwordFirst)) {
                alert("비밀번호는 4글자 이상, 16글자 미만입니다.");
                $("#password_first").focus();
                return;
            }
            if (passwordFirst !== passwordSecond) {
                alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
                $("#password_second").focus();
                return;
            }
            if (title.length < 3 || 100 < title.length) {
                alert("제목은 4글자 이상, 100글자 미만입니다.");
                $("#board_subject").focus();
                return;
            }
            if (content.length < 4 || 2000 < content.length) {
                alert("내용은 4글자 이상, 2000글자 미만입니다.");
                $("#board_writer").focus();
                return;
            }
            let obj = {
                "category": category,
                "writer": writer,
                "title": title,
                "content": content,
                "password": passwordFirst
            }

            sendPOST("doPostAction.jsp", obj)
        }

        // sendPOST("url","json 형태 파라미터" )
        function sendPOST(action, params) {

            let form = document.createElement('form');

            form.setAttribute('method', 'post');

            form.setAttribute('action', action);

            document.charset = "utf-8";

            for (let key in params) {

                let hiddenField = document.createElement('input');

                hiddenField.setAttribute('type', 'hidden');

                hiddenField.setAttribute('name', key);

                hiddenField.setAttribute("value", params[key]);

                form.appendChild(hiddenField);
            }

            document.body.appendChild(form);

            form.submit();
        }
    </script>
</html>
