package com.bll.lnkteacher

import com.bll.lnkteacher.MyApplication.Companion.mContext
import com.bll.lnkteacher.mvp.model.*

object DataBeanManager {

    var classGroups = mutableListOf<ClassGroup>()

    var schoolGroups= mutableListOf<Group>()

    var areaGroups= mutableListOf<Group>()

    var scoreList = intArrayOf(0, 60, 70, 80, 90, 100)

    private val listTitle = arrayOf(
        "首页",
        "教义",
        "班群",
        "教学",
        "笔记",
        "书架",
        "应用"
    )
    val textbookType = arrayOf(
        "我的课本",
        "我的课辅",
        "参考教材",
        "往期教材"
    )
    var teachingStrs = arrayOf(
        "作业布置",
        "作业批改",
        "考卷发放",
        "考卷批改"
    )
    var kmArray = arrayOf(
        "语文",
        "数学",
        "英语",
        "物理",
        "化学",
        "地理",
        "政治",
        "历史",
        "生物"
    ) //科目的数据

    val popClassGroups:MutableList<PopupBean>
        get(){
            val popClasss= mutableListOf<PopupBean>()
            for (i in 0 until classGroups.size){
                val item=classGroups[i]
                popClasss.add(PopupBean(item.classId, item.name, i == 0))
            }
            return popClasss
        }

    val popSchoolGroups:MutableList<PopupBean>
        get(){
            val popSchools= mutableListOf<PopupBean>()
            for (i in 0 until schoolGroups.size){
                val item=schoolGroups[i]
                popSchools.add(PopupBean(item.id, item.schoolName, i == 0))
            }
            return popSchools
        }

    val popAreaGroups:MutableList<PopupBean>
        get(){
            val popSchools= mutableListOf<PopupBean>()
            for (i in 0 until areaGroups.size){
                val item=areaGroups[i]
                popSchools.add(PopupBean(item.id, item.schoolName, i == 0))
            }
            return popSchools
        }

    /**
     * 获取index栏目
     *
     * @param context
     * @return
     */
    fun getIndexData(): MutableList<MainListBean> {
        val list = mutableListOf<MainListBean>()
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_sy)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_sy_check)
            checked = true
            name = listTitle[0]
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_jy)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_jy_check)
            checked = false
            name = listTitle[1]
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_group_nor)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_group_check)
            checked = false
            name = listTitle[2]
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_jx)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_jx_check)
            checked = false
            name = listTitle[3]
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_bj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_bj_check)
            checked = false
            name = listTitle[4]
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_sj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_sj_check)
            checked = false
            name = listTitle[5]
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_app)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_app_check)
            checked = false
            name = listTitle[6]
        })
        return list
    }


    val message: MutableList<MessageBean>
        get() {
            val list= mutableListOf<MessageBean>()
            val classGroups: MutableList<ClassGroup> = ArrayList()
            val classGroup = ClassGroup()
            classGroup.name = "三年一班"
            classGroups.add(classGroup)
            classGroups.add(classGroup)
            classGroups.add(classGroup)
            val messageBean = MessageBean()
            messageBean.createTime = System.currentTimeMillis()
            messageBean.content = "写语文作业第7、8题"
            messageBean.classGroups = classGroups
            list.add(messageBean)
            list.add(messageBean)
            list.add(messageBean)
            return list
        }

    val noteBook: MutableList<BaseTypeBean>
        get() {
            val list= mutableListOf<BaseTypeBean>()
            list.add(BaseTypeBean().apply {
                name = "我的日记"
                typeId = 0
            })
            list.add(BaseTypeBean().apply {
                name = "金句彩段"
                typeId = 1
            })
            list.add(BaseTypeBean().apply {
                name = "典型题型"
                typeId = 2
            })
            return list
        }

    //封面
    val homeworkCover: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            val moduleBean = ModuleBean()
            moduleBean.resId = R.mipmap.icon_homework_cover_1
            val moduleBean1 = ModuleBean()
            moduleBean1.resId = R.mipmap.icon_homework_cover_2
            val moduleBean2 = ModuleBean()
            moduleBean2.resId = R.mipmap.icon_homework_cover_3
            val moduleBean3 = ModuleBean()
            moduleBean3.resId = R.mipmap.icon_homework_cover_4
            list.add(moduleBean)
            list.add(moduleBean1)
            list.add(moduleBean2)
            list.add(moduleBean3)
            return list
        }

    //日记内容选择
    val noteModuleDiary: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = "横格本"
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_6
            })
            list.add(ModuleBean().apply {
                name = "方格本"
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_7
            })
            return list
        }

    //笔记本内容选择
    val noteModuleBook: MutableList<ModuleBean>
        get() {
            val list= mutableListOf<ModuleBean>()
            list.add(ModuleBean().apply {
                name = "空白本"
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name = "横格本"
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_1
            })
            list.add(ModuleBean().apply {
                name = "方格本"
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_2
            })
            list.add(ModuleBean().apply {
                name = "英语本"
                resId = R.mipmap.icon_note_module_bg_3
                resContentId = R.mipmap.icon_note_details_bg_3
            })
            list.add(ModuleBean().apply {
                name = "田字本"
                resId = R.mipmap.icon_note_module_bg_4
                resContentId = R.mipmap.icon_note_details_bg_4
            })
            list.add(ModuleBean().apply {
                name = "五线谱"
                resId = R.mipmap.icon_note_module_bg_5
                resContentId = R.mipmap.icon_note_details_bg_5
            })
            return list
        }

}