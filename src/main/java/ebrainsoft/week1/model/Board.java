package ebrainsoft.week1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Board {
    Long boardId;
    String category;
    LocalDate regDate;
    LocalDate editDate;
    Integer views;
    String writer;
    String password;
    String title;
    String content;
}
