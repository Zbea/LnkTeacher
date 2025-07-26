package com.bll.lnkteacher.ui.fragment.document

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.ui.adapter.DocumentAdapter
import com.bll.lnkteacher.utils.DP2PX
import com.bll.lnkteacher.utils.FileUploadManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.SPUtil
import com.bll.lnkteacher.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_give_lessons.rv_list
import java.io.File

class GiveLessonsFragment : BaseMainFragment() {

    private var mAdapter: DocumentAdapter? = null
    private var path = ""
    private var position:Int=0

    override fun onUpload(token: String) {
        val file= mAdapter?.data?.get(position)
        FileUploadManager(token).apply {
            upload(file?.path!!)
            setCallBack{
                hideLoading()
                SPUtil.putString(file.name,it)
                MethodManager.gotoPptDetails(requireActivity(), file.path,it)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize = 25
        initDialog(1)
        initRecycleView()
    }

    override fun lazyLoad() {
    }

    private fun initRecycleView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(), 20f), DP2PX.dip2px(requireActivity(), 20f),
            DP2PX.dip2px(requireActivity(), 20f), 0
        )
        layoutParams.weight = 1f
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(), 5)//创建布局管理
        mAdapter = DocumentAdapter(R.layout.item_document, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(3, 20))
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                this@GiveLessonsFragment.position=position
                val file = data[position]
                val format=FileUtils.getUrlFormat(file.path)
                if (format.equals(".ppt") || format.equals(".pptx")) {
                    val url=SPUtil.getString(file.name)
                    if (url.isNotEmpty()){
                        MethodManager.gotoPptDetails(requireActivity(), file.path,url)
                    }
                    else{
                        mQiniuPresenter.getToken(true)
                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                val file = data[position]
                onLongClick(file)
                true
            }
        }
    }

    private fun onLongClick(file:File) {
        CommonDialog(requireActivity(),1).setContent(R.string.toast_is_delete_tips).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
            override fun ok() {
                SPUtil.putString(file.name,"")
                FileUtils.deleteFile(file)
                MethodManager.notifyFileScan(requireActivity(),file.absolutePath)
                mAdapter?.data?.indexOf(file)?.let { mAdapter?.remove(it) }
            }
        })
    }

     fun onGradeSelectorEvent(grade:Int) {
        path = FileAddress().getPathPpt(DataBeanManager.getGradeStr(grade))
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