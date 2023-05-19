package ebrainsoft.week1.util;

import com.oreilly.servlet.MultipartRequest;

public class Validator {
    public boolean isAllValid(MultipartRequest multipartRequest) {
        String category = multipartRequest.getParameter("category");
        String writer = multipartRequest.getParameter("writer");
        String title = multipartRequest.getParameter("title");
        String content = multipartRequest.getParameter("content");
        String password = multipartRequest.getParameter("password");

        String passwordRegex = "[a-zA-Z0-9{}\\[\\]/?.,;:|()*~`!^\\-_+<>@#$%&='\"]{4,15}";
        return !category.equals("all") &&
                title.length() >= 3 && 100 >= title.length() &&
                writer.length() >= 3 && 4 >= writer.length() &&
                content.length() >= 4 && 2000 >= content.length() &&
                password.matches(passwordRegex);
    }
}
