package ebrainsoft.week2.service;

import ebrainsoft.model.Board;
import ebrainsoft.model.Comment;
import ebrainsoft.model.FileInfo;
import ebrainsoft.week2.repository.BoardRepository;
import ebrainsoft.week2.repository.CommentRepository;
import ebrainsoft.week2.repository.FileRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class DetailService implements Service {
    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) {
        String boardId = request.getParameter("id");

        try {
            Board findBoard = BoardRepository.findBoardById(boardId);
            BoardRepository.updateBoardView(findBoard);

            List<FileInfo> fileInfoList = FileRepository.findAllFileById(boardId);

            List<Comment> commentList = CommentRepository.findAllCommentById(boardId);

            request.setAttribute("files", fileInfoList);
            request.setAttribute("board", findBoard);
            request.setAttribute("commentList", commentList);
            request.setAttribute("status", request.getParameter("status"));
            request.setAttribute("type", request.getParameter("type"));

            request.getRequestDispatcher("/WEB-INF/week2/detail.jsp").forward(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
