package com.bll.lnkteacher.ui.activity.teaching

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.HomeworkWorkContent
import com.bll.lnkteacher.ui.adapter.HomeworkWorkNameAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_homework_work.*
import kotlinx.android.synthetic.main.item_testpaper_work_type.*

class HomeworkWorkActivity:BaseActivity() {
    private var homeworkWorkContent: HomeworkWorkContent?=null
    private var userBeans= mutableListOf<HomeworkWorkContent.UserBean>()
    private var currentUser: HomeworkWorkContent.UserBean?=null
    private var mAdapter:HomeworkWorkNameAdapter?=null
    private var index=0//当前学生的作业下标
    private var position=0//当前选中学生位置

    override fun layoutId(): Int {
        return R.layout.ac_homework_work
    }

    override fun initData() {

        for (i in 0..20){
            val userBean= HomeworkWorkContent().UserBean()
            userBean.name="张三"
            userBeans.add(userBean)
        }

        homeworkWorkContent= HomeworkWorkContent()
        homeworkWorkContent?.className="三年一班"
        homeworkWorkContent?.homeworkType="家庭作业本"
        homeworkWorkContent?.date=System.currentTimeMillis()
        homeworkWorkContent?.lists=userBeans

        currentUser=userBeans[position]
    }

    override fun initView() {
        setPageTitle("作业批改 ${homeworkWorkContent?.className} ${homeworkWorkContent?.homeworkType}")
        showView(tv_save)

        setImageContent()
        initRecyclerView()

        iv_up.setOnClickListener {
            if (index>0){
                index-=1
                setImageContent()
            }
        }
        iv_down.setOnClickListener {
            if (index< currentUser?.images?.size?.minus(1)!!){
                index+=1
                setImageContent()
            }
        }
    }

    private fun initRecyclerView(){
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkWorkNameAdapter(R.layout.item_homework_work_name, userBeans)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(this,10f), 0))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            currentUser=userBeans[position]
            index=0
            setImageContent()
        }

    }

    /**
     * 设置作业内容
     */
    private fun setImageContent(){
        GlideUtils.setImageRoundUrl(this, currentUser?.images?.get(index),iv_image,10)
        tv_page.text="${index+1}/${currentUser?.images?.size}"
    }

}