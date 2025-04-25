package com.bll.lnkteacher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bll.lnkteacher.manager.AppDaoManager;
import com.bll.lnkteacher.manager.BookGreenDaoManager;
import com.bll.lnkteacher.manager.NoteDaoManager;
import com.bll.lnkteacher.manager.TextbookGreenDaoManager;
import com.bll.lnkteacher.mvp.model.AppBean;
import com.bll.lnkteacher.mvp.model.AreaBean;
import com.bll.lnkteacher.mvp.model.HandoutBean;
import com.bll.lnkteacher.mvp.model.ItemTypeBean;
import com.bll.lnkteacher.mvp.model.book.Book;
import com.bll.lnkteacher.mvp.model.Note;
import com.bll.lnkteacher.mvp.model.PrivacyPassword;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.mvp.model.book.TextbookBean;
import com.bll.lnkteacher.ui.activity.AccountLoginActivity;
import com.bll.lnkteacher.ui.activity.drawing.FileDrawingActivity;
import com.bll.lnkteacher.ui.activity.drawing.NoteDrawingActivity;
import com.bll.lnkteacher.ui.activity.book.TextbookDetailsActivity;
import com.bll.lnkteacher.utils.ActivityManager;
import com.bll.lnkteacher.utils.AppUtils;
import com.bll.lnkteacher.utils.FileUtils;
import com.bll.lnkteacher.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MethodManager {

    public static User getUser(){
        return SPUtil.INSTANCE.getObj("user", User.class);
    }

    public static boolean isLogin(){
        String tokenStr=SPUtil.INSTANCE.getString("token");
        return !TextUtils.isEmpty(tokenStr) && getUser()!=null;
    }

    public static long getAccountId(){
        User user=SPUtil.INSTANCE.getObj("user", User.class);
        if (user==null){
            return 0L;
        }
        else {
            return user.accountId;
        }
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
     * key_book_type 0普通书籍 1pdf书籍 2pdf课本 3文档
     * @param context
     * @param bookBean
     */
    public static void gotoBookDetails(Context context, Book bookBean)  {
        AppUtils.stopApp(context,Constants.PACKAGE_READER);

        bookBean.isLook=true;
        bookBean.time=System.currentTimeMillis();
        BookGreenDaoManager.getInstance().insertOrReplaceBook(bookBean);

        List<AppBean> toolApps= getAppTools(context,1);
        JSONArray result = getJsonArray(toolApps);

        String format = FileUtils.getUrlFormat(bookBean.bookPath);
        int key_type = 0;
        if (format.contains("pdf")) {
            key_type = 1;
        }
        Intent intent = new Intent();
        intent.setAction( "com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage(Constants.PACKAGE_READER);
        intent.putExtra("path", bookBean.bookPath);
        intent.putExtra("key_book_id",bookBean.bookId+"");
        intent.putExtra("bookName", bookBean.bookName);
        intent.putExtra("tool",result.toString());
        intent.putExtra("userId",getUser().accountId);
        intent.putExtra("type", 1);
        intent.putExtra("drawPath", bookBean.bookDrawPath);
        intent.putExtra("key_book_type", key_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        new Handler(Looper.getMainLooper()).postDelayed(() -> EventBus.getDefault().post(Constants.BOOK_EVENT),3000);
    }

    /**
     * 跳转阅读器
     *  key_book_type 0普通书籍 1pdf书籍 2pdf课本 3文档
     * @param context
     * @param bookBean
     */
    public static void gotoTeachingDetails(Context context, TextbookBean bookBean)  {
        AppUtils.stopApp(context,Constants.PACKAGE_READER);

        List<AppBean> toolApps= getAppTools(context,1);
        JSONArray result = getJsonArray(toolApps);

        int key_type = 2;
        Intent intent = new Intent();
        intent.setAction( "com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage(Constants.PACKAGE_READER);
        intent.putExtra("path", bookBean.bookPath);
        intent.putExtra("key_book_id",bookBean.bookId+"");
        intent.putExtra("bookName", bookBean.bookName);
        intent.putExtra("tool",result.toString());
        intent.putExtra("userId",getUser().accountId);
        intent.putExtra("type", 0);
        intent.putExtra("drawPath", bookBean.bookDrawPath);
        intent.putExtra("key_book_type", key_type);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    /**
     * 跳转阅读器
     * @param context
     */
    public static void gotoHandouts(Context context, HandoutBean bean)  {
        AppUtils.stopApp(context,Constants.PACKAGE_READER);

        List<AppBean> toolApps= getAppTools(context,1);
        JSONArray result = getJsonArray(toolApps);

        Intent intent = new Intent();
        intent.setAction( "com.geniatech.reader.action.VIEW_BOOK_PATH");
        intent.setPackage(Constants.PACKAGE_READER);
        intent.putExtra("path", bean.bookPath);
        intent.putExtra("key_book_id",bean.id+"");
        intent.putExtra("bookName", bean.title);
        intent.putExtra("tool",result.toString());
        intent.putExtra("userId",getUser().accountId);
        intent.putExtra("type", 3);
        intent.putExtra("drawPath", bean.bookDrawPath);
        intent.putExtra("key_book_type", 3);

        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static @NonNull JSONArray getJsonArray(List<AppBean> toolApps) {
        JSONArray result =new JSONArray();
        for (AppBean item : toolApps) {
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
        return result;
    }


    public static void deleteBook(Book book){
        BookGreenDaoManager.getInstance().deleteBook(book); //删除本地数据库
        FileUtils.deleteFile(new File(book.bookPath));//删除下载的书籍资源
        FileUtils.deleteFile(new File(book.bookDrawPath));
        EventBus.getDefault().post(Constants.BOOK_EVENT) ;
    }

    public static void deleteTextbook(TextbookBean book){
        TextbookGreenDaoManager.getInstance().deleteBook(book); //删除本地数据库
        FileUtils.deleteFile(new File(book.bookPath));//删除下载的书籍资源
        FileUtils.deleteFile(new File(book.bookDrawPath));
        EventBus.getDefault().post(Constants.TEXT_BOOK_EVENT);
    }

    /**
     * 跳转课本详情
     */
    public static void gotoTextBookDetails(Context context,TextbookBean book){
        ActivityManager.getInstance().checkBookIDisExist(book.bookId);
        Intent intent=new Intent(context, TextbookDetailsActivity.class);
        intent.putExtra("book_id", book.bookId);
        intent.putExtra("book_type", book.category);
        intent.putExtra("android.intent.extra.KEEP_FOCUS",true);
        context.startActivity(intent);
    }

    /**
     * 跳转笔记写作
     */
    public static void gotoNote(Context context,Note note) {
        Intent intent = new Intent(context, NoteDrawingActivity.class);
        intent.putExtra("noteId",note.id);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);

        note.date=System.currentTimeMillis();
        NoteDaoManager.getInstance().insertOrReplace(note);
        EventBus.getDefault().post(Constants.NOTE_EVENT);
    }

    /**
     * 跳转截图列表
     * @param context
     * @param index
     * @param tabPath
     */
    public static void gotoScreenFile(Context context,int index,String tabPath){
        Intent intent=new Intent(context, FileDrawingActivity.class);
        intent.putExtra("pageIndex",index);
        intent.putExtra("pagePath",tabPath);
        ActivityManager.getInstance().finishActivity(intent.getClass().getName());
        context.startActivity(intent);
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


    /**
     * 获取状态栏的值
     * @return
     */
    public static int getStatusBarValue(){
        return Settings.System.getInt(MyApplication.Companion.getMContext().getContentResolver(), "statusbar_hide_time", 0);
    }

    /**
     * 设置状态栏的值
     *
     * @return
     */
    public static void setStatusBarValue(int value){
        Settings.System.putInt(MyApplication.Companion.getMContext().getContentResolver(),"statusbar_hide_time", value);
    }


    /**
     * 加载不失真背景
     * @param context
     * @param resId
     * @param imageView
     */
    public static void setImageResource(Context context, int resId, ImageView imageView){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // 防止自动缩放
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 获取省
     * @param context
     * @return
     * @throws IOException
     */
    public static List<AreaBean> getProvinces(Context context) throws IOException {
        String areaJson = FileUtils.readFileContent(context.getResources().getAssets().open("city.json"));
        return new Gson().fromJson(areaJson, new TypeToken<List<AreaBean>>(){}.getType());
    }

    /**
     * 初始化不选中 指定位置选中
     * @param list
     * @param position
     * @return
     */
    public static List<ItemTypeBean> setItemTypeBeanCheck(List<ItemTypeBean> list,int position){
        if (list.size()>position){
            for (ItemTypeBean item:list) {
                item.isCheck=false;
            }
            list.get(position).isCheck=true;
        }
        return list;
    }

}
