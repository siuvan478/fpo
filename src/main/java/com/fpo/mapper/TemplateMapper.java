package com.fpo.mapper;

import com.fpo.model.Template;
import java.util.List;

public interface TemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Template record);

    Template selectByPrimaryKey(Long id);

    List<Template> selectAll();

    int updateByPrimaryKey(Template record);
}