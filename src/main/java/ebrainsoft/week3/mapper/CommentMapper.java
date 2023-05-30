package ebrainsoft.week3.mapper;

import ebrainsoft.model.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> findAll();
}
