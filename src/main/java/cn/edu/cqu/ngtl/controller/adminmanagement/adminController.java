package cn.edu.cqu.ngtl.controller.adminmanagement;

import cn.edu.cqu.ngtl.dao.krim.KRIM_ROLE_T_Dao;
import cn.edu.cqu.ngtl.dao.krim.impl.*;
import cn.edu.cqu.ngtl.dao.tams.impl.TAMSCourseManagerDaoJpa;
import cn.edu.cqu.ngtl.dao.ut.impl.UTInstructorDaoJpa;
import cn.edu.cqu.ngtl.dataobject.krim.*;
import cn.edu.cqu.ngtl.dataobject.tams.TAMSCourseManager;
import cn.edu.cqu.ngtl.dataobject.tams.TAMSTaCategory;
import cn.edu.cqu.ngtl.dataobject.ut.UTInstructor;
import cn.edu.cqu.ngtl.form.adminmanagement.AdminInfoForm;
import cn.edu.cqu.ngtl.service.adminservice.IAdminService;
import cn.edu.cqu.ngtl.service.riceservice.impl.AdminConverterimpl;
import cn.edu.cqu.ngtl.viewobject.adminInfo.CourseManagerViewObject;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.impl.CollectionControllerServiceImpl;
import org.kuali.rice.krad.web.service.impl.CollectionControllerServiceImpl.CollectionActionParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by awake on 2016-10-21.
 */
@Controller
@RequestMapping("/admin")
public class adminController extends UifControllerBase {

    @Autowired
    private IAdminService adminService;

    @Autowired
    private TAMSCourseManagerDaoJpa tamsCourseManagerDaoJpa;


    /**
     * 127.0.0.1:8080/tams/portal/admin?methodToCall=getConsolePage&viewId=AdminView
     *
     * @param form
     * @return
     */
    @RequestMapping(params = "methodToCall=getConsolePage")
    public ModelAndView getConsolePage(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm infoForm = (AdminInfoForm) form;

        return this.getModelAndView(infoForm, "pageConsole");
    }

