package com.bll.lnkteacher

import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.model.exam.ExamClassUserList
import com.bll.lnkteacher.mvp.model.exam.ExamCorrectList
import com.bll.lnkteacher.mvp.model.exam.ExamList
import com.bll.lnkteacher.mvp.model.exam.ExamRankList
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetails
import com.bll.lnkteacher.mvp.model.testpaper.*
import com.bll.lnkteacher.net.BaseResult
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*


interface APIService{

    /**
     * 获取下载token
     */
    @POST("file/token")
    fun getQiniuToken(): Observable<BaseResult<String>>
    @POST("cloud/data/insert")
    fun cloudUpload(@Body requestBody: RequestBody): Observable<BaseResult<MutableList<Int>>>
    /**
     * 获取更新
     */
    @GET("app/info/one?type=2")
    fun onAppUpdate(): Observable<BaseResult<AppUpdateBean>>
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
     * //获取学豆列表
     */
    @GET("wallets/list")
    fun getSMoneyList(@QueryMap map: HashMap<String,String>): Observable<BaseResult<AccountList>>
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
    fun getTextBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
    /**
     * 教材参考列表
     */
    @GET("book/list")
    fun getHomeworkBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
    /**
     * 教材参考列表
     */
    @GET("book/lib/list")
    fun getTeachingBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
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
     * 创建班群
     */
    @POST("class/group/join")
    fun addClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 班群列表
     */
    @GET("class/page")
    fun getListClassGroup(): Observable<BaseResult<ClassGroupList>>

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
     * 获取班群学生列表
     */
    @POST("class/classInfo")
    fun getClassGroupUserList(@Body requestBody: RequestBody): Observable<BaseResult<List<ClassGroupUser>>>
    /**
     * 获取班群所有子群
     */
    @GET("class/group/listSubGroup")
    fun getClassGroupChildList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<ClassGroup>>>
    /**
     * 老师创建子群
     */
    @POST("class/group/createOther")
    fun createClassGroupUserGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 老师子群添加学生
     */
    @POST("class/group/joinSubGroup")
    fun addClassGroupUserGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
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
     * 获取考卷分类
     */
    @GET("common/type/list")
    fun getPaperType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TypeList>>
    /**
     * 删除分类
     */
    @POST("common/type/delete")
    fun deletePaperType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 删除作业分类
     */
    @POST("common/type/deleteHomework")
    fun deleteHomeworkType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 添加考卷分类成功
     */
    @POST("common/type/insert")
    fun addPaperType(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取考卷内容列表
     */
    @GET("task/list/exam")
    fun getPaperList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AssignContent>>

    /**
     * 查看考卷列表
     */
    @GET("task/image/listExamJob")
    fun getPaperImages(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AssignContent>>
    /**
     * 删除考卷
     */
    @POST("task/delete")
    fun deletePaper(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 布置考卷
     */
    @POST("task/group/insertExamJob")
    fun sendPapers(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 获取考卷订单列表
     */
    @GET("task/group/list")
    fun getPaperCorrectList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<CorrectList>>
    /**
     * 删除考卷批改列表
     */
    @POST("task/group/delete")
    fun deletePaperCorrectList(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 查看考卷图片
     */
    @GET("exam/change/image")
    fun getPaperCorrectImages(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<ContentListBean>>>
    /**
     * 获取班级同学提交考卷列表
     */
    @GET("exam/change/edit")
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
     * 发送单个班级
     */
    @POST("exam/change/sendStudent")
    fun sendPaperCorrectClass(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 发送整个群
     */
    @POST("task/group/sendStudent")
    fun sendPaperCorrectGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取作业卷内容列表
     */
    @GET("task/list/job")
    fun getHomeworkList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AssignContent>>
    /**
     * 获取作业卷内容图片
     */
    @GET("task/image/listJob")
    fun getHomeworkImages(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AssignContent>>
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
    fun assignHomeworkDetails(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<HomeworkAssignDetails>>
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
     * 我的讲义
     */
    @GET("teaching/list")
    fun getHandouts(@QueryMap map: HashMap<String, Any>): Observable<BaseResult<HandoutsList>>

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
     * 获取考试原图
     */
    @GET("school/exam/info")
    fun getExamImage(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<ExamList.ExamBean>>
    /**
     * 获取班级学生试卷
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

}