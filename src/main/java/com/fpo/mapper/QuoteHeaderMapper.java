package com.fpo.mapper;

import com.fpo.model.QuoteHeader;
import java.util.List;

public interface QuoteHeaderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(QuoteHeader record);

    QuoteHeader selectByPrimaryKey(Long id);

    List<QuoteHeader> selectAll();

    int updateByPrimaryKey(QuoteHeader record);
}