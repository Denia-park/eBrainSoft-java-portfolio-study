package ebrainsoft.week1.model.searchfilter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FilterCondition {
    private final String startDayFilter;
    private final String endDayFilter;
    private final String categoryFilter;
    private final String searchTextFilter;
    private final Integer curPage;
}
