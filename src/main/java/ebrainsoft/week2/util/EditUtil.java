package ebrainsoft.week2.util;

import com.oreilly.servlet.MultipartRequest;

import java.util.ArrayList;
import java.util.List;

public class EditUtil {
    public static List<String> getFileNames(MultipartRequest mr, String paramName) {
        String[] fileNames = mr.getParameterValues(paramName);

        if (fileNames == null) {
            return new ArrayList<>();
        }

        return List.of(fileNames);
    }

    public static boolean checkIsFileExist(List<String> existFileNames,
                                           List<String> deleteFileNames,
                                           List<String> addFileNameList) {
        return existFileNames.size() - deleteFileNames.size() + addFileNameList.size() > 0;
    }
}
