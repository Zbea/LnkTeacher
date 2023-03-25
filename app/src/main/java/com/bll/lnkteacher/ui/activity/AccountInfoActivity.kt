package com.bll.lnkteacher.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.mvp.presenter.AccountInfoPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.ActivityManager
import com.bll.lnkteacher.utils.SPUtil
import kotlinx.android.synthetic.main.ac_account_info.*

class AccountInfoActivity:BaseActivity(), IContractView.IAccountInfoView {

    private val presenter=AccountInfoPresenter(this)
    private var nickname=""

    override fun onLogout() {
        SPUtil.putString("token", "")
        SPUtil.removeObj("user")
        Handler().postDelayed(Runnable {
            startActivity(Intent(this, AccountLoginActivity::class.java))
            ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
        }, 500)
    }

    override fun onEditNameSuccess() {
        showToast("修改姓名成功")
        mUser?.nickname=nickname
        tv_name.text = nickname
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {

    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setPageTitle("我的账户")

        mUser?.apply {
            tv_user.text = account
            tv_name.text = nickname
            tv_phone.text =  telNumber.substring(0,3)+"****"+telNumber.substring(7,11)
            tv_school.text=schoolName
        }

        btn_edit_psd.setOnClickListener {
            startActivity(Intent(this,AccountRegisterActivity::class.java).setFlags(2))
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent("确认退出登录？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    SPUtil.putString("token", "")
                    SPUtil.removeObj("user")
                    Handler().postDelayed(Runnable {
                        startActivity(Intent(this@AccountInfoActivity, AccountLoginActivity::class.java))
                        ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
                    }, 500)
                }
            })
        }
    }

    /**
     * 修改名称
     */
    private fun editName(){
        InputContentDialog(this,tv_name.text.toString()).builder()
            ?.setOnDialogClickListener { string ->
            nickname = string
            presenter.editName(nickname)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mUser?.let { SPUtil.putObj("user", it) }
    }

}