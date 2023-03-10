package com.bll.lnkteacher.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.BaseTypeBeanDao;
import com.bll.lnkteacher.greendao.DaoMaster;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.mvp.model.BaseTypeBean;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class BaseTypeBeanDaoManager {
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static BaseTypeBeanDaoManager mDbController;


    private BaseTypeBeanDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;

    /**
     * 构造初始化
     */
    public BaseTypeBeanDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getBaseTypeBeanDao();
    }


    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static BaseTypeBeanDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (BaseTypeBeanDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new BaseTypeBeanDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(BaseTypeBean bean) {
        dao.insertOrReplace(bean);
    }


    public List<BaseTypeBean> queryAll() {
        WhereCondition whereCondition=BaseTypeBeanDao.Properties.UserId.eq(userId);
        List<BaseTypeBean> queryList = dao.queryBuilder().where(whereCondition)
                .orderAsc(BaseTypeBeanDao.Properties.Date).build().list();
        return queryList;
    }

    public void deleteBean(BaseTypeBean bean){
        dao.delete(bean);
    }


}
