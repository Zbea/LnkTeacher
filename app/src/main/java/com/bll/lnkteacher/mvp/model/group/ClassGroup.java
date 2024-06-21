package com.bll.lnkteacher.mvp.model.group;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ClassGroup implements Serializable {

    public int classId;
    public String name;//班群名
    public int studentCount;//人数
    public int grade;
    public long userId;
    public int state;//1主群 2 子群
    public int classGroupId;//班级id
    public String imageUrl;//课程表
    public String teacher;
    public boolean isCheck;
    public List<ClassGroupUser> students;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null)
            return false;
        if (!(obj instanceof ClassGroup))
            return false;
        if (this==obj)
            return true;
        ClassGroup item=(ClassGroup) obj;
        return this.classGroupId==item.classGroupId&& this.classId==item.classId && Objects.equals(this.name, item.name)&&this.grade==item.grade;
    }

}
