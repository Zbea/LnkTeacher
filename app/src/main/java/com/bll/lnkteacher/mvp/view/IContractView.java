package com.bll.lnkteacher.mvp.view;

import com.bll.lnkteacher.mvp.model.AccountQdBean;
import com.bll.lnkteacher.mvp.model.AccountOrder;
import com.bll.lnkteacher.mvp.model.AppList;
import com.bll.lnkteacher.mvp.model.book.BookStore;
import com.bll.lnkteacher.mvp.model.book.BookStoreType;
import com.bll.lnkteacher.mvp.model.CalenderList;
import com.bll.lnkteacher.mvp.model.CloudList;
import com.bll.lnkteacher.mvp.model.CommonData;
import com.bll.lnkteacher.mvp.model.FriendList;
import com.bll.lnkteacher.mvp.model.HandoutList;
import com.bll.lnkteacher.mvp.model.Message;
import com.bll.lnkteacher.mvp.model.SchoolBean;
import com.bll.lnkteacher.mvp.model.ShareNoteList;
import com.bll.lnkteacher.mvp.model.WallpaperList;
import com.bll.lnkteacher.mvp.model.book.TextbookStore;
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList;
import com.bll.lnkteacher.mvp.model.exam.ExamCorrectList;
import com.bll.lnkteacher.mvp.model.exam.ExamList;
import com.bll.lnkteacher.mvp.model.exam.ExamRankList;
import com.bll.lnkteacher.mvp.model.group.ClassGroup;
import com.bll.lnkteacher.mvp.model.group.ClassGroupTeacher;
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser;
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetailsList;
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList;
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

    //短信回调
    interface ISmsView extends IBaseView {
        default void onSms(){}
        default void onCheckSuccess(){}
    }

    //注册 找回密码
    interface IRegisterOrFindPsdView extends IBaseView {
        void onRegister();
        void onFindPsd();
    }

    //账户页面回调
    interface IAccountInfoView extends IBaseView {
        void onEditPhone();
        void onEditNameSuccess();
        void onEditSchool();
    }

    //钱包页面回调
    interface IWalletView extends IBaseView {
        void getXdList(List<AccountQdBean> list);
        void onXdOrder(AccountOrder order);
        void checkOrder(AccountOrder order);
    }

    //书城
    interface IBookStoreView extends IBaseView {
        default void onBook(BookStore bookStore){};
        default void onTextBook(TextbookStore bookStore){};
        default void onType(BookStoreType bookStoreType){};
        default void buyBookSuccess(){};
    }

    //班群
    interface IClassGroupView extends IBaseView{
        default void onClasss(List<ClassGroup> classGroups){};
        default void onClassInfo(ClassGroup classGroup){}
        default void onSubjects(List<String> subjects){};
        default void onSuccess(){};
        default void onUploadSuccess(){};
        default void onAllowSuccess(){};
        default void onPermission(){};
    }

    interface IClassGroupUserView extends IBaseView{
        //学生列表
        void onUserList(List<ClassGroupUser> users);
        //踢出学生成功
        default void onOutSuccess(){};
        //修改学生职位成功
        default void onEditSuccess(){};
    }

    interface IClassGroupChildView extends IBaseView{
        //学生列表
        default void onUserList(List<ClassGroupUser> users){};
        default void onChildUserList(List<ClassGroupUser> users){};
        default void onClassGroupChildList(List<ClassGroup> classItems){};
        default void onSuccess(){};
    }

    interface IClassGroupTeacherView extends IBaseView{
        void onUserList(List<ClassGroupTeacher> users);
        void onOutSuccess();
        void onTransferSuccess();
    }

    //主页
    interface ICommonView extends IBaseView{
        default void onCommon(CommonData commonData){};
        default void onListSchools(List<SchoolBean> list){};
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
         * 删除考卷内容成功
         */
        void onDeleteSuccess();
        void onEditSuccess();
        void onTopSuccess();
    }

    //考卷布置
    interface ITestPaperAssignContentView extends IBaseView{
        /**
         * 考卷内容列表
         * @param assignPaperContentList
         */
        void onList(AssignPaperContentList assignPaperContentList);
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
        default void onDeleteSuccess(){};
        /**
         * 发送考卷成功
         */
        default void onSendSuccess(){};
    }

    //考卷批改
    interface ITestPaperCorrectDetailsView extends IBaseView{
        /**
         * 获取班级学生已提交考卷
         * @param bean
         */
        void onClassPapers(TestPaperClassUserList bean);
        /**
         * 老师提交批改
         */
        default void onCorrectSuccess(){};
        default void onCompleteSuccess(){};
        default void onChangeQuestionType(){}
        default void onShare(){}
    }

    interface IAnalyseTeachingView extends IBaseView{
        /**
         * 获取班级学生已提交考卷
         * @param bean
         */
        void onClassPapers(TestPaperClassUserList bean);
        void onCreateSuccess();
        void onRefreshSuccess();
    }

    interface IExamAnalyseTeachingView extends IBaseView{
        /**
         * 获取班级学生已提交考卷
         * @param bean
         */
        void onClassPapers(ExamClassUserList bean);
        void onCreateSuccess();
        void onRefreshSuccess();
    }

    interface ITestPaperRankView extends IBaseView{
        void onGrade(List<RankBean> list);
        void onExamGrade(ExamRankList list);
    }

    //作业布置
    interface IHomeworkAssignView extends IBaseView{
        /**
         * 获取作业分类
         * @param list
         */
        default void onTypeList(TypeList list){};
        /**
         * 添加作业分类
         */
        default void onAddSuccess(){};
        /**
         * 删除分类
         */
        default void onDeleteSuccess(){};
        default void onEditSuccess(){};
        default void onTopSuccess(){};
        default void onBingSuccess(){};
        /**
         * 发送成功
         */
        void onCommitSuccess();
        /**
         * 布置详情
         * @param details
         */
        default void onDetails(HomeworkAssignDetailsList details){};
        default void onDetailsDeleteSuccess(){};
    }

    interface IHomeworkPaperAssignView extends IBaseView{
        default void onList(AssignPaperContentList contentList){};
        default void onCommitSuccess(){};
        default void onDeleteSuccess(){};
        default void onSetAutoAssign(){}
    }

    interface IMainRightView extends IBaseView{
        void onList(Message message);
        void onClassSchedule(String url);
    }

    interface IMainLeftView extends IBaseView{
        void onClassList(List<ClassGroup> classGroups);
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
        default void onDeleteSuccess(){};
        default void onSendSuccess(){};
    }

    interface IExamCorrectView extends IBaseView {
        void onExamClassUser(ExamClassUserList classUserList);
        default void onCorrectSuccess(){};
        default void onShare(){}
    }

    interface IFreeNoteView extends IBaseView{
        void onReceiveList(ShareNoteList list);
        void onShareList(ShareNoteList list);
        void onToken(String token);
        void onDeleteSuccess();
        void onShare();
        void onBind();
        void onUnbind();
        void onListFriend(FriendList list);
    }


    interface IDocumentView extends IBaseView{
        void onSendSuccess();
    }

}
