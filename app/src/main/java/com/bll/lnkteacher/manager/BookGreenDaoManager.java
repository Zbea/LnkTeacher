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
    public Book queryBookByBookID(Long bookID) {
        WhereCondition whereCondition= BookDao.Properties.Id.eq(bookID);
        Book queryBook = bookDao.queryBuilder().where(whereUser,whereCondition).build().unique();
        return queryBook;
    }


    //查询所有书籍
    public List<Book> queryAllBook(String type) {
        WhereCondition whereCondition=BookDao.Properties.Type.notEq(type);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereUser,whereCondition).orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }

    //根据类别 细分子类
    public List<Book> queryAllBook(String type,String flag) {
        WhereCondition whereCondition1=BookDao.Properties.Type.notEq(type);
        WhereCondition whereCondition2=BookDao.Properties.BookType.eq(flag);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }

    //根据类别 细分子类 分页处理
    public List<Book> queryAllBook(String type,String flag,int page, int pageSize) {
        WhereCondition whereCondition1=BookDao.Properties.Type.notEq(type);
        WhereCondition whereCondition2=BookDao.Properties.BookType.eq(flag);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time)
                .offset(page*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }

    //查找已收藏书籍
    public List<Book> queryAllBook(boolean isCollect) {
        WhereCondition whereCondition1=BookDao.Properties.IsCollect.eq(isCollect);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereUser,whereCondition1)
                .orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }

    //查找课本 细分子类
    public List<Book> queryAllTextBook(String type,int flag) {
        WhereCondition whereCondition1=BookDao.Properties.Type.eq(type);
        WhereCondition whereCondition2=BookDao.Properties.TextBook.eq(flag);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time).build().list();
        return queryBookList;
    }

    public List<Book> queryAllTextBook(String type,int flag,int page, int pageSize) {
        WhereCondition whereCondition1=BookDao.Properties.Type.eq(type);
        WhereCondition whereCondition2=BookDao.Properties.TextBook.eq(flag);
        List<Book> queryBookList = bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time)
                .offset(page*pageSize).limit(pageSize)
                .build().list();
        return queryBookList;
    }

    //查找课本 细分子类 根据科目查找书籍
    public Book queryTextBook(String type, int flag, int classId) {
        WhereCondition whereCondition1=BookDao.Properties.Type.eq(type);
        WhereCondition whereCondition2=BookDao.Properties.TextBook.eq(flag);
        WhereCondition whereCondition3=BookDao.Properties.ClassX.eq(classId);
        Book book = bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3).build().unique();
        return book;
    }

    //删除书籍数据d对象
    public void deleteBook(Book book){
        bookDao.delete(book);
    }

}
