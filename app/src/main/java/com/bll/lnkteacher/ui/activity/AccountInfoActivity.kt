package com.bll.lnkteacher.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.SchoolSelectDialog
import com.bll.lnkteacher.mvp.model.SchoolBean
import com.bll.lnkteacher.mvp.presenter.AccountInfoPresenter
import com.bll.lnkteacher.mvp.presenter.SchoolPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.ISchoolView
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.*

class AccountInfoActivity:BaseActivity(), IContractView.IAccountInfoView,ISchoolView {

    private val mSchoolPresenter=SchoolPresenter(this)
    private val presenter=AccountInfoPresenter(this)
    private var nickname=""
    private var school=0
    private var schoolBean: SchoolBean?=null
    private var schools= mutableListOf<SchoolBean>()

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
        mSchoolPresenter.getSchool()
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setPageTitle("我的账户")

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text =  telNumber.substring(0,3)+"****"+telNumber.substring(7,11)
            tv_course.text=subjectName
            tv_province_str.text = schoolProvince
            tv_city.text = schoolCity
            tv_school.text = schoolName
            tv_area.text = schoolArea
        }

        btn_edit_psd.setOnClickListener {
            startActivity(Intent(this,AccountRegisterActivity::class.java).setFlags(2))
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
                    SPUtil.putString("tokenTeacher", "")
                    SPUtil.removeObj("userTeacher")
                    startActivity(Intent(this@AccountInfoActivity, AccountLoginActivity::class.java))
                    ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
                }
            })
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
        SchoolSelectDialog(this,schools).builder().setOnDialogClickListener {
            school=it
            if (school==mUser?.schoolId)
                return@setOnDialogClickListener
            presenter.editSchool(it)
            for (item in schools){
                if (item.id==school)
                    schoolBean=item
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mUser?.let { SPUtil.putObj("userTeacher", it) }
    }

}