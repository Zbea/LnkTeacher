package com.bll.lnkteacher

import com.bll.lnkteacher.Constants.Companion.APK_PATH
import com.bll.lnkteacher.Constants.Companion.BOOK_PATH
import com.bll.lnkteacher.Constants.Companion.DIARY_PATH
import com.bll.lnkteacher.Constants.Companion.EXAM_PATH
import com.bll.lnkteacher.Constants.Companion.FREENOTE_PATH
import com.bll.lnkteacher.Constants.Companion.HOMEWORK_PATH
import com.bll.lnkteacher.Constants.Companion.IMAGE_PATH
import com.bll.lnkteacher.Constants.Companion.NOTE_PATH
import com.bll.lnkteacher.Constants.Companion.PPT_PATH
import com.bll.lnkteacher.Constants.Companion.SCREEN_PATH
import com.bll.lnkteacher.Constants.Companion.TESTPAPER_PATH
import com.bll.lnkteacher.Constants.Companion.TEXTBOOK_PATH
import com.bll.lnkteacher.Constants.Companion.ZIP_PATH
import com.bll.lnkteacher.MethodManager.getAccountId

class FileAddress {

    fun getLauncherPath():String{
        return  getPathApk("lnkTeacher")
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
     * 书籍地址
     * /storage/emulated/0/Books
     */
    fun getPathBook(fileName: String):String{
        return "$BOOK_PATH/${getAccountId()}/$fileName"
    }

    fun getPathBookDraw(fileName: String):String{
        return "$BOOK_PATH/${getAccountId()}/${fileName}draw"
    }
    /**
     * 教学教育地址
     */
    fun getPathTeachingBook(fileName: String):String{
        return "$BOOK_PATH/${getAccountId()}/teachingBook/$fileName"
    }
    fun getPathTeachingBookDraw(fileName: String):String{
        return "$BOOK_PATH/${getAccountId()}/teachingBook/${fileName}draw"
    }

    fun getPathHandout(fileName: String):String{
        return "$BOOK_PATH/${getAccountId()}/handout/$fileName"
    }
    fun getPathHandoutDraw(fileName: String):String{
        return "$BOOK_PATH/${getAccountId()}/handout/${fileName}draw"
    }

    fun getPathTextBook(fileName: String):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/textbook/$fileName"
    }
    fun getPathTextBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/textbook/${fileName}draw"
    }

    fun getPathHomeworkBook(fileName: String):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/homeworkBook/$fileName"
    }
    fun getPathHomeworkBookDraw(fileName: String):String{
        return "$TEXTBOOK_PATH/${getAccountId()}/homeworkBook/${fileName}/draw"
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
        return "$NOTE_PATH/${getAccountId()}/$typeStr/$noteBookStr"
    }

    /**
     * 日历保存地址
     */
    fun getPathImage(type:String,titleStr:String):String{
        return "$IMAGE_PATH/${getAccountId()}/$type/$titleStr"
    }
    /**
     * 课程表
     */
    fun getPathCourse(type:String):String{
        return "$IMAGE_PATH/${getAccountId()}/$type"
    }
    /**
     * 批改学生试卷保存地址
     */
    fun getPathTestPaperDrawing(paperCorrectId: Int?, classId: Int?, userId:Int):String{
        return "$TESTPAPER_PATH/${getAccountId()}/paperCorrectId$paperCorrectId/classId$classId/userId$userId"
    }
    /**
     * 老师手写地址
     */
    fun getPathHomeworkContent(typeId: Int,contentId: Int):String{
        return "$HOMEWORK_PATH/${getAccountId()}/content/$typeId/$contentId"
    }
    /**
     * 批改学生作业保存地址
     */
    fun getPathHomework(paperCorrectId: Int?,classId: Int?,userId:Int):String{
        return "$HOMEWORK_PATH/${getAccountId()}/correct/$paperCorrectId/$classId/$userId"
    }
    /**
     * 批改学生考试保存地址
     */
    fun getPathExam(paperCorrectId: Int?,classId: Int?,userId:Int):String{
        return "$EXAM_PATH/${getAccountId()}/examCorrectId$paperCorrectId/classId$classId/userId$userId"
    }
    /**
     * 计划总览路径
     */
    fun getPathPlan(year:Int,month:Int):String{
        return "$IMAGE_PATH/${getAccountId()}/month/$year$month"
    }
    /**
     * 计划总览路径
     */
    fun getPathPlan(startTime:String):String{
        return "$IMAGE_PATH/${getAccountId()}/week/$startTime"
    }

    /**
     * 七牛上传记录地址
     */
    fun getPathRecorder():String{
        return "$IMAGE_PATH/${getAccountId()}/recorder"
    }

    /**
     * 日记路径
     */
    fun getPathDiary(time:String):String{
        return "$DIARY_PATH/${getAccountId()}/$time"
    }

    /**
     * 随笔文件路径
     */
    fun getPathFreeNote(title:String):String{
        return "$FREENOTE_PATH/${getAccountId()}/$title"
    }

    /**
     * 截图
     */
    fun getPathScreen(typeStr: String):String{
        return "$SCREEN_PATH/${getAccountId()}/$typeStr"
    }

    /**
     * 授课ppt
     */
    fun getPathPpt(typeStr: String):String{
        return "$PPT_PATH/$typeStr"
    }

}