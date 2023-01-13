package com.bll.lnkteacher;


import android.content.Context;

import com.bll.lnkteacher.mvp.model.AppBean;
import com.bll.lnkteacher.mvp.model.BaseTypeBean;
import com.bll.lnkteacher.mvp.model.ClassGroup;
import com.bll.lnkteacher.mvp.model.CourseBean;
import com.bll.lnkteacher.mvp.model.Group;
import com.bll.lnkteacher.mvp.model.MainListBean;
import com.bll.lnkteacher.mvp.model.MessageBean;
import com.bll.lnkteacher.mvp.model.ModuleBean;
import com.bll.lnkteacher.mvp.model.PopWindowBean;

import java.util.ArrayList;
import java.util.List;

public class DataBeanManager {

    private static DataBeanManager incetance = null;

    public static DataBeanManager getIncetance() {

        if (incetance == null) {
            synchronized (DataBeanManager.class) {
                if (incetance == null) {
                    incetance = new DataBeanManager();
                }
            }
        }
        return incetance;
    }
    private List<ClassGroup> classGroups = new ArrayList<>();
    private List<Group> schoolGroups = new ArrayList<>();
    private List<Group> areaGroups = new ArrayList<>();

    public int[] scoreList = {0, 60, 70, 80, 90, 100};

    private String[] listTitle = {
            "首页",
            "书架",
            "班群",
            "教学",
            "笔记",
            "应用"};

    public String[] bookStoreType = {
            "教材",
            "古籍",
            "自然科学",
            "社会科学",
            "思维科学",
            "运动才艺"};

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

    public String[] bookType = {
            "诗经楚辞", "唐诗宋词", "经典古文",
            "四大名著", "中国科技", "小说散文",
            "外国原著", "历史地理", "政治经济",
            "军事战略", "科学技术", "艺术才能",
            "运动健康", "连环漫画"
    }; //书籍分类

    public String[] ydcy = {
            "运动", "健康", "棋类",
            "乐器", "谱曲", "舞蹈",
            "素描", "绘画", "壁纸",
            "练字", "演讲", "漫画"
    }; //运动才艺

    public String[] ZRKX = {
            "地球天体", "物理化学", "生命生物"
    };//自然科学

    public String[] SWKX = {
            "人工智能", "模式识别", "心理生理", "语言文字", "数学"
    };//思维科学

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
        h1.icon = context.getDrawable(R.mipmap.icon_main_sj);
        h1.icon_check = context.getDrawable(R.mipmap.icon_main_sj_check);
        h1.checked = false;
        h1.name = listTitle[1];

        MainListBean h3 = new MainListBean();
        h3.icon = context.getDrawable(R.mipmap.icon_main_group_nor);
        h3.icon_check = context.getDrawable(R.mipmap.icon_main_group_check);
        h3.checked = false;
        h3.name = listTitle[2];

        MainListBean h4 = new MainListBean();
        h4.icon = context.getDrawable(R.mipmap.icon_main_jx);
        h4.icon_check = context.getDrawable(R.mipmap.icon_main_jx_check);
        h4.checked = false;
        h4.name = listTitle[3];

        MainListBean h5 = new MainListBean();
        h5.icon = context.getDrawable(R.mipmap.icon_main_bj);
        h5.icon_check = context.getDrawable(R.mipmap.icon_main_bj_check);
        h5.checked = false;
        h5.name = listTitle[4];


        MainListBean h6 = new MainListBean();
        h6.icon = context.getDrawable(R.mipmap.icon_main_app);
        h6.icon_check = context.getDrawable(R.mipmap.icon_main_app_check);
        h6.checked = false;
        h6.name = listTitle[5];

        list.add(h0);
        list.add(h1);
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

