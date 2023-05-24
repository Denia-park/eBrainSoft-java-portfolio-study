package ebrainsoft.week2.service;

import ebrainsoft.model.Board;
import ebrainsoft.model.FileInfo;
import ebrainsoft.week2.repository.BoardRepository;
import ebrainsoft.week2.repository.FileRepository;
import ebrainsoft.week2.util.FileUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditService implements Service {
    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) {
        try {
            String referer = request.getHeader("referer");

            if (referer.contains("week2/detail")) {
                requestFromDetail(request, response);
            } else if (referer.contains("week2/edit")) {
                requestFromEdit(request, response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void requestFromEdit(HttpServletRequest request, HttpServletResponse response) {
        try {
            String boardId = request.getParameter("id");
            String userPassword = request.getParameter("pw");

            if (!BoardRepository.verifyPassword(boardId, userPassword)) {
                response.sendRedirect("edit?id=" + boardId + "&status=fail");
                return;
            }

            List<String> existFileNames = FileUtil.extractFileNamesFromRequest(request, "existFileNameList");
            List<String> deleteFileNames = FileUtil.extractFileNamesFromRequest(request, "deleteNameList");
            List<String> addFileNames = FileUtil.extractFileNamesFromRequest(request, "file");

            boolean isFileExist = FileUtil.checkIsFileExist(existFileNames, deleteFileNames, addFileNames);

            int result = BoardRepository.updateBoard(request, boardId, isFileExist);
            if (result < 0) {
                response.sendRedirect("edit?id=" + boardId + "&status=fail");
                return;
            }

            //사라진 애는 삭제
            FileRepository.deleteFileList(boardId, deleteFileNames);

            //추가된 애는 업데이트
            List<FileInfo> fileInfos = FileUtil.saveFilesFromRequest(request);
            FileRepository.saveFileOnDB(fileInfos, boardId);
            
            if (result > 0) {
                response.sendRedirect("index");
            } else {
                response.sendRedirect("edit?id=" + boardId + "&status=fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestFromDetail(HttpServletRequest request, HttpServletResponse response) throws SQLException, NoSuchAlgorithmException, IOException, ServletException {
        String boardId = request.getParameter("id");
        String userPassword = request.getParameter("pw");

        if (userPassword != null && !BoardRepository.verifyPassword(boardId, userPassword)) {
            response.sendRedirect("detail?id=" + boardId + "&type=edit&status=fail");
            return;
        }

        response.setHeader("BoardId", boardId);

        showEditView(request, response);
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
