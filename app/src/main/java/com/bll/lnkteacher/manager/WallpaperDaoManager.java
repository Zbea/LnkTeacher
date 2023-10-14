package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.WallpaperBeanDao;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.mvp.model.WallpaperBean;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class WallpaperDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static WallpaperDaoManager mDbController;
    private final WallpaperBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public WallpaperDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getWallpaperBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static WallpaperDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (WallpaperDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new WallpaperDaoManager();
                }
            }
        }
        User mUser=SPUtil.INSTANCE.getObj("user", User.class);
        long userId =0;
        if (mUser!=null) {
            userId=mUser.accountId;
        }
        whereUser= WallpaperBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(WallpaperBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<WallpaperBean> queryList() {
        return dao.queryBuilder().where(whereUser).orderDesc(WallpaperBeanDao.Properties.Date).build().list();
    }

    public List<WallpaperBean> queryList(int size,int index) {
        return dao.queryBuilder().where(whereUser)
                .limit(size).offset(index-1)
                .orderDesc(WallpaperBeanDao.Properties.Date).build().list();
    }

    public WallpaperBean queryBean(int id){
        WhereCondition whereCondition=WallpaperBeanDao.Properties.ContentId.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(WallpaperBeanDao.Properties.Date).build().unique();
    }

    public void deleteBean(WallpaperBean bean){
        dao.delete(bean);
    }


}
