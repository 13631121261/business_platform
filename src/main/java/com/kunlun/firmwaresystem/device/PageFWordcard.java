package com.kunlun.firmwaresystem.device;

import com.kunlun.firmwaresystem.entity.FWordcard;
import com.kunlun.firmwaresystem.entity.Logs;

import java.util.List;

public class PageFWordcard {
    List<FWordcard> fWordcardList;
    long page;
    long total;

    public PageFWordcard(List<FWordcard> fWordcardList,
                         long page,
                         long total) {
        this.fWordcardList = fWordcardList;
        this.page = page;
        this.total = total;
    }


    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setfWordcardList(List<FWordcard> fWordcardList) {
        this.fWordcardList = fWordcardList;
    }

    public List<FWordcard> getfWordcardList() {
        return fWordcardList;
    }
}