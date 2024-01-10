package com.kunlun.firmwaresystem.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Area;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface AlarmMapper extends BaseMapper<Alarm> {

}
