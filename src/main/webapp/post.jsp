<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="ebrainsoft.week1.model.BoardInfo" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String status = request.getParameter("status");
    pageContext.setAttribute("status", status);

    try {
        Connection con = MySqlConnection.getConnection();
        BoardInfo boardInfo = new BoardInfo();

        boardInfo.queryCategoryList(con);

        pageContext.setAttribute("categoryList", boardInfo.getCategoryList());

        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
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
                            <input class="upload-name" disabled type="text" value="파일선택">

                            <label for="ex_filename_1">파일 찾기</label>
                            <input class="upload-hidden" id="ex_filename_1" type="file">
                        </div>
                        <div class="file-upload">
                            <input class="upload-name" disabled type="text" value="파일선택">

                            <label for="ex_filename_2">파일 찾기</label>
                            <input class="upload-hidden" id="ex_filename_2" type="file">
                        </div>
                        <div class="file-upload">
                            <input class="upload-name" disabled type="text" value="파일선택">

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

        function isNotValidCategory(category) {
            if (category.val() === "all") {
                alert("카테고리를 선택해주세요.");
                category.focus();
                return true;
            }

            return false;
        }

        function isNotValidWriter(writer) {
            if (writer.val().length < 3 || 4 < writer.val().length) {
                alert("작성자는 3글자 이상, 5글자 미만입니다.");
                writer.focus();
                return;
            }

            return false;
        }

        function isNotValidPassword(passwordFirst, passwordSecond) {
            const regex = /[a-zA-Z0-9{}\[\]\/?.,;:|()*~`!^\-_+<>@#$%&\\='"]{4,15}/;
            if (!regex.test(passwordFirst.val())) {
                alert("비밀번호는 4글자 이상, 16글자 미만, 영문/숫자/특수문자 만 가능합니다.");
                $("#password_first").focus();
                return true;
            }

            if (passwordFirst.val() !== passwordSecond.val()) {
                alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
                passwordSecond.focus();
                return true;
            }

            return false;
        }

        function isNotValidTitle(title) {
            if (title.val().length < 3 || 100 < title.val().length) {
                alert("제목은 4글자 이상, 100글자 미만입니다.");
                title.focus();
                return true;
            }

            return false;
        }

        function isNotValidContent(content) {
            if (content.val().length < 4 || 2000 < content.val().length) {
                alert("내용은 4글자 이상, 2000글자 미만입니다.");
                content.focus();
                return true;
            }

            return false;
        }

        async function doVerify() {
            let category = $("#category");
            let writer = $("#board_writer");
            let passwordFirst = $("#password_first");
            let passwordSecond = $("#password_second");
            let title = $("#board_subject");
            let content = $("#board_content");

            if (isNotValidCategory(category)) {
                return;
            }

            if (isNotValidWriter(writer)) {
                return;
            }

            if (isNotValidPassword(passwordFirst, passwordSecond)) {
                return;
            }

            if (isNotValidTitle(title)) {
                return;
            }

            if (isNotValidContent(content)) {
                return;
            }

            await sendFormData(category.val(), writer.val(), title.val(), content.val(), passwordFirst.val());
        }

        async function sendFormData(category, writer, title, content, passwordFirst) {
            let file1 = $("#ex_filename_1")[0].files[0];
            let file2 = $("#ex_filename_2")[0].files[0];
            let file3 = $("#ex_filename_3")[0].files[0];

            let formData = new FormData();
            formData.set('enctype', 'multipart/form-data');
            formData.append('category', category);
            formData.append('writer', writer);
            formData.append('title', title);
            formData.append('content', content);
            formData.append('password', passwordFirst);
            formData.append('file1', file1);
            formData.append('file2', file2);
            formData.append('file3', file3);

            const response = await fetch('doPostAction.jsp', {
                method: 'POST',
                body: formData
            });

            if (response.redirected) {
                window.location.href = response.url;
            }
        }
    </script>
</html>
