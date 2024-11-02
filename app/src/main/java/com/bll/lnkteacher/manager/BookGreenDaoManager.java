package com.bll.lnkteacher.manager;


import com.bll.lnkteacher.Constants;
import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.BookDao;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.mvp.model.book.Book;
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
    /**
     *  查找课本、作业、教学
     * @param type 0课本 6作业 7教学 8期刊
     * @param bookID
     * @return
     */
    public Book queryTextBookByBookID(int type,int bookID) {
        WhereCondition whereCondition= BookDao.Properties.TypeId.eq(type);
        WhereCondition whereCondition1= BookDao.Properties.BookId.eq(bookID);
        return bookDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().unique();
    }

    //增加书籍
    public void insertOrReplaceBook(Book bean) {
        bookDao.insertOrReplace(bean);
    }

    /**
     * 获取半年以前的书籍
     * category 0课本 1书籍
     * @return
     */
    public List<Book> queryAllByHalfYear(int category){
        long time=System.currentTimeMillis()- Constants.halfYear;
        WhereCondition whereCondition= BookDao.Properties.Category.eq(category);
        WhereCondition whereCondition1= BookDao.Properties.Time.ge(time);
        return bookDao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
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

    public List<Book> queryAllBook() {
        WhereCondition whereCondition=BookDao.Properties.Category.eq(1);
        return bookDao.queryBuilder().where(whereUser,whereCondition).orderDesc(BookDao.Properties.Time).build().list();
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

    //查找课本 细分子类
    public List<Book> queryAllTextBook(int typeId) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookDao.Properties.TypeId.eq(typeId);
        return bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time).build().list();
    }

    public List<Book> queryAllTextBook(int typeId, int page, int pageSize) {
        WhereCondition whereCondition1=BookDao.Properties.Category.eq(0);
        WhereCondition whereCondition2=BookDao.Properties.TypeId.eq(typeId);
        return bookDao.queryBuilder().where(whereUser,whereCondition1,whereCondition2)
                .orderDesc(BookDao.Properties.Time)
                .offset((page-1)*pageSize).limit(pageSize)
                .build().list();
    }

    //删除书籍数据d对象
    public void deleteBook(Book book){
        bookDao.delete(book);
    }

    public void clear(){
        bookDao.deleteAll();
    }


}
