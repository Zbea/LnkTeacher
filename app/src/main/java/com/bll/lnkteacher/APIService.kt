package com.bll.lnkteacher

import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.net.BaseResult
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*


interface APIService{

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
     * //获取vip列表
     */
    @GET("wallets/vips/list")
    fun getVipList(@QueryMap map: HashMap<String,String>): Observable<BaseResult<AccountList>>
    /**
     * 提交vip订单
     */
    @POST("wallets/vips/buy/{id}")
    fun postOrderVip(@Path("id") id:String): Observable<BaseResult<AccountOrder>>

    /**
     * 书城列表
     */
    @GET("books")
    fun getBooks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<BookStore>>
    /**
     * 购买书籍
     */
    @GET("books/{id}/buy")
    fun buyBook(@Path("id") id:String): Observable<BaseResult<BookEvent>>
    /**
     * 下载书籍
     */
    @GET("books/{id}/download")
    fun downloadBook(@Path("id") id:String): Observable<BaseResult<BookEvent>>

    /**
     * 应用列表
     */
    @GET("applications")
    fun getApks(@QueryMap map: HashMap<String,Any>): Observable<BaseResult<AppBean>>

    /**
     * 下载软件
     */
    @GET("applications/{id}/download")
    fun downloadApk(@Path("id") id:String): Observable<BaseResult<AppBean>>

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


}