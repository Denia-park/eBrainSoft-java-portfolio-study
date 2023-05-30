package ebrainsoft.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class Comment {
    Long replyId;
    Long boardId;
    String regDatetime;
    String content;
}
