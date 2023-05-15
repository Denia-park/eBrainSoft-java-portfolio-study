package ebrainsoft.week1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Reply {
    Long replyId;
    Long boardId;
    LocalDate regDate;
    String content;
}
