package com.bll.lnkteacher;


import android.content.Context;

import com.bll.lnkteacher.mvp.model.BaseTypeBean;
import com.bll.lnkteacher.mvp.model.ClassGroup;
import com.bll.lnkteacher.mvp.model.Group;
import com.bll.lnkteacher.mvp.model.MainListBean;
import com.bll.lnkteacher.mvp.model.MessageBean;
import com.bll.lnkteacher.mvp.model.ModuleBean;

import java.util.ArrayList;
import java.util.List;

public class DataBeanManager {

    private static DataBeanManager instance = null;

    public static DataBeanManager getInstance() {

        if (instance == null) {
            synchronized (DataBeanManager.class) {
                if (instance == null) {
                    instance = new DataBeanManager();
                }
            }
        }
        return instance;
    }

    private List<ClassGroup> classGroups = new ArrayList<>();
    private List<Group> schoolGroups = new ArrayList<>();
    private List<Group> areaGroups = new ArrayList<>();

    public int[] scoreList = {0, 60, 70, 80, 90, 100};

    private String[] listTitle = {
            "首页",
            "教义",
            "班群",
            "教学",
            "笔记",
            "书架",
            "应用"};

    public String[] teachingStrs = {
            "作业布置",
            "作业批改",
            "考卷发放",
            "考卷批改"};

    public String[] kmArray = {
            "语文",
            "数学",
            "英语",
            "物理",
            "化学",
            "地理",
            "政治",
            "历史",
            "生物",
    }; //科目的数据


    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    public ArrayList<MainListBean> getIndexData(Context context) {

        ArrayList<MainListBean> list = new ArrayList<>();

        MainListBean h0 = new MainListBean();
        h0.icon = context.getDrawable(R.mipmap.icon_main_sy);
        h0.icon_check = context.getDrawable(R.mipmap.icon_main_sy_check);
        h0.checked = true;
        h0.name = listTitle[0];

        MainListBean h1 = new MainListBean();
        h1.icon = context.getDrawable(R.mipmap.icon_main_jy);
        h1.icon_check = context.getDrawable(R.mipmap.icon_main_jy_check);
        h1.checked = false;
        h1.name = listTitle[1];

        MainListBean h2 = new MainListBean();
        h2.icon = context.getDrawable(R.mipmap.icon_main_group_nor);
        h2.icon_check = context.getDrawable(R.mipmap.icon_main_group_check);
        h2.checked = false;
        h2.name = listTitle[2];

        MainListBean h3 = new MainListBean();
        h3.icon = context.getDrawable(R.mipmap.icon_main_jx);
        h3.icon_check = context.getDrawable(R.mipmap.icon_main_jx_check);
        h3.checked = false;
        h3.name = listTitle[3];

        MainListBean h4 = new MainListBean();
        h4.icon = context.getDrawable(R.mipmap.icon_main_bj);
        h4.icon_check = context.getDrawable(R.mipmap.icon_main_bj_check);
        h4.checked = false;
        h4.name = listTitle[4];

        MainListBean h5 = new MainListBean();
        h5.icon = context.getDrawable(R.mipmap.icon_main_sj);
        h5.icon_check = context.getDrawable(R.mipmap.icon_main_sj_check);
        h5.checked = false;
        h5.name = listTitle[5];


        MainListBean h6 = new MainListBean();
        h6.icon = context.getDrawable(R.mipmap.icon_main_app);
        h6.icon_check = context.getDrawable(R.mipmap.icon_main_app_check);
        h6.checked = false;
        h6.name = listTitle[6];

        list.add(h0);
        list.add(h1);
        list.add(h2);
        list.add(h3);
        list.add(h4);
        list.add(h5);
        list.add(h6);

        return list;
    }

    public List<MessageBean> getMessage() {

        List<MessageBean> list = new ArrayList<>();
        List<ClassGroup> classGroups = new ArrayList<>();
        ClassGroup classGroup = new ClassGroup();
        classGroup.name = "三年一班";
        classGroups.add(classGroup);
        classGroups.add(classGroup);
        classGroups.add(classGroup);

        MessageBean messageBean = new MessageBean();
        messageBean.createTime = System.currentTimeMillis();
        messageBean.content = "写语文作业第7、8题";
        messageBean.classGroups = classGroups;

        list.add(messageBean);
        list.add(messageBean);
        list.add(messageBean);

        return list;
    }


    public List<BaseTypeBean> getNoteBook() {
        List<BaseTypeBean> list = new ArrayList<>();

        BaseTypeBean noteBook = new BaseTypeBean();
        noteBook.name = "我的日记";
        noteBook.typeId = 0;
        list.add(noteBook);

        BaseTypeBean noteBook1 = new BaseTypeBean();
        noteBook1.name = "金句彩段";
        noteBook1.typeId = 1;
        list.add(noteBook1);

        BaseTypeBean noteBook2 = new BaseTypeBean();
        noteBook2.name = "典型题型";
        noteBook2.typeId = 2;
        list.add(noteBook2);

        return list;

    }


