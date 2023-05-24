package ebrainsoft.week2.controller;

import ebrainsoft.week2.service.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@MultipartConfig(
        location = "C:\\tempStudyFile\\tempAttaches",
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10,
        fileSizeThreshold = 1024 * 1024 * 5)
@WebServlet("/week2/*")
public class FrontController extends HttpServlet {
    public Map<String, Service> serviceMapping() {
        Map<String, Service> map = new ConcurrentHashMap<>();

        map.put("/week2/index", new IndexService());
        map.put("/week2/post", new PostService());
        map.put("/week2/edit", new EditService());
        map.put("/week2/detail", new DetailService());
        map.put("/week2/delete", new DeleteService());
        map.put("/week2/comment", new CommentService());

        return map;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        String uri = req.getRequestURI();
        String method = req.getMethod();
        Service service = serviceMapping().get(uri);
        String url = req.getRequestURL().toString();
        List<String> values = new ArrayList<>();
        req.getParameterNames().asIterator().forEachRemaining(values::add);
        log.info("uri [{}] = {}, {}", method, url, values);

        if (service != null) {
            service.doService(req, resp);
        }
    }
}
