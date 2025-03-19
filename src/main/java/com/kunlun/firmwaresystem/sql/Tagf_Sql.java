package com.kunlun.firmwaresystem.sql;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kunlun.firmwaresystem.entity.device.Tagf;
import com.kunlun.firmwaresystem.mappers.TagfMapper;
import java.util.List;
public class Tagf_Sql {
    public int add(TagfMapper TagfMapper, Tagf Tagf) {
        boolean status = check(TagfMapper, Tagf);
        if (status) {
            return -1;
        } else {TagfMapper.insert(Tagf);
            return  Tagf.getId();

        }

    }


    public void delete(TagfMapper TagfMapper, int id) {
        QueryWrapper<Tagf> queryWrapper = Wrappers.query();
        queryWrapper.eq("id", id);
        TagfMapper.delete(queryWrapper);
    }
    public int deletes(TagfMapper TagfMapper, List<Integer> id) {

      return TagfMapper.deleteBatchIds(id);
    }
    public int update(TagfMapper TagfMapper, Tagf Tagf) {

       return TagfMapper.updateById(Tagf);
    }



    public    List<Tagf> getAll(TagfMapper TagfMapper,String project_key,String type) {
        QueryWrapper<Tagf> queryWrapper = Wrappers.query();
        queryWrapper.eq("project_key",project_key);
        queryWrapper.eq("type",type);
        List<Tagf> Tagfs= TagfMapper.selectList(queryWrapper);
        return Tagfs;
    }

    public boolean check(TagfMapper TagfMapper, Tagf Tagf) {
        QueryWrapper<Tagf> queryWrapper = Wrappers.query();
        queryWrapper.eq("name", Tagf.getName());
        queryWrapper.eq("project_key", Tagf.getProject_key());
        Tagf Tagf1 = TagfMapper.selectOne(queryWrapper);
        if (Tagf1 == null) {
            return false;
        } else {
            return true;
        }
    }
}