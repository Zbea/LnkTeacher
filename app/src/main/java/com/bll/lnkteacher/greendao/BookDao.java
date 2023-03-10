package com.bll.lnkteacher.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkteacher.mvp.model.Book;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BOOK".
*/
public class BookDao extends AbstractDao<Book, Long> {

    public static final String TABLENAME = "BOOK";

    /**
     * Properties of entity Book.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property BookId = new Property(2, int.class, "bookId", false, "BOOK_ID");
        public final static Property ImageUrl = new Property(3, String.class, "imageUrl", false, "IMAGE_URL");
        public final static Property SubjectName = new Property(4, String.class, "subjectName", false, "SUBJECT_NAME");
        public final static Property CreatedAt = new Property(5, String.class, "createdAt", false, "CREATED_AT");
        public final static Property BookDesc = new Property(6, String.class, "bookDesc", false, "BOOK_DESC");
        public final static Property Semester = new Property(7, String.class, "semester", false, "SEMESTER");
        public final static Property Area = new Property(8, String.class, "area", false, "AREA");
        public final static Property BookName = new Property(9, String.class, "bookName", false, "BOOK_NAME");
        public final static Property Price = new Property(10, int.class, "price", false, "PRICE");
        public final static Property Grade = new Property(11, String.class, "grade", false, "GRADE");
        public final static Property BookVersion = new Property(12, String.class, "bookVersion", false, "BOOK_VERSION");
        public final static Property Version = new Property(13, String.class, "version", false, "VERSION");
        public final static Property Supply = new Property(14, String.class, "supply", false, "SUPPLY");
        public final static Property Category = new Property(15, int.class, "category", false, "CATEGORY");
        public final static Property TextBookType = new Property(16, String.class, "textBookType", false, "TEXT_BOOK_TYPE");
        public final static Property Status = new Property(17, int.class, "status", false, "STATUS");
        public final static Property DownloadUrl = new Property(18, String.class, "downloadUrl", false, "DOWNLOAD_URL");
        public final static Property BookPath = new Property(19, String.class, "bookPath", false, "BOOK_PATH");
        public final static Property Time = new Property(20, Long.class, "time", false, "TIME");
        public final static Property PageIndex = new Property(21, int.class, "pageIndex", false, "PAGE_INDEX");
        public final static Property PageUpUrl = new Property(22, String.class, "pageUpUrl", false, "PAGE_UP_URL");
        public final static Property PageUrl = new Property(23, String.class, "pageUrl", false, "PAGE_URL");
    }


    public BookDao(DaoConfig config) {
        super(config);
    }
    
    public BookDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOOK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"BOOK_ID\" INTEGER NOT NULL UNIQUE ," + // 2: bookId
                "\"IMAGE_URL\" TEXT," + // 3: imageUrl
                "\"SUBJECT_NAME\" TEXT," + // 4: subjectName
                "\"CREATED_AT\" TEXT," + // 5: createdAt
                "\"BOOK_DESC\" TEXT," + // 6: bookDesc
                "\"SEMESTER\" TEXT," + // 7: semester
                "\"AREA\" TEXT," + // 8: area
                "\"BOOK_NAME\" TEXT," + // 9: bookName
                "\"PRICE\" INTEGER NOT NULL ," + // 10: price
                "\"GRADE\" TEXT," + // 11: grade
                "\"BOOK_VERSION\" TEXT," + // 12: bookVersion
                "\"VERSION\" TEXT," + // 13: version
                "\"SUPPLY\" TEXT," + // 14: supply
                "\"CATEGORY\" INTEGER NOT NULL ," + // 15: category
                "\"TEXT_BOOK_TYPE\" TEXT," + // 16: textBookType
                "\"STATUS\" INTEGER NOT NULL ," + // 17: status
                "\"DOWNLOAD_URL\" TEXT," + // 18: downloadUrl
                "\"BOOK_PATH\" TEXT," + // 19: bookPath
                "\"TIME\" INTEGER," + // 20: time
                "\"PAGE_INDEX\" INTEGER NOT NULL ," + // 21: pageIndex
                "\"PAGE_UP_URL\" TEXT," + // 22: pageUpUrl
                "\"PAGE_URL\" TEXT);"); // 23: pageUrl
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOOK\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Book entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getBookId());
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(4, imageUrl);
        }
 
        String subjectName = entity.getSubjectName();
        if (subjectName != null) {
            stmt.bindString(5, subjectName);
        }
 
        String createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindString(6, createdAt);
        }
 
        String bookDesc = entity.getBookDesc();
        if (bookDesc != null) {
            stmt.bindString(7, bookDesc);
        }
 
        String semester = entity.getSemester();
        if (semester != null) {
            stmt.bindString(8, semester);
        }
 
        String area = entity.getArea();
        if (area != null) {
            stmt.bindString(9, area);
        }
 
        String bookName = entity.getBookName();
        if (bookName != null) {
            stmt.bindString(10, bookName);
        }
        stmt.bindLong(11, entity.getPrice());
 
        String grade = entity.getGrade();
        if (grade != null) {
            stmt.bindString(12, grade);
        }
 
        String bookVersion = entity.getBookVersion();
        if (bookVersion != null) {
            stmt.bindString(13, bookVersion);
        }
 
        String version = entity.getVersion();
        if (version != null) {
            stmt.bindString(14, version);
        }
 
        String supply = entity.getSupply();
        if (supply != null) {
            stmt.bindString(15, supply);
        }
        stmt.bindLong(16, entity.getCategory());
 
        String textBookType = entity.getTextBookType();
        if (textBookType != null) {
            stmt.bindString(17, textBookType);
        }
        stmt.bindLong(18, entity.getStatus());
 
        String downloadUrl = entity.getDownloadUrl();
        if (downloadUrl != null) {
            stmt.bindString(19, downloadUrl);
        }
 
        String bookPath = entity.getBookPath();
        if (bookPath != null) {
            stmt.bindString(20, bookPath);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(21, time);
        }
        stmt.bindLong(22, entity.getPageIndex());
 
        String pageUpUrl = entity.getPageUpUrl();
        if (pageUpUrl != null) {
            stmt.bindString(23, pageUpUrl);
        }
 
        String pageUrl = entity.getPageUrl();
        if (pageUrl != null) {
            stmt.bindString(24, pageUrl);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Book entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getBookId());
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(4, imageUrl);
        }
 
        String subjectName = entity.getSubjectName();
        if (subjectName != null) {
            stmt.bindString(5, subjectName);
        }
 
        String createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindString(6, createdAt);
        }
 
        String bookDesc = entity.getBookDesc();
        if (bookDesc != null) {
            stmt.bindString(7, bookDesc);
        }
 
        String semester = entity.getSemester();
        if (semester != null) {
            stmt.bindString(8, semester);
        }
 
        String area = entity.getArea();
        if (area != null) {
            stmt.bindString(9, area);
        }
 
        String bookName = entity.getBookName();
        if (bookName != null) {
            stmt.bindString(10, bookName);
        }
        stmt.bindLong(11, entity.getPrice());
 
        String grade = entity.getGrade();
        if (grade != null) {
            stmt.bindString(12, grade);
        }
 
        String bookVersion = entity.getBookVersion();
        if (bookVersion != null) {
            stmt.bindString(13, bookVersion);
        }
 
        String version = entity.getVersion();
        if (version != null) {
            stmt.bindString(14, version);
        }
 
        String supply = entity.getSupply();
        if (supply != null) {
            stmt.bindString(15, supply);
        }
        stmt.bindLong(16, entity.getCategory());
 
        String textBookType = entity.getTextBookType();
        if (textBookType != null) {
            stmt.bindString(17, textBookType);
        }
        stmt.bindLong(18, entity.getStatus());
 
        String downloadUrl = entity.getDownloadUrl();
        if (downloadUrl != null) {
            stmt.bindString(19, downloadUrl);
        }
 
        String bookPath = entity.getBookPath();
        if (bookPath != null) {
            stmt.bindString(20, bookPath);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(21, time);
        }
        stmt.bindLong(22, entity.getPageIndex());
 
        String pageUpUrl = entity.getPageUpUrl();
        if (pageUpUrl != null) {
            stmt.bindString(23, pageUpUrl);
        }
 
        String pageUrl = entity.getPageUrl();
        if (pageUrl != null) {
            stmt.bindString(24, pageUrl);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Book readEntity(Cursor cursor, int offset) {
        Book entity = new Book( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // userId
            cursor.getInt(offset + 2), // bookId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // imageUrl
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // subjectName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // createdAt
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // bookDesc
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // semester
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // area
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // bookName
            cursor.getInt(offset + 10), // price
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // grade
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // bookVersion
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // version
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // supply
            cursor.getInt(offset + 15), // category
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // textBookType
            cursor.getInt(offset + 17), // status
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // downloadUrl
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // bookPath
            cursor.isNull(offset + 20) ? null : cursor.getLong(offset + 20), // time
            cursor.getInt(offset + 21), // pageIndex
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // pageUpUrl
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23) // pageUrl
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Book entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setBookId(cursor.getInt(offset + 2));
        entity.setImageUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSubjectName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCreatedAt(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBookDesc(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSemester(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setArea(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setBookName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setPrice(cursor.getInt(offset + 10));
        entity.setGrade(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setBookVersion(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setVersion(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setSupply(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setCategory(cursor.getInt(offset + 15));
        entity.setTextBookType(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setStatus(cursor.getInt(offset + 17));
        entity.setDownloadUrl(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setBookPath(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setTime(cursor.isNull(offset + 20) ? null : cursor.getLong(offset + 20));
        entity.setPageIndex(cursor.getInt(offset + 21));
        entity.setPageUpUrl(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setPageUrl(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Book entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Book entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Book entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
