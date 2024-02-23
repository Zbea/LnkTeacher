package com.bll.lnkteacher.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.BookDao;
import com.bll.lnkteacher.greendao.CourseBeanDao;
import com.bll.lnkteacher.greendao.DaoMaster;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.mvp.model.CourseBean;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;


public class CourseGreenDaoManager {
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static CourseGreenDaoManager mDbController;
    private final CourseBeanDao courseDao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public CourseGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        courseDao = mDaoSession.getCourseBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static CourseGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (CourseGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new CourseGreenDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= CourseBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    //增加课程
    public void insertOrReplaceCourse(CourseBean bean) {
        courseDao.insertOrReplace(bean);
    }

    public void insertAll(List<CourseBean> lists){
        courseDao.insertOrReplaceInTx(lists);
    }

    public List<CourseBean> queryByTypeLists(int type,int classGroupId){
        WhereCondition whereCondition1= CourseBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= CourseBeanDao.Properties.ClassGroupId.eq(classGroupId);
        return courseDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().list();
    }

    public void editByTypeLists(int type,int classGroupId,int mode){
        List<CourseBean> list=queryByTypeLists(type, classGroupId);
        for (int i = 0; i < list.size(); i++) {
            CourseBean item = list.get(i);
            item.mode=mode;
        }
        insertAll(list);
    }

    //根据Id 查询
    public CourseBean queryID(int type,int classGroupId,int viewId) {
        WhereCondition whereCondition1= CourseBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= CourseBeanDao.Properties.ClassGroupId.eq(classGroupId);
        WhereCondition whereCondition3= CourseBeanDao.Properties.ViewId.eq(viewId);
        return  courseDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().unique();
    }

    public void delete(int type,int classGroupId){
        courseDao.deleteInTx(queryByTypeLists(type,classGroupId));
    }

    public void clear(){
        courseDao.deleteAll();
    }

}
