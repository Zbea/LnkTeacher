package com.bll.lnkteacher.ui.fragment.homework

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.Constants
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.CalendarSingleDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.HomeworkAssignDetailsDialog
import com.bll.lnkteacher.dialog.HomeworkPublishClassGroupSelectDialog
import com.bll.lnkteacher.manager.BookGreenDaoManager
import com.bll.lnkteacher.mvp.model.BookStore
import com.bll.lnkteacher.mvp.model.homework.HomeworkAssignDetailsList
import com.bll.lnkteacher.mvp.model.homework.HomeworkClassSelectItem
import com.bll.lnkteacher.mvp.model.homework.HomeworkClassCommitItem
import com.bll.lnkteacher.mvp.model.testpaper.TypeBean
import com.bll.lnkteacher.mvp.model.testpaper.TypeList
import com.bll.lnkteacher.mvp.presenter.HomeworkAssignPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.activity.teaching.HomeworkAssignContentActivity
import com.bll.lnkteacher.ui.adapter.HomeworkAssignAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.SToast
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.*

class HomeworkAssignFragment:BaseFragment(),IContractView.IHomeworkAssignView {

    private val mPresenter=HomeworkAssignPresenter(this)
    private var mAdapter:HomeworkAssignAdapter?=null
    private var types= mutableListOf<TypeBean>()
    private var position=0
    private var detailsDialog:HomeworkAssignDetailsDialog?=null
    private var selectCommitClass= mutableListOf<HomeworkClassSelectItem>()
    private var mCommitAdapter: HomeworkPublishClassGroupSelectDialog.MyAdapter?=null

    private var initDatas= mutableListOf<HomeworkClassSelectItem>()

    override fun onTypeList(list:  TypeList) {
        setPageNumber(list.total)
        types=list.list
        mAdapter?.setNewData(types)
        fetchHomeworkBooks()
    }
    override fun onAddSuccess() {
        if (types.size==pageSize){
            pageIndex+=1
        }
        fetchData()
    }
    override fun onDeleteSuccess() {
        showToast(R.string.delete_success)
        val typeBean=types[position]
        val homeworkBook=BookGreenDaoManager.getInstance().queryTextBookByBookID(6,typeBean.bookId)
        if (homeworkBook!=null)
        {
            homeworkBook.isHomework=false
            BookGreenDaoManager.getInstance().insertOrReplaceBook(homeworkBook)
        }
        mAdapter?.remove(position)
    }
    override fun onCommitSuccess() {
        MethodManager.saveCommitClass(types[position].id,selectCommitClass)
        showToast(R.string.teaching_assign_success)
    }
    override fun onDetails(details: HomeworkAssignDetailsList) {
        detailsDialog=HomeworkAssignDetailsDialog(requireContext(), details.list).builder()
        detailsDialog?.setDialogClickListener {
            mPresenter.deleteDetails(it)
        }

    }
    override fun onDetailsDeleteSuccess() {
        showToast(R.string.delete_success)
        detailsDialog?.refreshList()
        detailsDialog?.dismiss()
    }

