package ebrainsoft.week2.service;

import ebrainsoft.week2.repository.BoardRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteService implements Service {

    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) {
        String boardId = request.getParameter("id");
        String userPassword = request.getParameter("pw");

        final String SUCCESS_URL = "index";
        final String FAILURE_URL = "detail?id=" + boardId + "&type=delete&status=fail";


        try {
            if (!BoardRepository.verifyPassword(boardId, userPassword)) {
                response.sendRedirect(FAILURE_URL);
                return;
            }

            if (BoardRepository.deleteBoard(boardId) > 0) {
                response.sendRedirect(SUCCESS_URL);
                return;
            }

            response.sendRedirect(FAILURE_URL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
