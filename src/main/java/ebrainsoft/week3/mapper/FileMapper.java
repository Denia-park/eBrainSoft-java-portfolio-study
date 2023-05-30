package ebrainsoft.week3.mapper;

import ebrainsoft.model.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    List<File> findAll();
}
