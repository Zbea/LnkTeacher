package com.bll.lnkteacher.ui.fragment

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.ClassGroupCreateDialog
import com.bll.lnkteacher.dialog.GroupAddDialog
import com.bll.lnkteacher.dialog.GroupCreateDialog
import com.bll.lnkteacher.dialog.PopWindowRadioList
import com.bll.lnkteacher.mvp.model.PopWindowBean
import com.bll.lnkteacher.mvp.presenter.GroupManagerPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.fragment.group.ClassGroupFragment
import com.bll.lnkteacher.ui.fragment.group.GroupFragment
import com.bll.lnkteacher.utils.DP2PX
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.fragment_group_manager.*

class GroupManagerFragment:BaseFragment(),IContractView.IGroupManagerView {

    private val mGroupPresenter=GroupManagerPresenter(this)

    private var classGroupFragment: ClassGroupFragment? = null
    private var groupFragment: GroupFragment? = null
    private var areaFragment: GroupFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    private var groupPops = mutableListOf<PopWindowBean>()
    private var areaPops = mutableListOf<PopWindowBean>()

    override fun onCreateClassGroupSuccess() {
        classGroupFragment?.refreshData()
    }

    override fun onCreateGroupSuccess() {
        if (lastPosition==1){
            groupFragment?.refreshData()
        }
        else{
            areaFragment?.refreshData()
        }
    }

    override fun onAddSuccess() {
        if (lastPosition==1){
            groupFragment?.refreshData()
        }
        else{
            areaFragment?.refreshData()
        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_group_manager
    }

    override fun initView() {
        setTitle("群管理")
        showView(iv_create)

//        groupPops.add(PopWindowBean(0, "创建校群", true))
//        groupPops.add(PopWindowBean(1, "加入校群", false))
//
//        areaPops.add(PopWindowBean(0, "创建际群", true))
//        areaPops.add(PopWindowBean(1, "加入际群", false))

        classGroupFragment = ClassGroupFragment()
        groupFragment = GroupFragment().newInstance(2)
        areaFragment = GroupFragment().newInstance(1)

        switchFragment(lastFragment, classGroupFragment)

        iv_create.setOnClickListener { it: View? ->
            when (lastPosition) {
                0 -> {
                    ClassGroupCreateDialog(requireContext()).builder()
                        ?.setOnDialogClickListener {
                            mGroupPresenter.createClassGroup(it)
                        }
                }
                1 -> {
                    createGroup(2)
                }
                else -> {
                    createGroup(1)
                }
            }
        }

        iv_add.setOnClickListener {
            if (lastPosition==1){
                addGroup(2)
            }
            else{
                addGroup( 1)
            }
        }

        initTab()
    }

    override fun lazyLoad() {
    }

    /**
     * 设置tab
     */
    private fun initTab() {
        val strs= mutableListOf("班群","校群","际群")
        for (i in strs.indices){
            var radioButton = layoutInflater.inflate(R.layout.common_radiobutton, null) as RadioButton
            radioButton.id = i
            radioButton.text = strs[i]
            radioButton.isChecked = i == 0
            var layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                DP2PX.dip2px(activity, 45f))
            layoutParams.marginEnd = if (i == strs.size-1) 0 else DP2PX.dip2px(activity, 44f)
            radioButton.layoutParams = layoutParams
            rg_group.addView(radioButton)
        }

        rg_group.setOnCheckedChangeListener { radioGroup, i ->
            val position = i
            when (position) {
                0 -> {
                    disMissView(iv_add)
                    switchFragment(lastFragment, classGroupFragment)
                }

                1 -> {
                    showView(iv_add)
                    switchFragment(lastFragment, groupFragment)
                }

                2 -> {
                    showView(iv_add)
                    switchFragment(lastFragment, areaFragment)
                }

            }
            lastPosition = position!!
        }

    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = activity?.supportFragmentManager
            val ft = fm?.beginTransaction()

            if (!to?.isAdded!!) {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.add(R.id.fl_content_group, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    //顶部弹出pop选择框
    private fun showPopView(pops:MutableList<PopWindowBean>) {

        PopWindowRadioList(requireContext(), pops, iv_manager,  10).builder()
            ?.setOnSelectListener { item ->
                when (item.id) {
                    0 -> createGroup(if (lastPosition==1) 2 else 1)
                    1 -> addGroup(if (lastPosition==1) 2 else 1)
                }
            }
    }

    /**
     * 创建校群、际群
     */
    private fun createGroup(type:Int){
        GroupCreateDialog(requireContext(),type).builder()?.setOnDialogClickListener{ str, classIds->
            mGroupPresenter.createGroup(str,type,classIds)
        }
    }

    /**
     * 加入校群、际群
     */
    private fun addGroup(type: Int){
        GroupAddDialog(requireContext(),type).builder()?.setOnDialogClickListener { str, classIds ->
            mGroupPresenter.addGroup(str, type, classIds)
        }
    }

}