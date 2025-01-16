package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageStation;
import com.kunlun.firmwaresystem.entity.Station;
import com.kunlun.firmwaresystem.entity.web_Structure.StationTree;
import com.kunlun.firmwaresystem.mappers.StationMapper;
import com.kunlun.firmwaresystem.util.RedisUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kunlun.firmwaresystem.NewSystemApplication.redisUtil;
import static com.kunlun.firmwaresystem.gatewayJson.Constant.*;

public class Station_sql {
    public boolean addStation(StationMapper StationMapper, Station Station) {
        if (!checkStation(StationMapper, Station)) {
            System.out.println("输出="+Station.toString());
            try {
                int d = StationMapper.insert(Station);
                System.out.println(d);
            }catch (Exception e){
                System.out.println("异常="+e.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }

    public int updateStation(StationMapper StationMapper, Station Station) {
     //   System.out.println("更新网关1="+Station.toString());
        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        updateWrapper.eq("address", Station.getAddress());
        return StationMapper.update(Station, updateWrapper);
    }
    public int updateStation(StationMapper StationMapper,int id,int isyn) {
        System.out.println("更新网关2=");
        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        updateWrapper.set("isyn", isyn);
        updateWrapper.eq("id", id);
        return StationMapper.update(null,updateWrapper);
    }
    public int updateStation(StationMapper StationMapper,int id,String config_key,String config_name) {
        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        updateWrapper.eq("id", id);
        updateWrapper.set("config_key", config_key);
        updateWrapper.set("config_name", config_name);
        return StationMapper.update(null,updateWrapper);
    }
    public int delete(StationMapper StationMapper, String address) {

        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        updateWrapper.eq("address", address);
        return StationMapper.delete(updateWrapper);
    }
    public int deletes(StationMapper StationMapper, List<Integer> id) {
        return StationMapper.deleteBatchIds(id);
    }
    public int updateStation(StationMapper StationMapper, String address, String name, String wifiAddress, String projectKey, String config_name, double x, double y) {
        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        updateWrapper.eq("address", address);
        updateWrapper.set("config_key", projectKey);
        updateWrapper.set("name", name);
        updateWrapper.set("wifi_address", wifiAddress);
        updateWrapper.set("project_name", config_name);
        updateWrapper.set("x", x);
        updateWrapper.set("y", y);
        Station Station = (Station) redisUtil.get(redis_key_Station + address);

        redisUtil.set(redis_key_Station + address, Station);
        return StationMapper.update(null, updateWrapper);
    }

    public int updateStation(StationMapper StationMapper, String address, String projectKey) {
        UpdateWrapper updateWrapper = new UpdateWrapper();//照搬
        updateWrapper.eq("address", address);
        updateWrapper.set("project_key", projectKey);
        Station Station = (Station) redisUtil.get(redis_key_Station + address);
        redisUtil.set(redis_key_Station + address, Station);
        return StationMapper.update(null, updateWrapper);
    }

    /**
     * 查询是否已c存在此网关设备
     */
    private boolean checkStation(StationMapper StationMapper, Station Station) {
        QueryWrapper<Station> queryWrapper = Wrappers.query();
        queryWrapper.eq("address", Station.getAddress());
        // queryWrapper.eq("username", user.getCustomername());
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        List<Station> a = StationMapper.selectList(queryWrapper);
        if (a != null && a.size() > 0) {
            return true;
        } else
            return false;
    }

    public Station getStationByMac(StationMapper StationMapper, String address) {
        QueryWrapper<Station> queryWrapper = Wrappers.query();
        queryWrapper.eq("address", address);
        // queryWrapper.eq("username", user.getCustomername());
//若是数据库中符合传入的条件的记录有多条，那就不能用这个方法，会报错
        List<Station> a = StationMapper.selectList(queryWrapper);
        if (a != null && a.size() == 1) {
            return a.get(0);
        } else
            return null;
    }
    public Station getStationById(StationMapper StationMapper, int id) {
       Station Station= StationMapper.selectById(id);
       return  Station;
    }

    public Map<String, String> getAllStation(RedisUtils redisUtil, StationMapper StationMapper) {
        System.out.println("执行一次获取全部数据");
        List<Station> StationList = StationMapper.selectList(null);
        HashMap<String, String> StationMap = new HashMap<>();
        for (Station Station : StationList) {
            //System.out.println("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            redisUtil.set(redis_key_Station + Station.getAddress(), Station);
            StationMap.put(Station.getAddress(), Station.getAddress());
            redisUtil.set(redis_key_Station_onLine_time + Station.getAddress(), null);
            redisUtil.set(redis_key_Station_revice_count + Station.getAddress(), 0);
        }
        return StationMap;
    }


    public Map<String, String> updateStation(RedisUtils redisUtil, StationMapper StationMapper,String config_key) {
        System.out.println("更新一次取全部数据");
        QueryWrapper<Station> queryWrapper = Wrappers.query();
        queryWrapper.eq("config_key",config_key);
        List<Station> StationList = StationMapper.selectList(queryWrapper);
        HashMap<String, String> StationMap = new HashMap<>();
        for (Station Station : StationList) {
            updateStation(StationMapper,Station);
            redisUtil.set(redis_key_Station + Station.getAddress(), Station);
        }
        return StationMap;
    }

    public Map<String, Station> getAllStation( StationMapper StationMapper) {
        System.out.println("执行11一次获取全部数据");
        List<Station> StationList = StationMapper.selectList(null);
        HashMap<String, Station> StationMap = new HashMap<>();
        for (Station Station : StationList) {
            StationMap.put(Station.getAddress(), Station);
        }
        return StationMap;
    }
    public   List<StationTree> getAllStation(StationMapper StationMapper, String user_key, String project_key,String config_key) {
        System.out.println("执行333一次获取全部数据");
        QueryWrapper<Station> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key",user_key);
        queryWrapper.eq("project_key",project_key);
        queryWrapper.ne("config_key",config_key);
        List<Station> StationList = StationMapper.selectList(queryWrapper);
        HashMap<String, List<StationTree>> StationMap = new HashMap<>();
        for (Station Station : StationList) {
            StationTree StationTree=new StationTree();
            StationTree.setId(Station.getId());
            StationTree.setDisabled(false);

            StationTree.setAddress(Station.getAddress());

        }
        List<StationTree> only=new ArrayList<>();
        int i=-100;
        for(String key:StationMap.keySet()){
            List<StationTree> trees=StationMap.get(key);
            StationTree StationTree=new StationTree();
            StationTree.setLabel(key);
            StationTree.setId(i);
            StationTree.setChildren(trees);
            StationTree.setAddress("网关MAC");
            only.add(StationTree);
            i++;
        }
        return only;
    }

    public   List<Station> getAllStations(StationMapper StationMapper, String user_key, String project_key,String config_key) {
        System.out.println("执行44一次获取全部数据");
        QueryWrapper<Station> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key",user_key);
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("config_key",config_key);
        List<Station> StationList = StationMapper.selectList(queryWrapper);
        return StationList;
    }
    public   List<Station> getStationByMapKey(StationMapper StationMapper, String user_key, String project_key,String map_key) {
        QueryWrapper<Station> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key",user_key);
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("map_key",map_key);
        List<Station> StationList = StationMapper.selectList(queryWrapper);
        return StationList;
    }

   /* public   List<StationTree> getStationByconfig_key(StationMapper StationMapper, String user_key, String project_key,String config_key) {
        QueryWrapper<Station> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key",user_key);
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("config_key",config_key);
        List<Station> StationList = StationMapper.selectList(queryWrapper);
        HashMap<String, List<StationTree>> StationMap = new HashMap<>();
        for (Station Station : StationList) {
            StationTree StationTree=new StationTree();
            StationTree.setId(Station.getId());
            StationTree.setDisabled(false);
            StationTree.setAddress(Station.getAddress());

            if(StationList1==null){
                StationList1=new ArrayList<>();
                StationMap.put(Station.getConfig_name(),StationList1);
            }
            StationList1.add(StationTree);
        }
        List<StationTree> only=new ArrayList<>();
        int i=0;
        for(String key:StationMap.keySet()){
            List<StationTree> trees=StationMap.get(key);
            StationTree StationTree=new StationTree();
            StationTree.setLabel(key);
            StationTree.setId(i);
            StationTree.setChildren(trees);
            StationTree.setAddress("网关MAC");
            only.add(StationTree);
        }


        return only;
    }*/
    public PageStation selectPageStation(StationMapper StationMapper, int page, int limt, String quickSearch, String userKey,String project_key) {
        System.out.println("执行55一次获取全部数据"+page+"  "+limt);
        LambdaQueryWrapper<Station> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Station> userPage = new Page<>(page, limt);
        IPage<Station> userIPage;
      //  System.out.println("user_key="+userKey+" project_key="+project_key);
        userLambdaQueryWrapper.eq(Station::getUser_key, userKey).eq(Station::getProject_key,project_key).like(Station::getAddress, quickSearch).or().eq(Station::getUser_key, userKey).eq(Station::getProject_key,project_key);

       /* if (project_name != null && project_name.length() > 0) {
            StationConfig_sql project_sql = new StationConfig_sql();
            List<Station_config> StationConfigList = project_sql.getLikeProject(projectMapper, project_name, userKey);
            if (StationConfigList == null || StationConfigList.size() == 0) {
                PageStation pageStation = new PageStation(null, 0, 0);
                return pageStation;
            } else {
                for (int i = 0; i < StationConfigList.size(); i++) {
                    userLambdaQueryWrapper.eq(Station::getConfig_key, StationConfigList.get(i).getConfig_key());
                }
            }
        }*/
        userIPage=null;
        try {
            userIPage = StationMapper.selectPage(userPage, userLambdaQueryWrapper);
        }catch (Exception e){
            System.out.println("输出啊结果="+e.getMessage());
        }
        //    System.out.println("总页数： "+userIPage.getPages());
        System.out.println("总记录数： "+userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::println);
        return new PageStation(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
    }



}