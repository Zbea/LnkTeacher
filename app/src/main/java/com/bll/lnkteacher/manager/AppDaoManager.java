package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.AppBeanDao;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.mvp.model.AppBean;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class AppDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static AppDaoManager mDbController;
    private final AppBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public AppDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getAppBeanDao(); //note表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static AppDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (AppDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new AppDaoManager();
                }
            }
        }
        User mUser=SPUtil.INSTANCE.getObj("user", User.class);
        long userId =0;
        if (mUser!=null) {
            userId=mUser.accountId;
        }
        whereUser= AppBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(AppBean bean) {
        dao.insertOrReplace(bean);
    }


    public List<AppBean> queryList() {
        return dao.queryBuilder().where(whereUser).build().list();
    }

    public List<AppBean> queryTool() {
        WhereCondition whereCondition1=AppBeanDao.Properties.IsTool.eq(true);
        return dao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }
    public boolean isExist(String packageName){
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        AppBean appBean=dao.queryBuilder().where(whereUser,whereCondition).build().unique();
        return appBean!=null;
    }
    public boolean isExistTool(String packageName){
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        WhereCondition whereCondition1=AppBeanDao.Properties.IsTool.eq(true);
        AppBean appBean=dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
        return appBean!=null;
    }

    public void delete(String packageName) {
        WhereCondition whereCondition=AppBeanDao.Properties.PackageName.eq(packageName);
        AppBean appBean=dao.queryBuilder().where(whereUser,whereCondition).build().unique();
        if (appBean!=null)
            delete(appBean);
    }


    public void delete(AppBean item) {
        dao.delete(item);
    }

    public void clear(){
        dao.deleteAll();
    }

}