    /**
     * http://127.0.0.1:8080/tams/portal/admin?methodToCall=getRoleManagerPage&viewId=AdminView
     *
     * @param form
     * @param request
     * @param response
     * @return 角色管理页面
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=getRoleManagerPage")
    public ModelAndView getRoleManagerPage(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        AdminInfoForm infoForm = (AdminInfoForm) form;
        infoForm.setRMPkrimRoleTs(new KRIM_ROLE_T_DaoJpa().getAllKrimRoleTs());

        return this.getModelAndView(infoForm, "pageRoleManager");
    }


    /**
     * http://127.0.0.1:8080/tams/portal/admin?methodToCall=getUserRoleManagerPage&viewId=AdminView
     *
     * @param form
     * @param request
     * @param response
     * @return 用户管理页面
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=getUserRoleManagerPage")
    public ModelAndView getUserRoleManagerPage(@ModelAttribute("KualiForm") UifFormBase form,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdminInfoForm infoForm = (AdminInfoForm) form;
        infoForm.setURMutInstructors(new UTInstructorDaoJpa().getAllInstructors());
        return this.getModelAndView(infoForm, "pageUserRoleManager");
    }

    /**
     * http://127.0.0.1:8080/tams/portal/admin?methodToCall=getPermissionManagementPage&viewId=AdminView
     *
     * @param form
     * @param request
     * @param response
     * @return 权限页面
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=getPermissionManagementPage")
    public ModelAndView getPermissionManagementPage(@ModelAttribute("KualiForm") UifFormBase form,
                                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        AdminInfoForm infoForm = (AdminInfoForm) form;
        List<KRIM_PERM_T> krimPermTs = new ArrayList<KRIM_PERM_T>(new KRIM_PERM_T_DaoJpa().getAllPermissions());
        infoForm.setRMPkrimPermTs(krimPermTs);
        return this.getModelAndView(infoForm, "pagePermissionManagement");
    }

    /**
     * 更新角色
     *
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=updateExmKrimRole")
    public ModelAndView updateExmKrimRole(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        AdminInfoForm infoForm = (AdminInfoForm) form;

        List<KRIM_ROLE_T> krimRoleTs = new ArrayList<KRIM_ROLE_T>(infoForm.getRMPkrimRoleTs());

        CollectionActionParameters params = new CollectionActionParameters(infoForm, true);
        int index = params.getSelectedLineIndex();
        KRIM_ROLE_T krimRoleT = krimRoleTs.get(index);

        List<KRIM_ROLE_PERM_T> krimRolePermTs = new ArrayList<KRIM_ROLE_PERM_T>(
                new KRIM_ROLE_PERM_T_DaoJpa().getKrimRolePermTsByRole(krimRoleT));
        List<KRIM_PERM_T> krimPermTs = new ArrayList<KRIM_PERM_T>(new KRIM_PERM_T_DaoJpa().getAllPermissions());

        for (KRIM_ROLE_PERM_T krimRolePermT : krimRolePermTs) {
            KRIM_PERM_T tempKrimPermT = krimRolePermT.getKrimPermT();
            if (krimPermTs.contains(tempKrimPermT)) {
                tempKrimPermT.setChecked(true);
                krimPermTs.set(krimPermTs.indexOf(tempKrimPermT), tempKrimPermT);
            }
        }

        infoForm.setRMPkrimRoleT(krimRoleT);
        infoForm.setRMPkrimPermTs(krimPermTs);
        System.out.println(infoForm.isUpdateComponentRequest());
        return this.getModelAndView(infoForm, "pageUpdateRole");
    }


    /**
     * 新增角色
     *
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=addExmKrimRole")
    public ModelAndView addExmKrimRole(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        AdminInfoForm infoForm = (AdminInfoForm) form;

        infoForm.setRMPkrimRoleT(null);
        infoForm.setRMPkrimPermTs(new KRIM_PERM_T_DaoJpa().getAllPermissions());

        return this.getModelAndView(infoForm, "pageUpdateRole");
    }

    /**
     * 保存角色
     *
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @RequestMapping(params = "methodToCall=saveExmKrimRole")
    public ModelAndView saveExmKrimRole(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        AdminInfoForm infoForm = (AdminInfoForm) form;
        KRIM_ROLE_T_Dao krimRoleTDao = new KRIM_ROLE_T_DaoJpa();
        List<KRIM_ROLE_T> krimRoleTs = new ArrayList<KRIM_ROLE_T>(infoForm.getRMPkrimRoleTs());
        KRIM_ROLE_T krimRoleT = infoForm.getRMPkrimRoleT();
        if (krimRoleT == null) {
            infoForm.setErrMsg("角色为空");
            return this.showDialog("errWarnDialog", true, infoForm);
        }
        if (krimRoleT.getName() == null) {
            infoForm.setErrMsg("角色名称不能为空");
            return this.showDialog("errWarnDialog", true, infoForm);
        }
        KRIM_ROLE_T checkKrimRoleT = krimRoleTDao.getKrimRoleTByName(krimRoleT.getName());
        if (checkKrimRoleT != null) {
            if (krimRoleT.getId() == null || !krimRoleT.getId().equals(checkKrimRoleT.getId())) {
                infoForm.setErrMsg("角色名称已存在");
                return this.showDialog("errWarnDialog", true, infoForm);
            }
        }
        krimRoleT = krimRoleTDao.saveKrimRoleT(krimRoleT);
        infoForm.setRMPkrimRoleT(krimRoleT);

        List<KRIM_PERM_T> krimPermTs = infoForm.getRMPkrimPermTs();
        new KRIM_ROLE_PERM_T_DaoJpa().saveKrimRolePermTByRoleAndPerms(krimRoleT, krimPermTs);

        infoForm.setRMPkrimRoleTs(new KRIM_ROLE_T_DaoJpa().getAllKrimRoleTs());
        return this.getModelAndView(infoForm, "pageUpdateRole");
    }

    /**
     * 选择用户
     *
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(params = "methodToCall=selectURMInstructor")
    public ModelAndView selectURMInstructor(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
                                            HttpServletResponse response) {
        AdminInfoForm infoForm = (AdminInfoForm) form;

        CollectionActionParameters params = new CollectionActionParameters(infoForm, true);
        int index = params.getSelectedLineIndex();

        List<UTInstructor> utInstructors = infoForm.getURMutInstructors();
        UTInstructor utInstructor = utInstructors.get(index);
        infoForm.setURMutInstructor(utInstructor);

        if (utInstructor.getIdNumber() == null) {
            infoForm.setErrMsg("该同志没有同一认证号呀");
            return this.showDialog("errWarnDialog", true, infoForm);
        }

        List<KRIM_ROLE_MBR_T> krimRoleMbrTs = new ArrayList<>(
                new KRIM_ROLE_MBR_T_DaoJpa().getKrimEntityEntTypTsByMbrId(utInstructor.getId()));
        List<KRIM_ROLE_T> krimRoleTs = new ArrayList<>(new KRIM_ROLE_T_DaoJpa().getAllKrimRoleTs());

        for (KRIM_ROLE_MBR_T krimRoleMbrT : krimRoleMbrTs) {
            KRIM_ROLE_T tempKrimRoleT = krimRoleMbrT.getKrimRoleT();
            if (krimRoleTs.contains(tempKrimRoleT)) {
                tempKrimRoleT.setChecked(true);
                krimRoleTs.set(krimRoleTs.indexOf(tempKrimRoleT), tempKrimRoleT);
            }
        }
        infoForm.setURMPkrimRoleTs(krimRoleTs);

        return this.getModelAndView(infoForm, "pageUpdateUserRoleManager");
    }

    /**
     * 保存用户
     *
     * @param form
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(params = "methodToCall=saveURMPuser")
    public ModelAndView saveURMPuser(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
                                     HttpServletResponse response) {
        AdminInfoForm infoForm = (AdminInfoForm) form;
        UTInstructor utInstructor = infoForm.getURMutInstructor();
        KRIM_PRNCPL_T krimPrncplT = new KRIM_PRNCPL_T_DaoJpa().getKrimEntityEntTypTByPrncplId(utInstructor.getId());
        List<KRIM_ROLE_T> krimRoleTs = infoForm.getURMPkrimRoleTs();
        new KRIM_ROLE_MBR_T_DaoJpa().saveKrimRoleMbrTByPrncpltAndRoles(krimPrncplT, krimRoleTs);
        return this.getModelAndView(infoForm, "pageUserRoleManager");
    }

    /**
     * 获取课程类别管理页面
     * 127.0.0.1:8080/tams/portal/admin?methodToCall=getCourseCategoryPage&viewId=AdminView
     *
     * @param form
     * @return
     */
    @RequestMapping(params = "methodToCall=getCourseCategoryPage")
    public ModelAndView getCourseCategoryPage(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;
        adminInfoForm.setAllClassifications(
                adminService.getAllClassification()
        );

        return this.getModelAndView(adminInfoForm, "pageCourseCategory");
    }

