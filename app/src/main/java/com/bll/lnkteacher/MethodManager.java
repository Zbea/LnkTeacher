package com.bll.lnkteacher;

import android.content.Context;
import android.content.Intent;

import com.bll.lnkteacher.manager.AppDaoManager;
import com.bll.lnkteacher.manager.BookGreenDaoManager;
import com.bll.lnkteacher.mvp.model.AppBean;
import com.bll.lnkteacher.mvp.model.Book;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.mvp.model.homework.HomeworkClass;
import com.bll.lnkteacher.ui.activity.AccountLoginActivity;
import com.bll.lnkteacher.ui.activity.MainActivity;
import com.bll.lnkteacher.utils.ActivityManager;
import com.bll.lnkteacher.utils.AppUtils;
import com.bll.lnkteacher.utils.SPUtil;
import com.bll.lnkteacher.utils.SToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        context.startActivity(new Intent(context, AccountLoginActivity.class));
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

        List<AppBean> toolApps= AppDaoManager.getInstance().queryTool();
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
        intent.putExtra("android.intent.extra.LAUNCH_SCREEN", 2);
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
    
}
