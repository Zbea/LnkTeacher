package com.bll.lnkteacher

import com.bll.lnkteacher.MyApplication.Companion.mContext
import com.bll.lnkteacher.mvp.model.*
import com.bll.lnkteacher.mvp.model.group.ClassGroup
import com.bll.lnkteacher.mvp.model.group.Group

object DataBeanManager {

    var classGroups = mutableListOf<ClassGroup>()

    var schoolGroups= mutableListOf<Group>()

    var areaGroups= mutableListOf<Group>()

    var scoreList = intArrayOf(0, 60, 70, 80, 90, 100)

    var grades= mutableListOf<Grade>()

    private val listTitle = arrayOf(
        R.string.main_home_title,
        R.string.main_textbook_title,
        R.string.main_classgroup_title,
        R.string.main_teaching_title,
        R.string.main_note_title,
        R.string.main_bookcase_title,
        R.string.main_app_title
    )
    val textbookType = arrayOf(
        mContext.getString(R.string.textbook_tab_text),mContext.getString(R.string.textbook_tab_course),
        mContext.getString(R.string.textbook_tab_teaching),mContext.getString(R.string.textbook_tab_handouts)
    )

    var teachingStrs = arrayOf(
        mContext.getString(R.string.teaching_tab_homework_assign),mContext.getString(R.string.teaching_tab_homework_correct),
        mContext.getString(R.string.teaching_tab_testpaper_assign),mContext.getString(R.string.teaching_tab_testpaper_correct)
    )

    var groupStrs = arrayOf(
        mContext.getString(R.string.group_tab_class),mContext.getString(R.string.group_tab_school),
        mContext.getString(R.string.group_tab_area)
    )

    var groupJobs = arrayOf(
        mContext.getString(R.string.classGroup_teacher),mContext.getString(R.string.classGroup_headteacher)
    )

    fun getGradeClassGroups(grade:Int):MutableList<PopupBean>{
        val popClasss= mutableListOf<PopupBean>()
        for (item in classGroups){
            if (item.grade==grade){
                popClasss.add(PopupBean(item.classId, item.name, false))
            }
        }
        return popClasss
    }

    val popClassGroups:MutableList<PopupBean>
        get(){
            val popClasss= mutableListOf<PopupBean>()
            for (i in classGroups.indices){
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

    val popupGrades: MutableList<PopupBean>
        get() {
            val list= mutableListOf<PopupBean>()
            for (i in grades.indices){
                list.add(PopupBean(grades[i].type, grades[i].desc, i == 0))
            }
            return list
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
            name = mContext.getString(listTitle[0])
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_jy)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_jy_check)
            checked = false
            name = mContext.getString(listTitle[1])
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_group_nor)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_group_check)
            checked = false
            name = mContext.getString(listTitle[2])
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_jx)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_jx_check)
            checked = false
            name = mContext.getString(listTitle[3])
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_bj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_bj_check)
            checked = false
            name = mContext.getString(listTitle[4])
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_sj)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_sj_check)
            checked = false
            name = mContext.getString(listTitle[5])
        })
        list.add(MainListBean().apply {
            icon = mContext.getDrawable(R.mipmap.icon_main_app)
            icon_check = mContext.getDrawable(R.mipmap.icon_main_app_check)
            checked = false
            name = mContext.getString(listTitle[6])
        })
        return list
    }


    val noteBook: MutableList<NoteTypeBean>
        get() {
            val list= mutableListOf<NoteTypeBean>()
            list.add(NoteTypeBean().apply {
                name = mContext.getString(R.string.note_tab_diary)
                typeId = 0
            })
            list.add(NoteTypeBean().apply {
                name = mContext.getString(R.string.note_tab_article)
                typeId = 1
            })
            list.add(NoteTypeBean().apply {
                name = mContext.getString(R.string.note_tab_topic)
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
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_6
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_fgb)
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
                name = mContext.getString(R.string.note_type_kbb)
                resId = R.drawable.bg_gray_stroke_10dp_corner
                resContentId = 0
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_hgb)
                resId = R.mipmap.icon_note_module_bg_1
                resContentId = R.mipmap.icon_note_details_bg_1
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_fgb)
                resId = R.mipmap.icon_note_module_bg_2
                resContentId = R.mipmap.icon_note_details_bg_2
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_yyb)
                resId = R.mipmap.icon_note_module_bg_3
                resContentId = R.mipmap.icon_note_details_bg_3
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_tzb)
                resId = R.mipmap.icon_note_module_bg_4
                resContentId = R.mipmap.icon_note_details_bg_4
            })
            list.add(ModuleBean().apply {
                name = mContext.getString(R.string.note_type_wxp)
                resId = R.mipmap.icon_note_module_bg_5
                resContentId = R.mipmap.icon_note_details_bg_5
            })
            return list
        }

    //学期选择
    val semesters: MutableList<PopupBean>
        get() {
            val list = mutableListOf<PopupBean>()
            list.add(PopupBean(0, mContext.getString(R.string.semester_last),true))
            list.add(PopupBean(1,mContext.getString(R.string.semester_next),false))
            return list
        }

}