package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.FreeNoteBeanDao;
import com.bll.lnkteacher.mvp.model.FreeNoteBean;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class FreeNoteDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static FreeNoteDaoManager mDbController;
    private final FreeNoteBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public FreeNoteDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getFreeNoteBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static FreeNoteDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (FreeNoteDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new FreeNoteDaoManager();
                }
            }
        }
        User mUser=SPUtil.INSTANCE.getObj("user", User.class);
        whereUser= FreeNoteBeanDao.Properties.UserId.eq(mUser.accountId);
        return mDbController;
    }

    public void insertOrReplace(FreeNoteBean bean) {
        dao.insertOrReplace(bean);
    }

    public FreeNoteBean queryBean() {
        WhereCondition whereCondition= FreeNoteBeanDao.Properties.IsSave.eq(false);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(FreeNoteBeanDao.Properties.Date).build().unique();
    }

    public List<FreeNoteBean> queryList() {
        return dao.queryBuilder().where(whereUser).orderDesc(FreeNoteBeanDao.Properties.Date).build().list();
    }

    public List<FreeNoteBean> queryList( int page, int pageSize) {
        return dao.queryBuilder().where(whereUser).orderDesc(FreeNoteBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    public boolean isExist(long date) {
        WhereCondition whereCondition= FreeNoteBeanDao.Properties.Date.eq(date);
        return dao.queryBuilder().where(whereUser,whereCondition).build().unique()!=null;
    }

    public void deleteBean(FreeNoteBean bean){
        dao.delete(bean);
    }

    public void clear(){
        dao.deleteAll();
    }
}