    override fun onBook(bookStore: BookStore) {
        val books = bookStore.list
        for (book in books){
            if (!isExistBook(book.bookId)){
                val map=HashMap<String,Any>()
                map["name"]=book.bookName
                map["type"]=2
                map["subType"]=4//题卷本
                map["grade"]=book.grade
                map["bookId"]=book.bookId
                map["bgResId"]=book.imageUrl
                mPresenter.addType(map,false)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=9

        initRecyclerView()
        initPublishContentView()
        initDialog(2)
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight=1f
        layoutParams.setMargins(
            DP2PX.dip2px(activity,40f), DP2PX.dip2px(activity,60f),
            DP2PX.dip2px(activity,40f), 0)
        rv_list.layoutParams=layoutParams
        rv_list.layoutManager = GridLayoutManager(activity,3)

        mAdapter = HomeworkAssignAdapter(R.layout.item_homework_assign, types).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(3, DP2PX.dip2px(requireActivity(),50f)))
            setOnItemClickListener { _, _, position ->
                this@HomeworkAssignFragment.position=position
                val item=types[position]
                if(item.subType==1){
                    val intent= Intent(activity, HomeworkAssignContentActivity::class.java)
                    val bundle= Bundle()
                    bundle.putSerializable("homeworkType",item)
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
                else{
                    showView(ll_publish)
                    initPublishData()
//                    HomeworkPublishDialog(requireContext(),grade,item.id).builder().setOnDialogClickListener{ contentStr,homeClasss->
//                        selectCommitClass= homeClasss as MutableList<HomeworkClass>
//                        commitHomework(item,contentStr,homeClasss)
//                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@HomeworkAssignFragment.position=position
                deleteHomeworkBook(types[position])
                true
            }
        }
    }

    private fun initPublishContentView(){
        mCommitAdapter= HomeworkPublishClassGroupSelectDialog.MyAdapter(R.layout.item_publish_classgroup_selector, null)
        rv_class.layoutManager = LinearLayoutManager(context)//创建布局管理
        rv_class.adapter = mCommitAdapter
        mCommitAdapter?.bindToRecyclerView(rv_class)
        rv_class.addItemDecoration(SpaceItemDeco(0,0,0, 20))
        mCommitAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=initDatas[position]
            when (view.id){
                R.id.tv_date->{
                    CalendarSingleDialog(requireActivity()).builder().setOnDateListener{
                        item.date=it
                        mCommitAdapter?.notifyDataSetChanged()
                    }
                }
                R.id.cb_class->{
                    item.isCheck=!item.isCheck
                    if (!item.isCheck){
                        item.isCommit=false
                    }
                    mCommitAdapter?.notifyDataSetChanged()
                }
                R.id.cb_commit->{
                    item.isCommit=!item.isCommit
                    mCommitAdapter?.notifyDataSetChanged()
                }
            }
        }

        tv_cancel.setOnClickListener {
            disMissView(ll_publish)
        }

        tv_send.setOnClickListener {
            val contentStr = et_content.text.toString()
            selectCommitClass=getSelectClass()
            if (contentStr.isNotEmpty()) {
                if (selectCommitClass.isNotEmpty())
                {
                    disMissView(ll_publish)
                    commitHomework(types[position],contentStr)
                }
                else{
                    SToast.showText("未选中班级")
                }
            }
        }
    }

    private fun initPublishData(){
        initDatas.clear()
        et_content.setText("")
        val classs= DataBeanManager.getGradeClassGroups(grade)
        val homeworkClasss=MethodManager.getCommitClass(types[position].id)

        for (item in classs){
            initDatas.add(HomeworkClassSelectItem().apply {
                className=item.name
                classId=item.id
                date= DateUtils.getStartOfDayInMillis()+ Constants.dayLong
            })
        }

        for (item in initDatas){
            for (selectItem in homeworkClasss){
                if (item.classId==selectItem.classId){
                    item.isCheck=selectItem.isCheck
                    item.isCommit=selectItem.isCommit
                }
            }
        }
        mCommitAdapter?.setNewData(initDatas)
    }

    /**
     * 得到选中的班级信息
     */
    private fun getSelectClass():MutableList<HomeworkClassSelectItem>{
        val items= mutableListOf<HomeworkClassSelectItem>()
        for (item in initDatas){
            if (item.isCheck){
                if (!item.isCommit)
                    item.date=0
                items.add(item)
            }
        }
        return items
    }

    /**
     * 删除题卷本
     */
    private fun deleteHomeworkBook(item: TypeBean){
        CommonDialog(requireActivity()).setContent(R.string.toast_is_delete_tips).builder()
            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    if (item.subType==4){
                        val map=HashMap<String,Any>()
                        map["id"]=item.id
                        map["bookId"]=item.bookId
                        mPresenter.deleteType(map)
                    }
                    else{
                        val map=HashMap<String,Any>()
                        map["ids"]= arrayOf(item.id)
                        mPresenter.deleteHomeworkType(map)
                    }
                }
            })
    }

    /**
     * 发送作业本消息
     */
    private fun commitHomework(item:TypeBean, contentStr:String){
        val selects= mutableListOf<HomeworkClassCommitItem>()
        var isCommit=false
        for (ite in selectCommitClass){
            if (ite.date>0)
                isCommit=true
            selects.add(HomeworkClassCommitItem().apply {
                classId=ite.classId
                submitStatus=if (ite.isCommit) 0 else 1
                endTime=ite.date/1000
            })
        }
        val map=HashMap<String,Any>()
        map["subType"]=item.subType
        map["title"]=contentStr
        map["examList"]=selects
        map["name"]=item.name
        map["grade"]=grade
        map["showStatus"]=if (isCommit) 0 else 1
        map["commonTypeId"]=item.id
        mPresenter.commitHomework(map)
    }

    /**
     * 添加作业本
     */
    fun addHomeworkType(item: TypeBean, subType:Int){
        val map=HashMap<String,Any>()
        map["name"]=item.name
        map["type"]=2
        map["subType"]=subType
        map["grade"]=grade
        mPresenter.addType(map,true)
    }

    /**
     * 设置课本学期（月份为9月份之前为下学期）
     */
    private fun getSemester():Int{
       return if (DateUtils.getMonth()<9) 2 else 1
    }

    /**
     * 判断本书是否已经创建为教辅本
     */
    private fun isExistBook(bookId:Int):Boolean{
        var isExist=false
        for (item in types){
            if(item.bookId==bookId){
                isExist=true
            }
        }
        return isExist
    }

    /**
     * 刷新年级
     */
    fun changeGrade(grade:Int){
        this.grade=grade
        fetchData()
    }

    /**
     * 布置详情
     */
    fun showAssignDetails(){
        mPresenter.onDetails(grade)
    }

    override fun onRefreshData() {
       fetchData()
    }

    override fun fetchData() {
        if (grade==0)
            return
        val map=HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        map["grade"]=grade
        map["type"]=2
        mPresenter.getTypeList(map)
    }

    /**
     * 获取当前年级是否存在我的教辅
     */
    private fun fetchHomeworkBooks(){
        val map1 = HashMap<String, Any>()
        map1["page"] = pageIndex
        map1["size"] = 100
        map1["subjectName"]=DataBeanManager.getCourseId(mUser?.subjectName!!)
        map1["type"] = 1
        map1["area"] = mUser?.schoolProvince!!
        map1["grade"] = grade
        map1["semester"]=getSemester()
        mPresenter.getHomeworkBooks(map1)
    }
}