package com.bll.lnkteacher

import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.model.group.ClassGroupList
import com.bll.lnkteacher.mvp.model.group.ClassGroupUser
import com.bll.lnkteacher.mvp.model.group.Group
import com.bll.lnkteacher.mvp.model.group.GroupUser
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetails
import com.bll.lnkteacher.mvp.model.testpaper.*
import com.bll.lnkteacher.net.BaseResult
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface APIService{

    /**
     * 文件上传
     */
    @Multipart
    @POST("file/manyUpload")
    fun upload(@Part parts: List<MultipartBody.Part>): Observable<BaseResult<List<String>>>

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
    fun getHomewrokBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
    /**
     * 教材参考列表
     */
    @GET("book/list")
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
    @POST("class/create")
    fun createClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 班群列表
     */
    @GET("class/page")
    fun getListClassGroup(): Observable<BaseResult<ClassGroupList>>
    /**
     * 修改班群名
     */
    @POST("class/updateClassName")
    fun editClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 解散班群
     */
    @POST("class/disbandClass")
    fun dissolveClassGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 老师发送控制删除信息
     */
    @POST("delete/message/send")
    fun sendClassGroupControl(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 获取班群学生列表
     */
    @POST("class/classInfo")
    fun getClassGroupUserList(@Body requestBody: RequestBody): Observable<BaseResult<List<ClassGroupUser>>>
    /**
     * 学生安排职位
     */
    @POST("class/changeJob")
    fun changeJob(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
    /**
     * 学生是否双通
     */
    @POST("class/enableSend")
    fun changeStatus(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 班群学生踢出
     */
    @POST("class/removeStudent")
    fun outClassGroupUser(@Body requestBody: RequestBody): Observable<BaseResult<Any>>


    /**
     * 创建校群、际群
     */
    @POST("group/insert")
    fun createGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 加入校群、际群
     */
    @POST("group/addGroup")
    fun addGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 获取校群、际群列表
     */
    @GET("group/list")
    fun getGroupList(): Observable<BaseResult<MutableList<Group>>>

    /**
     * 加入校群、际群
     */
    @POST("group/quitGroup")
    fun quitGroup(@Body requestBody: RequestBody): Observable<BaseResult<Any>>

    /**
     * 查看校群、际群的班级列表
     */
    @GET("group/classList")
    fun checkGroupUser(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<GroupUser>>>

    /**
     * 获取考卷分类
     */
    @GET("common/type/list")
    fun getPaperType(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TypeList>>
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
     * 得到考卷班群分类信息
     */
    @GET("task/group/types")
    fun getPaperGroupTypes(): Observable<BaseResult<TestPaperGroupType>>
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
    fun getPaperCorrectClassList(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<TestPaperCorrectClass>>
    /**
     * 考试评分
     */
    @GET("task/group/one")
    fun getPaperGrade(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<List<TestPaperGrade>>>
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
     * 发送作业卷
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
     * 购买apk
     */
    @POST("buy/book/createOrder")
    fun buyApk(@Body requestBody: RequestBody): Observable<BaseResult<Any>>
}