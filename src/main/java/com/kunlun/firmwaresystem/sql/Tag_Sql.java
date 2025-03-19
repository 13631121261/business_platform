package com.kunlun.firmwaresystem.sql;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kunlun.firmwaresystem.device.PageTag;
import com.kunlun.firmwaresystem.entity.Tag;
import com.kunlun.firmwaresystem.mappers.TagMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag_Sql {
    public boolean addTag(TagMapper tagMapper, Tag tag) {
        boolean status = check(tagMapper, tag);
        if (status) {
            return false;
        } else {
            tagMapper.insert(tag);
            QueryWrapper<Tag> queryWrapper = Wrappers.query();
            queryWrapper.eq("mac", tag.getMac());
            Tag tag1 = tagMapper.selectOne(queryWrapper);
            //myPrintln("申请的ID="+ devicep1.getId());
            tag.setId(tag1.getId());
            return true;
        }
    }

    public boolean update(TagMapper tagMapper, Tag tag) {
        tagMapper.updateById(tag);
        return true;
    }

    public List<Tag> getTagByMac(TagMapper tagMapper, String user_key, String project_key, String mac) {
        QueryWrapper<Tag> queryWrapper = Wrappers.query();
        queryWrapper.eq("user_key", user_key);
        queryWrapper.eq("project_key", project_key);
        queryWrapper.eq("mac", mac);
        List<Tag> tags = tagMapper.selectList(queryWrapper);
        return tags;
    }


    public Map<String, Tag> getAllTag(TagMapper tagMapper) {
        LambdaQueryWrapper<Tag> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        List<Tag> tags = tagMapper.selectList(userLambdaQueryWrapper);
        HashMap<String, Tag> TagHashMap = new HashMap<>();
        for (Tag tag : tags) {
            //myPrintln("初始化"+Station.getSub_topic()+"==="+Station.getPub_topic());
            TagHashMap.put(tag.getMac(), tag);
        }
        return TagHashMap;
    }

    public List<Tag> getAllTag(TagMapper tagMapper, String userkey, String project_key) {
        LambdaQueryWrapper<Tag> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(Tag::getUser_key, userkey);
        userLambdaQueryWrapper.eq(Tag::getProject_key, project_key);
        List<Tag> tags = tagMapper.selectList(userLambdaQueryWrapper);
        return tags;
    }

    public List<Tag> getunAllTag(TagMapper tagMapper, String userkey, String project_key) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isbind",0);
        queryWrapper.eq("user_key",userkey);
     //   queryWrapper.eq("type",type);
        queryWrapper.eq("project_key",project_key);
        List<Tag> tagList = tagMapper.selectList(queryWrapper);
        return tagList;
    }
    public List<Tag> getAllTag(TagMapper tagMapper, String userkey, String project_key, String type) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_key",userkey);
        queryWrapper.eq("type",type);
        queryWrapper.eq("project_key",project_key);
        List<Tag> tagList = tagMapper.selectList(queryWrapper);
        return tagList;
    }
    public void delete(TagMapper tagMapper, String mac) {
        QueryWrapper<Tag> queryWrapper = Wrappers.query();
        queryWrapper.eq("mac", mac);
        tagMapper.delete(queryWrapper);
    }

    public PageTag selectPageTag(TagMapper tagMapper, int page, int limt, String like, String userkey, String project_key) {
        LambdaQueryWrapper<Tag> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Tag> userPage = new Page<>(page, limt);
        IPage<Tag> userIPage;
        userLambdaQueryWrapper.eq(Tag::getProject_key,project_key).eq(Tag::getUser_key, userkey).like(Tag::getMac, like);
        userIPage = tagMapper.selectPage(userPage, userLambdaQueryWrapper);

        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageTag pageTag = new PageTag(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageTag;
    }

    public PageTag selectPageTag_AOA(TagMapper tagMapper, int page, int limt, String like, String userkey, String project_key) {
        LambdaQueryWrapper<Tag> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Page<Tag> userPage = new Page<>(page, limt);
        IPage<Tag> userIPage;
        userLambdaQueryWrapper.eq(Tag::getProject_key,project_key).eq(Tag::getType,5).eq(Tag::getUser_key, userkey).like(Tag::getMac, like);
        userIPage = tagMapper.selectPage(userPage, userLambdaQueryWrapper);
        // userIPage.getRecords().forEach(System.out::myPrintln);
        PageTag pageTag = new PageTag(userIPage.getRecords(), userIPage.getPages(), userIPage.getTotal());
        return pageTag;
    }
    public int deletes(TagMapper tagMapper, List<Integer> id) {
        return tagMapper.deleteBatchIds(id);
    }
    public boolean check(TagMapper tagMapper, Tag tag) {
        QueryWrapper<Tag> queryWrapper = Wrappers.query();
        queryWrapper.eq("mac", tag.getMac());
        Tag tag1 = tagMapper.selectOne(queryWrapper);
        if (tag1 == null) {
            return false;
        } else {
            return true;
        }
    }
}