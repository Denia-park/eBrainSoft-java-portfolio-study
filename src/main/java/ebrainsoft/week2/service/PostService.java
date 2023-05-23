package ebrainsoft.week2.service;

import ebrainsoft.util.Validator;
import ebrainsoft.week2.repository.BoardRepository;
import ebrainsoft.week2.repository.CategoryRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PostService implements Service {
    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();

        if (method.equals("GET"))
            doGet(request, response);
        else if (method.equals("POST"))
            doPost(request, response);
    }

    /**
     * Get요청이 왔을 때 처리
     *
     * @param request
     * @param response
     */

    private void doGet(HttpServletRequest request, HttpServletResponse response) {
        String status = request.getParameter("status");

        try {
            List<String> categoryList = CategoryRepository.findAllCategory();

            request.setAttribute("categoryList", categoryList);
            request.setAttribute("status", status);

            request.getRequestDispatcher("/WEB-INF/week2/post.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Post요청이 왔을 때 처리
     *
     * @param request
     * @param response
     */

    private void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!Validator.isAllValidOnPost(request)) {
                response.sendRedirect("post?status=fail");
                return;
            }

            int result = BoardRepository.saveBoard(request);

            if (result < 0) {
                response.sendRedirect("post?status=fail");
                return;
            }

            response.sendRedirect("index");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
