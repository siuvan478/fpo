package com.fpo.mapper;

import com.fpo.model.Attachment;

import java.util.List;
import java.util.Map;

public interface AttachmentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Attachment record);

    Attachment selectByPrimaryKey(Long id);

    List<Attachment> selectAll();

    int updateByPrimaryKey(Attachment record);

    int updateBizIdByCondition(Map<String, Object> map);

    List<Attachment> selectListByBizIdAndType(Map<String, Object> map);
}