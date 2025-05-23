package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MethodManager;
import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.HomeworkContentTypeBeanDao;
import com.bll.lnkteacher.greendao.ItemTypeBeanDao;
import com.bll.lnkteacher.greendao.NoteDao;
import com.bll.lnkteacher.mvp.model.ItemTypeBean;
import com.bll.lnkteacher.mvp.model.Note;
import com.bll.lnkteacher.mvp.model.homework.HomeworkContentTypeBean;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class HomeworkContentTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static HomeworkContentTypeDaoManager mDbController;
    private final HomeworkContentTypeBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public HomeworkContentTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getHomeworkContentTypeBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static HomeworkContentTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (HomeworkContentTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new HomeworkContentTypeDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= HomeworkContentTypeBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(HomeworkContentTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public HomeworkContentTypeBean queryBean(int contentId) {
        WhereCondition whereUser1= HomeworkContentTypeBeanDao.Properties.ContentId.eq(contentId);
        return dao.queryBuilder().where(whereUser,whereUser1).build().unique();
    }

    public List<HomeworkContentTypeBean> queryAll(int typeId) {
        WhereCondition whereUser1= HomeworkContentTypeBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereUser1).orderDesc(HomeworkContentTypeBeanDao.Properties.Date).build().list();
    }

    public List<HomeworkContentTypeBean> queryAll(int typeId, int page, int pageSize) {
        WhereCondition whereUser1= HomeworkContentTypeBeanDao.Properties.TypeId.eq(typeId);
        return dao.queryBuilder().where(whereUser,whereUser1).orderDesc(HomeworkContentTypeBeanDao.Properties.Date)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    public void deleteBean(HomeworkContentTypeBean bean){
        dao.delete(bean);
    }

    public void clear(int typeId){
        List<HomeworkContentTypeBean> list=queryAll(typeId);
        dao.deleteInTx(list);
    }
}
