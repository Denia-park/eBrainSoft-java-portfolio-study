package ebrainsoft.week2.service;

import ebrainsoft.model.Board;
import ebrainsoft.model.BoardInfo;
import ebrainsoft.model.FilterCondition;
import ebrainsoft.model.SearchedBoard;
import ebrainsoft.util.SearchUtil;
import ebrainsoft.week2.repository.BoardRepository;
import ebrainsoft.week2.repository.CategoryRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class IndexService implements Service {
    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) {
        FilterCondition fc = SearchUtil.updateFilterConditionIfSearchConditionExist(request);

        try {
            List<String> categoryList = CategoryRepository.findAllCategory();

            SearchedBoard searchedBoard = BoardRepository.findAllBoardWithPageNum(fc, BoardInfo.PAGE_SIZE_LIMIT);
            int totalCount = searchedBoard.getTotalSearchedBoardNum();
            int totalPage = searchedBoard.getTotalSearchedBoardPageNum();
            int needPageNum = fc.getNeedPageNumComparedWithTotalPage(totalPage);
            List<Board> boardList = searchedBoard.getSearchedBoardList();

            BoardInfo boardInfo = new BoardInfo(totalCount, totalPage, needPageNum, boardList);

            request.getSession().setAttribute("curPage", boardInfo.getNeedPageNum());

            request.setAttribute("categoryList", categoryList);
            request.setAttribute("fc", fc);
            request.setAttribute("boardList", boardInfo.getBoardList());
            request.setAttribute("totalCount", boardInfo.getTotalCount());

            request.setAttribute("curPage", boardInfo.getNeedPageNum());
            request.setAttribute("totalPage", boardInfo.getTotalPage());
            request.setAttribute("prevPage", boardInfo.getPrevPage());
            request.setAttribute("nextPage", boardInfo.getNextPage());

            request.setAttribute("pageLimitStart", boardInfo.getPageLimitStart());
            request.setAttribute("pageLimitEnd", boardInfo.getPageLimitEnd(boardInfo.getPageLimitStart()));

            request.getRequestDispatcher("/WEB-INF/week2/index.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
