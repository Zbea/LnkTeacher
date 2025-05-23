package com.bll.lnkteacher.ui.activity.teaching

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.manager.HomeworkContentDaoManager
import com.bll.lnkteacher.manager.HomeworkContentTypeDaoManager
import com.bll.lnkteacher.mvp.model.homework.HomeworkContentTypeBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.drawing.HomeworkContentDrawingActivity
import com.bll.lnkteacher.ui.adapter.HomeworkDrawContentTypeAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_homework_assgin_content.rv_list
import kotlinx.android.synthetic.main.common_title.tv_btn_1

class HomeworkDrawContentTypeActivity:BaseAppCompatActivity(),IContractView.IHomeworkPaperAssignView {

    private var typeId=0
    private var typeBean: TypeBean?=null//作业卷分类
    private var items= mutableListOf<HomeworkContentTypeBean>()
    private var mAdapter:HomeworkDrawContentTypeAdapter?=null

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        typeBean= intent.getBundleExtra("bundle")?.getSerializable("homeworkType") as TypeBean
        typeId=typeBean?.id!!
        pageSize=12

    }

    override fun initView() {
        setPageTitle(typeBean?.name!!)
        setPageOk("创建")

        tv_btn_1.setOnClickListener {
            val item=HomeworkContentTypeBean()
            item.date=System.currentTimeMillis()
            item.title=DateUtils.longToStringNoYear1(item.date)
            item.typeId=typeId
            item.contentId=ToolUtils.getDateId()
            item.path=FileAddress().getPathHomeworkContent(typeId,item.contentId)
            HomeworkContentTypeDaoManager.getInstance().insertOrReplace(item)

            gotoDrawing(item)

            fetchData()
        }

        initRecyclerView()

        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,50f), DP2PX.dip2px(this,30f), DP2PX.dip2px(this,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkDrawContentTypeAdapter(R.layout.item_homework_draw_content, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.iv_delete){
                    val item=items[position]
                    HomeworkContentTypeDaoManager.getInstance().deleteBean(item)
                    HomeworkContentDaoManager.getInstance().deleteContent(item.contentId)
                    FileUtils.delete(item.path)
                    remove(position)
                }
            }
            setOnItemClickListener { adapter, view, position ->
                val item=items[position]
                gotoDrawing(item)
            }
        }
        rv_list?.addItemDecoration(SpaceItemDeco(35))
    }

    private fun gotoDrawing(item:HomeworkContentTypeBean){
        val intent= Intent(this@HomeworkDrawContentTypeActivity,HomeworkContentDrawingActivity::class.java)
        val bundle= Bundle()
        bundle.putSerializable("homeworkType",typeBean)
        intent.putExtra("bundle",bundle)
        intent.putExtra("contentId",item.contentId)
        customStartActivity(intent)
    }

    override fun fetchData() {
        items = HomeworkContentTypeDaoManager.getInstance().queryAll(typeId,pageIndex,pageSize)
        val total = HomeworkContentTypeDaoManager.getInstance().queryAll(typeId)
        setPageNumber(total.size)
        mAdapter?.setNewData(items)
    }

}