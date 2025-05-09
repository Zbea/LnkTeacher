package com.bll.lnkteacher.ui.activity.teaching

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseAppCompatActivity
import com.bll.lnkteacher.dialog.CalendarSingleDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.ImageDialog
import com.bll.lnkteacher.dialog.PopupCheckList
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.homework.HomeworkClassSelectItem
import com.bll.lnkteacher.mvp.model.testpaper.AssignPaperContentList
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.presenter.HomeworkPaperAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.HomeworkAssignContentAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.bll.lnkteacher.widget.SpaceItemDeco
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.cb_commit
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.cb_correct
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.rv_list
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.tv_commit_time
import kotlinx.android.synthetic.main.ac_testpaper_assgin_content.tv_group
import kotlinx.android.synthetic.main.common_title.tv_btn_1

class HomeworkAssignContentActivity:BaseAppCompatActivity(),IContractView.IHomeworkPaperAssignView {

    private var grade=0
    private lateinit var mPresenter:HomeworkPaperAssignPresenter
    private var mAdapter:HomeworkAssignContentAdapter?=null
    private var items= mutableListOf<AssignPaperContentList.AssignPaperContentBean>()//列表数据
    private var typeBean: TypeBean?=null//作业卷分类
    private var position=-1
    private var commitTime=0L
    private var isCommit=true
    private var isCorrect=false
    private var classIds= mutableListOf<Int>()
    private var taskId=0

    override fun onList(contentList: AssignPaperContentList) {
        setPageNumber(contentList.total)
        items=contentList.list
        mAdapter?.setNewData(items)
    }

    override fun onCommitSuccess() {
        showToast(R.string.teaching_assign_success)
        finish()
    }

    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        mAdapter?.remove(position)
    }

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_assgin_content
    }

    override fun initData() {
        initChangeScreenData()
        typeBean= intent.getBundleExtra("bundle")?.getSerializable("homeworkType") as TypeBean
        pageSize=if (typeBean?.subType==1) 12 else 10
        grade=typeBean?.grade!!
        val classSelectItem=Gson().fromJson(typeBean?.lastConfig,HomeworkClassSelectItem::class.java)
        if (classSelectItem!=null){
            isCorrect= classSelectItem.selfBatchStatus==1
            classIds= classSelectItem.classIds
        }
        commitTime=System.currentTimeMillis()+Constants.dayLong

        fetchData()
    }

    override fun initChangeScreenData() {
        mPresenter= HomeworkPaperAssignPresenter(this)
    }

    override fun initView() {
        setPageTitle(typeBean?.name!!)
        setPageOk("发送")

        tv_group.setOnClickListener {
            val totalClassGroups=if (typeBean?.addType==1) DataBeanManager.getClassGroupByMains(grade) else DataBeanManager.getClassGroups(grade)
            var classGroups= mutableListOf<ClassGroup>()
            if (typeBean?.classIds.isNullOrEmpty()){
                classGroups=totalClassGroups
            }
            else{
                val bindClassIds=typeBean?.classIds!!.split(",")
                for (classGroup in totalClassGroups){
                    if (bindClassIds.contains(classGroup.classId.toString())){
                        classGroups.add(classGroup)
                    }
                }
            }

            val popGroups= mutableListOf<PopupBean>()
            for (classGroup in classGroups){
                popGroups.add(PopupBean(classGroup.classId,classGroup.name,classIds.contains(classGroup.classId)))
            }

            PopupCheckList(this, popGroups, tv_group,  5).builder().setOnSelectListener{
                classIds.clear()
                for (item in it){
                    classIds.add(item.id)
                }
            }
        }

        tv_commit_time.text=DateUtils.longToStringWeek(commitTime)
        tv_commit_time.setOnClickListener {
            CalendarSingleDialog(this,160f,200f).builder().setOnDateListener{
                if (it<DateUtils.getStartOfDayInMillis()){
                    showToast("提交时间不能小于当天")
                }
                else{
                    tv_commit_time.text= DateUtils.longToStringWeek(it)
                    commitTime=it
                }
            }
        }

        cb_commit.isChecked=isCommit
        cb_commit.setOnCheckedChangeListener { compoundButton, b ->
            isCommit=b
        }

        cb_correct.isChecked=isCorrect
        cb_correct.setOnCheckedChangeListener { compoundButton, b ->
            isCorrect=b
        }

        tv_btn_1.setOnClickListener {
            if (classIds.size==0){
                showToast("请选择班级")
                return@setOnClickListener
            }
            if (taskId>0)
                commit()
        }

        if (typeBean?.subType==1){
            initRecyclerViewPaper()
        }
        else{
            initRecyclerView()
        }
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(DP2PX.dip2px(this,50f), DP2PX.dip2px(this,50f), DP2PX.dip2px(this,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkAssignContentAdapter(R.layout.item_homework_assign_content, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemChildClickListener { adapter, view, position ->
                onChildClick(view,position)
            }
            setOnItemChildLongClickListener { adapter, view, position ->
                this@HomeworkAssignContentActivity.position=position
                delete()
                true
            }
        }
        rv_list?.addItemDecoration(SpaceItemDeco(40))
    }

    private fun initRecyclerViewPaper(){
        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter = HomeworkAssignContentAdapter(R.layout.item_testpaper_assign_content, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
            setOnItemChildClickListener { adapter, view, position ->
                onChildClick(view,position)
            }
            setOnItemChildLongClickListener{ adapter, view, position ->
                this@HomeworkAssignContentActivity.position=position
                delete()
                true
            }
        }
        rv_list?.addItemDecoration(SpaceGridItemDeco(4,20))
    }

    private fun onChildClick(view: View,position:Int){
        val item =items[position]
        when(view.id){
            R.id.cb_check->{
                for (ite in items){
                    if (ite.isCheck){
                        ite.isCheck=false
                        mAdapter?.notifyItemChanged(items.indexOf(ite))
                    }
                }
                item.isCheck=true
                mAdapter?.notifyItemChanged(position)

                taskId=item.taskId
                if (item.answerUrl.isNullOrEmpty()){
                    cb_correct.isEnabled=false
                    if (isCorrect){
                        isCorrect=false
                        cb_correct.isChecked=false
                    }
                }
                else{
                    cb_correct.isEnabled=true
                }
            }
            R.id.tv_answer->{
                ImageDialog(this@HomeworkAssignContentActivity,item.answerUrl.split(",")).builder()
            }
            R.id.iv_image->{
                ImageDialog(this@HomeworkAssignContentActivity,item.examUrl.split(",")).builder()
            }
        }
    }

    private fun delete(){
        CommonDialog(this@HomeworkAssignContentActivity).setContent(R.string.is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val item=items[position]
                val ids= arrayListOf(item.taskId)
                mPresenter.deletePapers(ids)
            }
        })
    }

    /**
     * 布置作业卷
     */
     private fun commit(){
        val map=HashMap<String,Any>()
        map["classIds"]=classIds
        map["showStatus"]=if (isCommit) 0 else 1
        map["endTime"]=if (isCommit)commitTime/1000 else 0
        map["selfBatchStatus"]=if (isCommit&&isCorrect) 1 else 0
        map["taskId"]=taskId
        mPresenter.commitHomeworkReel(map)
    }

    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["commonTypeId"]=typeBean?.id!!
        mPresenter.getList(map)
    }

}