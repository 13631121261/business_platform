package com.kunlun.firmwaresystem.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.firmwaresystem.entity.Fence_group;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface FenceGroupMapper extends BaseMapper<Fence_group> {

}
