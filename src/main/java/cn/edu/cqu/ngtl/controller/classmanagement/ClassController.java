package cn.edu.cqu.ngtl.controller.classmanagement;

import cn.edu.cqu.ngtl.bo.User;
import cn.edu.cqu.ngtl.controller.BaseController;
import cn.edu.cqu.ngtl.dataobject.tams.TAMSAttachments;
import cn.edu.cqu.ngtl.dataobject.tams.TAMSClassEvaluation;
import cn.edu.cqu.ngtl.dataobject.tams.TAMSTeachCalendar;
import cn.edu.cqu.ngtl.dataobject.ut.UTClass;
import cn.edu.cqu.ngtl.form.classmanagement.ClassInfoForm;
import cn.edu.cqu.ngtl.service.classservice.IClassInfoService;
import cn.edu.cqu.ngtl.service.common.ExcelService;
import cn.edu.cqu.ngtl.service.common.impl.TamsFileControllerServiceImpl;
import cn.edu.cqu.ngtl.service.riceservice.ITAConverter;
import cn.edu.cqu.ngtl.service.taservice.ITAService;
import cn.edu.cqu.ngtl.viewobject.classinfo.ClassDetailInfoViewObject;
import cn.edu.cqu.ngtl.viewobject.classinfo.ClassTeacherViewObject;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.impl.CollectionControllerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tangjing on 16-10-20.
 * 课程管理相关的view及function
 */
@Controller
@RequestMapping("/class")
public class ClassController extends BaseController {

    @Autowired
    private IClassInfoService classInfoService;

    @Autowired
    private ITAConverter taConverter;

    @Autowired
    private ITAService taService;

    @Autowired
    private ExcelService excelService;



