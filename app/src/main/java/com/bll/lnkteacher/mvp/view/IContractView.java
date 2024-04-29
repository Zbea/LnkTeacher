package com.bll.lnkteacher.mvp.view;

import com.bll.lnkteacher.mvp.model.AccountQdBean;
import com.bll.lnkteacher.mvp.model.AccountOrder;
import com.bll.lnkteacher.mvp.model.AppList;
import com.bll.lnkteacher.mvp.model.AppUpdateBean;
import com.bll.lnkteacher.mvp.model.BookStore;
import com.bll.lnkteacher.mvp.model.BookStoreType;
import com.bll.lnkteacher.mvp.model.CalenderList;
import com.bll.lnkteacher.mvp.model.CloudList;
import com.bll.lnkteacher.mvp.model.CommonData;
import com.bll.lnkteacher.mvp.model.FriendList;
import com.bll.lnkteacher.mvp.model.Message;
import com.bll.lnkteacher.mvp.model.SchoolBean;
import com.bll.lnkteacher.mvp.model.ShareNoteList;
import com.bll.lnkteacher.mvp.model.WallpaperList;
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList;
import com.bll.lnkteacher.mvp.model.exam.ExamCorrectList;
import com.bll.lnkteacher.mvp.model.exam.ExamList;
import com.bll.lnkteacher.mvp.model.exam.ExamRankList;
import com.bll.lnkteacher.mvp.model.group.ClassGroup;
import com.bll.lnkteacher.mvp.model.group.ClassGroupTeacher;
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser;
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetails;
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean;
import com.bll.lnkteacher.mvp.model.testpaper.AssignContent;
import com.bll.lnkteacher.mvp.model.testpaper.CorrectList;
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList;
import com.bll.lnkteacher.mvp.model.testpaper.RankBean;
import com.bll.lnkteacher.mvp.model.testpaper.TypeList;
import com.bll.lnkteacher.mvp.model.User;
import com.bll.lnkteacher.net.IBaseView;

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
        void onEditNameSuccess();
        void onEditSchool();
        void onBind();
        void onUnbind();
        void onListFriend(FriendList list);
    }

    //钱包页面回调
    interface IWalletView extends IBaseView {
        void getXdList(List<AccountQdBean> list);
        void onXdOrder(AccountOrder order);
        void checkOrder(AccountOrder order);
    }

    //书城
    interface IBookStoreView extends IBaseView {
        void onBook(BookStore bookStore);
        void onType(BookStoreType bookStoreType);
        void buyBookSuccess();
    }

    //班群
    interface IClassGroupView extends IBaseView{
        void onClassList(List<ClassGroup> classGroups);
        void onSuccess();
        void onUploadSuccess();
    }

    interface IClassGroupUserView extends IBaseView{
        //学生列表
        void onUserList(List<ClassGroupUser> users);
        //踢出学生成功
        void onOutSuccess();
        //修改学生职位成功
        void onEditSuccess();
    }

    interface IClassGroupChildView extends IBaseView{
        //学生列表
        void onUserList(List<ClassGroupUser> users);
        void onClassGroupChildList(List<ClassGroup> classItems);
        void onSuccess();
    }

    interface IClassGroupTeacherView extends IBaseView{
        void onUserList(List<ClassGroupTeacher> users);
        void onOutSuccess();
        void onTransferSuccess();
    }

    //主页
    interface ICommonView extends IBaseView{
        void onClassList(List<ClassGroup> classGroups);
        void onCommon(CommonData commonData);
        void onAppUpdate(AppUpdateBean item);
    }

    interface ISchoolView extends IBaseView{
        void onListSchools(List<SchoolBean> list);
    }

    //考卷布置
    interface ITestPaperAssignView extends IBaseView{
        /**
         * 考卷布置分类列表
         */
        void onType(TypeList typeList);
        /**
         * 添加考卷分类成功
         */
        void onTypeSuccess();
        /**
         * 考卷内容列表
         * @param assignContent
         */
        void onList(AssignContent assignContent);
        /**
         * 考卷图片列表
         * @param lists
         */
        void onImageList(List<ContentListBean> lists);
        /**
         * 删除考卷内容成功
         */
        void onDeleteSuccess();
        /**
         * 发送考卷成功
         */
        void onSendSuccess();
    }

    //考卷批改
    interface ITestPaperCorrectView extends IBaseView{
        /**
         * 考卷批改列表
         * @param bean
         */
        void onList(CorrectList bean);
        /**
         * 删除考卷批改
         */
        void onDeleteSuccess();
    }

    //考卷批改
    interface ITestPaperCorrectDetailsView extends IBaseView{
        /**
         * 获取考卷原图
         * @param list
         */
        void onImageList(List<ContentListBean> list);
        /**
         * 获取班级学生已提交考卷
         * @param bean
         */
        void onClassPapers(TestPaperClassUserList bean);
        /**
         * 老师提交批改
         */
        void onCorrectSuccess();
        void onSendSuccess();
    }

    interface ITestPaperRankView extends IBaseView{
        void onGrade(List<RankBean> list);
        void onExamGrade(ExamRankList list);
    }

    //文件上传
    interface IFileUploadView extends IBaseView{
        void onToken(String token);
    }

    //作业布置
    interface IHomeworkAssignView extends IBaseView{
        /**
         * 获取作业分类
         * @param list
         */
        void onTypeList(TypeList list);
        /**
         * 添加作业分类
         */
        void onAddSuccess();
        /**
         * 删除分类
         */
        void onDeleteSuccess();
        /**
         * 发送成功
         */
        void onCommitSuccess();
        /**
         * 布置详情
         * @param details
         */
        void onDetails(HomeworkAssignDetails details);
        void onDetailsDeleteSuccess();
        void onBook(BookStore bookStore);
    }

    interface IHomeworkPaperAssignView extends IBaseView{

        /**
         * 获取作业卷内容列表
         * @param homeworkContent
         */
        void onList(AssignContent homeworkContent);
        /**
         * 获取作业卷内容图片
         * @param lists
         */
        void onImageList(List<ContentListBean> lists);
        /**
         * 发送成功
         */
        void onCommitSuccess();
        void onDeleteSuccess();
    }


    interface IHomeworkCorrectView extends IBaseView{
        /**
         * 作业批改列表
         * @param list
         */
        void onList(CorrectList list);
        /**
         * 删除作业批改
         */
        void onDeleteSuccess();
    }

    interface IMainView extends IBaseView{
        void onList(Message message);
        void onListFriend(FriendList list);
    }

    interface IMessageView extends IBaseView{
        void onList(Message message);
        void onSend();
        void onDeleteSuccess();
    }

    interface ITextbookView extends IBaseView{
        void onAddHomeworkBook();
    }

    //应用
    interface IAPPView extends IBaseView {
        void onType(CommonData commonData);
        void onAppList(AppList appBean);
        void buySuccess();
    }

    interface IWallpaperView extends IBaseView {
        void onList(WallpaperList list);
        void buySuccess();
    }

    interface IQiniuView extends IBaseView {
        void onToken(String token);
    }

    /**
     * 云书库上传
     */
    interface ICloudUploadView extends IBaseView{
        void onSuccessCloudUpload(List<Integer> cloudIds);
        void onDeleteSuccess();
    }

    interface ICloudView extends IBaseView {
        void onList(CloudList item);
        void onType(List<String> types);
        void onDelete();
    }

    interface ICalenderView extends IBaseView {
        void onList(CalenderList list);
        void buySuccess();
    }

    interface IExamCorrectListView extends IBaseView {
        void onList(ExamCorrectList list);
        void onSuccess();
    }

    interface IExamListView extends IBaseView {
        void onList(ExamList list);
        void onExamImage(ExamList.ExamBean url);
        void onExamClassUser(ExamClassUserList classUserList);
    }

    interface IExamCorrectView extends IBaseView {
        void onExamClassUser(ExamClassUserList classUserList);
        void onCorrectSuccess();
    }

    interface IShareNoteView extends IBaseView{
        void onList(ShareNoteList list);
        void onToken(String token);
        void onDeleteSuccess();
        void onShare();
    }

}
