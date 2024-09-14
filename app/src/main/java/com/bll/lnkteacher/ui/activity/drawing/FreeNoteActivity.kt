package com.bll.lnkteacher.ui.activity.drawing

import com.bll.lnkteacher.DataBeanManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseDrawingActivity
import com.bll.lnkteacher.dialog.CatalogFreeNoteDialog
import com.bll.lnkteacher.dialog.CommonDialog
import com.bll.lnkteacher.dialog.FreeNoteFriendManageDialog
import com.bll.lnkteacher.dialog.InputContentDialog
import com.bll.lnkteacher.dialog.NoteModuleAddDialog
import com.bll.lnkteacher.dialog.PopupFreeNoteReceiveList
import com.bll.lnkteacher.dialog.PopupFreeNoteShareList
import com.bll.lnkteacher.greendao.StringConverter
import com.bll.lnkteacher.manager.FreeNoteDaoManager
import com.bll.lnkteacher.mvp.model.FreeNoteBean
import com.bll.lnkteacher.mvp.model.FriendList
import com.bll.lnkteacher.mvp.model.FriendList.FriendBean
import com.bll.lnkteacher.mvp.model.ShareNoteList
import com.bll.lnkteacher.mvp.presenter.FreeNotePresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.utils.DateUtils
import com.bll.lnkteacher.utils.FileImageUploadManager
import com.bll.lnkteacher.utils.FileMultitaskDownManager
import com.bll.lnkteacher.utils.FileUtils
import com.bll.lnkteacher.utils.GlideUtils
import com.bll.lnkteacher.utils.ToolUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_free_note.tv_add
import kotlinx.android.synthetic.main.ac_free_note.tv_delete
import kotlinx.android.synthetic.main.ac_free_note.tv_name
import kotlinx.android.synthetic.main.ac_free_note.tv_receive_list
import kotlinx.android.synthetic.main.ac_free_note.tv_save
import kotlinx.android.synthetic.main.ac_free_note.tv_share
import kotlinx.android.synthetic.main.ac_free_note.tv_share_list
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_expand
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class FreeNoteActivity:BaseDrawingActivity(), IContractView.IFreeNoteView {

    private lateinit var presenter:FreeNotePresenter
    private var bgRes=""
    private var freeNoteBean:FreeNoteBean?=null
    private var posImage=0
    private var images= mutableListOf<String>()//手写地址
    private var bgResList= mutableListOf<String>()//背景地址
    private var receivePopWindow: PopupFreeNoteReceiveList?=null
    private var receiveTotal=0//接收总数
    private var receiveNotes= mutableListOf<ShareNoteList.ShareNoteBean>()
    private var receivePosition=0//接收列表position
    private var sharePopWindow: PopupFreeNoteShareList?=null
    private var shareTotal=0//分享总数
    private var shareNotes= mutableListOf<ShareNoteList.ShareNoteBean>()
    private var friendIds= mutableListOf<Int>()
    private var friends= mutableListOf<FriendBean>()

    override fun onReceiveList(list: ShareNoteList) {
        receiveNotes=list.list
        receiveTotal=list.total
        receivePopWindow?.setData(receiveNotes)
    }

    override fun onShareList(list: ShareNoteList) {
        shareNotes=list.list
        shareTotal=list.total
        sharePopWindow?.setData(shareNotes)
    }

    override fun onToken(token: String) {
        showLoading()
        //分享只能是有手写页面
        val sBgRes= mutableListOf<String>()
        val imagePaths= mutableListOf<String>()
        for (i in images.indices){
            if (File(images[i]).exists()){
                imagePaths.add(images[i])
                sBgRes.add(bgResList[i])
            }
        }
        FileImageUploadManager(token, imagePaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map=HashMap<String,Any>()
                    map["userIds"]=friendIds
                    map["title"]=freeNoteBean?.title!!
                    map["bgRes"]=ToolUtils.getImagesStr(sBgRes)
                    map["paths"]=ToolUtils.getImagesStr(urls)
                    map["date"]=freeNoteBean?.date!!
                    presenter.commitShare(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast("分享失败")
                }
            })
        }
    }
    override fun onDeleteSuccess() {
        receivePopWindow?.deleteData(receivePosition)
    }
    override fun onShare() {
        showToast("分享成功")
        fetchShareNotes(1,false)
    }

    override fun onBind() {
        presenter.getFriends()
        showToast("添加好友成功")
    }

    override fun onUnbind() {
        val iterator = friends.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (friendIds.contains(item.friendId)) {
                iterator.remove()
            }
        }
        showToast("解绑好友成功")
    }

    override fun onListFriend(list: FriendList) {
        friends=list.list
    }

    override fun layoutId(): Int {
        return R.layout.ac_free_note
    }
    override fun initData() {
        initChangeScreenData()
        freeNoteBean=FreeNoteDaoManager.getInstance().queryBean()
        freeNoteBean?.title=DateUtils.longToStringNoYear(System.currentTimeMillis())
        if (freeNoteBean==null){
            createFreeNote()
        }
    }

    override fun initChangeScreenData() {
        presenter=FreeNotePresenter(this)
        fetchShareNotes(1,false)
        fetchReceiveNotes(1,false)
        presenter.getFriends()
    }

    override fun initView() {
        disMissView(iv_expand)

        tv_name.setOnClickListener {
            InputContentDialog(this,tv_name.text.toString()).builder().setOnDialogClickListener{
                tv_name.text=it
                freeNoteBean?.title=it
            }
        }

        iv_btn.setOnClickListener {
            NoteModuleAddDialog(this,getCurrentScreenPos(),DataBeanManager.freenoteModules).builder()
                .setOnDialogClickListener { moduleBean ->
                    bgRes=ToolUtils.getImageResStr(this, moduleBean.resContentId)
                    GlideUtils.setImageUrl(this,moduleBean.resContentId,v_content_b)
                    bgResList[posImage]=bgRes
                }
        }

        tv_delete.setOnClickListener {
            CommonDialog(this).setContent("确定删除当前随笔？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    FreeNoteDaoManager.getInstance().deleteBean(freeNoteBean)
                    FileUtils.deleteFile(File(FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!))))
                    if (freeNoteBean?.isSave==true){
                        freeNoteBean=FreeNoteDaoManager.getInstance().queryBean()
                        posImage=freeNoteBean?.page!!
                    }
                    else{
                        createFreeNote()
                        posImage=0
                    }
                    showView(tv_save)
                    initFreeNote()
                    onChangeContent()
                }
            })
        }

        tv_save.setOnClickListener {
            freeNoteBean?.isSave=true
            saveFreeNote()
            createFreeNote()
            posImage=0
            initFreeNote()
            onChangeContent()
        }


        tv_receive_list.setOnClickListener {
            if (receivePopWindow==null){
                receivePopWindow=PopupFreeNoteReceiveList(this,tv_receive_list,receiveTotal).builder()
                receivePopWindow?.setData(receiveNotes)
                receivePopWindow?.setOnClickListener(object : PopupFreeNoteReceiveList.OnClickListener {
                    override fun onPage(pageIndex: Int) {
                        fetchReceiveNotes(pageIndex,true)
                    }
                    override fun onDelete(position: Int) {
                        receivePosition=position
                        val map=HashMap<String,Any>()
                        map["ids"]= arrayOf(receiveNotes[position].id)
                        presenter.deleteShareNote(map)
                    }
                    override fun onDownload(position: Int) {
                        downloadShareNote(receiveNotes[position])
                    }

                    override fun onClick(position: Int) {
                        val item=receiveNotes[position]
                        val freeNoteBean=FreeNoteDaoManager.getInstance().queryByDate(item.date)
                        setChangeFreeNote(freeNoteBean)
                    }
                })
            }
            else{
                receivePopWindow?.show()
            }
        }

        tv_share_list.setOnClickListener {
            if (sharePopWindow==null){
                sharePopWindow=PopupFreeNoteShareList(this,tv_share_list,shareTotal).builder()
                sharePopWindow?.setData(shareNotes)
                sharePopWindow?.setOnClickListener(object : PopupFreeNoteShareList.OnClickListener {
                    override fun onPage(pageIndex: Int) {
                        fetchShareNotes(pageIndex,true)
                    }
                })
            }
            else{
                sharePopWindow?.show()
            }
        }

        tv_share.setOnClickListener {
            if (friends.size==0){
                showToast("未加好友")
                return@setOnClickListener
            }
            FreeNoteFriendManageDialog(this,friends).builder().setOnDialogClickListener{ type,ids->
                if (type==0){
                    val path=FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!))
                    if (FileUtils.isExistContent(path)){
                        friendIds= ids as MutableList<Int>
                        presenter.getToken()
                    }
                    else{
                        showToast("暂无分享内容")
                    }
                }
                else{
                    presenter.unbindFriend(ids)
                }
            }
        }

        tv_add.setOnClickListener {
            InputContentDialog(this,"输入好友账号").builder()
                .setOnDialogClickListener { string ->
                    presenter.onBindFriend(string)
                }
        }

        initFreeNote()
        onChangeContent()
    }

    /**
     * 切换随笔
     */
    private fun setChangeFreeNote(item:FreeNoteBean){
        saveFreeNote()
        freeNoteBean=item
        posImage=freeNoteBean?.page!!
        initFreeNote()
        if (freeNoteBean?.isSave==true){
            disMissView(tv_save)
        }
        else{
            showView(tv_save)
        }
        onChangeContent()
    }

    private fun initFreeNote(){
        bgResList= freeNoteBean?.bgRes as MutableList<String>
        if (!freeNoteBean?.paths.isNullOrEmpty()) {
            images= freeNoteBean?.paths as MutableList<String>
        }
        else{
            images.clear()
        }
        tv_name.text=freeNoteBean?.title
    }

    /**
     * 创建新随笔
     */
    private fun createFreeNote(){
        bgRes= ToolUtils.getImageResStr(this,R.mipmap.icon_freenote_bg_1)
        freeNoteBean= FreeNoteBean()
        freeNoteBean?.date=System.currentTimeMillis()
        freeNoteBean?.title=DateUtils.longToStringNoYear(freeNoteBean?.date!!)
        freeNoteBean?.bgRes= arrayListOf(bgRes)
        freeNoteBean?.type=0
        FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
    }

    override fun onCatalog() {
        CatalogFreeNoteDialog(this,freeNoteBean!!.date).builder().setOnItemClickListener{
            setChangeFreeNote(it)
        }
    }

    override fun onPageDown() {
        posImage+=1
        if (posImage>=bgResList.size){
            bgRes= ToolUtils.getImageResStr(this,R.mipmap.icon_freenote_bg_1)
            bgResList.add(bgRes)
        }
        onChangeContent()
    }

    override fun onPageUp() {
        if (posImage>0){
            posImage-=1
            onChangeContent()
        }
    }


    override fun onChangeContent() {
        GlideUtils.setImageUrl(this,ToolUtils.getImageResId(this, bgResList[posImage]),v_content_b)
        val path=FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!))+"/${posImage+1}.png"
        //判断路径是否已经创建
        if (!images.contains(path)){
            images.add(path)
        }
        tv_page.text="${posImage+1}"
        tv_page_total.text="${images.size}"

        elik_b?.setLoadFilePath(path, true)
    }

    private fun saveFreeNote(){
        val path=FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!))
        if (FileUtils.isExistContent(path)){
            freeNoteBean?.paths = images
            freeNoteBean?.bgRes = bgResList
            freeNoteBean?.page=posImage
            FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
        }
    }

    /**
     * 下载分享随笔
     */
    private fun downloadShareNote(item:ShareNoteList.ShareNoteBean){
        val path=FileAddress().getPathFreeNote(DateUtils.longToString(item.date))
        val savePaths= mutableListOf<String>()
        val urls=item.paths.split(",")
        for (i in urls.indices)
        {
            savePaths.add(path+"/${i+1}.png")
        }
        FileMultitaskDownManager.with(this).create(urls).setPath(savePaths).startMultiTaskDownLoad(
            object : FileMultitaskDownManager.MultiTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    val freeNoteBean= FreeNoteBean()
                    freeNoteBean.userId=mUserId!!
                    freeNoteBean.title=item.title
                    freeNoteBean.date=item.date
                    freeNoteBean.bgRes= StringConverter().convertToEntityProperty(item.bgRes)
                    freeNoteBean.paths=savePaths
                    freeNoteBean.isSave=true
                    freeNoteBean.type=1
                    FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
                    showToast("下载成功")
                    receivePopWindow?.setRefreshData()
                    setChangeFreeNote(freeNoteBean)
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    showToast("下载失败")
                }
            })
    }

    private fun fetchReceiveNotes(page:Int, isShow: Boolean){
        val map=HashMap<String,Any>()
        map["size"]=6
        map["page"]=page
        presenter.getReceiveNotes(map,isShow)
    }

    private fun fetchShareNotes(page:Int, isShow: Boolean){
        val map=HashMap<String,Any>()
        map["size"]=6
        map["page"]=page
        presenter.getShareNotes(map,isShow)
    }


    override fun onDestroy() {
        super.onDestroy()
        saveFreeNote()
    }

}
