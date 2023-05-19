package ebrainsoft.week1.model.searchfilter;

import lombok.Getter;

@Getter
public class FilterCondition {
    private String startDayFilter;
    private String endDayFilter;
    private String categoryFilter;
    private String searchTextFilter;

    public FilterCondition(String startDayFilter, String endDayFilter, String categoryFilter, String searchTextFilter) {
        this.startDayFilter = startDayFilter;
        this.endDayFilter = endDayFilter;
        this.categoryFilter = categoryFilter;
        this.searchTextFilter = searchTextFilter;
    }
}
