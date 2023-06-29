package com.bll.lnkteacher

import com.bll.lnkteacher.Constants.Companion.BOOK_PATH
import com.bll.lnkteacher.Constants.Companion.DATE_PATH
import com.bll.lnkteacher.Constants.Companion.HOMEWORK_PATH
import com.bll.lnkteacher.Constants.Companion.NOTE_PATH
import com.bll.lnkteacher.Constants.Companion.TESTPAPER_PATH
import com.bll.lnkteacher.Constants.Companion.TEXTBOOK_CATALOG_TXT
import com.bll.lnkteacher.Constants.Companion.TEXTBOOK_PATH
import com.bll.lnkteacher.Constants.Companion.TEXTBOOK_PICTURE_FILES
import com.bll.lnkteacher.Constants.Companion.ZIP_PATH
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.utils.SPUtil
import java.io.File

class FileAddress {

    val mUserId=SPUtil.getObj("userTeacher", User::class.java)?.accountId.toString()

    /**
     * 书籍地址
     * /storage/emulated/0/Books
     */
    fun getPathBook(fileName: String):String{
        return "$BOOK_PATH/$fileName"
    }

    fun getPathBookDraw(fileName: String):String{
        return "/storage/emulated/0/Android/data/com.geniatech.knote.reader/files/note/$fileName"
    }

    fun getPathTextBook(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/$fileName"
    }
    fun getPathTextBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/${fileName}/draw"
    }
    /**
     * 书籍目录地址
     */
    fun getPathTextBookCatalog(path:String):String{
        return path + File.separator + TEXTBOOK_CATALOG_TXT
    }
    /**
     * 书籍图片地址
     */
    fun getPathTextBookPicture(path:String):String{
        return path + File.separator + TEXTBOOK_PICTURE_FILES
    }
    /**
     * zip保存地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/Zip/fileName.zip
     */
    fun getPathZip(fileName:String):String{
        return ZIP_PATH+File.separator + fileName + ".zip"
    }

    /**
     * apk下载地址
     */
    fun getPathApk(fileName: String):String{
        return Constants.APK_PATH+ File.separator + fileName + ".apk"
    }

    /**
     * 笔记保存地址
     */
    fun getPathNote(typeStr: String?,noteBookStr: String?,date:Long):String{
        return "$NOTE_PATH/$mUserId/$typeStr/$noteBookStr/$date"
    }

    /**
     * 笔记保存地址
     */
    fun getPathNote(typeStr: String?,noteBookStr: String?):String{
        return "$NOTE_PATH/$mUserId/$typeStr/$noteBookStr"
    }

    /**
     * 日历保存地址
     */
    fun getPathDate(dateStr:String):String{
        return "$DATE_PATH/$mUserId/$dateStr"
    }

    /**
     * 批改学生试卷保存地址
     */
    fun getPathTestPaper(paperCorrectId: Int?,classId: Int?,userId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/paperCorrectId$paperCorrectId/classId$classId/userId$userId"
    }
    /**
     * 批改学生作业保存地址
     */
    fun getPathHomework(paperCorrectId: Int?,classId: Int?,userId:Int):String{
        return "$HOMEWORK_PATH/$mUserId/homeworkCorrectId$paperCorrectId/classId$classId/userId$userId"
    }

}