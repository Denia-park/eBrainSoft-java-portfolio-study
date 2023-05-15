package ebrainsoft.week1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class File {
    Long fileId;
    Long boardId;
    String fileName;
    String fileRealName;
}
