package com.bll.lnkteacher.mvp.view;

import com.bll.lnkteacher.mvp.model.AccountList;
import com.bll.lnkteacher.mvp.model.AccountOrder;
import com.bll.lnkteacher.mvp.model.AppBean;
import com.bll.lnkteacher.mvp.model.BookEvent;
import com.bll.lnkteacher.mvp.model.BookStore;
import com.bll.lnkteacher.mvp.model.ClassGroup;
import com.bll.lnkteacher.mvp.model.ClassGroupUser;
import com.bll.lnkteacher.mvp.model.Group;
import com.bll.lnkteacher.mvp.model.GroupUser;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.net.IBaseView;

import java.util.ArrayList;
import java.util.List;

public interface IContractView {

    //登录
    interface ILoginView extends IBaseView {
        void getLogin(User user);
        void getAccount(User user);
    }

    //注册 找回密码
    interface IRegisterOrFindPsdView extends IBaseView {
        void onSms();
        void onRegister();
        void onFindPsd();
        void onEditPsd();
    }

    //账户页面回调
    interface IAccountInfoView extends IBaseView {
        void onLogout();
        void onEditNameSuccess();
        void getVipList(AccountList list);
        void onVipOrder(AccountOrder order);
    }

    //钱包页面回调
    interface IWalletView extends IBaseView {
        void getXdList(AccountList list);
        void onXdOrder(AccountOrder order);
        void checkOrder(AccountOrder order);
    }

    //书城
    interface IBookStoreView extends IBaseView {
        void onBookStore(BookStore bookStore);//商城列表
        void onBuyBook(BookEvent bookEvent);//购买书籍回调
        void onDownBook(BookEvent bookEvent);//下载书籍回调
    }

    //班群
    interface IClassGroupView extends IBaseView{
        void onCreateSuccess();
        void onClassList(List<ClassGroup> list);
        void onEditSuccess();
        void onDissolveSuccess();
    }
    //群管理
    interface IGroupManagerView extends IBaseView{
        void onCreateClassGroupSuccess();
        void onCreateGroupSuccess();
        void onAddSuccess();
    }

    //班群学生
    interface IClassGroupUserView extends IBaseView{
        //学生列表
        void onUserList(List<ClassGroupUser> users);
        //踢出学生成功
        void onOutSuccess();
        //修改学生职位成功
        void onEditSuccess();
    }

    //校群
    interface IGroupView extends IBaseView{
        void onGroupList(List<Group> groups);
        void onQuitSuccess();
        void getGroupUser(List<GroupUser> users);
    }

    //主页
    interface IMainView extends IBaseView{
        void onClassList(List<ClassGroup> classGroups);
        void onGroupList(List<Group> groups);
    }

}
