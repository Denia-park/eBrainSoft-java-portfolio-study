package ebrainsoft.week2.service;

import ebrainsoft.model.Board;
import ebrainsoft.model.FileInfo;
import ebrainsoft.week2.repository.BoardRepository;
import ebrainsoft.week2.repository.FileRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditService implements Service {
    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) {
        try {
            String boardId = request.getParameter("id");
            String userPassword = request.getParameter("pw");

            if (userPassword != null && !BoardRepository.verifyPassword(boardId, userPassword)) {
                response.sendRedirect("detail?id=" + boardId + "&type=edit&status=fail");
                return;
            }

            response.setHeader("BoardId", boardId);

            showEditView(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void showEditView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        String boardId = request.getParameter("id");
        String status = request.getParameter("status");

        Board findBoard = BoardRepository.findBoardById(boardId);

        List<FileInfo> fileInfoList = FileRepository.findAllFileById(boardId);

        List<String> existFileNameList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            existFileNameList.add(fileInfo.getFileRealName());
        }

        while (fileInfoList.size() != 3) {
            fileInfoList.add(new FileInfo(null, null, null));
        }

        request.setAttribute("existFileNameList", existFileNameList);
        request.setAttribute("files", fileInfoList);
        request.setAttribute("board", findBoard);
        request.setAttribute("status", status);

        request.getRequestDispatcher("/WEB-INF/week2/edit.jsp").forward(request, response);
    }
}
