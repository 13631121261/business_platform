package com.kunlun.firmwaresystem.sql;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageWifiVersion;
import com.kunlun.firmwaresystem.entity.Wifi_firmware;
import com.kunlun.firmwaresystem.mappers.WifiMapper;
import com.kunlun.firmwaresystem.util.MyBatisUtils;
import net.sf.json.JSONObject;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;
import static com.kunlun.firmwaresystem.util.constant.code_ok;
import static com.kunlun.firmwaresystem.util.constant.wifi_type;

public class Wifi {
    public Wifi_firmware getVersionByKey(WifiMapper wifiMapper, String userKey, String version) {
        QueryWrapper<Wifi_firmware> queryWrapper = Wrappers.query();
        queryWrapper.eq("version", version);
        queryWrapper.eq("user_key", userKey);
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        Wifi_firmware a = wifiMapper.selectOne(queryWrapper);
        return a;
    }

    //获取全部wifi固件信息
    public String getAllVersion() {
        SqlSession session = null;
        try {
            session = MyBatisUtils.openSession();
            List<Wifi_firmware> list = session.selectList("Wifi_firmware.selectAll");
            JSONObject json = new JSONObject();
            json.put("code", code_ok);
            json.put("type", wifi_type);
            json.put("length", list.size());
            json.put("data", list);
            return json.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(session);
        }
    }

    public String getVersionById(int id) {
        SqlSession session = null;
        try {
            session = MyBatisUtils.openSession();
            List<Wifi_firmware> list = session.selectList("Wifi_firmware.selectById", id);
            JSONObject json = new JSONObject();
            json.put("code", code_ok);
            json.put("type", wifi_type);
            json.put("length", list.size());
            json.put("data", list);
            return json.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            MyBatisUtils.closeSession(session);
        }
    }

    public Wifi_firmware getVersionOnAuto(WifiMapper wifiMapper) {
        QueryWrapper<Wifi_firmware> queryWrapper = Wrappers.query();
        queryWrapper.like("auto", 1);
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        Wifi_firmware a = wifiMapper.selectOne(queryWrapper);
        return a;
    }

    public List<Wifi_firmware> getCustomerVersion(WifiMapper wifiMapper, String userKey) {
        QueryWrapper<Wifi_firmware> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", userKey);
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        List<Wifi_firmware> a = wifiMapper.selectList(queryWrapper);
        return a;
    }

    public int insertWifi(Wifi_firmware wifi_firmware) {
        SqlSession session = null;
        try {
            session = MyBatisUtils.openSession();
            session.insert("Wifi_firmware.insert", wifi_firmware);
            session.commit();  //提交事务数据
            myPrintln(wifi_firmware.getId()+"");
        } catch (Exception e) {
            if (session != null) {
                session.rollback();  //如果出现异常，回滚事务
                myPrintln("异常=" + e.getMessage());
                //   return -1;
            } else {
                myPrintln("异常=" + e.getMessage());
                //  return -1;
            }

        } finally {
            if (session != null) {
                session.close();
                return wifi_firmware.getId();
            }
            return -1;
        }
    }

    public PageWifiVersion selectPageWifiVersion(WifiMapper wifiMapper, int page, int limt, String remake, String version, String userKey) {
        LambdaQueryWrapper<Wifi_firmware> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Wifi_firmware> userPage = new Page<>(page, limt);
        IPage<Wifi_firmware> userIPage;
        userLambdaQueryWrapper.eq(Wifi_firmware::getUser_key, userKey);
        if (remake != null) {
            userLambdaQueryWrapper.like(Wifi_firmware::getRemake, remake);
            userLambdaQueryWrapper.like(Wifi_firmware::getVersion, version);
        }
        userIPage = wifiMapper.selectPage(userPage, userLambdaQueryWrapper);
        myPrintln("总页数： " + userIPage.getPages());
        myPrintln("总记录数： " + userIPage.getTotal());
        //  userIPage.getRecords().forEach(System.out::myPrintln);
        PageWifiVersion pageWifiVersion = new PageWifiVersion(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageWifiVersion;
    }

    public int delete(WifiMapper wifiMapper, String userKey, String version) {
        QueryWrapper<Wifi_firmware> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", userKey);
        queryWrapper.eq("version", version);
        return wifiMapper.delete(queryWrapper);
    }
}
