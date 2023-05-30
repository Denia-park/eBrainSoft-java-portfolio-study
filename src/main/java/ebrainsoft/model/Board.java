package ebrainsoft.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class Board {
    Long boardId;
    String category;
    String regDatetime;
    String editDatetime;
    Integer views;
    String writer;
    String password;
    String title;
    String content;
    Boolean fileExist;
}
