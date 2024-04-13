package com.bll.lnkteacher

import android.os.Environment

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//    ┃　　　┃   神兽保佑
//    ┃　　　┃   代码无BUG！
//    ┃　　　┗━━━┓
//    ┃　　　　　　　┣┓
//    ┃　　　　　　　┏┛
//    ┗┓┓┏━┳┓┏┛
//      ┃┫┫　┃┫┫
//      ┗┻┛　┗┻┛
/**
 * desc: 常量  分辨率为 1404x1872，屏幕尺寸为 10.3
 */
class Constants {

    companion object {

        const val WIDTH = 1404
        const val HEIGHT = 1872 //38->52 50->69
        const val halfYear = 180 * 24 * 60 * 60 * 1000
        const val dayLong = 24 * 60 * 60 * 1000
        const val weekTime = 7 * 24 * 60 * 60 * 1000
        const val SCREEN_LEFT = 1//左屏
        const val SCREEN_RIGHT = 2//右屏
        const val SCREEN_FULL = 3//全屏

//                                const val URL_BASE = "https://api2.qinglanmb.com/v1/"
        const val URL_BASE = "http://192.168.101.100:10800/v1/"

        ///storage/emulated/0/Android/data/yourPackageName/files/Zip
        val ZIP_PATH = MyApplication.mContext.getExternalFilesDir("Zip")?.path

        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH = MyApplication.mContext.getExternalFilesDir("APK")?.path

        val TESTPAPER_PATH = MyApplication.mContext.getExternalFilesDir("TestPaper")?.path
        val HOMEWORK_PATH = MyApplication.mContext.getExternalFilesDir("Homework")?.path
            val EXAM_PATH = MyApplication.mContext.getExternalFilesDir("Exam")?.path
        val TEXTBOOK_PATH = MyApplication.mContext.getExternalFilesDir("TextBookFile")!!.path
        val TEXTBOOK_CATALOG_TXT = "catalog.txt" //book文本信息的json文件
        val TEXTBOOK_PICTURE_FILES = "contents" //图片资源的最确路径

        //笔记保存目录
        val NOTE_PATH = MyApplication.mContext.getExternalFilesDir("Note")?.path
        val IMAGE_PATH = MyApplication.mContext.getExternalFilesDir("Image")?.path
        val FREENOTE_PATH = MyApplication.mContext.getExternalFilesDir("FreeNote")!!.path
        val RECORDER_PATH = MyApplication.mContext.getExternalFilesDir("Recorder")!!.path

        val BOOK_PATH = Environment.getExternalStoragePublicDirectory("Books").absolutePath
        val BOOK_DRAW_PATH = Environment.getExternalStoragePublicDirectory("Notes").absolutePath
        val SCREEN_PATH = Environment.getExternalStoragePublicDirectory("Screenshots").absolutePath

        //eventbus通知标志
        const val AUTO_UPLOAD_DAY_EVENT = "AutoUploadDayEvent" //每天3点自动上传
        const val AUTO_UPLOAD_YEAR_EVENT = "AutoUploadYearEvent" //每天3点自动上传
        const val AUTO_REFRESH_EVENT = "AutoRefreshEvent"//每天更新
        const val DATE_EVENT = "DateEvent"
        const val BOOK_EVENT = "BookEvent"
        const val TEXT_BOOK_EVENT = "TextBookEvent"
        const val COURSE_EVENT = "CourseEvent"
        const val NOTE_BOOK_MANAGER_EVENT = "NoteBookManagerEvent"
        const val NOTE_EVENT = "NoteEvent"
        const val CLASSGROUP_EVENT = "ClassGroupEvent"
        const val CLASSGROUP_CHANGE_EVENT = "ClassGroupChangeEvent"
        const val MESSAGE_EVENT = "MessageEvent"
        const val HOMEWORK_CORRECT_EVENT = "HomeworkCorrectEvent"
        const val EXAM_CORRECT_EVENT = "ExamCorrectEvent"
        const val APP_INSTALL_EVENT = "AppInstallEvent"
        const val APP_INSTALL_INSERT_EVENT = "AppInstallInsertEvent"
        const val APP_UNINSTALL_EVENT = "AppUnInstallEvent"
        const val PRIVACY_PASSWORD_EVENT = "PrivacyPasswordEvent"
        const val SCREENSHOT_MANAGER_EVENT = "ScreenshotManagerEvent"
        const val SETTING_DATA_CLEAR_EVENT = "SettingDataClearEvent" //一键清除
        const val CLASSGROUP_TEACHING_PLAN_EVENT = "ClassGroupTeachingPlanEvent"
        const val CALENDER_EVENT = "CalenderEvent"
        const val CALENDER_SET_EVENT = "CalenderSetEvent"

        const val PACKAGE_READER = "com.geniatech.knote.reader"
        const val PACKAGE_GEOMETRY = "com.geometry"

        //广播
        const val DATA_CLEAR_BROADCAST_EVENT = "com.android.settings.cleardata"
        const val LOGIN_BROADCAST_EVENT = "com.bll.lnkteacher.account.login"
        const val LOGOUT_BROADCAST_EVENT = "com.bll.lnkteacher.account.logout"
        const val ACTION_REFRESH = "com.bll.lnkteacher.refresh"
        const val ACTION_UPLOAD_REFRESH = "com.bll.lnkteacher.upload"//每天3自动更新
        const val ACTION_UPLOAD_YEAR = "com.bll.lnkteacher.upload.year"//每年12月31 3点自动上传

        const val INTENT_SCREEN_LABEL = "android.intent.extra.LAUNCH_SCREEN"
    }

}


