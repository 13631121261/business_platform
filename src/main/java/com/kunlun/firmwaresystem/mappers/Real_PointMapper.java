package com.kunlun.firmwaresystem.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Real_Point;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface Real_PointMapper extends BaseMapper<Real_Point> {

}
