package com.bll.lnkteacher.ui.activity.teaching

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Handler
import android.view.EinkPWInterface
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkteacher.FileAddress
import com.bll.lnkteacher.R
import com.bll.lnkteacher.base.BaseActivity
import com.bll.lnkteacher.mvp.model.ItemList
import com.bll.lnkteacher.mvp.model.testpaper.ContentListBean
import com.bll.lnkteacher.mvp.model.testpaper.CorrectClassBean
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperCorrectClass
import com.bll.lnkteacher.mvp.model.testpaper.TestPaperGrade
import com.bll.lnkteacher.mvp.presenter.FileUploadPresenter
import com.bll.lnkteacher.mvp.presenter.TestPaperCorrectDetailsPresenter
import com.bll.lnkteacher.mvp.view.IContractView
import com.bll.lnkteacher.mvp.view.IContractView.IFileUploadView
import com.bll.lnkteacher.ui.adapter.TestPaperCorrectUserAdapter
import com.bll.lnkteacher.utils.*
import com.bll.lnkteacher.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_homework_work.*
import java.io.File

class HomeworkCorrectActivity : BaseActivity(), IContractView.ITestPaperCorrectDetailsView, IFileUploadView {
    private var id = 0
    private var subType = 0
    private val mUploadPresenter = FileUploadPresenter(this)
    private val mPresenter = TestPaperCorrectDetailsPresenter(this)
    private var mClassBean: CorrectClassBean? = null
    private var userItems = mutableListOf<TestPaperCorrectClass.UserBean>()

    private var mAdapter: TestPaperCorrectUserAdapter? = null
    private var url = ""//上个学生提交地址
    private var posImage = 0//当前图片下标
    private var posUser = 0//当前学生下标
    private var currentImages: Array<String>? = null
    private var recordPath = ""
    private var mediaPlayer: MediaPlayer? = null

    private var elik: EinkPWInterface? = null
    private val commitItems = mutableListOf<ItemList>()

