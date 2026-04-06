package day.happy365.dropx.mapper;

import day.happy365.dropx.model.Content;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ContentMapper {

    void insert(Content content);

    Content findByExtractCode(@Param("extractCode") String extractCode,
                              @Param("contentType") String contentType);

    int countByExtractCode(@Param("extractCode") String extractCode);

    void updateExpiredStatus();

    void markAsUsed(@Param("contentId") Integer contentId);
}
