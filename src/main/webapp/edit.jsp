<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ebrainsoft.week1.connection.MySqlConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="ebrainsoft.week1.model.Comment" %>
<%@ page import="ebrainsoft.week1.model.Board" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String boardId = request.getParameter("id");
    String status = request.getParameter("status");

    pageContext.setAttribute("status", status);

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

        Board findBoard = Board.builder().
                boardId(resultSet.getLong("BOARD_ID")).
                category(resultSet.getString("CATEGORY")).
                regDate(regDate).
                editDate(editDate).
                views(Integer.valueOf(resultSet.getString("VIEWS"))).
                writer(resultSet.getString("WRITER")).
                password(resultSet.getString("PASSWORD")).
                title(resultSet.getString("TITLE")).
                content(resultSet.getString("CONTENT")).
                fileExist(resultSet.getBoolean("FILE_EXIST")).
                build();

        //파일 보여주기.
        sql = "select * from file where BOARD_ID = " + boardId;

        resultSet = statement.executeQuery(sql);
        List<String[]> fileNameList = new ArrayList<>();
        List<String> existFileNameList = new ArrayList<>();
        while (resultSet.next()) {
            String fileRealName = resultSet.getString("FILE_REAL_NAME");
            String fileUrlName = URLEncoder.encode(fileRealName, StandardCharsets.UTF_8);
            fileNameList.add(new String[]{resultSet.getString("FILE_NAME"), fileUrlName, fileRealName});
            existFileNameList.add(fileRealName);
        }

        while (fileNameList.size() != 3) {
            fileNameList.add(new String[]{null, null});
        }

        pageContext.setAttribute("existFileNameList", existFileNameList);
        pageContext.setAttribute("files", fileNameList);
        pageContext.setAttribute("board", findBoard);

        connection.close();
        statement.close();
        resultSet.close();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }

%>
<!doctype html>
<html>
    <head>
        <meta charset="utf-8">
        <meta content="width=device-width, initial-scale=1" name="viewport">
        <title>자유 게시판</title>
        <script src="urlsafe-base64.js"></script>
        <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
        <script src="https://kit.fontawesome.com/1b3dd0a9c0.js" crossorigin="anonymous"></script>
        <!-- 공통 CSS -->
        <link href="style.css" rel="stylesheet">
    </head>
    <body>
        <div>
            <div>
                <h3> 기본 정보 </h3>
            </div>
            <div class="caption">
                <span class="t_red">*</span> 표시는 필수입력 항목입니다.
            </div>
            <table class="post_table">
                <tbody id="tbody">
                <tr>
                    <th scope="row">카테고리<span class="t_red">*</span></th>
                    <td>
                        ${board.category}
                    </td>
                </tr>
                <tr>
                    <th scope="row">등록 일시</th>
                    <td>${board.regDate}</td>
                </tr>
                <tr>
                    <th scope="row">수정 일시</th>
                    <td>${board.editDate}</td>
                </tr>
                <tr>
                    <th scope="row">조회수</th>
                    <td>${board.views}</td>
                </tr>
                <tr>
                    <th scope="row">작성자<span class="t_red">*</span></th>
                    <td><input class="tbox01" id="board_writer" name="board_writer" value="${board.writer}"/></td>
                </tr>
                <tr>
                    <th scope="row">비밀번호<span class="t_red">*</span></th>
                    <td><input class="tbox01" id="password_first" placeholder="비밀번호" value=""/></td>
                </tr>
                <tr>
                    <th scope="row">제목<span class="t_red">*</span></th>
                    <td><input class="tbox01" id="board_subject" name="board_subject" value="${board.title}"/></td>
                </tr>
                <tr>
                    <th scope="row">내용<span class="t_red">*</span></th>
                    <td><textarea class="textarea01" id="board_content" name="board_content">${board.content}</textarea>
                    </td>
                </tr>
                <tr>
                    <th scope="row">파일첨부</th>
                    <td>
                        <c:forEach var="data" items="${files}" varStatus="stat">
                            <c:choose>
                                <c:when test="${data[0] != null}">
                                    <div class="file edit_file_div" id="edit_file_upload_${stat.index+1}">
                                        <i class="fa-solid fa-download"></i> ${data[0]}
                                        <button class="edit_file_btn"
                                                onclick="location.href='downloadAction.jsp?file=${data[1]}'">
                                            Download
                                        </button>
                                        <button class="edit_file_btn"
                                                onclick="restoreFileUploadBtn(${stat.index+1},'${data[2]}')">
                                            X
                                        </button>
                                    </div>
                                    <div class="file-upload edit_file_div" id="edit_file_upload_hidden${stat.index+1}"
                                         style="display:none;">
                                        <input class="upload-name" disabled type="text" value="파일선택">

                                        <label for="ex_filename_${stat.index+1}">파일 찾기</label>
                                        <input class="upload-hidden" id="ex_filename_${stat.index+1}" type="file">
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="file-upload edit_file_div">
                                        <input class="upload-name" disabled type="text" value="파일선택">

                                        <label for="ex_filename_${stat.index+1}">파일 찾기</label>
                                        <input class="upload-hidden" id="ex_filename_${stat.index+1}" type="file">
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
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
                alert("게시글 수정에 실패하였습니다.");
            }
        });

        let deleteNameList = [];

        function restoreFileUploadBtn(divIndex, deleteFileName) {
            let fileHiddenTarget = $("#edit_file_upload_hidden" + divIndex);
            fileHiddenTarget.show();
            let fileTarget = $("#edit_file_upload_" + divIndex);
            fileTarget.hide();

            deleteNameList.push(deleteFileName);
        }

        /** 게시판 - 목록 페이지 이동 */
        function doCancel() {
            location.href = "index.jsp";
        }

        async function doVerify() {

            let writer = $("#board_writer").val();
            let passwordFirst = $("#password_first").val();
            let title = $("#board_subject").val();
            let content = $("#board_content").val();
            let file1 = $("#ex_filename_1")[0].files[0];
            let file2 = $("#ex_filename_2")[0].files[0];
            let file3 = $("#ex_filename_3")[0].files[0];


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

            let formData = new FormData();
            formData.set('enctype', 'multipart/form-data');
            formData.append('writer', writer);
            formData.append('title', title);
            formData.append('content', content);
            formData.append('file1', file1);
            formData.append('file2', file2);
            formData.append('file3', file3);

            <c:forEach var="data" items="${existFileNameList}">
            formData.append('existFileNameList', '${data}');
            </c:forEach>

            for (const name of deleteNameList) {
                formData.append('deleteNameList', name);
            }

            const response = await fetch('doEditAction.jsp?id=' + '${board.boardId}' + '&pw=' + encode(btoa(passwordFirst)), {
                    method: 'POST',
                    body: formData
                }
            );
            if (response.redirected) {
                window.location.href = response.url;
            }
        }
    </script>
</html>

