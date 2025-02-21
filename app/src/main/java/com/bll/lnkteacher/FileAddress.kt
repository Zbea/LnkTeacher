package com.bll.lnkteacher

import com.bll.lnkteacher.Constants.Companion.APK_PATH
import com.bll.lnkteacher.Constants.Companion.BOOK_PATH
import com.bll.lnkteacher.Constants.Companion.DIARY_PATH
import com.bll.lnkteacher.Constants.Companion.EXAM_PATH
import com.bll.lnkteacher.Constants.Companion.FREENOTE_PATH
import com.bll.lnkteacher.Constants.Companion.HOMEWORK_PATH
import com.bll.lnkteacher.Constants.Companion.IMAGE_PATH
import com.bll.lnkteacher.Constants.Companion.NOTE_PATH
import com.bll.lnkteacher.Constants.Companion.SCREEN_PATH
import com.bll.lnkteacher.Constants.Companion.TESTPAPER_PATH
import com.bll.lnkteacher.Constants.Companion.TEXTBOOK_PATH
import com.bll.lnkteacher.Constants.Companion.ZIP_PATH
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.utils.SPUtil

class FileAddress {

    val mUserId=SPUtil.getObj("user", User::class.java)?.accountId.toString()

    /**
     * 书籍地址
     * /storage/emulated/0/Books
     */
    fun getPathBook(fileName: String):String{
        return "$BOOK_PATH/$mUserId/$fileName"
    }

    fun getPathBookDraw(fileName: String):String{
        return "$BOOK_PATH/$mUserId/${fileName}draw"
    }

    /**
     * 教学教育地址
     */
    fun getPathTeachingBook(fileName: String):String{
        return "$BOOK_PATH/$mUserId/teaching/$fileName"
    }
    fun getPathTeachingBookDraw(fileName: String):String{
        return "$BOOK_PATH/$mUserId/teaching/${fileName}draw"
    }

    fun getPathHandout(fileName: String):String{
        return "$BOOK_PATH/$mUserId/handout/$fileName"
    }
    fun getPathHandoutDraw(fileName: String):String{
        return "$BOOK_PATH/$mUserId/handout/${fileName}draw"
    }

    fun getPathTextBook(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/textbook/$fileName"
    }
    fun getPathTextBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/textbook/${fileName}draw"
    }

    fun getPathHomeworkBook(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/$fileName"
    }
    fun getPathHomeworkBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/$mUserId/${fileName}/draw"
    }
    /**
     * 书籍目录地址
     */
    fun getPathTextBookCatalog(path:String):String{
        return "$path/catalog.txt"
    }
    /**
     * 书籍图片地址
     */
    fun getPathTextBookPicture(path:String):String{
        return "$path/contents"
    }
    /**
     * zip保存地址
     * ///storage/emulated/0/Android/data/yourPackageName/files/Zip/fileName.zip
     */
    fun getPathZip(fileName:String):String{
        return "$ZIP_PATH/$fileName.zip"
    }

    /**
     * apk下载地址
     */
    fun getPathApk(fileName: String):String{
        return "$APK_PATH/$fileName.apk"
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
    fun getPathImage(type:String,titleStr:String):String{
        return "$IMAGE_PATH/$mUserId/$type/$titleStr"
    }
    /**
     * 课程表
     */
    fun getPathCourse(type:String):String{
        return "$IMAGE_PATH/$mUserId/$type"
    }
    /**
     * 批改学生试卷保存地址
     */
    fun getPathTestPaperDrawing(paperCorrectId: Int?, classId: Int?, userId:Int):String{
        return "$TESTPAPER_PATH/$mUserId/paperCorrectId$paperCorrectId/classId$classId/userId$userId"
    }
    /**
     * 批改学生作业保存地址
     */
    fun getPathHomework(paperCorrectId: Int?,classId: Int?,userId:Int):String{
        return "$HOMEWORK_PATH/$mUserId/homeworkCorrectId$paperCorrectId/classId$classId/userId$userId"
    }
    /**
     * 批改学生考试保存地址
     */
    fun getPathExam(paperCorrectId: Int?,classId: Int?,userId:Int):String{
        return "$EXAM_PATH/$mUserId/examCorrectId$paperCorrectId/classId$classId/userId$userId"
    }
    /**
     * 得到老师测卷手写地址
     */
    fun getPathTestPaperDrawing(id: Int):String{
        return "$IMAGE_PATH/$mUserId/testDrawing/$id"
    }
    /**
     * 得到老师考试手写地址
     */
    fun getPathExamDrawing(id: Int):String{
        return "$IMAGE_PATH/$mUserId/examDrawing/$id"
    }

    /**
     * 计划总览路径
     */
    fun getPathPlan(year:Int,month:Int):String{
        return "$IMAGE_PATH/${mUserId}/month/$year$month"
    }
    /**
     * 计划总览路径
     */
    fun getPathPlan(startTime:String):String{
        return "$IMAGE_PATH/${mUserId}/week/$startTime"
    }

    /**
     * 七牛上传记录地址
     */
    fun getPathRecorder():String{
        return "$IMAGE_PATH/${mUserId}/recorder"
    }

    /**
     * 日记路径
     */
    fun getPathDiary(time:String):String{
        return "$DIARY_PATH/${mUserId}/$time"
    }

    /**
     * 随笔文件路径
     */
    fun getPathFreeNote(title:String):String{
        return "$FREENOTE_PATH/${mUserId}/$title"
    }

    /**
     * 截图
     */
    fun getPathScreen(typeStr: String):String{
        return "$SCREEN_PATH/${mUserId}/$typeStr"
    }

}