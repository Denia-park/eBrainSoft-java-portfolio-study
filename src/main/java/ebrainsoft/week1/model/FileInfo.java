package ebrainsoft.week1.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileInfo {
    private final String fileName;
    private final String fileRealName;
    private final String urlEncodedFileRealName;
}
