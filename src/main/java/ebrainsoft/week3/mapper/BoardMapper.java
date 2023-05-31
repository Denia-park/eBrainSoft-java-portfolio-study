package ebrainsoft.week3.mapper;

import ebrainsoft.model.Board;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
    List<Board> findAll(Map<String, Object> map);

    int countAll(Map<String, Object> map);
}
