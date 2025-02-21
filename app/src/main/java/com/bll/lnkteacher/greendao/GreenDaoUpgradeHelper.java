package com.bll.lnkteacher.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

public class GreenDaoUpgradeHelper extends DaoMaster.DevOpenHelper{

    public GreenDaoUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
    //这里重写onUpgrade方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db,ifNotExists);
                    }
                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                },
                BookDao.class,
                DateEventDao.class,
                NoteContentDao.class,
                NoteDao.class,
                AppBeanDao.class,
                WallpaperBeanDao.class,
                DiaryBeanDao.class,
                FreeNoteBeanDao.class,
                ItemTypeBeanDao.class,
                CalenderItemBeanDao.class,
                ClassScheduleBeanDao.class,
                HandoutBeanDao.class,
                TextbookBeanDao.class
        );
    }
}