    /**
     * 获取课程负责人页面
     * 127.0.0.1:8080/tams/portal/admin?methodToCall=getCourseManagerPage&viewId=AdminView
     *
     * @param form
     * @return
     */
    @RequestMapping(params = "methodToCall=getCourseManagerPage")
    public ModelAndView getCourseManagerPage(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm infoForm = (AdminInfoForm) form;
        infoForm.setCourseManagerViewObjects(new AdminConverterimpl().getCourseManagerToTableViewObject(
                new TAMSCourseManagerDaoJpa().getAllCourseManager()
        ));
        return this.getModelAndView(infoForm, "pageCourseManager");
    }


    /**
     * 编辑课程负责人信息
     * 只接受来自pageCourseManager的请求
     * BUG:当前方法直接return pageid会导致url中的MTC和viewid丢失，只留下一个pageid
     *
     * @param form
     * @return 现在是关闭了btn的ajaxsubmit然后redict回pageCourseManager，需要考虑使用ajax实现局部刷新
     */
    @RequestMapping(params = {"pageId=pageCourseManager", "methodToCall=updateCourseManager"})
    public ModelAndView updateCourseManager(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm infoForm = (AdminInfoForm) form;
        CollectionControllerServiceImpl.CollectionActionParameters params =
                new CollectionControllerServiceImpl.CollectionActionParameters(infoForm, true);
        int index = params.getSelectedLineIndex();
        CourseManagerViewObject selectedObject = infoForm.getCourseManagerViewObjects().get(index);
        infoForm.setSelectedCourseManagerObject(selectedObject);
        infoForm.setCourseNm(selectedObject.getCourseNm());
        infoForm.setCourseNmb(selectedObject.getCourseNmb());
        infoForm.setCourseManager(selectedObject.getCourseManager());
        infoForm.setInstructorCode(selectedObject.getInstructorCode());
        return this.getModelAndView(infoForm, "pageCourseManager");
    }


    @RequestMapping(params = {"pageId=pageCourseManager", "methodToCall=saveUpdateCourseManager"})
    public ModelAndView saveUpdateCourseManager(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm infoForm = (AdminInfoForm) form;
        CourseManagerViewObject selectedObject = infoForm.getSelectedCourseManagerObject();
        TAMSCourseManager tamsCourseManager = tamsCourseManagerDaoJpa.getCourseManagerByInstructorId(selectedObject.getId());
        UTInstructor newManager = new UTInstructorDaoJpa().getInstructorByNameAndCode(infoForm.getCourseManager(),infoForm.getInstructorCode());
        if(newManager!=null) {
            tamsCourseManager.setCourseManagerId(newManager.getId());
            tamsCourseManagerDaoJpa.saveCourseManager(tamsCourseManager);
            //TODO 页面数据无法刷新
            return this.getCourseManagerPage(form);
        }else{
            infoForm.setErrMsg("查无此人");
        }
        //TODO 报错页面待做
        return null;
    }


