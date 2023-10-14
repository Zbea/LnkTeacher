package com.bll.lnkteacher.manager;


import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.BookDao;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.mvp.model.Book;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

/**
 * Created by ly on 2021/1/19 17:52
 */
public class BookGreenDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static BookGreenDaoManager mDbController;
    private final BookDao bookDao;  //book表
    static WhereCondition whereUser;


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
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("user", User.class)).accountId;
        whereUser= BookDao.Properties.UserId.eq(userId);
        return mDbController;
    }


    //增加书籍
    public void insertOrReplaceBook(Book bean) {
        bookDao.insertOrReplace(bean);
    }


    /**
     *  查找课本、作业、教学
     * @param type 0课本 6作业 7教学
     * @param bookID
     * @return
     */
    public Book queryTextBookByBookID(int type,int bookID) {
        WhereCondition whereCondition= BookDao.Properties.TypeId.eq(type);
        WhereCondition whereCondition1= BookDao.Properties.BookId.eq(bookID);
        return bookDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }

    /**
     * 查找书籍
     * @param bookID
     * @return
     */
    public Book queryBookByBookID(int bookID) {
        WhereCondition whereCondition= BookDao.Properties.Category.eq(1);
        WhereCondition whereCondition1= BookDao.Properties.BookPlusId.eq(bookID);
        return bookDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }


    /**
     * 获取打开过的书籍
     * @param isLook
     * @return
     */
    public List<Book> queryAllBook(boolean isLook) {
        WhereCondition whereCondition=BookDao.Properties.Category.eq(1);
        WhereCondition whereCondition1=BookDao.Properties.IsLook.eq(isLook);
        return bookDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).orderDesc(BookDao.Properties.Time).limit(13).build().list();
    }

    //根据类别 细分子类
    public List<Book> queryAllBook(String type) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(1);
        WhereCondition whereCondition2=BookDao.Properties.SubtypeStr.eq(type);
        return bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time).build().list();
    }

    //根据类别 细分子类 分页处理
    public List<Book> queryAllBook(String type, int page, int pageSize) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(1);
        WhereCondition whereCondition2=BookDao.Properties.SubtypeStr.eq(type);
        return bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    //根据名称搜索 分页
    public List<Book> queryBookByName(String name, String type, int page, int pageSize) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(1);
        WhereCondition whereCondition2=BookDao.Properties.BookName.like("%"+name+"%");
        WhereCondition whereCondition3=BookDao.Properties.SubtypeStr.eq(type);
        return bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(BookDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }
    //根据名称搜素全部
    public List<Book> queryBookByName(String name, String type) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(1);
        WhereCondition whereCondition2=BookDao.Properties.BookName.like("%"+name+"%");
        WhereCondition whereCondition3=BookDao.Properties.SubtypeStr.eq(type);
        return bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2,whereCondition3)
                .orderDesc(BookDao.Properties.Time)
                .build().list();
    }

    //查找课本 细分子类
    public List<Book> queryAllTextBook(String textType) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookDao.Properties.SubtypeStr.eq(textType);
        return bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time).build().list();
    }

    public List<Book> queryAllTextBook(String textType, int page, int pageSize) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookDao.Properties.SubtypeStr.eq(textType);
        return bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }


    //删除书籍数据d对象
    public void deleteBook(Book book){
        bookDao.delete(book);
    }

}
