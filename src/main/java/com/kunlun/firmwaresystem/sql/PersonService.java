package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.entity.Person;

public interface PersonService extends IService<Person> {
    // 您可以在这里定义自定义方法
    // 但 saveOrUpdateBatch 方法已经由 IService 提供了！
}