    @RequestMapping(params = "methodToCall=logout")
    public ModelAndView logout(@ModelAttribute("KualiForm") UifFormBase form) throws Exception {
        String redirctURL = ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY) + "/portal/home?methodToCall=logout&viewId=PortalView";
        return this.performRedirect(form, redirctURL);
    }

    /**
     * http://127.0.0.1:8080/tams/portal/class?methodToCall=getClassListPage&viewId=ClassView
     **/
    @RequestMapping(params = "methodToCall=getClassListPage")
    public ModelAndView getClassListPage(@ModelAttribute("KualiForm") UifFormBase form,
                                         HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        final UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        String uId = userSession.getLoggedInUserPrincipalId();
        infoForm.setClassList(
                taConverter.classInfoToViewObject(
                        classInfoService.getAllClassesFilterByUid(uId)
                )
        );
        return this.getModelAndView(infoForm, "pageClassList");
    }

    /**
     * pageId限定了只接受来自pageClassList的请求
     * 从classlist跳转到某个class对应的classInfoPage
     *
     * @param form
     * @return
     */
    @RequestMapping(params = {"methodToCall=getClassInfoPage"})
    public ModelAndView getClassInfoPage(@ModelAttribute("KualiForm") UifFormBase form) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);
        try {
            /**
             * param in
             */
            CollectionControllerServiceImpl.CollectionActionParameters params =
                    new CollectionControllerServiceImpl.CollectionActionParameters(infoForm, true);
            int index = params.getSelectedLineIndex();

            ClassTeacherViewObject classObject = infoForm.getClassList().get(index);
            /**
             * param in end
             */
            Integer id = classObject.getId();

            UTClass utClass = classInfoService.getClassInfoById(classObject.getId());

            ClassDetailInfoViewObject detailInfoViewObject = taConverter.classInfoToViewObject(
                    utClass
            );

            infoForm.setDetailInfoViewObject(detailInfoViewObject);

            //跳转前加上classId
            infoForm.setCurrClassId(id.toString());

        } catch (Exception e) {

        }
        return this.getModelAndView(infoForm, "pageClassInfo");
    }

    /**
     * 获取学生申请当助教页面(填表)
     * http://127.0.0.1:8080/tams/portal/class?methodToCall=getApplyTAPage&viewId=ClassView
     **/
    @RequestMapping(params = "methodToCall=getApplyTAPage")
    public ModelAndView getApplyTAPage(@ModelAttribute("KualiForm") UifFormBase form,
                                       HttpServletRequest request) {
        final UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        String stuId = userSession.getLoggedInUserPrincipalId();
        //FIXME  不能写死
        stuId = "20131840";

        Integer classId = 290739;

        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        infoForm.setApplyAssistantViewObject(
                taConverter.applyAssistantToTableViewObject(
                        classInfoService.getStudentInfoById(stuId),
                        classInfoService.getClassInfoById(classId)
                )
        );

        return this.getModelAndView(infoForm, "pageApplyForTaForm");
    }

    @RequestMapping(params = {"pageId=pageApplyForTaForm", "methodToCall=submitTaForm"})
    public ModelAndView submitTaForm(@ModelAttribute("KualiForm") UifFormBase form) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        taService.submitApplicationAssistant(taConverter.submitInfoToTaApplication(infoForm));

        return null;
    }


    /**
     * 获取教学日历页面
     * http://127.0.0.1:8080/tams/portal/class?methodToCall=getTeachingCalendar&viewId=ClassView
     **/
    @RequestMapping(params = "methodToCall=getTeachingCalendar")
    public ModelAndView getTeachingCalendar(@ModelAttribute("KualiForm") UifFormBase form,
                                            HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        final UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        String uId = userSession.getLoggedInUserPrincipalId();

        String classId = infoForm.getCurrClassId();
        if (classId == null) //// FIXME: 16-11-18 不是跳转过来应该跳转到报错页面
            return this.getModelAndView(infoForm, "pageTeachingCalendar");

        infoForm.setAllCalendar(
                taConverter.TeachCalendarToViewObject(
                        classInfoService.getAllTaTeachCalendarFilterByUidAndClassId(
                                uId,
                                classId),
                        false
                )
        );

        infoForm.setTotalElapsedTime(
                taConverter.countCalendarTotalElapsedTime(
                        infoForm.getAllCalendar()
                )
        );

        return this.getModelAndView(infoForm, "pageTeachingCalendar");
    }

    /**
     * 获取新建教学日历页面
     **/
    @RequestMapping(params = "methodToCall=getAddTeachCalendarPage")
    public ModelAndView getAddTeachCalendarPage(@ModelAttribute("KualiForm") UifFormBase form,
                                                HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        return this.getModelAndView(infoForm, "pageAddTeachCalendar");
    }

    /**
     * 获取教学日历详情页面
     **/
    @RequestMapping(params = "methodToCall=getViewTeachingCalendarPage")
    public ModelAndView getViewTeachingCalendarPage(@ModelAttribute("KualiForm") UifFormBase form,
                                                HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        CollectionControllerServiceImpl.CollectionActionParameters params = new CollectionControllerServiceImpl.CollectionActionParameters(infoForm, true);
        int index = params.getSelectedLineIndex();

        try {
            infoForm.setCurrentCalendarInfo(
                    infoForm.getAllCalendar().get(index)
            );

            //code 当做 id 用
            List<TAMSAttachments> attachments = new TamsFileControllerServiceImpl().getAllCalendarAttachments(infoForm.getAllCalendar().get(index).getCode());
            infoForm.setCalendarFiles(
                    taConverter.attachmentsToFileViewObject(attachments)
            );
            infoForm.setAllCalendar(null);  //节省内存

            String classId = infoForm.getCurrClassId();
            String uId = GlobalVariables.getUserSession().getPrincipalId();
            infoForm.setAllActivities(
                    taConverter.activitiesToViewObject(
                            classInfoService.getAllTaTeachActivityAsCalendarFilterByUidAndClassId(
                                    uId, classId)
                    )
            );
            return this.getModelAndView(infoForm, "pageViewTeachingCalendar");
        }
        catch (IndexOutOfBoundsException e) {
            // 应该返回错误页面，选择数据在内存中不存在，可能存在脏数据
            return this.getModelAndView(infoForm, "pageViewTeachingCalendar");
        }
    }

    /**
     * 下载教学日历的附件
     **/
    @RequestMapping(params = "methodToCall=downloadCalendarFile")
    public ModelAndView downloadCalendarFile(@ModelAttribute("KualiForm") UifFormBase form,
                                                HttpServletResponse response) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        String classId = infoForm.getCurrClassId();
        String calendarId = infoForm.getCurrentCalendarInfo() != null ? infoForm.getCurrentCalendarInfo().getCode() : null;
        if (classId == null || calendarId == null) //// FIXME: 16-11-18 不是跳转过来应该跳转到报错页面
            return this.getModelAndView(infoForm, "pageViewTeachingCalendar");

        CollectionControllerServiceImpl.CollectionActionParameters params = new CollectionControllerServiceImpl.CollectionActionParameters(infoForm, true);
        int index = params.getSelectedLineIndex();

        try {
            String attachmentId = infoForm.getCalendarFiles().get(index).getId();

            new TamsFileControllerServiceImpl().downloadCalendarFile(classId, calendarId, attachmentId, response);

            return this.getModelAndView(infoForm, "pageViewTeachingCalendar");
        }
        catch (IndexOutOfBoundsException e) {
            return this.getModelAndView(infoForm, "pageViewTeachingCalendar");
        }
    }

    /**
     * 删除教学日历中附件
     */
    @RequestMapping(params = "methodToCall=removeCalendarFile")
    public ModelAndView removeCalendarFile(@ModelAttribute("KualiForm") UifFormBase form,
                                           HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        String classId = infoForm.getCurrClassId();
        if (classId == null) //// FIXME: 16-11-18 不是跳转过来应该跳转到报错页面
            return this.getModelAndView(infoForm, "pageViewTeachingCalendar");

        CollectionControllerServiceImpl.CollectionActionParameters params = new CollectionControllerServiceImpl.CollectionActionParameters(infoForm, true);
        int index = params.getSelectedLineIndex();

        try {
            String attachmentId = infoForm.getCalendarFiles().get(index).getId();

            classInfoService.removeCalendarFileById(classId, attachmentId);

            return this.getViewTeachingCalendarPage(infoForm, request);
        }
        catch (IndexOutOfBoundsException e) {
            return this.getViewTeachingCalendarPage(infoForm, request);
        }
    }

    /**
     * 提交新建教学日历页面
     **/
    @RequestMapping(params = "methodToCall=submitTeachCalendarPage")
    public ModelAndView submitTeachCalendarPage(@ModelAttribute("KualiForm") UifFormBase form,
                                                HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        UserSession session = GlobalVariables.getUserSession();
        String uId = session.getPrincipalId();

        String classId = infoForm.getCurrClassId();

        String arr[] = infoForm.getAddTeachCTime().split("~");
        TAMSTeachCalendar added = infoForm.getTeachCalendar();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//infoForm.getTeachCalendar().getStartTime()  infoForm.getTeachCalendar().getEndTime()
        try {
            added.setStartTime(
                    outputFormat.format(
                            inputFormat.parse(
                                    arr[0]
                            )
                    )
            );
            added.setEndTime(
                    outputFormat.format(
                            inputFormat.parse(
                                    arr[1]
                            )
                    )
            );
        } catch (Exception e) {
            //do nothing
        }
        if(infoForm.getFileList() != null && infoForm.getFileList().size() != 0)
            added.setHasAttachment(true);

        //添加日历信息到数据库
        added = classInfoService.instructorAddTeachCalendar(uId, classId, added);
        if (added.getId() != null) { //添加数据库成功
            //添加附件
            if(added.isHasAttachment()) {
                new TamsFileControllerServiceImpl().saveCalendarAttachments(uId, classId, added.getId(), infoForm.getFileList());
            }

            return this.getTeachingCalendar(infoForm, request);
        }
        else //// FIXME: 16-11-18 应当返回错误页面
            return this.getTeachingCalendar(infoForm, request);
    }

    /**
     * 删除教学日历
     */
    @RequestMapping(params = "methodToCall=deleteTeachCalendar")
    public ModelAndView deleteTeachCalendar(@ModelAttribute("KualiForm") UifFormBase form,
                                            HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        /** uid **/
        UserSession session = GlobalVariables.getUserSession();
        String uId = session.getPrincipalId();

        /** classid **/
        String classId = infoForm.getCurrClassId();

        CollectionControllerServiceImpl.CollectionActionParameters params =
                new CollectionControllerServiceImpl.CollectionActionParameters(infoForm, true);
        int index = params.getSelectedLineIndex();

        /** calendarid **/
        String teachCalendarId = infoForm.getAllCalendar().get(index).getCode();

        if (classInfoService.removeTeachCalenderById(uId, classId, teachCalendarId)) {
            //删除教学日历的附件信息
            boolean result = classInfoService.removeAllCalendarFilesByClassIdAndCalendarId(
                    classId, teachCalendarId);

            return this.getTeachingCalendar(infoForm, request);
        }
        else //// FIXME: 16-11-18 应当返回错误页面
            return this.getTeachingCalendar(infoForm, request);
    }

    /**
     * 获取教学活动页面
     * http://127.0.0.1:8080/tams/portal/class?methodToCall=getTeachActivities&viewId=ClassView
     **/
    @RequestMapping(params = "methodToCall=getTeachActivities")
    public ModelAndView getTeachActivities(@ModelAttribute("KualiForm") UifFormBase form,
                                           HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        final UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        String uId = userSession.getLoggedInUserPrincipalId();

        String classId = infoForm.getCurrClassId();
        if (classId == null) //// FIXME: 16-11-18 不是跳转过来应该跳转到报错页面
            return this.getModelAndView(infoForm, "pageTeachingCalendar");

        infoForm.setAllActivities(
                taConverter.activitiesToViewObject(
                        classInfoService.getAllTaTeachActivityAsCalendarFilterByUidAndClassId(
                                uId, classId)
                )
        );

//        infoForm.getAllActivities();

        return this.getModelAndView(infoForm, "pageTeachActivities");
    }

    /**
     * 获取新建教学活动的页面
     * http://127.0.0.1:8080/tams/portal/class?methodToCall=getAddActivityPage&viewId=ClassView
     **/
    @RequestMapping(params = "methodToCall=getAddActivityPage")
    public ModelAndView getAddActivityPage(@ModelAttribute("KualiForm") UifFormBase form,
                                           HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        return this.getModelAndView(infoForm, "pageAddActivity");
    }

    /**
     * 根据条件查询班级列表
     *
     * @param form
     * @return
     */
    @RequestMapping(params = "methodToCall=searchClassByCondition")
    public ModelAndView searchClassByCondition(@ModelAttribute("KualiForm") UifFormBase form,
                                               HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        final UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        String uId = userSession.getLoggedInUserPrincipalId();
        Map<String, String> conditions = new HashMap<>();

        //put conditions
        conditions.put("ClassNumber", infoForm.getCondClassNumber());
        conditions.put("DepartmentId", infoForm.getCondDepartmentName());
        conditions.put("InstructorName", infoForm.getCondInstructorName());
        conditions.put("CourseName", infoForm.getCondCourseName());
        conditions.put("CourseCode", infoForm.getCondCourseCode());

        infoForm.setClassList(
                taConverter.classInfoToViewObject(
                        classInfoService.getAllClassesFilterByUidAndCondition(uId, conditions)
                )
        );

        return this.getModelAndView(infoForm, "pageClassList");
    }


    /**
     * 获取教师申请助教的页面
     * http://127.0.0.1:8080/tams/portal/class?methodToCall=getRequestTaPage&viewId=ClassView
     **/
    @RequestMapping(params = "methodToCall=getRequestTaPage")
    public ModelAndView getRequestTaPage(@ModelAttribute("KualiForm") UifFormBase form) {

        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        String uId = GlobalVariables.getUserSession().getPrincipalId();
        Integer classId = 290739;

        User instructor = (User) GlobalVariables.getUserSession().retrieveObject("user");
        infoForm.setApplyViewObject(
                taConverter.instructorAndClassInfoToViewObject(instructor, classInfoService.getClassInfoById(classId))
        );
        infoForm.setAllCalendar(
                taConverter.TeachCalendarToViewObject(
                        classInfoService.getAllTaTeachCalendarFilterByUidAndClassId(
                                uId,
                                classId.toString()),
                        true
                )
        );
        infoForm.setTotalElapsedTime(
                taConverter.countCalendarTotalElapsedTime(
                        infoForm.getAllCalendar()
                )
        );
        infoForm.setTotalBudget(
                taConverter.countCalendarTotalBudget(
                        infoForm.getAllCalendar()
                )
        );
        infoForm.setClassEvaluations(new ArrayList<TAMSClassEvaluation>());

        return this.getModelAndView(infoForm, "pageRequestTa");
    }

    /**
     * 成绩评定删除辅助方法
     *
     * @param form
     * @return
     */
    @RequestMapping(params = "methodToCall=deleteEvaluationLine")
    public ModelAndView deleteEvaluationLine(@ModelAttribute("KualiForm") UifFormBase form) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);
        CollectionControllerServiceImpl.CollectionActionParameters params =
                new CollectionControllerServiceImpl.CollectionActionParameters(infoForm, true);
        int index = params.getSelectedLineIndex();

        infoForm.getClassEvaluations().remove(index);

        return this.getModelAndView(infoForm, "pageRequestTa");
    }

    /**
     * 教师提交申请助教的请求
     */
    @RequestMapping(params = "methodToCall=submitRequestTaPage")
    public ModelAndView submitRequestTaPage(@ModelAttribute("KualiForm") UifFormBase form,
                                            HttpServletRequest request) {

        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        String assistantNumber = infoForm.getApplyViewObject().getAssistantNumber();
        List<TAMSClassEvaluation> classEvaluations = infoForm.getClassEvaluations();
        String classId = "290739";
        String instructorId = GlobalVariables.getUserSession().getPrincipalId();
        boolean result = classInfoService.instructorAddClassTaApply(instructorId, classId, assistantNumber, classEvaluations);

        return this.getModelAndView(infoForm, "pageRequestTa");
    }


    /**
     * 将表格打印为excel，整体可用，各列具体参数还需修改
     *
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(params = {"pageId=pageClassList", "methodToCall=exportClassListExcel"})
    public ModelAndView exportClassListExcel(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
                                             HttpServletResponse response) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);


        if (infoForm.getClassList() == null) {
            // TODO: 2016/10/21 错误处理
//            examForm.setErrMsg("导出内容为空");
            return this.showDialog("errWarnDialog", true, infoForm);
        }

        List<ClassTeacherViewObject> classList = infoForm.getClassList();
        String fileName = "教学班列表" + "-" + GlobalVariables.getUserSession().getLoggedInUserPrincipalId() + "-" + System.currentTimeMillis() + ".xls";

        try {
            String filePath = excelService.printClasslistExcel(classList, "exportfolder", fileName, "2003");
            String baseUrl = CoreApiServiceLocator.getKualiConfigurationService()
                    .getPropertyValueAsString(KRADConstants.ConfigParameters.APPLICATION_URL);

            return this.performRedirect(infoForm, baseUrl + File.separator + filePath);
        } catch (IOException e) {
            String baseUrl = CoreApiServiceLocator.getKualiConfigurationService()
                    .getPropertyValueAsString(KRADConstants.ConfigParameters.APPLICATION_URL);
            return this.performRedirect(infoForm, baseUrl + "/tams");
        }
    }

    /**
     * 上传文件方法
     */
    @Override
    public ModelAndView addFileUploadLine(UifFormBase form) {
        return super.addFileUploadLine(form);
    }

    /**
     * 文件上传之后，点击文件名(href)就会调用此方法
     */
    @Override
    public void getFileFromLine(UifFormBase form, HttpServletResponse response) {
        new TamsFileControllerServiceImpl().getFileFromLine(form, response);
    }

    @Override
    protected UifFormBase createInitialForm() {
        // 开发用的setService,不设置这个那么上传时会调用默认的fileService(有中文乱码等问题)
//        setFileControllerService(new TamsFileControllerServiceImpl());
        return new ClassInfoForm();
    }

    @RequestMapping(params = "methodToCall=getTAInfoPage")
    public ModelAndView getTAInfoPage(@ModelAttribute("KualiForm") UifFormBase form) {
        return null;
    }

    //我的助教（教师用户看到的）(管理助教)界面
    /**
     * 获取助教管理页面(包含我的助教列表+申请助教列表)
     * 127.0.0.1:8080/tams/portal/ta?methodToCall=getTaManagementPage&viewId=TaView
     * @param form
     * @return
     */
    @RequestMapping(params = "methodToCall=getTaManagementPage")
    public ModelAndView getTaManagementPage(@ModelAttribute("KualiForm") UifFormBase form,
                                            HttpServletRequest request) {
        ClassInfoForm infoForm = (ClassInfoForm) form;
        super.baseStart(infoForm);

        final UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        String uId = userSession.getLoggedInUserPrincipalId();

        String classId = infoForm.getCurrClassId();

        infoForm.setAllMyTa(taConverter.myTaCombinePayDayClass(
                classInfoService.getAllTaFilteredByClassid(classId)
        ));


        infoForm.setAllApplication(taConverter.applicationToViewObjectClass(
                taService.getAllApplicationFilterByUid(uId)
        ));
        return this.getModelAndView(infoForm, "pageTaManagement");
    }

}
