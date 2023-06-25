package com.bll.lnkteacher.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkteacher.MyApplication;
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
    /**
     *
     */
    private static CourseGreenDaoManager mDbController;


    private CourseBeanDao courseDao;

    private long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("userTeacher", User.class)).accountId;

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
        return mDbController;
    }


    //增加课程
    public void insertOrReplaceCourse(CourseBean bean) {
        courseDao.insertOrReplace(bean);
    }

    public void insertAll(List<CourseBean> lists){
        courseDao.insertOrReplaceInTx(lists);
    }

    //根据Id 查询
    public CourseBean queryID(int id) {
        WhereCondition whereCondition= CourseBeanDao.Properties.UserId.eq(userId);
        WhereCondition whereCondition1= CourseBeanDao.Properties.ViewId.eq(id);
        return  courseDao.queryBuilder().where(whereCondition,whereCondition1).build().unique();
    }

    //删除
    public void deleteCourse(CourseBean bean){
        courseDao.delete(bean);
    }

    //全部删除
    public void deleteAll(){
        courseDao.deleteAll();
    }


}
