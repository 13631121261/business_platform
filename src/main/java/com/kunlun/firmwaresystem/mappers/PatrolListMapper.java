package com.kunlun.firmwaresystem.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.firmwaresystem.entity.Patrol;
import com.kunlun.firmwaresystem.entity.Patrol_list;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface PatrolListMapper extends BaseMapper<Patrol_list> {

}
