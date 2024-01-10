package com.kunlun.firmwaresystem.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.firmwaresystem.entity.Area;
import com.kunlun.firmwaresystem.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface DepartmentMapper extends BaseMapper<Department> {
}
