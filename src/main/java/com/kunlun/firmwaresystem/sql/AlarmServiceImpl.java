package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kunlun.firmwaresystem.entity.Alarm;
import com.kunlun.firmwaresystem.mappers.AlarmMapper;
import org.springframework.stereotype.Service;

@Service // 别忘了加注解，让Spring管理
public class AlarmServiceImpl extends ServiceImpl<AlarmMapper, Alarm> implements AlarmService {
    // 无需任何代码，ServiceImpl 已经实现了 saveOrUpdateBatch 等方法
}
