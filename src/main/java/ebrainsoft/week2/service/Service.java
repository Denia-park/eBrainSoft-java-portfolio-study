package ebrainsoft.week2.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Service {
    /**
     * Command 패턴에서 사용되며 Service가 동작하기 위해서 반드시 필요한 메서드.
     * <p>
     * 서비스할때 필요한 동작을 정의
     *
     * @param request  HttpRequest
     * @param response HttpResponse
     */
    void doService(HttpServletRequest request, HttpServletResponse response);
}
