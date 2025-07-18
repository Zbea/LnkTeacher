package com.bll.lnkteacher.ui.fragment.document

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseFragment
import com.bll.lnkteacher.dialog.ClassGroupSelectorDialog
import com.bll.lnkteacher.dialog.LongClickManageDialog
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.presenter.DocumentPresenter
import com.bll.lnkteacher.mvp.presenter.QiniuPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.ui.adapter.DocumentAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_give_lessons.rv_list

class DocumentFragment:BaseFragment(),IContractView.IQiniuView,IContractView.IDocumentView {
    private var qiniuPresenter=QiniuPresenter(this)
    private var documentPresenter=DocumentPresenter(this,1)
    private var mAdapter: DocumentAdapter? = null
    private var path=""
    private var classIds= mutableListOf<Int>()
    private var position=0

    override fun onToken(token: String) {
        val file= mAdapter?.data?.get(position)
        FileUploadManager(token).apply {
            upload(file?.path!!)
            setCallBack{
                val map=HashMap<String,Any>()
                map["title"]=FileUtils.getUrlName(file.path)
                map["url"]=it
                map["classIds"]=classIds
                documentPresenter.sendDocument(map)
            }
        }
    }

    override fun onSendSuccess() {
        showToast(1,"发送成功")
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=25
        initRecycleView()
        path=FileAddress().getPathDocument("默认")
    }
    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecycleView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(), 20f), DP2PX.dip2px(requireActivity(), 20f),
            DP2PX.dip2px(requireActivity(), 20f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(), 5)//创建布局管理
        mAdapter = DocumentAdapter(R.layout.item_document, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(3, 20))
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val file = data[position]
                MethodManager.gotoDocument(requireActivity(), file)
            }
            setOnItemLongClickListener { adapter, view, position ->
                this@DocumentFragment.position=position
                onLongClick()
                true
            }
        }
    }

    private fun onLongClick() {
        val file= mAdapter?.data?.get(position)!!
        val beans = mutableListOf<ItemList>()
        beans.add(ItemList().apply {
            name = "删除"
            resId = R.mipmap.icon_setting_delete
        })
        beans.add(ItemList().apply {
            name = "发送"
            resId = R.mipmap.icon_setting_upload
        })
        LongClickManageDialog(requireActivity(),1, file.name, beans).builder()
            .setOnDialogClickListener { position->
                when(position){
                    0->{
                        FileUtils.deleteFile(file)
                        val drawPath = file.parent + "/${FileUtils.getUrlName(file.path)}draw/"
                        FileUtils.delete(drawPath)
                        MethodManager.notifyFileScan(requireActivity(),file.absolutePath)
                        mAdapter?.data?.indexOf(file)?.let { mAdapter?.remove(it) }
                    }
                    1->{
                        if (!FileUtils.getUrlFormat(file.path).equals(".pdf")){
                            showToast(1,"目前只支持发送pdf文件给学生")
                            return@setOnDialogClickListener
                        }
                        ClassGroupSelectorDialog(requireActivity(),1,DataBeanManager.classGroups).builder().setOnDialogSelectListener{
                            classIds= it.toMutableList()
                            qiniuPresenter.getToken()
                        }
                    }
                }
            }
    }

    fun onTypeSelectorEvent(typeStr:String) {
        path = FileAddress().getPathDocument(typeStr)
        pageIndex=1
        fetchData()
    }

    override fun fetchData() {
        val totalNum = FileUtils.getFiles(path).size
        setPageNumber(totalNum)
        val files = FileUtils.getDescFiles(path, pageIndex, pageSize)
        mAdapter?.setNewData(files)
    }

    override fun onRefreshData() {
        fetchData()
    }

}