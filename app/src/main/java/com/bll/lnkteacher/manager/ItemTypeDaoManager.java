package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MethodManager;
import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.ItemTypeBeanDao;
import com.bll.lnkteacher.mvp.model.ItemTypeBean;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class ItemTypeDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static ItemTypeDaoManager mDbController;
    private final ItemTypeBeanDao dao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public ItemTypeDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getItemTypeBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static ItemTypeDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (ItemTypeDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new ItemTypeDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= ItemTypeBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplace(ItemTypeBean bean) {
        dao.insertOrReplace(bean);
    }

    public List<ItemTypeBean> queryAll(int type) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1).orderAsc(ItemTypeBeanDao.Properties.Date).build().list();
    }

    public List<ItemTypeBean> queryAllOrderDesc(int type) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1).orderDesc(ItemTypeBeanDao.Properties.Date).build().list();
    }

    public Boolean isExist(String title,int type){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Title.eq(title);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(type);
        return dao.queryBuilder().where(whereUser,whereUser1,whereUser2).unique()!=null;
    }

    /**
     * 查看日记分类是否已经下载
     * @param typeId
     * @return
     */
    public Boolean isExistDiaryType(int typeId){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.TypeId.eq(typeId);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(4);
        return !dao.queryBuilder().where(whereUser, whereUser1, whereUser2).build().list().isEmpty();
    }

    public void saveBookBean(String title,boolean isNew) {
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.Type.eq(2);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Title.eq(title);
        ItemTypeBean bean=dao.queryBuilder().where(whereUser,whereUser1,whereUser2).orderAsc(ItemTypeBeanDao.Properties.Date).build().unique();
        bean.setIsNew(isNew);
        insertOrReplace(bean);
    }

    public Boolean isExistBookType(){
        WhereCondition whereUser1= ItemTypeBeanDao.Properties.IsNew.eq(true);
        WhereCondition whereUser2= ItemTypeBeanDao.Properties.Type.eq(2);
        return !dao.queryBuilder().where(whereUser, whereUser1, whereUser2).build().list().isEmpty();
    }

    public void deleteBean(ItemTypeBean bean){
        dao.delete(bean);
    }

    public void clear(int type){
        dao.deleteInTx(queryAll(type));
    }

    public void clear(){
        dao.deleteAll();
    }
}
