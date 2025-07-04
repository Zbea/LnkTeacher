package com.bll.lnkteacher.base

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MyApplication
import com.bll.lnkteacher.dialog.AppUpdateDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.AppUpdateBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.SystemUpdateInfo
import com.bll.lnkteacher.mvp.presenter.CloudUploadPresenter
import com.bll.lnkteacher.mvp.presenter.QiniuPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.AppUtils
import com.bll.lnkteacher.utils.DeviceUtil
import com.bll.lnkteacher.utils.FileDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.ToolUtils
import com.google.gson.Gson
import com.htfy.params.ServerParams
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.common_fragment_title.tv_grade
import org.json.JSONObject


abstract class BaseMainFragment : BaseFragment(), IContractView.ICloudUploadView,IContractView.IQiniuView {

    val mQiniuPresenter= QiniuPresenter(this)
    var mCloudUploadPresenter= CloudUploadPresenter(this)
    var popGrades= mutableListOf<PopupBean>()
    var appUpdateDialog:AppUpdateDialog?=null

    override fun onToken(token: String) {
        onUpload(token)
    }

    override fun onSuccessCloudUpload(cloudIds: MutableList<Int>?) {
        uploadSuccess(cloudIds)
    }

    override fun initView() {
        tv_grade.setOnClickListener {
            PopupRadioList(requireActivity(), popGrades, tv_grade,tv_grade.width,  5).builder().setOnSelectListener { item ->
                if (grade!=item.id){
                    tv_grade?.text=item.name
                    grade=item.id
                    onGradeSelectorEvent()
                }
            }
        }
    }

    /**
     * 初始化时，刷新年级（根据班群年级）
     */
    fun setGradeStr(){
        if (grade==0){
            grade=DataBeanManager.getClassGroupsGrade()
            popGrades = DataBeanManager.popupGrades
            if (grade>0){
                tv_grade.text= DataBeanManager.getGradeStr(grade)
                popGrades = DataBeanManager.popupGrades(grade)
                onGradeSelectorEvent()
            }
        }
    }

    /**
     * 年级选择事件
     */
    open fun onGradeSelectorEvent(){

    }

    /**
     * 常用更新数据
     */
    fun onCheckUpdate() {
        if (NetworkUtil.isNetworkConnected()) {
            mCommonPresenter.getCommon()
            checkAppUpdate()
            checkSystemUpdate()
        }
    }

    /**
     * 检查系统更新
     */
    private fun checkSystemUpdate(){
        val url= Constants.RELEASE_BASE_URL+"Device/CheckUpdate"

        val  jsonBody = JSONObject()
        jsonBody.put(Constants.SN, DeviceUtil.getOtaSerialNumber())
        jsonBody.put(Constants.KEY, ServerParams.getInstance().GetHtMd5Key(DeviceUtil.getOtaSerialNumber()))
        jsonBody.put(Constants.VERSION_NO, DeviceUtil.getOtaProductVersion())

        val  jsonObjectRequest= JsonObjectRequest(Request.Method.POST,url,jsonBody, {
            showLog(it.toString())
            val code= it.optInt("Code")
            val jsonObject=it.optJSONObject("Data")
            if (code==200&&jsonObject!=null){
                val item= Gson().fromJson(jsonObject.toString(),SystemUpdateInfo::class.java)
                requireActivity().runOnUiThread {
                    AppUpdateDialog(requireActivity(),2,item).builder()
                }
            }
        },null)
        MyApplication.requestQueue?.add(jsonObjectRequest)
    }

    /**
     * 检查应用更新
     */
    private fun checkAppUpdate(){
        val url=Constants.URL_BASE+"app/info/one?type=2"

        val  jsonObjectRequest= StringRequest(Request.Method.GET,url, {
            val jsonObject= JSONObject(it)
            val code= jsonObject.optInt("code")
            val dataString=jsonObject.optString("data")
            val item= Gson().fromJson(dataString,AppUpdateBean::class.java)
            if (code==0){
                if (item.versionCode > AppUtils.getVersionCode(requireActivity())) {
                    requireActivity().runOnUiThread {
                        downLoadAPP(item)
                    }
                }
            }
        },null)
        MyApplication.requestQueue?.add(jsonObjectRequest)
    }

    /**
     * 下载应用
     */
    private fun downLoadAPP(bean: AppUpdateBean) {
        val targetFileStr = FileAddress().getLauncherPath()
        if (FileUtils.isExist(targetFileStr)){
            AppUtils.installApp(requireActivity(), targetFileStr)
        }
        else{
            if (appUpdateDialog==null||appUpdateDialog?.isShow()==false){
                appUpdateDialog= AppUpdateDialog(requireActivity(), 1, bean).builder()
                FileDownManager.with(requireActivity()).create(bean.downloadUrl).setPath(targetFileStr).startSingleTaskDownLoad(object :
                    FileDownManager.SingleTaskCallBack {
                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        if (task != null && task.isRunning) {
                            requireActivity().runOnUiThread {
                                val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M") + "/" +
                                        ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                                appUpdateDialog?.setUpdateBtn(s)
                            }
                        }
                    }
                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                    override fun completed(task: BaseDownloadTask?) {
                        appUpdateDialog?.dismiss()
                        AppUtils.installApp(requireActivity(), targetFileStr)
                    }
                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        appUpdateDialog?.dismiss()
                    }
                })
            }
        }
    }


    /**
     * 开始上传
     */
    open fun onUpload(token: String){

    }
    /**
     * 上传成功(书籍云id) 上传成功后删掉重复上传的数据
     */
    open fun uploadSuccess(cloudIds: MutableList<Int>?){
        if (!cloudIds.isNullOrEmpty())
        {
            mCloudUploadPresenter.deleteCloud(cloudIds)
        }
    }
}
