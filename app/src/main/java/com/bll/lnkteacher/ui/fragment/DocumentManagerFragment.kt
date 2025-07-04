package com.bll.lnkteacher.ui.fragment

import PopupClick
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.MethodManager
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseMainFragment
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.DocumentDetailsDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.PopupRadioList
import com.bll.lnkteacher.mvp.model.ItemTypeBean
import com.bll.lnkteacher.mvp.model.PopupBean
import com.bll.lnkteacher.ui.fragment.document.DocumentFragment
import com.bll.lnkteacher.ui.fragment.document.GiveLessonsFragment
import com.bll.lnkteacher.utils.FileUtils
import kotlinx.android.synthetic.main.common_fragment_title.iv_manager
import kotlinx.android.synthetic.main.common_fragment_title.tv_grade
import kotlinx.android.synthetic.main.common_fragment_title.tv_type
import java.io.File

class DocumentManagerFragment:BaseMainFragment() {

    private var documentFragment: DocumentFragment? = null
    private var giveLessonsFragment: GiveLessonsFragment? = null

    private var lastPosition = 0
    private var lastFragment: Fragment? = null

    private var popupBeans = mutableListOf<PopupBean>()
    private var documentTypeNames= mutableListOf<String>()
    private var documentTypePops= mutableListOf<PopupBean>()
    private var typeStr=""

    override fun getLayoutId(): Int {
        return R.layout.fragment_document_manager
    }

    override fun initView() {
        super.initView()
        setTitle(DataBeanManager.getIndexLeftData()[4].name)
        showView(tv_grade)

        popupBeans.add(PopupBean(0, "创建分类", false))
        popupBeans.add(PopupBean(1, "删除分类", false))
        popupBeans.add(PopupBean(2, "文档明细", false))

        documentFragment = DocumentFragment()
        giveLessonsFragment = GiveLessonsFragment()
        switchFragment(lastFragment, giveLessonsFragment)

        tv_type.setOnClickListener {
            PopupRadioList(requireActivity(), documentTypePops, tv_type,tv_type.width,  5).builder().setOnSelectListener { item ->
                if (typeStr!=item.name){
                    typeStr=item.name
                    tv_type?.text=typeStr
                    onDocumentSelectorEvent()
                }
            }
        }

        iv_manager.setOnClickListener {
            PopupClick(requireActivity(), popupBeans, iv_manager, 5).builder().setOnSelectListener { item ->
                when (item.id) {
                    0 -> {
                        InputContentDialog(requireActivity(), "创建分类").builder().setOnDialogClickListener {
                            if (documentTypeNames.contains(it)){
                                showToast(1,R.string.toast_existed)
                                return@setOnDialogClickListener
                            }
                            val path=FileAddress().getPathDocument(it)
                            MethodManager.createFileScan(requireActivity(),path)
                            documentTypeNames.add(it)
                            documentTypePops.add(PopupBean(documentTypePops.size,it))
                        }
                    }
                    1 -> {
                        val path=FileAddress().getPathDocument(typeStr)
                        if (typeStr == "默认") {
                            showToast(1,"默认分类，无法删除")
                            return@setOnSelectListener
                        }
                        if (FileUtils.isExistContent(path)) {
                            showToast(1,"存在内容，无法删除")
                            return@setOnSelectListener
                        }
                        CommonDialog(requireActivity(),1).setContent(R.string.is_delete_tips).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun ok() {
                                FileUtils.delete(path)
                                MethodManager.notifyFileScan(requireActivity(),path)
                                getDocumentType()
                            }
                        })
                    }
                    2 -> {
                        DocumentDetailsDialog(requireActivity()).builder()
                    }
                }
            }
        }

        initTab()

        Handler().postDelayed({
            setGradeStr()
        },500)
    }

    override fun lazyLoad() {
        setGradeStr()
        getDocumentType()
    }

    private fun initTab() {
        val strs= DataBeanManager.documentType
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
                showView(tv_grade)
                disMissView(tv_type,iv_manager)
                switchFragment(lastFragment, giveLessonsFragment)
            }
            1 -> {
                showView(tv_type,iv_manager)
                disMissView(tv_grade)
                switchFragment(lastFragment, documentFragment)
            }
        }
        lastPosition = position
    }

    /**
     * 获取文档分类
     */
    private fun getDocumentType(){
        val path = FileAddress().getPathDocument("默认")
        if (!FileUtils.isExist(path)){
            MethodManager.createFileScan(requireActivity(),path)
        }

        documentTypePops.clear()
        documentTypeNames.clear()
        documentTypeNames=FileUtils.getDirectorys(File(path).parent)
        for (name in documentTypeNames){
            documentTypePops.add(PopupBean(documentTypeNames.indexOf(name),name))
        }
        if (documentTypeNames.contains(typeStr)){
            documentTypePops[documentTypeNames.indexOf(typeStr)].isCheck=true
        }
        else{
            typeStr="默认"
            documentTypePops[0].isCheck=true
        }
        tv_type.text=typeStr

        onDocumentSelectorEvent()
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
                ft?.add(R.id.fl_content_document, to)?.commit()
            } else {
                if (from != null) {
                    ft?.hide(from)
                }
                ft?.show(to)?.commit()
            }
        }
    }

    override fun onGradeSelectorEvent() {
        giveLessonsFragment?.onGradeSelectorEvent(grade)
    }

    private fun onDocumentSelectorEvent(){
        documentFragment?.onTypeSelectorEvent(typeStr)
    }

    override fun onRefreshData() {
        lazyLoad()
        giveLessonsFragment?.onRefreshData()
        documentFragment?.onRefreshData()
    }
}