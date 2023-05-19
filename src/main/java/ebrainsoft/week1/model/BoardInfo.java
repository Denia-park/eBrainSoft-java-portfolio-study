package ebrainsoft.week1.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BoardInfo {
    private final int totalCount;
    private final int totalPage;
    private final int needPageNum;
    private final List<Board> boardList;
}
