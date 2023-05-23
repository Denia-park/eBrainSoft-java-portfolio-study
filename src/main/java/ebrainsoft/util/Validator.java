package ebrainsoft.util;

import com.oreilly.servlet.MultipartRequest;

import javax.servlet.http.HttpServletRequest;

public class Validator {
    public static boolean isAllValidOnPost(HttpServletRequest request) {
        String category = request.getParameter("category");
        String writer = request.getParameter("writer");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String password = request.getParameter("password");

        String passwordRegex = "[a-zA-Z0-9{}\\[\\]/?.,;:|()*~`!^\\-_+<>@#$%&='\"]{4,15}";
        return !category.equals("all") &&
                title.length() >= 3 && 100 >= title.length() &&
                writer.length() >= 3 && 4 >= writer.length() &&
                content.length() >= 4 && 2000 >= content.length() &&
                password.matches(passwordRegex);
    }

    public static boolean isAllValidOnEdit(HttpServletRequest request) {
        String writer = request.getParameter("writer");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        return title.length() >= 3 && 100 >= title.length() &&
                writer.length() >= 3 && 4 >= writer.length() &&
                content.length() >= 4 && 2000 >= content.length();
    }

    public static boolean isAllValidOnPost(MultipartRequest mr) {
        String category = mr.getParameter("category");
        String writer = mr.getParameter("writer");
        String title = mr.getParameter("title");
        String content = mr.getParameter("content");
        String password = mr.getParameter("password");

        String passwordRegex = "[a-zA-Z0-9{}\\[\\]/?.,;:|()*~`!^\\-_+<>@#$%&='\"]{4,15}";
        return !category.equals("all") &&
                title.length() >= 3 && 100 >= title.length() &&
                writer.length() >= 3 && 4 >= writer.length() &&
                content.length() >= 4 && 2000 >= content.length() &&
                password.matches(passwordRegex);
    }

    public static boolean isAllValidOnEdit(MultipartRequest mr) {
        String writer = mr.getParameter("writer");
        String title = mr.getParameter("title");
        String content = mr.getParameter("content");

        return title.length() >= 3 && 100 >= title.length() &&
                writer.length() >= 3 && 4 >= writer.length() &&
                content.length() >= 4 && 2000 >= content.length();
    }
}
