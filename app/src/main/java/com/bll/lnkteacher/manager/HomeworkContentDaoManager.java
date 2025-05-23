package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MethodManager;
import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.HomeworkContentBeanDao;
import com.bll.lnkteacher.greendao.NoteContentDao;
import com.bll.lnkteacher.mvp.model.NoteContent;
import com.bll.lnkteacher.mvp.model.homework.HomeworkContentBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static HomeworkContentDaoManager mDbController;
    private final HomeworkContentBeanDao dao;  //note表
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public HomeworkContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkContentBeanDao(); //note表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkContentDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkContentDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= HomeworkContentBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplaceNote(HomeworkContentBean bean) {
        dao.insertOrReplace(bean);
    }

    public long getInsertId(){
        List<HomeworkContentBean> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<HomeworkContentBean> queryAllByContent(int contentId) {
        WhereCondition whereCondition=HomeworkContentBeanDao.Properties.ContentId.eq(contentId);
        return dao.queryBuilder().where(whereUser,whereCondition).orderAsc(HomeworkContentBeanDao.Properties.Date).build().list();
    }

    public List<HomeworkContentBean> queryAllByType(int typeId) {
        WhereCondition whereCondition=HomeworkContentBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereCondition).orderAsc(HomeworkContentBeanDao.Properties.Date).build().list();
    }

    public void deleteContent(int contentId){
        List<HomeworkContentBean> contentBeans=queryAllByContent(contentId);
        dao.deleteInTx(contentBeans);
    }

    public void deleteType(int typeId){
        List<HomeworkContentBean> contentBeans=queryAllByType(typeId);
        dao.deleteInTx(contentBeans);
    }
}
