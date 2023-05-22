package ebrainsoft.model;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchedBoard {
    private final int totalSearchedBoardNum;
    private final int totalSearchedBoardPageNum;
    private final List<Board> searchedBoardList;

    public SearchedBoard(int totalSearchedBoardNum, int pageSizeLimit, List<Board> searchedBoardList) {
        this.totalSearchedBoardNum = totalSearchedBoardNum;
        this.totalSearchedBoardPageNum = getTotalPage(totalSearchedBoardNum, pageSizeLimit);
        this.searchedBoardList = searchedBoardList;
    }


    private int getTotalPage(int totalCount, int pageSizeLimit) {
        return Math.max(1, (int) Math.ceil((double) totalCount / pageSizeLimit));
    }
}
