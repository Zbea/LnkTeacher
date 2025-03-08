package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MethodManager;
import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.ClassScheduleBeanDao;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.mvp.model.ClassScheduleBean;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;


public class ClassScheduleGreenDaoManager {
    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static ClassScheduleGreenDaoManager mDbController;
    private final ClassScheduleBeanDao courseDao;
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public ClassScheduleGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        courseDao = mDaoSession.getClassScheduleBeanDao();
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static ClassScheduleGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (ClassScheduleGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new ClassScheduleGreenDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= ClassScheduleBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    //增加课程
    public void insertOrReplaceCourse(ClassScheduleBean bean) {
        courseDao.insertOrReplace(bean);
    }

    public void insertAll(List<ClassScheduleBean> lists){
        courseDao.insertOrReplaceInTx(lists);
    }

    public List<ClassScheduleBean> queryByTypeLists(int type, int classGroupId){
        WhereCondition whereCondition1= ClassScheduleBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= ClassScheduleBeanDao.Properties.ClassGroupId.eq(classGroupId);
        return courseDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2).build().list();
    }

    public void editByTypeLists(int type,int classGroupId,int mode){
        List<ClassScheduleBean> list=queryByTypeLists(type, classGroupId);
        for (int i = 0; i < list.size(); i++) {
            ClassScheduleBean item = list.get(i);
            item.mode=mode;
        }
        insertAll(list);
    }

    //根据Id 查询
    public ClassScheduleBean queryID(int type, int classGroupId, int viewId) {
        WhereCondition whereCondition1= ClassScheduleBeanDao.Properties.Type.eq(type);
        WhereCondition whereCondition2= ClassScheduleBeanDao.Properties.ClassGroupId.eq(classGroupId);
        WhereCondition whereCondition3= ClassScheduleBeanDao.Properties.ViewId.eq(viewId);
        return  courseDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().unique();
    }

    public void delete(int type,int classGroupId){
        courseDao.deleteInTx(queryByTypeLists(type,classGroupId));
    }

    public void clear(){
        courseDao.deleteAll();
    }

}
