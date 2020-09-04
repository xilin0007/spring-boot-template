package com.fxl.sbtemplate.orderservice.mapper;


import com.fxl.sbtemplate.orderservice.model.OrderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKey(OrderInfo record);

    List<OrderInfo> listOrderByMemb(@Param("memberId") String memberId);

    void insertBatch(List<OrderInfo> list);

    void updateBatch(List<OrderInfo> list);

    int updateByOrderNo(OrderInfo record);

    OrderInfo getOrderStatus(@Param("orderNo") String orderNo);

    int updateStatusBatch(List<OrderInfo> list);
}