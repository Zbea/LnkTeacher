package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.RecordBeanDao;
import com.bll.lnkteacher.mvp.model.RecordBean;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class RecordDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static RecordDaoManager mDbController;
    private final RecordBeanDao recordBeanDao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public RecordDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        recordBeanDao = mDaoSession.getRecordBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static RecordDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (RecordDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new RecordDaoManager();
                }
            }
        }
        User mUser=SPUtil.INSTANCE.getObj("user", User.class);
        whereUser= RecordBeanDao.Properties.UserId.eq(mUser.accountId);
        return mDbController;
    }

    public void insertOrReplace(RecordBean bean) {
        recordBeanDao.insertOrReplace(bean);
    }

    public List<RecordBean> queryAllList() {
        return recordBeanDao.queryBuilder().where(whereUser)
                .orderDesc(RecordBeanDao.Properties.Date).build().list();
    }

    public List<RecordBean> queryAllList(int page, int pageSize) {
        return recordBeanDao.queryBuilder().where(whereUser)
                .orderDesc(RecordBeanDao.Properties.Date).offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    public void deleteBean(RecordBean bean){
        recordBeanDao.delete(bean);
    }

    public void clear(){
        recordBeanDao.deleteAll();
    }

}
