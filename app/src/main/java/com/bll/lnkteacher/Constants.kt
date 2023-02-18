package com.bll.lnkteacher

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
class Constants private constructor() {

    companion object {

        val PAGE_SIZE = 12

//        const val URL_BASE = "https://api2.bailianlong.com/v1/"
        const val URL_BASE = "http://192.168.101.10:10800/v1/"

        ///storage/emulated/0/Android/data/yourPackageName/files/Zip
        val ZIP_PATH = MyApplication.mContext.getExternalFilesDir("Zip").path

        ///storage/emulated/0/Android/data/yourPackageName/files/APK
        val APK_PATH = MyApplication.mContext.getExternalFilesDir("APK").path

        //解压的目录
        ///storage/emulated/0/Android/data/yourPackageName/files/BookFile
        val BOOK_PATH = MyApplication.mContext.getExternalFilesDir("BookFile").path

        val CATALOG_TXT = "catalog.txt" //book文本信息的json文件
        val BOOK_PICTURE_FILES = "contents" //图片资源的最确路径

        //截图保存目录
        val SCREEN_PATH = MyApplication.mContext.getExternalFilesDir("Screen").path

        //笔记保存目录
        val NOTE_PATH = MyApplication.mContext.getExternalFilesDir("Note").path

        //日历保存
        val DATE_PATH = MyApplication.mContext.getExternalFilesDir("Date").path

        //eventbus通知标志
        val DATE_EVENT = "DateEvent"
        val BOOK_EVENT = "BookEvent"
        val TEXT_BOOK_EVENT = "TextBookEvent"
        val COURSE_EVENT = "CourseEvent"
        val NOTE_BOOK_MANAGER_EVENT = "NoteBookManagerEvent"
        val NOTE_EVENT = "NoteEvent"
        val CLASSGROUP_EVENT = "ClassGroupEvent"
    }

}