    /**
     * 科目列表
     *
     * @return
     */
    public List<CourseBean> getCourses() {

        List<CourseBean> list = new ArrayList();
        for (int i = 0; i < kmArray.length; i++) {
            CourseBean courseBean = new CourseBean();
            courseBean.name = kmArray[i];
            courseBean.courseId = i;
            list.add(courseBean);
        }


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

    //年级分类
    public List<PopWindowBean> getBookTypeGrade() {
        List<PopWindowBean> list = new ArrayList<>();

        PopWindowBean baseTypeBean = new PopWindowBean();
        baseTypeBean.id = 0;
        baseTypeBean.name = "小学低年级";
        baseTypeBean.isCheck = true;
        list.add(baseTypeBean);

        PopWindowBean baseTypeBean1 = new PopWindowBean();
        baseTypeBean1.id = 1;
        baseTypeBean1.name = "小学高年级";
        list.add(baseTypeBean1);

        PopWindowBean baseTypeBean2 = new PopWindowBean();
        baseTypeBean2.id = 2;
        baseTypeBean2.name = "初中学生";
        list.add(baseTypeBean2);

        PopWindowBean baseTypeBean3 = new PopWindowBean();
        baseTypeBean3.id = 3;
        baseTypeBean3.name = "高中学生";
        list.add(baseTypeBean3);


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
        baseTypeBean1.name = "参考课本";
        list.add(baseTypeBean1);

        BaseTypeBean baseTypeBean3 = new BaseTypeBean();
        baseTypeBean3.typeId = 2;
        baseTypeBean3.name = "课辅习题";
        list.add(baseTypeBean3);

        return list;
    }

    //古籍分类
    public List<BaseTypeBean> getBookTypeGj() {
        List<BaseTypeBean> list = new ArrayList<>();

        BaseTypeBean baseTypeBean = new BaseTypeBean();
        baseTypeBean.typeId = 0;
        baseTypeBean.name = "诗经楚辞";
        list.add(baseTypeBean);

        BaseTypeBean baseTypeBean1 = new BaseTypeBean();
        baseTypeBean1.typeId = 1;
        baseTypeBean1.name = "唐诗宋词";
        list.add(baseTypeBean1);

        BaseTypeBean baseTypeBean2 = new BaseTypeBean();
        baseTypeBean2.typeId = 2;
        baseTypeBean2.name = "古代经典";
        list.add(baseTypeBean2);

        BaseTypeBean baseTypeBean3 = new BaseTypeBean();
        baseTypeBean3.typeId = 3;
        baseTypeBean3.name = "四大名著";
        list.add(baseTypeBean3);

        BaseTypeBean baseTypeBean4 = new BaseTypeBean();
        baseTypeBean4.typeId = 4;
        baseTypeBean4.name = "中国科技";
        list.add(baseTypeBean4);

        return list;
    }

    //社会科学分类
    public List<BaseTypeBean> getBookTypeSHKX() {
        List<BaseTypeBean> list = new ArrayList<>();

        BaseTypeBean baseTypeBean = new BaseTypeBean();
        baseTypeBean.typeId = 5;
        baseTypeBean.name = "小说散文";
        list.add(baseTypeBean);

        BaseTypeBean baseTypeBean1 = new BaseTypeBean();
        baseTypeBean1.typeId = 6;
        baseTypeBean1.name = "外国原著";
        list.add(baseTypeBean1);

        BaseTypeBean baseTypeBean2 = new BaseTypeBean();
        baseTypeBean2.typeId = 7;
        baseTypeBean2.name = "历史地理";
        list.add(baseTypeBean2);

        BaseTypeBean baseTypeBean3 = new BaseTypeBean();
        baseTypeBean3.typeId = 8;
        baseTypeBean3.name = "政治经济";
        list.add(baseTypeBean3);

        BaseTypeBean baseTypeBean4 = new BaseTypeBean();
        baseTypeBean4.typeId = 9;
        baseTypeBean4.name = "军事战略";
        list.add(baseTypeBean4);

        return list;
    }

    //运动才艺
    public List<BaseTypeBean> getBookTypeYDCY() {
        List<BaseTypeBean> list = new ArrayList<>();

        for (int i = 0; i < ydcy.length; i++) {
            BaseTypeBean baseTypeBean = new BaseTypeBean();
            baseTypeBean.typeId = i == 0 || i == 1 ? 11 : (i == ydcy.length - 1 ? 13 : 12);
            baseTypeBean.name = ydcy[i];
            list.add(baseTypeBean);
        }

        return list;
    }

    //思维科学
    public List<BaseTypeBean> getBookTypeSWKX() {
        List<BaseTypeBean> list = new ArrayList<>();

        for (int i = 0; i < SWKX.length; i++) {
            BaseTypeBean baseTypeBean = new BaseTypeBean();
            baseTypeBean.typeId = 10;
            baseTypeBean.name = SWKX[i];
            list.add(baseTypeBean);
        }

        return list;
    }

    //自然科学
    public List<BaseTypeBean> getBookTypeZRKX() {
        List<BaseTypeBean> list = new ArrayList<>();

        for (int i = 0; i < ZRKX.length; i++) {
            BaseTypeBean baseTypeBean = new BaseTypeBean();
            baseTypeBean.typeId = 10;
            baseTypeBean.name = ZRKX[i];
            list.add(baseTypeBean);
        }

        return list;
    }


    public List<AppBean> getAppBaseList() {
        List<AppBean> apps = new ArrayList<>();
        AppBean appBean = new AppBean();
        appBean.appId = 0;
        appBean.appName = "应用市场";
        appBean.image = MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_center);
        appBean.isBase = true;
        apps.add(appBean);

        AppBean appBean1 = new AppBean();
        appBean1.appId = 1;
        appBean1.appName = "操机技巧";
        appBean1.image = MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_cz);
        appBean1.isBase = true;
        apps.add(appBean1);

        AppBean appBean2 = new AppBean();
        appBean2.appId = 2;
        appBean2.appName = "官方壁纸";
        appBean2.image = MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_wallpaper);
        appBean2.isBase = true;
        apps.add(appBean2);

        AppBean appBean3 = new AppBean();
        appBean3.appId = 3;
        appBean3.appName = "设备管家";
        appBean3.image = MyApplication.Companion.getMContext().getDrawable(R.mipmap.icon_app_steward);
        appBean3.isBase = true;
        apps.add(appBean3);

        return apps;
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
