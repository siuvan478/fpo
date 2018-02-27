package com.fpo.mapper;

import com.fpo.model.OrderHeader;
import java.util.List;

public interface OrderHeaderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderHeader record);

    OrderHeader selectByPrimaryKey(Long id);

    List<OrderHeader> selectAll();

    int updateByPrimaryKey(OrderHeader record);
}