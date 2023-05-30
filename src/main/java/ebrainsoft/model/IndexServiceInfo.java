package ebrainsoft.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class IndexServiceInfo {
    BoardInfo boardInfo;
    List<String> categoryList;
}
