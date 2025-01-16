package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageWordcarda;
import com.kunlun.firmwaresystem.entity.Wordcard_a;
import com.kunlun.firmwaresystem.mappers.WordCardaMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCarda_Sql {
    public boolean addWordCarda(WordCardaMapper wordCardaMapper, Wordcard_a wordCard_a) {
        boolean status = check(wordCardaMapper, wordCard_a);
        if (status) {
            return false;
        } else {
            wordCardaMapper.insert(wordCard_a);
            return true;
        }
    }

    public Map<String, Wordcard_a> getAllWordCarda(WordCardaMapper wordCardaMapper) {
        List<Wordcard_a> wordcard_aList = wordCardaMapper.selectList(null);
        HashMap<String, Wordcard_a> wordCardAHashMap = new HashMap<>();
        for (Wordcard_a wordCard_a : wordcard_aList) {
            //System.out.println("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            wordCardAHashMap.put(wordCard_a.getMac(), wordCard_a);
        }
        return wordCardAHashMap;
    }

    //查询未绑定的工卡
    public List<Wordcard_a> getAllWordCarda(WordCardaMapper wordCardaMapper, String project_key, String user_key) {
        QueryWrapper<Wordcard_a> userLambdaQueryWrapper = Wrappers.query();
        userLambdaQueryWrapper.eq("isbind",0);
        userLambdaQueryWrapper.eq("isbind",0);
        userLambdaQueryWrapper.eq("user_key",user_key);
        List<Wordcard_a> wordcard_aList = wordCardaMapper.selectList(userLambdaQueryWrapper);
        return wordcard_aList;
    }

    public void delete(WordCardaMapper wordCardaMapper, String mac) {
        QueryWrapper<Wordcard_a> queryWrapper = Wrappers.query();
        queryWrapper.eq("mac", mac);
        wordCardaMapper.delete(queryWrapper);
    }

    public int update(WordCardaMapper wordCardaMapper, Wordcard_a wordCard_a) {

        return wordCardaMapper.updateById(wordCard_a);
    }

    public PageWordcarda selectPageWordcard(WordCardaMapper wordCardaMapper, int page, int limt, String mac, String userkey, String name) {
        LambdaQueryWrapper<Wordcard_a> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Wordcard_a> userPage = new Page<>(page, limt);
        IPage<Wordcard_a> userIPage;
        userLambdaQueryWrapper.eq(Wordcard_a::getCustomer_key, userkey);
        userLambdaQueryWrapper.like(Wordcard_a::getMac, mac);
        userIPage = wordCardaMapper.selectPage(userPage, userLambdaQueryWrapper);
        System.out.println("总页数： " + userIPage.getPages());
        System.out.println("总记录数： " + userIPage.getTotal());
        // userIPage.getRecords().forEach(System.out::println);
        PageWordcarda pageWordcarda = new PageWordcarda(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageWordcarda;
    }

    public boolean check(WordCardaMapper wordCardaMapper, Wordcard_a wordCard_a) {
        QueryWrapper<Wordcard_a> queryWrapper = Wrappers.query();
        queryWrapper.eq("mac", wordCard_a.getMac());
        Wordcard_a wordcard_a1 = wordCardaMapper.selectOne(queryWrapper);
        if (wordcard_a1 == null) {
            return false;
        } else {
            return true;
        }
    }
    public  List<Wordcard_a> getWordcard(WordCardaMapper wordCardaMapper, String mac, String userkey) {
        QueryWrapper<Wordcard_a> queryWrapper = Wrappers.query();
        queryWrapper.like("mac", mac);
        queryWrapper.eq("user_key",userkey);
        List<Wordcard_a> wordcard_as = wordCardaMapper.selectList(queryWrapper);
       return wordcard_as;
    }
}