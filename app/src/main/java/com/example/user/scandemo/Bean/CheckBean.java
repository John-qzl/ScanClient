package com.example.user.scandemo.Bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by qiaozhili on 2018/9/26 22:55.
 */

public class CheckBean extends DataSupport{
    private String title;
    private String department;
    private String name;
    private String checkID;
    private int ypd;
    private int pdzs;

    public int getYpd() {
        return ypd;
    }

    public void setYpd(int ypd) {
        this.ypd = ypd;
    }

    public int getPdzs() {
        return pdzs;
    }

    public void setPdzs(int pdzs) {
        this.pdzs = pdzs;
    }

    public String getCheckID() {
        return checkID;
    }

    public void setCheckID(String checkID) {
        this.checkID = checkID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
