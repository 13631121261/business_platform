package com.kunlun.firmwaresystem.mappers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kunlun.firmwaresystem.entity.Company;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CompanyMapper extends BaseMapper<Company> {

}
