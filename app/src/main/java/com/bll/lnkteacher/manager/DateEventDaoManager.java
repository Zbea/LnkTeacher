package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.DateEventDao;
import com.bll.lnkteacher.mvp.model.DateEvent;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class DateEventDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static DateEventDaoManager mDbController;

    private DateEventDao dao;

    private long userId= Objects.requireNonNull(SPUtil.INSTANCE.getObj("userTeacher", User.class)).accountId;
    private WhereCondition whereUser= DateEventDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public DateEventDaoManager() {
        mDaoSession=MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getDateEventDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static DateEventDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (DateEventDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new DateEventDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(DateEvent bean) {
        dao.insertOrReplace(bean);
    }

    /**
     * @return
     */
    public DateEvent queryBean(int classId,long date) {
        WhereCondition whereCondition=DateEventDao.Properties.ClassId.eq(classId);
        WhereCondition whereCondition1=DateEventDao.Properties.Date.eq(date);
        DateEvent list = dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
        return list;
    }
    /**
     * @return
     */
    public List<DateEvent> queryBeans(int classId) {
        WhereCondition whereCondition=DateEventDao.Properties.ClassId.eq(classId);
        List<DateEvent> lists = dao.queryBuilder().where(whereUser,whereCondition).build().list();
        return lists;
    }
    /**
     * @return
     */
    public List<DateEvent> queryBeans(int classId,long start,long end) {
        WhereCondition whereCondition=DateEventDao.Properties.ClassId.eq(classId);
        WhereCondition whereCondition1=DateEventDao.Properties.Date.between(start,end);
        List<DateEvent> lists = dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        return lists;
    }

    public void deleteBean(DateEvent bean){
        dao.delete(bean);
    }

    public void deletes(List<DateEvent> lists){
        dao.deleteInTx(lists);
    }

    public void update(List<DateEvent> lists){
        dao.insertOrReplaceInTx(lists);
    }

}
