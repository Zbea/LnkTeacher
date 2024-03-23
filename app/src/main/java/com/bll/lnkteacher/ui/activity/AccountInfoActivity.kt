package com.bll.lnkteacher.ui.activity

import android.annotation.SuppressLint
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.*
import com.bll.lnkteacher.mvp.model.PrivacyPassword
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.presenter.AccountInfoPresenter
import com.bll.lnkteacher.mvp.presenter.SchoolPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.ISchoolView
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.*
import org.greenrobot.eventbus.EventBus

class AccountInfoActivity:BaseActivity(), IContractView.IAccountInfoView,ISchoolView {

    private lateinit var mSchoolPresenter:SchoolPresenter
    private lateinit var presenter:AccountInfoPresenter
    private var nickname=""
    private var school=0
    private var schoolBean: SchoolBean?=null
    private var schools= mutableListOf<SchoolBean>()
    private var privacyPassword:PrivacyPassword?=null
    private var schoolSelectDialog:SchoolSelectDialog?=null

    override fun onLogout() {
    }

    override fun onEditNameSuccess() {
        showToast("修改姓名成功")
        mUser?.nickname=nickname
        tv_name.text = nickname
    }

    override fun onEditSchool() {
        mUser?.schoolId = schoolBean?.id
        mUser?.schoolProvince=schoolBean?.province
        mUser?.schoolCity=schoolBean?.city
        mUser?.schoolArea=schoolBean?.area
        mUser?.schoolName=schoolBean?.schoolName
        tv_province_str.text = schoolBean?.province
        tv_city.text = schoolBean?.city
        tv_school.text = schoolBean?.schoolName
        tv_area.text = schoolBean?.area
    }

    override fun onListSchools(list: MutableList<SchoolBean>) {
        schools=list
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        initChangeScreenData()
        privacyPassword=MethodManager.getPrivacyPassword()
        mSchoolPresenter.getSchool()
    }

    override fun initChangeScreenData() {
        mSchoolPresenter=SchoolPresenter(this,getCurrentScreenPos())
        presenter=AccountInfoPresenter(this,getCurrentScreenPos())
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setPageTitle("我的账户")

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text =  telNumber.substring(0,3)+"****"+telNumber.substring(7,11)
            tv_course_str.text=subjectName
            tv_province_str.text = schoolProvince
            tv_city.text = schoolCity
            tv_school.text = schoolName
            tv_area.text = schoolArea
        }

        if (privacyPassword!=null){
            showView(tv_check_pad)
            if (privacyPassword?.isSet == true){
                btn_psd_check.text="取消密码"
            }
            else{
                btn_psd_check.text="设置密码"
            }
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_edit_school.setOnClickListener {
            editSchool()
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent("确认退出登录？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    MethodManager.logout(this@AccountInfoActivity)
                }
            })
        }

        btn_psd_check.setOnClickListener {
            setPassword()
        }

    }

    /**
     * 设置查看密码
     */
    private fun setPassword(){
        if (privacyPassword==null){
            PrivacyPasswordCreateDialog(this).builder().setOnDialogClickListener{
                privacyPassword=it
                showView(tv_check_pad)
                btn_psd_check.text="取消密码"
                MethodManager.savePrivacyPassword(privacyPassword)
                EventBus.getDefault().post(Constants.PRIVACY_PASSWORD_EVENT)
            }
        }
        else{
            PrivacyPasswordDialog(this).builder()?.setOnDialogClickListener{
                privacyPassword?.isSet=!privacyPassword?.isSet!!
                btn_psd_check.text=if (privacyPassword?.isSet==true) "取消密码" else "设置密码"
                MethodManager.savePrivacyPassword(privacyPassword)
                EventBus.getDefault().post(Constants.PRIVACY_PASSWORD_EVENT)
            }
        }

    }

    /**
     * 修改名称
     */
    private fun editName(){
        InputContentDialog(this,tv_name.text.toString()).builder()
            .setOnDialogClickListener { string ->
                nickname = string
                presenter.editName(nickname)
            }
    }

    /**
     * 修改学校
     */
    private fun editSchool() {
        if (schoolSelectDialog==null){
            schoolSelectDialog=SchoolSelectDialog(this,schools).builder()
            schoolSelectDialog?.setOnDialogClickListener {
                school=it.id
                if (school==mUser?.schoolId)
                    return@setOnDialogClickListener
                presenter.editSchool(it.id)
                for (item in schools){
                    if (item.id==school)
                        schoolBean=item
                }
            }
        }
        else{
            schoolSelectDialog?.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mUser?.let { SPUtil.putObj("user", it) }
    }

}