    override fun onToken(token: String) {
        showLoading()
        val commitPaths = mutableListOf<String>()
        for (item in commitItems) {
            commitPaths.add(item.url)
        }
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    url = ToolUtils.getImagesStr(urls)
                    val map = HashMap<String, Any>()
                    map["studentTaskId"] = userItems[posUser].studentTaskId
                    map["submitUrl"] = url
                    map["status"] = 2
                    mPresenter.commitPaperStudent(map)
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast("上传失败")
                }
            })
        }
    }

    override fun onImageList(list: MutableList<ContentListBean>?) {
    }

    override fun onClassPapers(bean: TestPaperCorrectClass?) {
        val beans = bean?.list!!
        for (item in beans) {
            if (item.status != 3) {
                userItems.add(item)
            }
        }
        if (userItems.size > 0) {
            userItems[posUser].isCheck = true
            setContentView()
        }
        mAdapter?.setNewData(userItems)
    }

    override fun onGrade(list: MutableList<TestPaperGrade>?) {
    }

    override fun onCorrectSuccess() {
        showToast(userItems[posUser].name + getString(R.string.teaching_correct_success))
        userItems[posUser].submitUrl = url
        userItems[posUser].status = 2
        mAdapter?.notifyItemChanged(posUser)
        //批改完成之后删除文件夹
        FileUtils.deleteFile(File(getPath()))
        elik?.setPWEnabled(false)
        disMissView(tv_save)
    }


    override fun layoutId(): Int {
        return R.layout.ac_homework_work
    }

    override fun initData() {
        id = intent.flags
        mClassBean =
            intent.getBundleExtra("bundle")?.getSerializable("classBean") as CorrectClassBean
        subType = intent.getIntExtra("subType", 0)
        mPresenter.getClassPapers(mClassBean?.examChangeId!!)
    }

    override fun initView() {
        elik = iv_image.pwInterFace
        elik?.setPWEnabled(false)

        setPageTitle("${getString(R.string.teaching_tab_homework_correct)}  ${mClassBean?.examName}  ${mClassBean?.name}")

        //如果是朗读本
        if (subType == 3) {
            disMissView(tv_save, ll_content)
            showView(ll_record)
        } else {
            disMissView(ll_record)
            showView(tv_save, ll_content)
        }

        initRecyclerView()

        tv_save.setOnClickListener {
            val item = userItems[posUser]
            if (item.status == 1) {
                showLoading()
                Handler().postDelayed({
                    commitPapers()
                },1000)
            }
        }

        iv_up.setOnClickListener {
            if (posImage > 0) {
                posImage -= 1
                setContentImage()
            }
        }
        iv_down.setOnClickListener {
            if (posImage < getImageSize() - 1) {
                posImage += 1
                setContentImage()
            }
        }

        iv_play.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(recordPath)
                mediaPlayer?.setOnCompletionListener {
                    changeMediaView(false)
                }
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                changeMediaView(true)
            }
            else{
                if (mediaPlayer?.isPlaying == true){
                    mediaPlayer?.pause()
                    changeMediaView(false)
                }
                else{
                    mediaPlayer?.start()
                    changeMediaView(true)
                }
            }
        }
    }

    /**
     * 更改播放view状态
     */
    private fun changeMediaView(boolean: Boolean){
        if (boolean){
            iv_play.setImageResource(R.mipmap.icon_record_pause)
            tv_play.setText(R.string.pause)
        }
        else{
            iv_play.setImageResource(R.mipmap.icon_record_play)
            tv_play.setText(R.string.play)
        }
    }


    private fun release(){
        if (mediaPlayer!=null){
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun initRecyclerView() {

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = TestPaperCorrectUserAdapter(R.layout.item_homework_correct_name, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, DP2PX.dip2px(this@HomeworkCorrectActivity, 20f)))
            setOnItemClickListener { adapter, view, position ->
                userItems[posUser].isCheck = false
                posUser = position//设置当前学生下标
                userItems[posUser].isCheck = true
                notifyDataSetChanged()
                posImage = 0
                setContentView()
                if (subType == 3) {
                    release()
                }
            }
        }

    }


    /**
     * 设置切换内容展示
     */
    private fun setContentView() {
        val userItem = userItems[posUser]
        if (subType != 3) {
            if (userItem.status == 2) {
                currentImages = userItem.submitUrl.split(",").toTypedArray()
                elik?.setPWEnabled(false)
                disMissView(tv_save)
                setContentImage()
            } else {
                showView(tv_save)
                currentImages = userItem.studentUrl.split(",").toTypedArray()
                loadPapers()
            }
        } else {
            if (!userItem.studentUrl.isNullOrEmpty()) {
                recordPath = userItem.studentUrl.split(",")[0]
                showView(iv_file, ll_play)
            } else {
                disMissView(iv_file, ll_play)
            }
        }
    }


    /**
     * 设置学生提交图片展示
     */
    private fun setContentImage() {
        hideLoading()
        //批改成功后删掉原来，加载提交后的图片
        if (userItems[posUser].status == 2) {
            GlideUtils.setImageRoundUrl(this, currentImages?.get(posImage), iv_image, 10)
        } else {
            elik?.setPWEnabled(true)
            val masterImage = "${getPath()}/${posImage + 1}.png"//原图
            GlideUtils.setImageFile(this, File(masterImage), iv_image)
        }
        tv_page.text = "${posImage + 1}/${getImageSize()}"

        val drawPath = getPathDrawStr(posImage + 1)
        elik?.setLoadFilePath(drawPath, true)
        elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik?.saveBitmap(true) {}
            }
        })
    }

    /**
     * 下载学生提交试卷
     */
    private fun loadPapers() {
        showLoading()
        val file = File(getPath())
        val files = FileUtils.getFiles(file.path)
        if (files.isNullOrEmpty()) {
            val imageDownLoad = ImageDownLoadUtils(this, currentImages, file.path)
            imageDownLoad.startDownload()
            imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                    runOnUiThread {
                        setContentImage()
                    }
                }
                override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                    imageDownLoad.reloadImage()
                }
            })
        } else {
            setContentImage()
        }
    }

    /**
     * 每份多少张考卷
     */
    private fun getImageSize(): Int {
        if (currentImages.isNullOrEmpty())
            return 0
        return currentImages!!.size
    }

    /**
     * 文件路径
     */
    private fun getPath(): String {
        return FileAddress().getPathHomework(id, mClassBean?.classId, userItems[posUser].userId)
    }

    /**
     * 得到当前手绘图片
     */
    private fun getPathDrawStr(index: Int): String {
        return "${getPath()}/draw${index}.tch"//手绘地址
    }

    /**
     * 提交学生考卷
     */
    private fun commitPapers() {
        commitItems.clear()
        //手写,图片合图
        for (i in currentImages?.indices!!) {
            val index = i + 1
            val masterImage = "${getPath()}/${index}.png"//原图
            val drawPath = getPathDrawStr(index).replace("tch", "png")
            val mergePath = getPath()//合并后的路径
            var mergePathStr = "${getPath()}/merge${index}.png"//合并后图片地址
            Thread{
                val oldBitmap = BitmapFactory.decodeFile(masterImage)
                val drawBitmap = BitmapFactory.decodeFile(drawPath)
                if (drawBitmap!=null){
                    val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
                    BitmapUtils.saveBmpGallery(this, mergeBitmap, mergePath, "merge${index}")
                }
                else{
                    mergePathStr=masterImage
                }
                commitItems.add(ItemList().apply {
                    id = i
                    url = mergePathStr
                })
                if (commitItems.size==currentImages?.size){
                    commitItems.sort()
                    mUploadPresenter.getToken()
                }
            }.start()

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        release()
    }

}