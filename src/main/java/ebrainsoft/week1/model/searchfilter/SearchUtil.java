package ebrainsoft.week1.model.searchfilter;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;

@Getter
public class SearchUtil {
    private static final String REG_START_DATE = "reg_start_date";
    private static final String REG_END_DATE = "reg_end_date";
    private static final String CATEGORY = "category";
    private static final String SEARCH_TEXT = "searchText";
    private static final String PAGE = "page";
    private static final String CUR_PAGE = "curPage";

    private static final String ALL_CATEGORY_VALUE = "all";
    private static final String EMPTY_STRING = "";

    private static final int DEFAULT_YEAR_GAP = 1;
    private static final int DEFAULT_START_PAGE_NUM = 1;

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

        String startDay = (String) session.getAttribute(REG_START_DATE);
        String endDay = (String) session.getAttribute(REG_END_DATE);
        String category = getCategory(session);
        String searchText = getSearchText(session);
        Integer needPageNum = getNeedPageNum(request);

        //한번도 검색을 안 누른 경우 -> 새로 조회, 현재 날짜로 조회
        if (hasNotFilterConditionInSession(session)) {
            startDay = String.valueOf(LocalDate.now().minusYears(DEFAULT_YEAR_GAP));
            endDay = String.valueOf(LocalDate.now());
        }

        return new FilterCondition(startDay, endDay, category, searchText, needPageNum);
    }

    private static boolean hasNotFilterConditionInSession(HttpSession session) {
        return session.getAttribute(REG_START_DATE) == null ||
                session.getAttribute(REG_END_DATE) == null ||
                session.getAttribute(CATEGORY) == null ||
                session.getAttribute(SEARCH_TEXT) == null;
    }

    private static FilterCondition updateFilterAsSearchCondition(HttpServletRequest request) {
        String startDayFilter = request.getParameter(REG_START_DATE);
        String endDayFilter = request.getParameter(REG_END_DATE);
        String categoryFilter = getCategory(request);
        String searchTextFilter = getSearchText(request);
        Integer needPageNum = getNeedPageNum(request);

        updateSessionInfo(request);

        return new FilterCondition(startDayFilter, endDayFilter, categoryFilter, searchTextFilter, needPageNum);
    }

    private static Integer getNeedPageNum(HttpServletRequest request) {
        String pageParam = request.getParameter(PAGE);

        if (pageParam != null) {
            return updateCurPage(pageParam);
        }

        return getSessionCurPage(request);
    }

    private static Integer getSessionCurPage(HttpServletRequest request) {
        Object tempPageParam = request.getSession().getAttribute(CUR_PAGE);

        if (tempPageParam != null) {
            return (Integer) tempPageParam;
        }

        return DEFAULT_START_PAGE_NUM;
    }

    private static Integer updateCurPage(String pageParam) {
        if (isDigit(pageParam)) {
            return Integer.parseInt(pageParam);
        }

        return DEFAULT_START_PAGE_NUM;
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
        String tempCategory = (String) session.getAttribute(CATEGORY);

        if (tempCategory != null && !tempCategory.equals(ALL_CATEGORY_VALUE)) {
            return tempCategory;
        }

        return ALL_CATEGORY_VALUE;
    }

    private static String getSearchText(HttpSession session) {
        String tempSearchText = (String) session.getAttribute(SEARCH_TEXT);

        if (tempSearchText != null && !tempSearchText.isEmpty()) {
            return tempSearchText;
        }

        return EMPTY_STRING;
    }

    private static String getCategory(HttpServletRequest request) {
        String tempCategory = request.getParameter(CATEGORY);

        if (tempCategory != null && !tempCategory.equals(ALL_CATEGORY_VALUE)) {
            return tempCategory;
        }

        return ALL_CATEGORY_VALUE;
    }

    private static String getSearchText(HttpServletRequest request) {
        String tempSearchText = request.getParameter(SEARCH_TEXT);

        if (tempSearchText != null && !tempSearchText.isEmpty()) {
            return tempSearchText;
        }

        return EMPTY_STRING;
    }

    private static void updateSessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();

        session.setAttribute(REG_START_DATE, request.getParameter(REG_START_DATE));
        session.setAttribute(REG_END_DATE, request.getParameter(REG_END_DATE));
        session.setAttribute(CATEGORY, request.getParameter(CATEGORY));
        session.setAttribute(SEARCH_TEXT, request.getParameter(SEARCH_TEXT));
        session.setAttribute(CUR_PAGE, DEFAULT_START_PAGE_NUM);
    }

    private static boolean hasSearchCondition(HttpServletRequest request) {
        return request.getParameter(REG_START_DATE) != null &&
                request.getParameter(REG_END_DATE) != null &&
                request.getParameter(CATEGORY) != null &&
                request.getParameter(SEARCH_TEXT) != null;
    }
}
