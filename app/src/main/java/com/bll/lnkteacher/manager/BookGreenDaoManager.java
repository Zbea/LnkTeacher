package com.bll.lnkteacher.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.BookDao;
import com.bll.lnkteacher.greendao.DaoMaster;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.mvp.model.Book;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * Created by ly on 2021/1/19 17:52
 */
public class BookGreenDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    /**
     *
     */
    private static BookGreenDaoManager mDbController;


    private BookDao bookDao;  //book表

    private long userId= SPUtil.INSTANCE.getObj("user", User.class).accountId;
    WhereCondition whereUser= BookDao.Properties.UserId.eq(userId);


    /**
     * 构造初始化
     */
    public BookGreenDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();

        bookDao = mDaoSession.getBookDao(); //book表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static BookGreenDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (BookGreenDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new BookGreenDaoManager();
                }
            }
        }
        return mDbController;
    }


    //增加书籍
    public void insertOrReplaceBook(Book bean) {
        bookDao.insertOrReplace(bean);
    }


    //根据bookId 查询书籍
    public Book queryBookByBookID(int bookID) {
        WhereCondition whereCondition= BookDao.Properties.BookId.eq(bookID);
        Book queryBook = bookDao.queryBuilder().where(whereUser,whereCondition).build().unique();
        return queryBook;
    }


    //查找课本 细分子类
    public List<Book> queryAllTextBook(int category,String textType) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(category);
        WhereCondition whereCondition2=BookDao.Properties.TextBookType.eq(textType);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }

    public List<Book> queryAllTextBook(int category,String textType,int page, int pageSize) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(category);
        WhereCondition whereCondition2=BookDao.Properties.TextBookType.eq(textType);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }

    //删除书籍数据d对象
    public void deleteBook(Book book){
        bookDao.delete(book);
    }

}
