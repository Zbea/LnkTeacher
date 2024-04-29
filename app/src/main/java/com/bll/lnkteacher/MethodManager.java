package com.bll.lnkteacher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bll.lnkteacher.manager.AppDaoManager;
import com.bll.lnkteacher.manager.BookGreenDaoManager;
import com.bll.lnkteacher.manager.NoteDaoManager;
import com.bll.lnkteacher.mvp.model.AppBean;
import com.bll.lnkteacher.mvp.model.Book;
import com.bll.lnkteacher.mvp.model.Note;
import com.bll.lnkteacher.mvp.model.PrivacyPassword;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.mvp.model.homework.HomeworkClass;
import com.bll.lnkteacher.ui.activity.AccountLoginActivity;
import com.bll.lnkteacher.ui.activity.NoteDrawingActivity;
import com.bll.lnkteacher.ui.activity.book.BookDetailsActivity;
import com.bll.lnkteacher.utils.ActivityManager;
import com.bll.lnkteacher.utils.AppUtils;
import com.bll.lnkteacher.utils.FileUtils;
import com.bll.lnkteacher.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MethodManager {

    private static User user=SPUtil.INSTANCE.getObj("user", User.class);

    public static void getUser(){
        user=SPUtil.INSTANCE.getObj("user", User.class);
    }

    /**
     * 退出登录
     * @param context
     */
    public static void logout(Context context){
        SPUtil.INSTANCE.putString("token", "");
        SPUtil.INSTANCE.removeObj("user");

        Intent i=new Intent(context, AccountLoginActivity.class);
        i.putExtra("android.intent.extra.LAUNCH_SCREEN", 3);
        context.startActivity(i);
        ActivityManager.getInstance().finishOthers(AccountLoginActivity.class);

        //发出退出登录广播
        Intent intent = new Intent();
        intent.putExtra("token", "");
        intent.putExtra("userId", 0L);
        intent.setAction(Constants.LOGOUT_BROADCAST_EVENT);
        context.sendBroadcast(intent);
    }

    /**
     * 跳转阅读器
     * @param context
     * @param bookBean
     */
    public static void gotoBookDetails(Context context, Book bookBean)  {
        AppUtils.stopApp(context,Constants.PACKAGE_READER);
        User user=SPUtil.INSTANCE.getObj("user", User.class);

        bookBean.isLook=true;
        bookBean.time=System.currentTimeMillis();
        BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean);
        EventBus.getDefault().post(Constants.BOOK_EVENT);

        List<AppBean> toolApps= getAppTools(context,1);
        JSONArray result =new JSONArray();
        for (AppBean item :toolApps) {
            if (Objects.equals(item.packageName, Constants.PACKAGE_GEOMETRY))
                continue;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("appName", item.appName);
                jsonObject.put("packageName", item.packageName);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            result.put(jsonObject);
        }

        Intent intent = new Intent();
        intent.setAction( "com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage(Constants.PACKAGE_READER);
        intent.putExtra("path", bookBean.bookPath);
        intent.putExtra("key_book_id",bookBean.bookId+"");
        intent.putExtra("bookName", bookBean.bookName);
        intent.putExtra("tool",result.toString());
        intent.putExtra("userId",user.accountId);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 1);
        context.startActivity(intent);
    }

    /**
     * @param book
     * @param type 1书籍 0课本
     */
    public static void deleteBook(Book book,int type){
        BookGreenDaoManager.getInstance().deleteBook(book); //删除本地数据库
        FileUtils.deleteFile(new File(book.bookPath));//删除下载的书籍资源
        File file=new File(book.bookDrawPath);
        if (file.exists())
            FileUtils.deleteFile(file);
        if (type==1){
            EventBus.getDefault().post(Constants.BOOK_EVENT) ;
        }
        else {
            EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT);
        }

    }

    /**
     * 跳转课本详情
     */
    public static void gotoTextBookDetails(Context context,Book book){
        ActivityManager.getInstance().checkBookIDisExist(book.bookId);
        Intent intent=new Intent(context, BookDetailsActivity.class);
        intent.putExtra("book_id", book.bookId);
        intent.putExtra("book_type", book.typeId);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        context.startActivity(intent);
    }

    /**
     * 跳转笔记写作
     */
    public static void gotoNote(Context context,Note note) {
        note.date=System.currentTimeMillis();
        NoteDaoManager.getInstance().insertOrReplace(note);
        EventBus.getDefault().post(Constants.NOTE_EVENT);

        Intent intent = new Intent(context, NoteDrawingActivity.class);
        intent.putExtra("noteId",note.id);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);
    }

    /**
     * 根据作业本id保存当前作业的最后一次提交
     */
    public static void saveCommitClass(int typeId,List<HomeworkClass> items){
        SPUtil.INSTANCE.putCommitClasss(typeId+"CommitClass",items);
    }

    /**
     * 得到作业最后一次提交所选班级信息
     * @return
     */
    public static List<HomeworkClass> getCommitClass(int typeId){
        return SPUtil.INSTANCE.getCommitClasss(typeId+"CommitClass");
    }

    /**
     * 获取工具app
     * @param context
     * @param type 0 全部应用 1 设置为工具应用
     * @return
     */
    public static List<AppBean> getAppTools(Context context,int type){
        List<AppBean> apps;
        if (type==0){
            apps=AppDaoManager.getInstance().queryList();
        }
        else {
            apps=AppDaoManager.getInstance().queryTool();
        }
        //从数据库中拿到应用集合 遍历查询已存储的应用是否已经卸载 卸载删除
        Iterator<AppBean> it=apps.iterator();
        while (it.hasNext()){
            AppBean item= it.next();
            if (!AppUtils.isAvailable(context,item.packageName)&& !Objects.equals(item.packageName, Constants.PACKAGE_GEOMETRY)){
                it.remove();
                AppDaoManager.getInstance().delete(item);
            }
        }
        return apps;
    }

    /**
     * 保存私密密码
     * type 0日记1密本
     * @param privacyPassword
     */
    public static void savePrivacyPassword(int type,PrivacyPassword privacyPassword){
        if (type==0){
            SPUtil.INSTANCE.putObj("privacyPasswordDiary",privacyPassword);
        }
        else{
            SPUtil.INSTANCE.putObj("privacyPasswordNote",privacyPassword);
        }
    }

    /**
     * 获取私密密码
     * type 0日记1密本
     * @return
     */
    public static PrivacyPassword getPrivacyPassword(int type){
        if (type==0){
            return SPUtil.INSTANCE.getObj("privacyPasswordDiary", PrivacyPassword.class);
        }
        else{
            return SPUtil.INSTANCE.getObj("privacyPasswordNote", PrivacyPassword.class);
        }
    }


}
