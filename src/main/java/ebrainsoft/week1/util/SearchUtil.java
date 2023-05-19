package ebrainsoft.week1.util;

import ebrainsoft.week1.model.BoardInfo;
import ebrainsoft.week1.model.FilterCondition;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;

@Getter
public class SearchUtil {
    private SearchUtil() {
    }

    public static FilterCondition updateFilterConditionIfSearchConditionExist(HttpServletRequest request) {
        //검색 버튼을 누른 경우
        if (hasSearchCondition(request)) {
            return updateFilterAsSearchCondition(request);
        }

        //검색 버튼 안누르고 조회하는 경우
        return updateFilterAsDefault(request);
    }

    private static FilterCondition updateFilterAsDefault(HttpServletRequest request) {
        HttpSession session = request.getSession();

        String startDay = (String) session.getAttribute(BoardInfo.REG_START_DATE);
        String endDay = (String) session.getAttribute(BoardInfo.REG_END_DATE);
        String category = getCategory(session);
        String searchText = getSearchText(session);
        Integer needPageNum = getNeedPageNum(request);

        //한번도 검색을 안 누른 경우 -> 새로 조회, 현재 날짜로 조회
        if (hasNotFilterConditionInSession(session)) {
            startDay = String.valueOf(LocalDate.now().minusYears(BoardInfo.DEFAULT_YEAR_GAP));
            endDay = String.valueOf(LocalDate.now());
        }

        return new FilterCondition(startDay, endDay, category, searchText, needPageNum);
    }

    private static boolean hasNotFilterConditionInSession(HttpSession session) {
        return session.getAttribute(BoardInfo.REG_START_DATE) == null ||
                session.getAttribute(BoardInfo.REG_END_DATE) == null ||
                session.getAttribute(BoardInfo.SEARCH_CATEGORY) == null ||
                session.getAttribute(BoardInfo.SEARCH_TEXT) == null;
    }

    private static FilterCondition updateFilterAsSearchCondition(HttpServletRequest request) {
        String startDayFilter = request.getParameter(BoardInfo.REG_START_DATE);
        String endDayFilter = request.getParameter(BoardInfo.REG_END_DATE);
        String categoryFilter = getCategory(request);
        String searchTextFilter = getSearchText(request);
        Integer needPageNum = getNeedPageNum(request);

        updateSessionInfo(request);

        return new FilterCondition(startDayFilter, endDayFilter, categoryFilter, searchTextFilter, needPageNum);
    }

    private static Integer getNeedPageNum(HttpServletRequest request) {
        String pageParam = request.getParameter(BoardInfo.PAGE);

        if (pageParam != null) {
            return updateCurPage(pageParam);
        }

        return getSessionCurPage(request);
    }

    private static Integer getSessionCurPage(HttpServletRequest request) {
        Object tempPageParam = request.getSession().getAttribute(BoardInfo.CUR_PAGE);

        if (tempPageParam != null) {
            return (Integer) tempPageParam;
        }

        return BoardInfo.DEFAULT_START_PAGE_NUM;
    }

    private static Integer updateCurPage(String pageParam) {
        if (isDigit(pageParam)) {
            return Integer.parseInt(pageParam);
        }

        return BoardInfo.DEFAULT_START_PAGE_NUM;
    }


    private static boolean isDigit(String pageParam) {
        char[] chars = pageParam.toCharArray();

        for (char c : chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    private static String getCategory(HttpSession session) {
        String tempCategory = (String) session.getAttribute(BoardInfo.SEARCH_CATEGORY);

        if (tempCategory != null && !tempCategory.equals(BoardInfo.ALL_CATEGORY_VALUE)) {
            return tempCategory;
        }

        return BoardInfo.ALL_CATEGORY_VALUE;
    }

    private static String getSearchText(HttpSession session) {
        String tempSearchText = (String) session.getAttribute(BoardInfo.SEARCH_TEXT);

        if (tempSearchText != null && !tempSearchText.isEmpty()) {
            return tempSearchText;
        }

        return BoardInfo.EMPTY_STRING;
    }

    private static String getCategory(HttpServletRequest request) {
        String tempCategory = request.getParameter(BoardInfo.SEARCH_CATEGORY);

        if (tempCategory != null && !tempCategory.equals(BoardInfo.ALL_CATEGORY_VALUE)) {
            return tempCategory;
        }

        return BoardInfo.ALL_CATEGORY_VALUE;
    }

    private static String getSearchText(HttpServletRequest request) {
        String tempSearchText = request.getParameter(BoardInfo.SEARCH_TEXT);

        if (tempSearchText != null && !tempSearchText.isEmpty()) {
            return tempSearchText;
        }

        return BoardInfo.EMPTY_STRING;
    }

    private static void updateSessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();

        session.setAttribute(BoardInfo.REG_START_DATE, request.getParameter(BoardInfo.REG_START_DATE));
        session.setAttribute(BoardInfo.REG_END_DATE, request.getParameter(BoardInfo.REG_END_DATE));
        session.setAttribute(BoardInfo.SEARCH_CATEGORY, request.getParameter(BoardInfo.SEARCH_CATEGORY));
        session.setAttribute(BoardInfo.SEARCH_TEXT, request.getParameter(BoardInfo.SEARCH_TEXT));
        session.setAttribute(BoardInfo.CUR_PAGE, BoardInfo.DEFAULT_START_PAGE_NUM);
    }

    private static boolean hasSearchCondition(HttpServletRequest request) {
        return request.getParameter(BoardInfo.REG_START_DATE) != null &&
                request.getParameter(BoardInfo.REG_END_DATE) != null &&
                request.getParameter(BoardInfo.SEARCH_CATEGORY) != null &&
                request.getParameter(BoardInfo.SEARCH_TEXT) != null;
    }
}
