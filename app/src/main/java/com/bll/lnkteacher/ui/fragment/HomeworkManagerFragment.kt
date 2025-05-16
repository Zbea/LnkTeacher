package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.HomeworkCreateDialog
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.ui.fragment.homework.HomeworkAssignFragment
import com.bll.lnkteacher.ui.fragment.homework.HomeworkCorrectFragment
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import kotlinx.android.synthetic.main.common_fragment_title.tv_grade

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

        setTitle(DataBeanManager.getIndexRightData()[1].name)
        showView(iv_manager,tv_grade)

        iv_manager?.setOnClickListener {
            showPopView()
        }

        homeworkPopBeans.add(PopupBean(0, getString(R.string.teaching_pop_assign_details)))
        homeworkPopBeans.add(PopupBean(1, getString(R.string.teaching_pop_create_book)))
        homeworkPopBeans.add(PopupBean(2, getString(R.string.teaching_pop_create_reel)))

        homeworkAssignFragment = HomeworkAssignFragment()
        homeworkCorrectFragment = HomeworkCorrectFragment()
        switchFragment(lastFragment, homeworkAssignFragment)

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
                setImageManager(R.mipmap.icon_manager)
                switchFragment(lastFragment, homeworkAssignFragment)
            }

            1 -> {
                disMissView(iv_manager)
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
        if (grade==0)
        {
            showToast("请选择年级")
            return
        }
        PopupClick(requireActivity(), homeworkPopBeans, iv_manager,  10).builder()
            .setOnSelectListener { item ->
                when(item.id){
                    0->{
                        homeworkAssignFragment?.showAssignDetails()
                    }
                    1->{
                        showCreateHomeworkName("输入作业本名称", item.id)
                    }
                    2->{
                        showCreateHomeworkName("输入作业卷名称", item.id)
                    }
                }
            }
    }

    /**
     * 新增作业本
     */
    private fun showCreateHomeworkName(hint: String,typeId:Int) {
        HomeworkCreateDialog(requireContext(),grade, hint).builder()
            .setOnDialogClickListener { str,ids ->
                var subtype=2
                var typeName="作业本"
                if (typeId==2){
                    subtype=1
                    typeName="作业卷"
                }
                val homeworkType = TypeBean()
                homeworkType.name = str+typeName
                homeworkType.subType = typeId
                homeworkAssignFragment?.addHomeworkType(homeworkType,subtype,ids)
            }

    }

    override fun onGradeSelectorEvent() {
        homeworkAssignFragment?.changeGrade(grade)
        homeworkCorrectFragment?.changeGrade(grade)
    }

    override fun onRefreshData() {
        lazyLoad()
        homeworkAssignFragment?.onRefreshData()
        homeworkCorrectFragment?.onRefreshData()
    }

}