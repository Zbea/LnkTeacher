package com.bll.lnkteacher

import com.bll.lnkteacher.Constants.Companion.BOOK_PATH
import com.bll.lnkteacher.Constants.Companion.BOOK_PICTURE_FILES
import com.bll.lnkteacher.Constants.Companion.CATALOG_TXT
import com.bll.lnkteacher.Constants.Companion.DATE_PATH
import com.bll.lnkteacher.Constants.Companion.HOMEWORK_PATH
import com.bll.lnkteacher.Constants.Companion.NOTE_PATH
import com.bll.lnkteacher.Constants.Companion.TESTPAPER_PATH
import com.bll.lnkteacher.Constants.Companion.ZIP_PATH
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.utils.SPUtil
import java.io.File

class FileAddress {

    val mUserId=SPUtil.getObj("user", User::class.java)?.accountId.toString()

    /**
     * 书籍地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/BookFile/mUserId/fileName
     */
    fun getPathBook(fileName: String):String{
        return "$BOOK_PATH/$mUserId/$fileName"
    }

    /**
     * 书籍目录地址
     */
    fun getPathBookCatalog(path:String):String{
        return path + File.separator + CATALOG_TXT
    }
    /**
     * 书籍图片地址
     */
    fun getPathBookPicture(path:String):String{
        return path + File.separator + BOOK_PICTURE_FILES
    }
    /**
     * zip保存地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/Zip/fileName.zip
     */
    fun getPathZip(fileName:String):String{
        return ZIP_PATH+File.separator + fileName + ".zip"
    }

    /**
     * 书籍zip解压地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/BookFile/mUserId/fileName
     */
    fun getPathBookUnzip(fileName:String):String{
        val unzipTargetFile = File(BOOK_PATH, mUserId)
        if (!unzipTargetFile.exists()) {
            unzipTargetFile.mkdir()
        }
        return unzipTargetFile.path + File.separator + fileName
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