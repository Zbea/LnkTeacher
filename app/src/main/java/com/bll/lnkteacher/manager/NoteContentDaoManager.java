package com.bll.lnkteacher.manager;

import com.bll.lnkteacher.MyApplication;
import com.bll.lnkteacher.greendao.BookDao;
import com.bll.lnkteacher.greendao.DaoSession;
import com.bll.lnkteacher.greendao.NoteContentDao;
import com.bll.lnkteacher.mvp.model.NoteContent;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.Objects;

public class NoteContentDaoManager {

    /**
     * DaoSession
     */
    private DaoSession mDaoSession;
    private static NoteContentDaoManager mDbController;
    private final NoteContentDao dao;  //note表
    private static WhereCondition whereUser;

    /**
     * 构造初始化
     */
    public NoteContentDaoManager() {
        mDaoSession = MyApplication.Companion.getMDaoSession();
        dao = mDaoSession.getNoteContentDao(); //note表
    }

    /**
     * 获取单例（context 最好用application的context  防止内存泄漏）
     */
    public static NoteContentDaoManager getInstance() {
        if (mDbController == null) {
            synchronized (NoteContentDaoManager.class) {
                if (mDbController == null) {
                    mDbController = new NoteContentDaoManager();
                }
            }
        }
        long userId = Objects.requireNonNull(SPUtil.INSTANCE.getObj("userTeacher", User.class)).accountId;
        whereUser= NoteContentDao.Properties.UserId.eq(userId);
        return mDbController;
    }

    public void insertOrReplaceNote(NoteContent bean) {
        dao.insertOrReplace(bean);
    }

    public long getInsertId(){
        List<NoteContent> queryList = dao.queryBuilder().build().list();
        return queryList.get(queryList.size()-1).id;
    }


    public List<NoteContent> queryAll(String type, long noteId) {
        WhereCondition whereCondition=NoteContentDao.Properties.TypeStr.eq(type);
        WhereCondition whereCondition1=NoteContentDao.Properties.NoteId.eq(noteId);
        return dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
    }

    public void editNotes(String type, long noteId,String editType){
        List<NoteContent> noteContents=queryAll(type,noteId);
        for (NoteContent noteContent: noteContents) {
            noteContent.typeStr=editType;
        }
        dao.insertOrReplaceInTx(noteContents);
    }

    public void deleteNote(NoteContent noteContent){
        dao.delete(noteContent);
    }

    public void deleteType(String type,long noteId){
        WhereCondition whereCondition=NoteContentDao.Properties.TypeStr.eq(type);
        WhereCondition whereCondition1=NoteContentDao.Properties.NoteId.eq(noteId);
        List<NoteContent> list = dao.queryBuilder().where(whereUser,whereCondition,whereCondition1).build().list();
        dao.deleteInTx(list);
    }

}
