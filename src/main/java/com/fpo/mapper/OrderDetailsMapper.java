package com.fpo.mapper;

import com.fpo.model.OrderDetails;
import java.util.List;

public interface OrderDetailsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderDetails record);

    OrderDetails selectByPrimaryKey(Long id);

    List<OrderDetails> selectAll();

    int updateByPrimaryKey(OrderDetails record);

    void deleteByHeaderId(Long id);
}