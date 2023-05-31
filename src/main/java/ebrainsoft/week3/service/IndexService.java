package ebrainsoft.week3.service;

import ebrainsoft.model.*;
import ebrainsoft.week3.mapper.BoardMapper;
import ebrainsoft.week3.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexService {
    private final CategoryMapper categoryMapper;
    private final BoardMapper boardMapper;

    public IndexServiceInfo getIndexInfo(FilterCondition fc) {
        List<Category> categories = categoryMapper.findAll();
        List<String> categoryList = categories.stream().map(Category::getName).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        updateMap(map, fc);

        int searchedTotalCount = boardMapper.countAll(map);
        List<Board> boardList = boardMapper.findAll(map);

        int searchedTotalPage = getTotalPage(searchedTotalCount);
        BoardInfo boardInfo = new BoardInfo(searchedTotalCount, searchedTotalPage, fc.getNeedPageNum(), boardList);

        return new IndexServiceInfo(boardInfo, categoryList);
    }

    private void updateMap(Map<String, Object> map, FilterCondition fc) {
        map.put("startDayFilter", fc.getFilterStartTime());
        map.put("endDayFilter", fc.getFilterEndTime());
        map.put("categoryFilter", fc.getCategoryFilter());
        map.put("searchTextFilter", fc.getSearchTextFilter());
        map.put("pageSizeLimit", BoardInfo.PAGE_SIZE_LIMIT);
        map.put("pageOffset", (fc.getNeedPageNum() - 1) * BoardInfo.PAGE_SIZE_LIMIT);
    }

    private int getTotalPage(int totalCount) {
        return Math.max(1, (int) Math.ceil((double) totalCount / BoardInfo.PAGE_SIZE_LIMIT));
    }
}
