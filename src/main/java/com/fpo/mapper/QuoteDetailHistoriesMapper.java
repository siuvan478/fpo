package com.fpo.mapper;

import com.fpo.model.QuoteDetailHistories;

import java.util.List;

public interface QuoteDetailHistoriesMapper {
    int deleteByPrimaryKey(Long id);

    int insert(QuoteDetailHistories record);

    QuoteDetailHistories selectByPrimaryKey(Long id);

    List<QuoteDetailHistories> selectAll();

    int updateByPrimaryKey(QuoteDetailHistories record);

    int batchInsert(List<QuoteDetailHistories> list);
}