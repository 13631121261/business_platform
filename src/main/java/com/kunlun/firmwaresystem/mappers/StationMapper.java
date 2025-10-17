package com.kunlun.firmwaresystem.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.kunlun.firmwaresystem.entity.Station;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface StationMapper extends BaseMapper<Station> {
    int saveOrUpdateBatch(@Param("list") List<Station> stationList);
}
