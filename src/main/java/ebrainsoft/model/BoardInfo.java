package ebrainsoft.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BoardInfo {
    public static final int PAGE_SIZE_LIMIT = 10;
    public static final int PAGE_NUM_WIDTH_LIMIT = 5;
    public static final String REG_START_DATE = "reg_start_date";
    public static final String REG_END_DATE = "reg_end_date";
    public static final String SEARCH_CATEGORY = "searchCategory";
    public static final String SEARCH_TEXT = "searchText";
    public static final String PAGE = "page";
    public static final String CUR_PAGE = "curPage";
    public static final String CUSTOM_DATE_FORMAT = "yyyy.MM.dd HH:mm";

    public static final String ALL_CATEGORY_VALUE = "all";
    public static final String EMPTY_STRING = "";

    public static final int DEFAULT_YEAR_GAP = 1;
    public static final int DEFAULT_START_PAGE_NUM = 1;

    private final int totalCount;
    private final int totalPage;
    private final int needPageNum;
    private final List<Board> boardList;

    public int getNeedPageNum() {
        return needPageNum;
    }

    public int getPrevPage() {
        return this.needPageNum == DEFAULT_START_PAGE_NUM ? DEFAULT_START_PAGE_NUM : this.needPageNum - 1;
    }

    public int getNextPage() {
        return this.needPageNum == this.totalPage ? this.totalPage : this.needPageNum + 1;
    }

    public int getPageLimitStart() {
        return 1 + (((this.needPageNum - 1) / PAGE_NUM_WIDTH_LIMIT) * PAGE_NUM_WIDTH_LIMIT);
    }

    public int getPageLimitEnd(int pageLimitStart) {
        return Math.min(pageLimitStart + (PAGE_NUM_WIDTH_LIMIT - DEFAULT_START_PAGE_NUM), this.totalPage);
    }
}
