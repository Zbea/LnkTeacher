package com.bll.lnkteacher.ui.activity.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.dialog.SchoolSelectDialog
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.presenter.RegisterOrFindPsdPresenter
import com.bll.lnkteacher.mvp.presenter.SmsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.ISmsView
import com.bll.lnkteacher.utils.MD5Utils
import com.bll.lnkteacher.utils.NetworkUtil
import com.bll.lnkteacher.utils.SPUtil
import com.bll.lnkteacher.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_account_register.btn_code
import kotlinx.android.synthetic.main.ac_account_register.btn_register
import kotlinx.android.synthetic.main.ac_account_register.ed_code
import kotlinx.android.synthetic.main.ac_account_register.ed_name
import kotlinx.android.synthetic.main.ac_account_register.ed_password
import kotlinx.android.synthetic.main.ac_account_register.ed_phone
import kotlinx.android.synthetic.main.ac_account_register.ed_user
import kotlinx.android.synthetic.main.ac_account_register.ll_name
import kotlinx.android.synthetic.main.ac_account_register.ll_school
import kotlinx.android.synthetic.main.ac_account_register.tv_course_btn
import kotlinx.android.synthetic.main.ac_account_register.tv_password
import kotlinx.android.synthetic.main.ac_account_register.tv_school


class AccountRegisterActivity : BaseAppCompatActivity(), IContractView.IRegisterOrFindPsdView,ISmsView{

    private lateinit var presenter:RegisterOrFindPsdPresenter
    private lateinit var smsPresenter: SmsPresenter
    private var countDownTimer: CountDownTimer? = null
    private var flags = 0
    private var school=0
    private var schoolSelectDialog:SchoolSelectDialog?=null

    override fun onSms() {
        showToast("发送验证码成功")
        showCountDownView()
    }

    override fun onListSchools(list: MutableList<SchoolBean>) {
        selectorSchool(list)
    }
    override fun onRegister() {
        showToast("注册成功")
        setIntent()
    }
    override fun onFindPsd() {
        showToast("修改密码成功")
        setIntent()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_register
    }

    override fun initData() {
        fetchCommonData()
        initChangeScreenData()
        flags=intent.flags
        if (flags==0){
            if (!NetworkUtil.isNetworkConnected()){
                showToast("网络连接失败")
            }
        }
    }

    override fun initChangeScreenData() {
        presenter= RegisterOrFindPsdPresenter(this,getCurrentScreenPos())
        smsPresenter= SmsPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        when (flags) {
            1 -> {
                setPageTitle("修改密码")
                disMissView(ll_name,ll_school)
                ed_user.setText(SPUtil.getString("account"))
                ed_phone.setText(mUser?.telNumber)
                tv_password.text="修改密码"
                btn_register.text="提交"
            }
            else -> {
                setPageTitle("注册账号")
            }
        }

        btn_code.setOnClickListener {
            val phone=ed_phone.text.toString().trim()
            if (!ToolUtils.isPhoneNum(phone)) {
                showToast(getString(R.string.phone_tip))
                return@setOnClickListener
            }
            smsPresenter.sms(phone)
        }

        tv_course_btn.setOnClickListener {
            PopupRadioList(this, DataBeanManager.popupCourses, tv_course_btn,tv_course_btn.width, 5).builder()
                .setOnSelectListener { item ->
                    tv_course_btn.text = item.name
                }
        }

        tv_school.setOnClickListener {
            mCommonPresenter.getSchool()
        }

        btn_register.setOnClickListener {

            val account=ed_user.text.toString().trim()
            val psd=ed_password.text.toString().trim()
            val name=ed_name.text.toString().trim()
            val phone=ed_phone.text.toString().trim()
            val code=ed_code.text.toString().trim()
            val course=tv_course_btn.text.toString().trim()
            val role=1

            if (psd.isEmpty()) {
                showToast("请输入密码")
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                showToast("请输入电话号码")
                return@setOnClickListener
            }

            if (code.isEmpty()) {
                showToast("请输入验证码")
                return@setOnClickListener
            }

            if (!ToolUtils.isLetterOrDigit(psd, 6, 20)) {
                showToast(getString(R.string.psw_tip))
                return@setOnClickListener
            }

            if (!ToolUtils.isPhoneNum(phone)) {
                showToast(getString(R.string.phone_tip))
                return@setOnClickListener
            }
            when (flags) {
                0 -> {
                    if (account.isEmpty()) {
                        showToast("请输入用户名")
                        return@setOnClickListener
                    }
                    if (name.isEmpty()) {
                        showToast("请输入姓名")
                        return@setOnClickListener
                    }
                    if (!ToolUtils.isLetterOrDigit(account, 4, 12)) {
                        showToast(getString(R.string.user_tip))
                        return@setOnClickListener
                    }
                    if (course.isEmpty()) {
                        showToast("请选择科目")
                        return@setOnClickListener
                    }
                    if (school==0){
                        showToast("请选择学校")
                        return@setOnClickListener
                    }

                    val map=HashMap<String,Any>()
                    map["account"]=account
                    map["password"]=MD5Utils.digest(psd)
                    map["nickname"]=name
                    map["code"]=code
                    map["telNumber"]=phone
                    map["schoolId"]=school
                    map["subjectName"]=course
                    map["role"]=role
                    presenter.register(map)
                }
                1 -> {
                    if (account.isEmpty()) {
                        showToast("请输入用户名")
                        return@setOnClickListener
                    }
                    presenter.findPsd(role,account,MD5Utils.digest(psd),phone, code)
                }
            }

        }

    }

    //验证码倒计时刷新ui
    private fun showCountDownView() {
        btn_code.isEnabled = false
        btn_code.isClickable = false
        countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onFinish() {
                runOnUiThread {
                    btn_code.isEnabled = true
                    btn_code.isClickable = true
                    btn_code.text = "获取验证码"
                }

            }
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                runOnUiThread {
                    btn_code.text = "${millisUntilFinished / 1000}s"
                }
            }
        }.start()

    }



    private fun setIntent(){
        val intent = Intent()
        intent.putExtra("user", ed_user.text.toString())
        intent.putExtra("psw", ed_password.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * 选择学校
     */
    private fun selectorSchool(schools:MutableList<SchoolBean>){
        if (schoolSelectDialog==null){
            schoolSelectDialog=SchoolSelectDialog(this,schools).builder()
            schoolSelectDialog?.setOnDialogClickListener{
                school=it.id
                tv_school.text=it.name
            }
        }
        else{
            schoolSelectDialog?.show()
        }
    }

    override fun onRefreshData() {
        fetchCommonData()
    }
}
