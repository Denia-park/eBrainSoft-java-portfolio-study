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
    private static final String CUR_PAGE = "curPage";

    private static final String ALL_CATEGORY_VALUE = "all";
    private static final String EMPTY_STRING = "";

    private static final int DEFAULT_YEAR_GAP = 1;
    private static final int DEFAULT_START_PAGE = 1;

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
        String searchText = searchText(session);

        //한번도 검색을 안 누른 경우 -> 새로 조회, 현재 날짜로 조회
        if (hasNotFilterConditionInSession(session)) {
            startDay = String.valueOf(LocalDate.now().minusYears(DEFAULT_YEAR_GAP));
            endDay = String.valueOf(LocalDate.now());
        }

        return new FilterCondition(startDay, endDay, category, searchText);
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
        String searchTextFilter = searchText(request);

        updateSessionInfo(request);

        return new FilterCondition(startDayFilter, endDayFilter, categoryFilter, searchTextFilter);
    }

    private static String getCategory(HttpSession session) {
        String tempCategory = (String) session.getAttribute(CATEGORY);

        if (tempCategory != null && !tempCategory.equals(ALL_CATEGORY_VALUE)) {
            return tempCategory;
        }

        return ALL_CATEGORY_VALUE;
    }

    private static String searchText(HttpSession session) {
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

    private static String searchText(HttpServletRequest request) {
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
        session.setAttribute(CUR_PAGE, DEFAULT_START_PAGE);
    }

    private static boolean hasSearchCondition(HttpServletRequest request) {
        return request.getParameter(REG_START_DATE) != null &&
                request.getParameter(REG_END_DATE) != null &&
                request.getParameter(CATEGORY) != null &&
                request.getParameter(SEARCH_TEXT) != null;
    }
}