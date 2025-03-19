package com.kunlun.firmwaresystem.sql;

import com.kunlun.firmwaresystem.entity.device.DeviceModel;
import com.kunlun.firmwaresystem.mappers.DeviceModelMapper;
import com.kunlun.firmwaresystem.util.RedisUtils;

import java.util.HashMap;
import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class DeviceModel_sql {

    public List<DeviceModel> getAllModel(RedisUtils redisUtil, DeviceModelMapper deviceModelMapper) {
        if (deviceModelMapper == null) {
            myPrintln("空值");
        }
        List<DeviceModel> deviceModelList = deviceModelMapper.selectList(null);

        for (DeviceModel deviceModel : deviceModelList) {
            HashMap hashMap = new HashMap<String, String>();
            String[] server_char_uuid = deviceModel.getUuids().split("\\*");
            String[] uuid_handles = deviceModel.getHandles().split("\\*");
            for (int i = 0; i < server_char_uuid.length; i++) {
                String[] uuids = server_char_uuid[i].split("\\$");
                String[] handles = uuid_handles[i].split("#");
                myPrintln("ssdd=");
                for (int j = 1; j < uuids.length; j++) {
                    hashMap.put(uuids[j], handles[j - 1]);
                    myPrintln(uuids[j]);
                }

            }
            deviceModel.setUuids_handles(hashMap);
        }

        redisUtil.set("ModelList", deviceModelList);
        List<DeviceModel> dd = (List<DeviceModel>) redisUtil.get("ModelList");
        for (DeviceModel d : dd) {
            myPrintln("初始化加载=" + d.getName() + "===uuid=" + d.getUuids_handles().get("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"));
        }
        return deviceModelList;

    }

}
