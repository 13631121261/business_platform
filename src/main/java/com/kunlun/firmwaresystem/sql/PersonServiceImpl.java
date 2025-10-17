package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kunlun.firmwaresystem.entity.Person;
import com.kunlun.firmwaresystem.mappers.PersonMapper;
import org.springframework.stereotype.Service;

@Service // 别忘了加注解，让Spring管理
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements PersonService {
    // 无需任何代码，ServiceImpl 已经实现了 saveOrUpdateBatch 等方法
}