    /**
     * 添加新的课程大类
     * pageCourseCategory
     *
     * @param form
     * @return
     */
    @RequestMapping(params = {"pageId=pageCourseCategory", "methodToCall=addNewCategory"})
    public ModelAndView addNewCategory(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;
        // 新添加的term，对应外部的dialog
        adminService.addCourseClassificationOnlyWithName(adminInfoForm.getNewClassification());

        return this.getCourseCategoryPage(form);
    }

    /**
     * 编辑/删除现有项返回方法
     * pageCourseCategory
     *
     * @param form
     * @return
     */
    @RequestMapping(params = {"pageId=pageCourseCategory", "methodToCall=selectCurObj"})
    public ModelAndView selectCurObj(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;

        CollectionControllerServiceImpl.CollectionActionParameters params =
                new CollectionControllerServiceImpl.CollectionActionParameters(adminInfoForm, true);
        int index = params.getSelectedLineIndex();

        adminInfoForm.setOldClassification(adminInfoForm.getAllClassifications().get(index));

        return this.getModelAndView(adminInfoForm, "pageCourseCategory");
    }

    /**
     * 编辑课程大类
     * pageCourseCategory
     *
     * @param form
     * @return
     */
    @RequestMapping(params = {"pageId=pageCourseCategory", "methodToCall=updateCourseCategory"})
    public ModelAndView updateCourseCategory(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;

        adminService.changeCourseClassificationNameById(adminInfoForm.getOldClassification().getId(),
                adminInfoForm.getOldClassification().getName());

        return this.getCourseCategoryPage(form);
    }

    /**
     * 删除课程大类
     * pageCourseCategory
     *
     * @param form
     * @return
     */
    @RequestMapping(params = {"pageId=pageCourseCategory", "methodToCall=deleteTermCourseCategory"})
    public ModelAndView deleteTermCourseCategory(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;

        if (adminService.removeCourseClassificationById(adminInfoForm.getOldClassification().getId()))
            return this.getCourseCategoryPage(form);
        else
            //应该返回错误页面
            return this.getCourseCategoryPage(form);
    }

    /**
     * 获取助教类别管理页面
     * 127.0.0.1:8080/tams/portal/admin?methodToCall=getTaCategoryPage&viewId=AdminView
     *
     * @param form
     * @return
     */
    @RequestMapping(params = "methodToCall=getTaCategoryPage")
    public ModelAndView getTaCategoryPage(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;

        adminInfoForm.setNewTaCategory(new TAMSTaCategory());

        adminInfoForm.setAllTaCategories(
                adminService.getAllTaCategories()
        );

        return this.getModelAndView(adminInfoForm, "pageTaCategory");
    }

    /**
     * 添加新的助教类别
     * pageTaCategory
     *
     * @param form
     * @return
     */
    @RequestMapping(params = {"pageId=pageTaCategory", "methodToCall=addTaCategory"})
    public ModelAndView addTaCategory(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;

        adminService.addTaCategory(adminInfoForm.getNewTaCategory());

        return this.getTaCategoryPage(form);
    }

    /**
     * 编辑/删除现有项返回方法
     * pageTaCategory
     *
     * @param form
     * @return
     */
    @RequestMapping(params = {"pageId=pageTaCategory", "methodToCall=selectCurTa"})
    public ModelAndView selectCurTa(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;

        CollectionControllerServiceImpl.CollectionActionParameters params =
                new CollectionControllerServiceImpl.CollectionActionParameters(adminInfoForm, true);
        int index = params.getSelectedLineIndex();

        adminInfoForm.setOldTaCategory(adminInfoForm.getAllTaCategories().get(index));

        return this.getModelAndView(adminInfoForm, "pageTaCategory");
    }

    /**
     * 编辑课程大类
     * pageCourseCategory
     *
     * @param form
     * @return
     */
    @RequestMapping(params = {"pageId=pageTaCategory", "methodToCall=updateTaCategory"})
    public ModelAndView updateTaCategory(@ModelAttribute("KualiForm") UifFormBase form) {
        AdminInfoForm adminInfoForm = (AdminInfoForm) form;

        adminService.changeTaCategoryByEntiy(adminInfoForm.getOldTaCategory());

        return this.getTaCategoryPage(form);
    }

    @Override
    protected UifFormBase createInitialForm() {

        return new AdminInfoForm();
    }


}