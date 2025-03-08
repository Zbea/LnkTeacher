package com.bll.lnkteacher.manager;


import com.bll.lnkteacher.Constants;
import com.bll.lnkteacher.MethodManager;
import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.BookDao;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.TextbookBeanDao;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.mvp.model.book.TextbookBean;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class TextbookGreenDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static TextbookGreenDaoManager mDbController;
    private final TextbookBeanDao bookDao;  //book表
    static WhereCondition whereUser;


    /**
     * 构造初始化
     */
    public TextbookGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        bookDao = mDaoSession.getTextbookBeanDao(); //book表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static TextbookGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (TextbookGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new TextbookGreenDaoManager();
                }
            }
        }
        long userId = MethodManager.getAccountId();
        whereUser= TextbookBeanDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    //增加书籍
    public void insertOrReplaceBook(TextbookBean bean) {
        bookDao.insertOrReplace(bean);
    }

    /**
     *  查找课本、作业、教学
     * @param type 0课本 1作业 2教学 3期刊
     */
    public TextbookBean queryTextBookByBookId(int type, int bookID) {
        WhereCondition whereCondition= TextbookBeanDao.Properties.Category.eq(type);
        WhereCondition whereCondition1= TextbookBeanDao.Properties.BookId.eq(bookID);
        return bookDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }

    //查找课本 细分子类
    public List<TextbookBean> queryAllTextBook(int typeId) {
        WhereCondition whereCondition1=TextbookBeanDao.Properties.Category.eq(typeId);
        return bookDao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(TextbookBeanDao.Properties.Time).build().list();
    }

    public List<TextbookBean> queryAllTextBook(int typeId, int page, int pageSize) {
        WhereCondition whereCondition1=TextbookBeanDao.Properties.Category.eq(typeId);
        return bookDao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(TextbookBeanDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    /**
     * 获取半年以前的课本
     */
    public List<TextbookBean> queryTextBookByHalfYear(){
        long time=System.currentTimeMillis()- Constants.halfYear;
        WhereCondition whereCondition1= TextbookBeanDao.Properties.Time.le(time);
        return bookDao.queryBuilder().where(whereUser,whereCondition1).build().list();
    }

    public void deleteBook(TextbookBean book){
        bookDao.delete(book);
    }

    public void clear(){
        bookDao.deleteAll();
    }


}
