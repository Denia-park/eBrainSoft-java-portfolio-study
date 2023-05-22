package ebrainsoft.week2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class Comment {
    Long replyId;
    Long boardId;
    String regDate;
    String content;
}
