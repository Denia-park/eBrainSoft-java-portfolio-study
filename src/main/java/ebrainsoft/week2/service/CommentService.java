package ebrainsoft.week2.service;

import ebrainsoft.week2.repository.CommentRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class CommentService implements Service {
    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) {
        try {
            String boardId = request.getParameter("id");
            String content = request.getParameter("content");

            CommentRepository.saveComment(boardId, content);

            //덧글 작성 글로 리다이렉트
            response.sendRedirect("detail?id=" + boardId);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
