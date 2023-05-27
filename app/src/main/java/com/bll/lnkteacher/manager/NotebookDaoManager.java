package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.NotebookDao;
import com.bll.lnkteacher.mvp.model.Notebook;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class NotebookDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static NotebookDaoManager mDbController;

    private NotebookDao dao;

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    private WhereCondition whereUser= NotebookDao.Properties.UserId.eq(userId);

    /**
     * 构造初始化
     */
    public NotebookDaoManager() {
        mDaoSession=MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getNotebookDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NotebookDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (NotebookDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NotebookDaoManager();
                }
            }
        }
        return mDbController;
    }

    public void insertOrReplace(Notebook bean) {
        dao.insertOrReplace(bean);
    }

    public long getInsertId(){
        List<Notebook> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }

    public List<Notebook> queryAll() {
        return dao.queryBuilder().where(whereUser).orderDesc(NotebookDao.Properties.CreateDate).build().list();
    }

    /**
     * @return
     */
    public List<Notebook> queryAll(String type) {
        WhereCondition whereCondition=NotebookDao.Properties.TypeStr.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NotebookDao.Properties.CreateDate).build().list();
    }

    /**
     * @return
     */
    public List<Notebook> queryAll(String type,int page, int pageSize) {
        WhereCondition whereCondition=NotebookDao.Properties.TypeStr.eq(type);
        return dao.queryBuilder().where(whereUser,whereCondition).orderDesc(NotebookDao.Properties.CreateDate)
                .offset((page-1)*pageSize).limit(pageSize).build().list();
    }

    /**
     * 是否存在笔记
     * @return
     */
    public Boolean isExist(String typeStr,String title){
        WhereCondition whereCondition1=NotebookDao.Properties.TypeStr.eq(typeStr);
        WhereCondition whereCondition2= NotebookDao.Properties.Title.eq(title);
        return dao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).unique()!=null;
    }

    public void deleteBean(Notebook bean){
        dao.delete(bean);
    }

}