    //教材分类
    public List<BaseTypeBean> getBookTypeJc() {
        List<BaseTypeBean> list = new ArrayList<>();

        BaseTypeBean baseTypeBean = new BaseTypeBean();
        baseTypeBean.typeId = 0;
        baseTypeBean.name = "我的课本";
        list.add(baseTypeBean);

        BaseTypeBean baseTypeBean1 = new BaseTypeBean();
        baseTypeBean1.typeId = 1;
        baseTypeBean1.name = "我的课辅";
        list.add(baseTypeBean1);

        BaseTypeBean baseTypeBean2 = new BaseTypeBean();
        baseTypeBean2.typeId = 2;
        baseTypeBean2.name = "参考教材";
        list.add(baseTypeBean2);

        BaseTypeBean baseTypeBean3 = new BaseTypeBean();
        baseTypeBean3.typeId = 3;
        baseTypeBean3.name = "我的讲义";
        list.add(baseTypeBean3);

        return list;
    }


    //封面
    public List<ModuleBean> getHomeworkCover() {
        List<ModuleBean> list = new ArrayList<>();
        ModuleBean moduleBean = new ModuleBean();
        moduleBean.resId = R.mipmap.icon_homework_cover_1;

        ModuleBean moduleBean1 = new ModuleBean();
        moduleBean1.resId = R.mipmap.icon_homework_cover_2;

        ModuleBean moduleBean2 = new ModuleBean();
        moduleBean2.resId = R.mipmap.icon_homework_cover_3;

        ModuleBean moduleBean3 = new ModuleBean();
        moduleBean3.resId = R.mipmap.icon_homework_cover_4;

        list.add(moduleBean);
        list.add(moduleBean1);
        list.add(moduleBean2);
        list.add(moduleBean3);
        return list;
    }


    /**
     * 获取班群列表
     * @return
     */
    public List<ClassGroup> getClassGroups() {
        return classGroups;
    }

    /**
     * 设置班群列表数据
     * @param list
     */
    public void setClassGroups(List<ClassGroup> list){
        classGroups=list;
    }

    /**
     * 获取校群列表
     * @return
     */
    public List<Group> getGroupsSchool() {
        return schoolGroups;
    }

    /**
     * 设置校群
     * @param list
     */
    public void setGroupsSchool(List<Group> list){
        schoolGroups=list;
    }

    /**
     * 获取际群列表
     * @return
     */
    public List<Group> getGroupsArea() {
        return areaGroups;
    }

    /**
     * 设置际群
     * @param list
     */
    public void setGroupsArea(List<Group> list){
        areaGroups=list;
    }

    //日记内容选择
    public List<ModuleBean> getNoteModuleDiary() {
        List<ModuleBean> list = new ArrayList<>();
        ModuleBean moduleBean = new ModuleBean();
        moduleBean.name = "横格本";
        moduleBean.resId = R.mipmap.icon_note_module_bg_1;
        moduleBean.resContentId = R.mipmap.icon_note_details_bg_6;

        ModuleBean moduleBean1 = new ModuleBean();
        moduleBean1.name = "方格本";
        moduleBean1.resId = R.mipmap.icon_note_module_bg_2;
        moduleBean1.resContentId = R.mipmap.icon_note_details_bg_7;

        list.add(moduleBean);
        list.add(moduleBean1);
        return list;
    }

    //笔记本内容选择
    public List<ModuleBean> getNoteModuleBook() {
        List<ModuleBean> list = new ArrayList<>();
        ModuleBean moduleBean = new ModuleBean();
        moduleBean.name = "空白本";
        moduleBean.resId = R.drawable.bg_gray_stroke_10dp_corner;
        moduleBean.resContentId = 0;

        ModuleBean moduleBean1 = new ModuleBean();
        moduleBean1.name = "横格本";
        moduleBean1.resId = R.mipmap.icon_note_module_bg_1;
        moduleBean1.resContentId = R.mipmap.icon_note_details_bg_1;

        ModuleBean moduleBean2 = new ModuleBean();
        moduleBean2.name = "方格本";
        moduleBean2.resId = R.mipmap.icon_note_module_bg_2;
        moduleBean2.resContentId = R.mipmap.icon_note_details_bg_2;

        ModuleBean moduleBean3 = new ModuleBean();
        moduleBean3.name = "英语本";
        moduleBean3.resId = R.mipmap.icon_note_module_bg_3;
        moduleBean3.resContentId = R.mipmap.icon_note_details_bg_3;

        ModuleBean moduleBean4 = new ModuleBean();
        moduleBean4.name = "田字本";
        moduleBean4.resId = R.mipmap.icon_note_module_bg_4;
        moduleBean4.resContentId = R.mipmap.icon_note_details_bg_4;

        ModuleBean moduleBean5 = new ModuleBean();
        moduleBean5.name = "五线谱";
        moduleBean5.resId = R.mipmap.icon_note_module_bg_5;
        moduleBean5.resContentId = R.mipmap.icon_note_details_bg_5;

        list.add(moduleBean);
        list.add(moduleBean1);
        list.add(moduleBean2);
        list.add(moduleBean3);
        list.add(moduleBean4);
        list.add(moduleBean5);
        return list;
    }

}
