package com.bll.lnkteacher.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.CityBean
import com.bll.lnkteacher.mvp.presenter.RegisterOrFindPsdPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.MD5Utils
import com.bll.lnkteacher.utils.ToolUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_account_register.*


/**
 *  //2. 帐号规则 4 - 12 位字母、数字
//3. 密码规则 6 - 20 位字母、数字
//4. 姓名规则 2 - 5 位中文
//5. 手机号码规则 11 位有效手机号
//6. 验证码规则数字即可
 */
class AccountRegisterActivity : BaseActivity(),
    IContractView.IRegisterOrFindPsdView {

    private val presenter= RegisterOrFindPsdPresenter(this)
    private var countDownTimer: CountDownTimer? = null
    private var flags = 0
    private var cityBean: CityBean?=null
    private var provinces= mutableListOf<String>()
    private var citys= mutableListOf<String>()
    private var cityAdapter:SpinnerAdapter?=null
    private var provinceStr=""
    private var cityStr=""


    override fun onSms() {
        showToast("发送验证码成功")
        showCountDownView()
    }

    override fun onRegister() {
        showToast("注册成功")
        setIntent()
    }
    override fun onFindPsd() {
        showToast("设置密码成功")
        setIntent()
    }

    override fun onEditPsd() {
        showToast("修改密码成功")
        finish()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_register
    }

    override fun initData() {
        flags=intent.flags

        val citysStr = FileUtils.readFileContent(resources.assets.open("city.json"))
        cityBean = Gson().fromJson(citysStr, CityBean::class.java)

        for (item in cityBean?.provinces!!){
            provinces.add(item.provinceName)
        }

        for (item in cityBean?.provinces!![0].citys){
            citys.add(item.citysName)
        }
    }

    override fun initView() {

        if (flags==2){
            setPageTitle("修改密码")
            ll_name.visibility= View.GONE
            ll_user.visibility=View.GONE
            ll_school.visibility=View.GONE
            btn_register.text="提交"
        }
        else if (flags==1){
            setPageTitle("找回密码")
            ll_name.visibility= View.GONE
            ll_school.visibility=View.GONE
            btn_register.text="提交"
        }
        else{
            setPageTitle("注册账号")
        }

        sp_province.dropDownWidth=DP2PX.dip2px(this,115f)
        sp_province.setPopupBackgroundResource(R.drawable.bg_gray_stroke_5dp_corner)
        val mAdapter=SpinnerAdapter(this,provinces)
        sp_province.adapter=mAdapter
        sp_province.setSelection(0)
        sp_province.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                provinceStr=provinces[p2]
                citys.clear()
                for (item in cityBean?.provinces!![p2].citys){
                    citys.add(item.citysName)
                }
                cityAdapter?.notifyDataSetChanged()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        sp_city.dropDownWidth=DP2PX.dip2px(this,115f)
        sp_city.setPopupBackgroundResource(R.drawable.bg_gray_stroke_5dp_corner)
        cityAdapter=SpinnerAdapter(this,citys)
        sp_province.setSelection(0)
        sp_city.adapter=cityAdapter
        sp_city.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                cityStr=citys[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        btn_code.setOnClickListener {
            val phone=ed_phone.text.toString().trim()
            if (!ToolUtils.isPhoneNum(phone)) {
                showToast(getString(R.string.phone_tip))
                return@setOnClickListener
            }
            presenter.sms(phone)
        }

        btn_register.setOnClickListener {

            val account=ed_user.text.toString().trim()
            val psd=ed_password.text.toString().trim()
            val name=ed_name.text.toString().trim()
            val phone=ed_phone.text.toString().trim()
            val code=ed_code.text.toString().trim()
            val course=et_course.text.toString().trim()
            val area=et_area.text.toString().trim()
            val schoolName=et_school_name.text.toString().trim()
            val role=1

            if (psd.isNullOrEmpty()) {
                showToast("请输入密码")
                return@setOnClickListener
            }
            if (phone.isNullOrEmpty()) {
                showToast("请输入电话号码")
                return@setOnClickListener
            }

            if (code.isNullOrEmpty()) {
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
            if (flags==0){

                if (account.isNullOrEmpty()) {
                    showToast("请输入用户名")
                    return@setOnClickListener
                }
                if (name.isNullOrEmpty()) {
                    showToast("请输入姓名")
                    return@setOnClickListener
                }
                if (!ToolUtils.isLetterOrDigit(account, 4, 12)) {
                    showToast(getString(R.string.user_tip))
                    return@setOnClickListener
                }
                if (course.isNullOrEmpty()) {
                    showToast("请输入科目")
                    return@setOnClickListener
                }
                if (provinceStr.isNullOrEmpty()||cityStr.isNullOrEmpty()) {
                    showToast("请选择省市")
                    return@setOnClickListener
                }
                if (area.isNullOrEmpty()) {
                    showToast("请输入学校地址")
                    return@setOnClickListener
                }
                if (schoolName.isNullOrEmpty()) {
                    showToast("请输入学校名称")
                    return@setOnClickListener
                }

                val map=HashMap<String,String>()
                map["account"]=account
                map["password"]=MD5Utils.digest(psd)
                map["nickname"]=name
                map["code"]=code
                map["telNumber"]=phone
                map["addr"]= "$provinceStr,$cityStr"
                map["addrInfo"]=area
                map["schoolName"]=schoolName
                map["subjectName"]=course

                presenter.register(map)
            }
            else if (flags==1){
                if (account.isNullOrEmpty()) {
                    showToast("请输入用户名")
                    return@setOnClickListener
                }
                presenter.findPsd(role.toString(),account,MD5Utils.digest(psd),phone, code)
            }
            else{
                presenter.editPsd(MD5Utils.digest(psd),code)
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


    class SpinnerAdapter(val context: Context, val list: List<String>) : BaseAdapter() {

        override fun getCount(): Int {
            return list.size
        }
        override fun getItem(position: Int): Any {
            return list[position]
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
            val view= LayoutInflater.from(context).inflate(R.layout.item_dropdown,null)
            val tvName=view.findViewById<TextView>(R.id.tv_name)
            tvName.text=list[position]
            tvName.setSingleLine()
            tvName.ellipsize= TextUtils.TruncateAt.END
            tvName.height= DP2PX.dip2px(context,40f)
            return view
        }
    }


}
