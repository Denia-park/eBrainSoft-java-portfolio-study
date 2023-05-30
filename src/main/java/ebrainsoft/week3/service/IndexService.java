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

    public IndexServiceInfo getIndexInfo(FilterCondition fc) throws Exception {
        List<Category> categories = categoryMapper.findAll();
        List<String> categoryList = categories.stream().map(Category::getName).collect(Collectors.toList());

        //TODO
        //페이징 전체 개수 세기

        int pageSizeLimit = BoardInfo.PAGE_SIZE_LIMIT;
        Map<String, Object> map = new HashMap<>();
        map.put("startDayFilter", fc.getFilterStartTime());
        map.put("endDayFilter", fc.getFilterEndTime());
        map.put("categoryFilter", fc.getCategoryFilter());
        map.put("searchTextFilter", fc.getSearchTextFilter());
        map.put("pageSizeLimit", pageSizeLimit);
        map.put("pageOffset", (fc.getNeedPageNum() - 1) * pageSizeLimit);

        List<Board> boardList = boardMapper.findAll(map);

        BoardInfo boardInfo = new BoardInfo(1, 1, 1, boardList);

//        SearchedBoard searchedBoard = BoardRepository.findAllBoardWithPageNum(fc, BoardInfo.PAGE_SIZE_LIMIT);
//
//        int totalCount = searchedBoard.getTotalSearchedBoardNum();
//        int totalPage = searchedBoard.getTotalSearchedBoardPageNum();
//        int needPageNum = fc.getNeedPageNumComparedWithTotalPage(totalPage);
//        List<Board> boardList = searchedBoard.getSearchedBoardList();
//
//        BoardInfo boardInfo = new BoardInfo(totalCount, totalPage, needPageNum, boardList);

        return new IndexServiceInfo(boardInfo, categoryList);
    }
}
