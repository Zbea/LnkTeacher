package com.bll.lnkteacher

import com.bll.lnkteacher.mvp.model.AccountOrder
import com.bll.lnkteacher.mvp.model.AccountQdBean
import com.bll.lnkteacher.mvp.model.AppList
import com.bll.lnkteacher.mvp.model.CalenderList
import com.bll.lnkteacher.mvp.model.CloudList
import com.bll.lnkteacher.mvp.model.CommonData
import com.bll.lnkteacher.mvp.model.FriendList
import com.bll.lnkteacher.mvp.model.HandoutList
import com.bll.lnkteacher.mvp.model.Message
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.model.ShareNoteList
import com.bll.lnkteacher.mvp.model.User
import com.bll.lnkteacher.mvp.model.WallpaperList
import com.bll.lnkteacher.mvp.model.book.BookStore
import com.bll.lnkteacher.mvp.model.book.BookStoreType
import com.bll.lnkteacher.mvp.model.book.TextbookStore
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamCorrectList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.model.exam.ExamRankList
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetailsList
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignSearchBean
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.mvp.model.testpaper.CorrectList
import com.bll.lnkteacher.mvp.model.testpaper.RankBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperClassUserList
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.net.BaseResult
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface APIService{


    /**
     * 获取下载token
     */
    @POST("file/token")
    fun getQiniuToken(): Observable<BaseResult<String>>
    @POST("cloud/data/insert")
    fun cloudUpload(@Body requestBody: RequestBody): Observable<BaseResult<MutableList<Int>>>

    /**
     * 获取云列表
     */
    @GET("cloud/data/list")
    fun getCloudList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<CloudList>>
    /**
     * 获取分类
     */
    @GET("cloud/data/types")
    fun getCloudType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<MutableList<String>>>
    /**
     * 删除云列表
     */
    @POST("cloud/data/delete")
    fun deleteCloudList(@Body requestBody: RequestBody): Observable<BaseResult<Any>>


    /**
     * 公共年级接口
     */
    @GET("userTypes")
    fun getCommonGrade(): Observable<BaseResult<CommonData>>
    /**
     * 获取学校列表
     */
    @GET("school/list")
    fun getCommonSchool(): Observable<BaseResult<MutableList<SchoolBean>>>
    /**
     * 用户登录 "/login"
     */
    @POST("login")
    fun login(@Body requestBody: RequestBody): Observable<BaseResult<User>>
    /**
     * 用户个人信息 "/accounts"
     */
    @GET("accounts")
    fun accounts(): Observable<BaseResult<User>>

    /**
     * 短信信息 "/sms"
     */
    @GET("sms")
    fun getSms(@Query("telNumber") num:String): Observable<BaseResult<Any>>

    /**
     * 注册 "/user/createTeacher"
     */
    @POST("user/createTeacher")
    fun register(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 忘记密码 "/password"
     */
    @POST("password")
    fun findPassword(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改密码 "/accounts/password"
     */
    @PATCH("accounts/password")
    fun editPassword(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改姓名 "/accounts/nickname"
     */
    @PATCH("accounts/nickname")
    fun editName(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 验证手机号
     */
    @POST("accounts/checkCode")
    fun checkPhone(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改电话
     */
    @POST("accounts/changeTel")
    fun editPhone(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改学校信息
     */
    @POST("accounts/changeAddress")
    fun editSchool(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 退出登录 "/accounts/logout"
     */
    @POST("accounts/logout")
    fun logout(): Observable<BaseResult<Any>>
    /**
     * 绑定好友
     */
    @POST("add/friend/teacher")
    fun onBindFriend(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 解绑好友
     */
    @POST("friend/delete")
    fun onUnbindFriend(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取好友列表
     */
    @GET("friend/list")
    fun onFriendList(): Observable<BaseResult<FriendList>>

    /**
     * 获取分享列表
     */
    @GET("friend/message/list")
    fun getReceiveList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ShareNoteList>>
    /**
     * 获取发送列表
     */
    @GET("friend/message/sendList")
    fun getShareList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ShareNoteList>>
    /**
     * 删除列表
     */
    @POST("friend/message/delete")
    fun deleteShare(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 分享随笔
     */
    @POST("friend/message/send")
    fun shareFreeNote(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * //获取学豆列表
     */
    @GET("wallets/list")
    fun getQdList(): Observable<BaseResult<MutableList<AccountQdBean>>>
    /**
     * 提交学豆订单
     */
    @POST("wallets/order/{id}")
    fun postOrder(@Path("id") id:String ): Observable<BaseResult<AccountOrder>>
    /**
     * 查看订单状态
     */
    @GET("wallets/order/{id}")
    fun getOrderStatus(@Path("id") id:String): Observable<BaseResult<AccountOrder>>

    /**
     * 教材分类
     */
    @GET("book/types")
    fun getBookType(): Observable<BaseResult<BookStoreType>>
    /**
     * 教材列表
     */
    @GET("textbook/list")
    fun getTextBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TextbookStore>>
    /**
     * 教材参考列表
     */
    @GET("book/list")
    fun getHomeworkBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TextbookStore>>
    /**
     * 教学教育
     */
    @GET("book/lib/list")
    fun getTeachingBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TextbookStore>>
    /**
     * 书城列表
     */
    @GET("book/plus/list")
    fun getBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>

    /**
     * 购买书籍
     */
    @POST("buy/book/createOrder")
    fun buyBooks(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 创建班群
     */
    @POST("class/group/insert")
    fun createClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改班群
     */
    @POST("class/group/edit")
    fun editClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改子群
     */
    @POST("class/group/editName")
    fun editClassGroupChild(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 加入班群
     */
    @POST("class/group/join")
    fun addClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 班群列表
     */
    @GET("class/page")
    fun getListClassGroup(): Observable<BaseResult<ClassGroupList>>
    /**
     * 班群信息
     */
    @GET("class/group/infoV2")
    fun getGroupInfo(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ClassGroup>>
    /**
     * 班群科目
     */
    @GET("class/teacherSubject")
    fun getClassGroupSubjects(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<String>>>
    /**
     * 解散班群
     */
    @POST("class/group/delete")
    fun dissolveClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 老师退出班群
     */
    @POST("class/group/quit")
    fun outClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 班群添加课程表
     */
    @POST("class/group/uploadCourse")
    fun uploadClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 上传老师排课表
     */
    @POST("course/update")
    fun uploadClassSchedule(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取班群学生列表
     */
    @POST("class/classInfo")
    fun getClassGroupChildUser(@Body requestBody: RequestBody): Observable<BaseResult<List<ClassGroupUser>>>
    /**
     * 获取子群学生列表
     */
    @GET("class/listStudent")
    fun getClassGroupUser(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<ClassGroupUser>>>
    /**
     * 获取班群所有子群
     */
    @GET("class/group/listSubGroup")
    fun getClassGroupChildList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<ClassGroup>>>
    /**
     * 老师创建子群
     */
    @POST("class/group/createOther")
    fun createClassGroupChild(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 更新子群
     */
    @POST("class/updateAllStudent")
    fun refreshClassGroupChild(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 老师子群添加学生
     */
    @POST("class/group/joinSubGroup")
    fun addClassGroupUserGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 是否允许加入班群
     */
    @POST("class/editAllowJoin")
    fun allowJoinGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 学生安排职位
     */
    @POST("class/changeJob")
    fun changeJob(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 班群学生踢出
     */
    @POST("class/group/removeStudent")
    fun outClassGroupUser(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 转移学生
     */
    @POST("class/group/moveGroup")
    fun moveClassGroupUser(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取老师列表
     */
    @GET("class/group/info")
    fun getClassGroupTeacherList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ClassGroupList>>
    /**
     * 班群踢出老师
     */
    @POST("class/group/removeTeacher")
    fun outTeacher(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 转让班主任
     */
    @POST("class/group/transfer")
    fun transferTeacher(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 获取作业、考卷分类
     */
    @GET("common/type/list")
    fun getPaperType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TypeList>>
    /**
     * 删除题卷本
     */
    @POST("common/type/delete")
    fun deleteHomeworkBookType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改作业本名称
     */
    @POST("common/type/updateName")
    fun editType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 置顶作业本
     */
    @POST("common/type/updateTop")
    fun topType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 绑定作业本
     */
    @POST("common/type/updateBind")
    fun bingType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 删除作业分类
     */
    @POST("common/type/deleteHomework")
    fun deleteHomeworkType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 添加作业、考卷分类成功
     */
    @POST("common/type/insert")
    fun addPaperType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取考卷内容列表
     */
    @GET("task/list/exam")
    fun getTestPaperContentList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AssignPaperContentList>>

    /**
     * 删除考卷
     */
    @POST("task/delete")
    fun deleteTestPaperContent(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 布置考卷
     */
    @POST("task/group/insertExamJob")
    fun sendTestPaperContent(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 获取作业订单列表
     */
    @GET("task/group/list")
    fun getHomeworkCorrectList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<CorrectList>>
    /**
     * 获取考卷订单列表
     */
    @GET("task/group/listV2")
    fun getPaperCorrectList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<CorrectList>>
    /**
     * 删除考卷批改列表
     */
    @POST("task/group/delete")
    fun deletePaperCorrectList(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取班级同学提交考卷列表
     */
    @GET("task/group/info")
    fun getPaperCorrectClassList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TestPaperClassUserList>>
    /**
     * 考试评分
     */
    @GET("task/group/one")
    fun getPaperGrade(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<RankBean>>>
    /**
     * 提交学生批改
     */
    @POST("student/task/update")
    fun commitPaperStudent(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 发送班级
     */
    @POST("student/task/sendClass")
    fun sendPaperCorrectClass(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 作业全部保存
     */
    @POST("student/task/allCompleted")
    fun completeCorrectPaper(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 作业搜索
     */
    @GET("task/listTimeRange")
    fun getHomeworkAssignContent(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<MutableList<HomeworkAssignSearchBean>>>
    /**
     * 获取作业卷内容列表
     */
    @GET("task/list/job")
    fun getHomeworkList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AssignPaperContentList>>
    /**
     * 设置自动发送
     */
    @POST("sys/task/updateAll2")
    fun setHomeworkAutoAssign(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 发送作业本
     */
    @POST("task/group/insertJob")
    fun commitHomework(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 发送作业卷
     */
    @POST("task/group/insertSubJob")
    fun commitHomeworkReel(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 布置详情
     */
    @GET("homework/info/list")
    fun assignHomeworkDetails(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HomeworkAssignDetailsList>>
    /**
     * 删除布置信息
     */
    @POST("homework/info/delete")
    fun deleteAssignDetails(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 发送信息
     */
    @POST("message/inform/insertTeacher")
    fun sendMessage(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 删除消息
     */
    @POST("message/inform/delete")
    fun deleteMessages(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 消息列表
     */
    @GET("message/inform/list")
    fun getMessages(@QueryMap map: HashMap<String, Any>): Observable<BaseResult<Message>>
    /**
     * 获取任课课程表
     */
    @GET("course/info")
    fun getClassSchedule(): Observable<BaseResult<String>>
    /**
     * 应用列表
     */
    @GET("application/list")
    fun getApks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AppList>>
    /**
     * 应用列表
     */
    @GET("application/types")
    fun getApkTypes(): Observable<BaseResult<CommonData>>
    /**
     * 购买apk
     */
    @POST("buy/book/createOrder")
    fun onBuy(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 壁纸
     */
    @GET("font/draw/list")
    fun getWallpaperList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<WallpaperList>>
    /**
     * 台历列表
     */
    @GET("calendar/list")
    fun getCalenderList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<CalenderList>>

    /**
     * 获取考试批改列表
     */
    @GET("school/exam/list")
    fun getExamCorrectList(): Observable<BaseResult<ExamCorrectList>>
    /**
     * 班级批改完成
     */
    @POST("school/exam/finish")
    fun onExamCorrectComplete(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取考试列表
     */
    @GET("school/exam/teacher")
    fun getExamList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ExamList>>
    /**
     * 发送班级
     */
    @POST("school/exam/sendToStudent")
    fun sendExamCorrectClass(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 删除考试列表
     */
    @POST("school/exam/delete")
    fun deleteExamList(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取班级学生
     */
    @GET("school/exam/job")
    fun getExamClass(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ExamClassUserList>>
    /**
     * 批改完成
     */
    @POST("school/exam/update")
    fun onExamCorrectUserComplete(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取考试所有学生成绩
     */
    @GET("school/exam/allJob")
    fun getExamScores(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ExamRankList>>


    /**
     * 讲义列表
     */
    @GET("teaching/list")
    fun getHandoutList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HandoutList>>
    /**
     * 讲义分类
     */
    @GET("teaching/category")
    fun getHandoutTypes(): Observable<BaseResult<List<String>>>
    /**
     * 删除讲义
     */
    @POST("teaching/delete")
    fun onDeleteHandout(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 修改讲义
     */
    @POST("teaching/updateTitle")
    fun onEditHandout(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 定置讲义
     */
    @POST("teaching/updateTop")
    fun onTopHandout(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
}