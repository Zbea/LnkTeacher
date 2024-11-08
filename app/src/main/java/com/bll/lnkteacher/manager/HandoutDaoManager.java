package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.HandoutBeanDao;
import com.bll.lnkteacher.greendao.NoteDao;
import com.bll.lnkteacher.mvp.model.HandoutBean;
import com.bll.lnkteacher.mvp.model.Note;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class HandoutDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static HandoutDaoManager mDbController;
    private final HandoutBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public HandoutDaoManager() {
        mDaoSession=MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHandoutBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HandoutDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HandoutDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HandoutDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= HandoutBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(HandoutBean bean) {
        dao.insertOrReplace(bean);
    }

    public boolean isExist(long id){
        WhereCondition whereCondition1= HandoutBeanDao.Properties.Id.eq(id);
        return dao.queryBuilder().where(whereUser,whereCondition1).unique()!=null;
    }

    public List<HandoutBean> queryAll(String type) {
        WhereCondition whereCondition= HandoutBeanDao.Properties.TypeStr.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(HandoutBeanDao.Properties.Date).build().list();
    }

    public List<HandoutBean> queryAll(String type, int page, int pageSize) {
        WhereCondition whereCondition= HandoutBeanDao.Properties.TypeStr.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(HandoutBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    public void deleteBean(HandoutBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
