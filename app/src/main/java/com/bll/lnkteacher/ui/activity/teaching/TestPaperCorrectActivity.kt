package com.bll.lnkteacher.ui.activity.teaching

import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.HomeworkCorrectContent
import com.bll.lnkteacher.ui.adapter.HomeworkWorkNameAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_testpaper_correct.*
import kotlinx.android.synthetic.main.item_testpaper_work_type.*

class TestPaperCorrectActivity:BaseActivity() {
    private var homeworkCorrectContent: HomeworkCorrectContent?=null
    private var userBeans= mutableListOf<HomeworkCorrectContent.UserBean>()
    private var currentUser: HomeworkCorrectContent.UserBean?=null
    private var mAdapter:HomeworkWorkNameAdapter?=null
    private var index=0//当前学生的作业下标
    private var position=0//当前选中学生位置

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_correct
    }

    override fun initData() {

        for (i in 0..20){
            val userBean= HomeworkCorrectContent().UserBean()
            userBean.name="张三"
            userBeans.add(userBean)
        }

        homeworkCorrectContent=
            HomeworkCorrectContent()
        homeworkCorrectContent?.className="三年一班"
        homeworkCorrectContent?.homeworkType="家庭作业本"
        homeworkCorrectContent?.date=System.currentTimeMillis()
        homeworkCorrectContent?.lists=userBeans

        currentUser=userBeans[position]
    }

    override fun initView() {
        setPageTitle("作业批改 ${homeworkCorrectContent?.className} ${homeworkCorrectContent?.homeworkType}")
        showView(tv_save)

        setImageContent()
        initRecyclerView()

        et_score.doAfterTextChanged {
            var score=it.toString()
            currentUser?.score=score
        }

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
        mAdapter = HomeworkWorkNameAdapter(R.layout.item_homework_correct_name, userBeans).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(this@TestPaperCorrectActivity,10f), 0))
            setOnItemClickListener { adapter, view, position ->
                this@TestPaperCorrectActivity.position=position
                currentUser=userBeans[position]
                index=0
                et_score.setText(if(currentUser?.score.isNullOrEmpty()) "" else currentUser?.score)
                setImageContent()
            }
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