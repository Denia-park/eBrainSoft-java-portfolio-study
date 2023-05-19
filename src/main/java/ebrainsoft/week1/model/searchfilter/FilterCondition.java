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
    private final Integer needPageNum;

    private final String START_TIME_OF_THE_DAY = "00:00:00";
    private final String END_TIME_OF_THE_DAY = "23:59:59";

    public String getFilterStartTime() {
        return startDayFilter + " " + START_TIME_OF_THE_DAY;
    }

    public String getFilterEndTime() {
        return endDayFilter + " " + END_TIME_OF_THE_DAY;
    }
}
