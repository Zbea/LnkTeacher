package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.TestPaperGrade
import com.bll.lnkteacher.ui.adapter.TestPaperGradeAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.widget.SpaceGridItemDecoGrade
import kotlinx.android.synthetic.main.common_title.*
import kotlinx.android.synthetic.main.fragment_teaching_testpaper_assign.*

class TestPaperGradeActivity:BaseActivity(){

    private var popClasss= mutableListOf<PopupBean>()
    private var mAdapter: TestPaperGradeAdapter?=null
    private var datas= mutableListOf<TestPaperGrade>()

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_grade
    }

    override fun initData() {
        for (i in 0..50){
            var item=TestPaperGrade()
            item.name="张三"
            item.score=98.5
            item.rank=1
            item.className="三年级一班"
            datas.add(item)
        }
        popClasss= DataBeanManager.popClassGroups
    }

    override fun initView() {

        initRecyclerView()
        showView(tv_class)

        tv_class.setOnClickListener {
            showClassGroup()
        }
    }


    private fun initRecyclerView(){

        mAdapter = TestPaperGradeAdapter(R.layout.item_testpaper_grade,datas)
        rv_list.layoutManager = GridLayoutManager(this,2)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDecoGrade(DP2PX.dip2px(this,40f),0))

    }


    private var popWindowClass:PopupRadioList?=null
    /**
     * 班级选择
     */
    private fun showClassGroup(){
        if (popWindowClass==null){
            popWindowClass=PopupRadioList(this, popClasss, tv_class,tv_class.width,  5).builder()
            popWindowClass?.setOnSelectListener { item->

            }
        }
        else{
            popWindowClass?.show()
        }
    }

}