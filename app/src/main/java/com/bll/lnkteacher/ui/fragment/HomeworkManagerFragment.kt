package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.ui.fragment.homework.HomeworkAssignFragment
import com.bll.lnkteacher.ui.fragment.homework.HomeworkCorrectFragment
import kotlinx.android.synthetic.main.common_fragment_title.*

class HomeworkManagerFragment : BaseMainFragment(){

    private var homeworkAssignFragment: HomeworkAssignFragment? = null
    private var homeworkCorrectFragment: HomeworkCorrectFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    private var homeworkPopBeans = mutableListOf<PopupBean>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_homework_manager
    }

    override fun initView() {
        super.initView()

        setTitle(R.string.main_homework_title)
        showView(iv_manager,tv_grade)

        homeworkPopBeans.add(PopupBean(0, getString(R.string.teaching_pop_assign_details), true))
        homeworkPopBeans.add(PopupBean(1, getString(R.string.teaching_pop_create_book), false))
        homeworkPopBeans.add(PopupBean(2, getString(R.string.teaching_pop_create_reel), false))

        homeworkAssignFragment = HomeworkAssignFragment()
        homeworkCorrectFragment = HomeworkCorrectFragment()

        switchFragment(lastFragment, homeworkAssignFragment)

        iv_manager?.setOnClickListener {
            showPopView()
        }

        initTab()
    }

    override fun lazyLoad() {
        setGradeStr()
    }

    private fun initTab() {
        val strs=DataBeanManager.teachingStrs
        for (i in strs.indices){
            itemTabTypes.add(ItemTypeBean().apply {
                title=strs[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        when (position) {
            0 -> {
                showView(iv_manager,tv_grade)
                iv_manager.setImageResource(R.mipmap.icon_manager)
                switchFragment(lastFragment, homeworkAssignFragment)
            }

            1 -> {
                disMissView(iv_manager,tv_grade)
                switchFragment(lastFragment, homeworkCorrectFragment)
            }
        }
        lastPosition = position
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
                ft?.add(R.id.fl_content_homework, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    //顶部弹出pop选择框
    private fun showPopView() {
        PopupClick(requireActivity(), homeworkPopBeans, iv_manager,  10).builder()
            .setOnSelectListener { item ->
                if (grade==0)
                {
                    showToast("请选择年级")
                    return@setOnSelectListener
                }
                when(item.id){
                    0->{
                        homeworkAssignFragment?.showAssignDetails()
                    }
                    1->{
                        showCreateHomeworkName(item.name, item.id)
                    }
                    else->{
                        showCreateHomeworkName(item.name, item.id)
                    }
                }
            }
    }

    /**
     * 新增作业本
     */
    private fun showCreateHomeworkName(hint: String,typeId:Int) {
        InputContentDialog(requireContext(), hint).builder()
            .setOnDialogClickListener { str ->
                var subtype=2
                var typeName=getString(R.string.teaching_book)
                if (typeId==2){
                    subtype=1
                    typeName=getString(R.string.teaching_reel)
                }
                val homeworkType = TypeBean()
                homeworkType.name = str+ typeName
                homeworkType.subType = typeId
                homeworkAssignFragment?.addHomeworkType(homeworkType,subtype)
            }

    }

    override fun onGradeSelectorEvent() {
        homeworkAssignFragment?.changeGrade(grade)
        homeworkCorrectFragment?.changeGrade(grade)
    }

}