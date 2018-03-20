package com.fpo.mapper;

import com.fpo.model.QuoteDetails;

import java.math.BigDecimal;
import java.util.List;

public interface QuoteDetailsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(QuoteDetails record);

    QuoteDetails selectByPrimaryKey(Long id);

    List<QuoteDetails> selectAll();

    int updateByPrimaryKey(QuoteDetails record);

    void deleteByHeaderId(Long id);

    int batchInsert(List<QuoteDetails> list);

    BigDecimal getMinPriceGroup(Long orderId);
}