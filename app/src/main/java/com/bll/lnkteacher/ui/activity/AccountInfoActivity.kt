package com.bll.lnkteacher.ui.activity

import android.annotation.SuppressLint
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.EditPhoneDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.SchoolSelectDialog
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.presenter.AccountInfoPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_name
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_phone
import kotlinx.android.synthetic.main.ac_account_info.btn_edit_school
import kotlinx.android.synthetic.main.ac_account_info.btn_logout
import kotlinx.android.synthetic.main.ac_account_info.tv_area
import kotlinx.android.synthetic.main.ac_account_info.tv_city
import kotlinx.android.synthetic.main.ac_account_info.tv_course_str
import kotlinx.android.synthetic.main.ac_account_info.tv_name
import kotlinx.android.synthetic.main.ac_account_info.tv_phone
import kotlinx.android.synthetic.main.ac_account_info.tv_province_str
import kotlinx.android.synthetic.main.ac_account_info.tv_school
import kotlinx.android.synthetic.main.ac_account_info.tv_user

class AccountInfoActivity:BaseActivity(), IContractView.IAccountInfoView {

    private lateinit var presenter:AccountInfoPresenter
    private var nickname=""
    private var school=0
    private var schoolBean: SchoolBean?=null
    private var schoolSelectDialog:SchoolSelectDialog?=null
    private var phone=""

    override fun onSms() {
        showToast("短信发送成功")
    }

    override fun onCheckSuccess() {
        editPhone()
    }

    override fun onEditPhone() {
        showToast("修改手机号码成功")
        mUser?.telNumber=phone
        tv_phone.text=phone
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


    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        initChangeScreenData()
    }

    override fun initChangeScreenData() {
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

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_edit_school.setOnClickListener {
            editSchool()
        }

        btn_edit_phone.setOnClickListener {
            presenter.sms(mUser?.telNumber!!)
            InputContentDialog(this,1,"请输入验证码",1).builder().setOnDialogClickListener{
                presenter.checkPhone(it)
            }
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

    }


    private fun editPhone(){
        EditPhoneDialog(this).builder().setOnDialogClickListener(object : EditPhoneDialog.OnDialogClickListener {
            override fun onClick(code: String, phone: String) {
                this@AccountInfoActivity.phone=phone
                presenter.editPhone(code, phone)
            }
            override fun onPhone(phone: String) {
                presenter.sms(phone)
            }
        })
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
            schoolSelectDialog=SchoolSelectDialog(this,DataBeanManager.schools).builder()
            schoolSelectDialog?.setOnDialogClickListener {
                school=it.id
                presenter.editSchool(it.id)
                for (item in DataBeanManager.schools){
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