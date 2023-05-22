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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@MultipartConfig(
        location = "C:\\tempStudyFile\\attaches",
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10,
        fileSizeThreshold = 1024 * 1024 * 5)
@WebServlet("/*")
public class FrontController extends HttpServlet {
    public Map<String, Service> serviceMapping() {
        Map<String, Service> map = new ConcurrentHashMap<>();

        map.put("/index", new IndexService());
        map.put("/post", new PostService());
        map.put("/edit", new EditService());
        map.put("/detail", new DetailService());

        return map;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        String path = req.getServletPath();

        Service service = serviceMapping().get(path);
        if (service != null) {
            service.doService(req, resp);
        }
    }
}
