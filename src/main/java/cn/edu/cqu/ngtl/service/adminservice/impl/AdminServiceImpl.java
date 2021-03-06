package cn.edu.cqu.ngtl.service.adminservice.impl;

import cn.edu.cqu.ngtl.bo.User;
import cn.edu.cqu.ngtl.dao.cm.CMCourseClassificationDao;
import cn.edu.cqu.ngtl.dao.krim.KRIM_ROLE_MBR_T_Dao;
import cn.edu.cqu.ngtl.dao.krim.KRIM_ROLE_T_Dao;
import cn.edu.cqu.ngtl.dao.krim.impl.KRIM_PRNCPL_T_DaoJpa;
import cn.edu.cqu.ngtl.dao.tams.*;
import cn.edu.cqu.ngtl.dao.ut.*;
import cn.edu.cqu.ngtl.dao.ut.impl.UTSessionDaoJpa;
import cn.edu.cqu.ngtl.dataobject.cm.CMCourseClassification;
import cn.edu.cqu.ngtl.dataobject.enums.SESSION_ACTIVE;
import cn.edu.cqu.ngtl.dataobject.krim.KRIM_PRNCPL_T;
import cn.edu.cqu.ngtl.dataobject.krim.KRIM_ROLE_T;
import cn.edu.cqu.ngtl.dataobject.tams.*;
import cn.edu.cqu.ngtl.dataobject.ut.UTClassInstructor;
import cn.edu.cqu.ngtl.dataobject.ut.UTDepartment;
import cn.edu.cqu.ngtl.dataobject.ut.UTInstructor;
import cn.edu.cqu.ngtl.dataobject.ut.UTSession;
import cn.edu.cqu.ngtl.dataobject.view.UTClassInformation;
import cn.edu.cqu.ngtl.service.adminservice.IAdminService;
import cn.edu.cqu.ngtl.service.exportservice.IPDFService;
import cn.edu.cqu.ngtl.service.userservice.IUserInfoService;
import cn.edu.cqu.ngtl.tools.utils.TimeUtil;
import cn.edu.cqu.ngtl.viewobject.adminInfo.*;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.krad.util.GlobalVariables.getUserSession;

/**
 * Created by tangjing on 16-10-25.
 */
@Service
@Component("AdminServiceImpl")
public class AdminServiceImpl implements IAdminService {

    private static final Logger logger = Logger.getRootLogger();
    private static final String COURSE_MANAGER_ROLE_NAME = "课程负责人";
    private static final String TEACHER_ROLE_NAME = "教师";
    @Autowired
    private TAMSTimeSettingTypeDao timeSettingTypeDao;
    @Autowired
    private IUserInfoService iUserInfoService;
    @Autowired
    private TAMSDeptFundingDraftDao tamsDeptFundingDraftDao;
    @Autowired
    private UTInstructorDao utInstructorDao;
    @Autowired
    private TAMSDeptFundingDao tamsDeptFundingDao;
    @Autowired
    private TAMSClassFundingDao tamsClassFundingDao;
    @Autowired
    private TAMSClassFundingDraftDao tamsClassFundingDraftDao;
    @Autowired
    private CMCourseClassificationDao courseClassificationDao;
    @Autowired
    private TAMSTaCategoryDao tamsTaCategoryDao;
    @Autowired
    private TAMSIssueTypeDao issueTypeDao;
    @Autowired
    private UTSessionDao sessionDao;
    @Autowired
    private UTDepartmentDao utDepartmentDao;
    @Autowired
    private TAMSDeptFundingDao deptFundingDao;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private TAMSTimeSettingsDao timeSettingsDao;
    @Autowired
    private TAMSWorkflowStatusRDao workflowStatusRDao;
    @Autowired
    private TAMSWorkflowStatusDao workflowStatusDao;
    @Autowired
    private TAMSWorkflowRoleFunctionDao workflowRoleFunctionDao;
    //课程负责人过滤
    @Autowired
    private TAMSCourseManagerDao tamsCourseManagerDao;
    @Autowired
    private TAMSUniversityFundingDao tamsUniversityFundingDao;
    @Autowired
    private UTCourseDao utCourseDao;
    @Autowired
    private KRIM_ROLE_MBR_T_Dao krim_role_mbr_t_dao;
    @Autowired
    private KRIM_ROLE_T_Dao krim_role_t_dao;
    @Autowired
    private KRIM_PRNCPL_T_DaoJpa krim_prncpl_t_dao;
    @Autowired
    private TAMSTaDao tamsTaDao;
    @Autowired
    private UTClassInfoDao utClassInfoDao;
    @Autowired
    private TAMSTaBlackListDao tamsTaBlackListDao;
    @Autowired
    private UTClassInstructorDao utClassInstructorDao;

    @Autowired
    IPDFService PDFService;

    @Autowired
    private TAMSTaTravelSubsidyDao tamsTaTravelSubsidyDao;


    @Override
    public List<TAMSDeptFundingDraft> getAllDeptFundingDraft() {
        return tamsDeptFundingDraftDao.selectAll();
    }

    @Override
    public List<TAMSCourseManager> getCourseManagerByCondition(Map<String, String> conditions,User user) {
        List<TAMSCourseManager> tamsCourseManagers = new ArrayList<>();
        if(userInfoService.isCollegeStaff(user.getCode())){
            tamsCourseManagers = tamsCourseManagerDao.selectCourseManagerByConditionWithDeptId(conditions,user.getDepartmentId()==null?"":user.getDepartmentId().toString());
        }else {
            tamsCourseManagers = tamsCourseManagerDao.selectCourseManagerByCondition(conditions);
        }
        return tamsCourseManagers;
    }

    //历史批次经费过滤
    @Override
    public List<TAMSUniversityFunding> getUniFundPreByCondition(Map<String, String> conditions) {
        List<TAMSUniversityFunding> tamsUniversityFundings = tamsUniversityFundingDao.selectUniFundPreByCondition(conditions);
        return tamsUniversityFundings;
    }

    //课程经费过滤
    @Override
    public List<ClassFundingViewObject> getClassFundByCondition(Map<String, String> conditions) {
        User user = (User) getUserSession().retrieveObject("user");
        /**如果是教务处管理员或者系统管理员则显示草稿表的内容，在下拉框里显示发布的数据
         */
        if (userInfoService.isAcademicAffairsStaff(user.getCode()) || userInfoService.isSysAdmin(user.getCode())) {
            return tamsClassFundingDraftDao.selectClassFundDraftByCondition(conditions);
        } else {
            return tamsClassFundingDao.selectClassFundByCondition(conditions);
        }

    }
    //助教经费过滤

    @Override
    public List<TaFundingViewObject> getTaFundByCondition(Map<String, String> conditions) {
        List<TaFundingViewObject> taFundingViewObjects = tamsTaDao.selectTaFundByCondition(conditions);
        return taFundingViewObjects;
    }

    //经费明细过滤
    @Override
    public List<DetailFundingViewObject> getDetailFundByCondition(Map<String, String> conditions) {
        return tamsTaDao.selectDetailFundByCondition(conditions);
    }

    @Override
    public List<CMCourseClassification> getAllClassification() {
        List<CMCourseClassification> courseClassifications = courseClassificationDao.selectAll();
        return courseClassifications.size() != 0 ? courseClassifications :null;
    }

    //获取工作流类型
    @Override
    public List<TAMSWorkflowStatus> getWorkFlowCategory() {
        List<TAMSWorkflowStatus> workflowStatuses = workflowStatusDao.selectAll();
        return workflowStatuses.size() != 0 ? workflowStatuses :null;
    }

    //工作流类型过滤
    @Override
    public List<TAMSWorkflowStatus> getWorkFlowCategoryByCondition(String workflowfunction) {
        List<TAMSWorkflowStatus> workflowStatuses = workflowStatusDao.selectWorkFlowByCondition(workflowfunction);
        return workflowStatuses.size() != 0 ? workflowStatuses :null;
    }

    /*
    //添加工作流类型
    @Override
    public boolean addWorkFlowCategory(Map<String, String> conditions) {
        if(workflowStatusDao.insertOne(conditions))
            return true;
        else
            return false;
    }

    //修改工作流类型
    @Override
    public boolean modifyWorkFlowCategory(Map<String, String> conditions, String status, String order) {
        if(workflowStatusDao.modifyOne(conditions,status,order))
            return true;
        else
            return false;
    }
    */

    //保存工作流类型对象
    @Override
    public boolean saveWorkFlowCategory(TAMSWorkflowStatus tamsWorkflowStatus) {
        if (workflowStatusDao.saveOne(tamsWorkflowStatus))
            return true;
        else
            return false;
    }


    //删除工作流类型
    @Override
    public boolean deleteWorkFlowCategory(TAMSWorkflowStatus tamsWorkflowStatus) {
        if (workflowStatusDao.deleteOne(tamsWorkflowStatus))
            return true;
        else
            return false;
    }

    @Override
    public boolean addCourseClassificationOnlyWithName(String name) {

        if (courseClassificationDao.selectOneByName(name) != null)
            //存在同名数据
            return false;

        CMCourseClassification courseClassification = new CMCourseClassification();
        courseClassification.setName(name);

        return courseClassificationDao.insertOneByEntity(courseClassification);
    }

    @Override
    public boolean changeCourseClassificationNameById(Integer id, String name) {
        CMCourseClassification courseClassification = courseClassificationDao.selectOneById(id);

        if (courseClassification == null)
            return false;
        if (courseClassificationDao.selectOneByName(name) != null)
            //存在同名数据
            return false;

        courseClassification.setId(id);
        courseClassification.setName(name);

        return courseClassificationDao.updateOneByEntity(courseClassification);
    }

    @Override
    public boolean removeCourseClassificationById(Integer id) {
        CMCourseClassification courseClassification = courseClassificationDao.selectOneById(id);

        if (courseClassification == null)
            return false;

        return courseClassificationDao.deleteOneByEntity(courseClassification);
    }

    @Override
    public List<TAMSTaCategory> getAllTaCategories() {
        List<TAMSTaCategory> tamsTaCategories = tamsTaCategoryDao.selectAll();
        return tamsTaCategories.size() != 0 ? tamsTaCategories :null;
    }

    @Override
    public boolean addTaCategory(TAMSTaCategory newTaCategory) {
        if (tamsTaCategoryDao.selectOneByName(newTaCategory.getName()) != null)
            //存在同名数据
            return false;

        return tamsTaCategoryDao.insertOneByEntity(newTaCategory);
    }

    @Override
    public boolean changeTaCategoryByEntiy(TAMSTaCategory tamsTaCategory) {
        TAMSTaCategory isExist = tamsTaCategoryDao.selectOneByName(tamsTaCategory.getName());
        if (isExist != null && !isExist.getId().equals(tamsTaCategory.getId()))
            //存在同名数据,而且非本数据
            return false;

        return tamsTaCategoryDao.updateOneByEntity(tamsTaCategory);
    }

    @Override
    public boolean removeTaCategoryById(Integer id) {
        TAMSTaCategory tamsTaCategory = tamsTaCategoryDao.selectOneById(id);

        if (tamsTaCategory == null)
            return false;

        return tamsTaCategoryDao.deleteOneByEntity(tamsTaCategory);
    }

    @Override
    public List<TAMSIssueType> getAllIssueTypes() {

        return issueTypeDao.selectAll();

    }

    @Override
    public boolean addTaIssueType(TAMSIssueType issueType) {

        TAMSIssueType isInDataBase = issueTypeDao.selectOneByTypeName(issueType.getTypeName());

        if (isInDataBase != null)
            return false;

        return issueTypeDao.insertOneByEntity(issueType);
    }

    @Override
    public List<TAMSCourseManager> getAllCourseManager() {

        List<TAMSCourseManager> allTamsCourseManagers = tamsCourseManagerDao.getAllCourseManager();

        return allTamsCourseManagers;
    }

    @Override
    public List<UTSession> getAllSessions() {

        return sessionDao.selectAll();

    }

    @Override
    public List<UTSession> getSelectedSessions(String termName, String startTime, String endTime) throws ParseException {
        return sessionDao.selectByCondition(termName, startTime, endTime);
    }

    @Override
    public boolean addSession(UTSession session) {
        UTSession isExist = sessionDao.selectByYearAndTerm(session.getYear(), session.getTerm());

        if (isExist != null)
            return false;

        //新建数据的预处理
        session.setActive(SESSION_ACTIVE.NO);
        //// FIXME: 16-10-27 还需要处理预算

        if (sessionDao.insertOneByEntity(session)) {
            //批次经费初始化
            TAMSUniversityFunding tamsUniversityFunding = new TAMSUniversityFunding();
            Integer sessionId = sessionDao.selectSessionByCondition(session.getTerm(), session.getYear());
            tamsUniversityFunding.setSessionId(sessionId);
            tamsUniversityFunding.setUniversityId("1");
            tamsUniversityFunding.setPlanFunding("0");
            tamsUniversityFunding.setActualFunding("0");
            tamsUniversityFunding.setApplyFunding("0");
            tamsUniversityFunding.setPhdFunding("0");
            tamsUniversityFunding.setBonus("0");
            tamsUniversityFunding.setTravelSubsidy("0");
            tamsUniversityFundingDao.insertOneByEntity(tamsUniversityFunding);
            //学院经费草稿初始化
            List<TAMSDeptFundingDraft> tamsDeptFundingDrafts = new ArrayList<>();
            //学院经费表初始化
            List<TAMSDeptFunding> tamsDeptFundings = new ArrayList<>();
            //只初始化有课程的学院
            List<UTDepartment> departments = utDepartmentDao.getAllHasCourseDepartment();
            for (int i = 0; i < departments.size(); i++) {
                TAMSDeptFundingDraft tamsDeptFundingDraft = new TAMSDeptFundingDraft();
                tamsDeptFundingDraft.setSessionId(sessionId);
                tamsDeptFundingDraft.setDepartmentId(departments.get(i).getId());
                tamsDeptFundingDraft.setPlanFunding("0");
                tamsDeptFundingDraft.setActualFunding("0");
                tamsDeptFundingDraft.setApplyFunding("0");
                tamsDeptFundingDraft.setPhdFunding("0");
                tamsDeptFundingDraft.setBonus("0");
                tamsDeptFundingDraft.setTravelSubsidy("0");
                tamsDeptFundingDrafts.add(tamsDeptFundingDraft);

                TAMSDeptFunding tamsDeptFunding = new TAMSDeptFunding();
                tamsDeptFunding.setSessionId(sessionId);
                tamsDeptFunding.setDepartmentId(departments.get(i).getId());
                tamsDeptFunding.setPlanFunding("0");
                tamsDeptFunding.setActualFunding("0");
                tamsDeptFunding.setApplyFunding("0");
                tamsDeptFunding.setPhdFunding("0");
                tamsDeptFunding.setBonus("0");
                tamsDeptFunding.setTravelSubsidy("0");
                tamsDeptFundings.add(tamsDeptFunding);
            }
            tamsDeptFundingDraftDao.saveBatchByEntities(tamsDeptFundingDrafts);



            return true;
        } else
            return false;
    }


    @Override
    public boolean changeIssueType(TAMSIssueType issueType) {
        TAMSIssueType isExist = issueTypeDao.selectOneByTypeName(issueType.getTypeName());
        if (isExist != null && !isExist.getId().equals(issueType.getId()))
            //存在同名数据,而且非本数据
            return false;

        return issueTypeDao.updateOneByEntity(issueType);
    }

    @Override
    public boolean removeIssueTypeById(String id) {
        TAMSIssueType issueType = issueTypeDao.selectOneById(id);

        if (issueType == null)
            return false;

        return issueTypeDao.deleteOneByEntity(issueType);
    }

    @Override
    public boolean changeSession(UTSession session) {
        return sessionDao.updateOneByEntity(session);
    }

    @Override
    public boolean removeTermByYearAndTerm(String termYear, String termTerm) {
        UTSession session = sessionDao.selectByYearAndTerm(termYear, termTerm);

        if (session == null)
            return false;

        return sessionDao.deleteOneByEntity(session);
    }


    @Override
    public boolean setCurrentSession(String termYear, String termTerm) {

        UTSession session = sessionDao.selectByYearAndTerm(termYear, termTerm);
        if (session.getActive().equals("Y")) {
            return true;
        } else {
            UTSession utSession = sessionDao.getCurrentSession();
            utSession.setActive("N");
            sessionDao.updateOneByEntity(utSession);
            session.setActive("Y");
            return sessionDao.updateOneByEntity(session);

        }
    }

    @Override
    public List<UTInstructor> getInstructorByconditions(Map<String, String> conditions) {
        return utInstructorDao.getInstructorByConditions(conditions);
    }


    /*
    @Override
    public List<TAMSDeptFunding> getCurrFundingBySession() {

        return deptFundingDao.selectCurrBySession();

    }

    @Override
    public List<TAMSDeptFunding> getPreviousFundingBySession() {

        return deptFundingDao.selectPreBySession();

    }
    */

    //获取学校经费
    @Override
    public List<TAMSUniversityFunding> getCurrFundingBySession() {

        return tamsUniversityFundingDao.selectCurrBySession();

    }

    @Override
    public List<TAMSUniversityFunding> getPreviousFundingBySession() {

        return tamsUniversityFundingDao.selectPreBySession();

    }

    //获取学院当前经费
    @Override
    public List<TAMSDeptFunding> getDepartmentCurrFundingBySession() {

        User user = (User) getUserSession().retrieveObject("user");

        /**如果是教务处管理员或者系统管理员则显示草稿表的内容，在下拉框里显示发布的数据
         */
        if (userInfoService.isAcademicAffairsStaff(user.getCode()) || userInfoService.isSysAdmin(user.getCode())) {
            return tamsDeptFundingDraftDao.selectDepartmentCurrDraftBySession();
        } else
            return deptFundingDao.selectDepartmentCurrBySession();
    }

    //学院当前经费过滤
    public List<TAMSDeptFunding> getDeptFundCurrByCondition(Map<String, String> conditions) {
        User user = (User) getUserSession().retrieveObject("user");

        /**如果是教务处管理员或者系统管理员则显示过滤后草稿表的内容，在下拉框里显示发布的数据
         */
        if (userInfoService.isAcademicAffairsStaff(user.getCode()) || userInfoService.isSysAdmin(user.getCode())) {
            List<TAMSDeptFunding> list = tamsDeptFundingDraftDao.selectDeptFundDraftCurrByCondition(conditions);
            return list;
        } else {
            return tamsDeptFundingDao.selectDeptFundCurrByCondition(conditions);
        }
    }

    //获取学院历史经费
    @Override
    public List<TAMSDeptFunding> getDepartmentPreFundingBySession() {

        //DAO里面直接通过根据不同的角色传deptId的值来区别学院管理员和学校管理员的显示
        return deptFundingDao.selectDepartmentPreBySession();
    }

    @Override
    public List<TAMSDeptFunding> getDepartmentPreFundingByCondition(Map<String, String> conditions) {
        /*
        if (!userInfoService.isSysAdmin(uId)){
            List<TAMSDeptFunding> deptFundings = deptFundingDao.selectDeptFundPreByCondition(conditions);
            return deptFundings;
        }
        return null;
        */
        return deptFundingDao.selectDeptFundPreByCondition(conditions);
    }

    @Override
    public List<TAMSWorkflowStatusR> getWorkflowStatusRelationByRoleFunctionId(String roleFunctionId) {
        if (roleFunctionId == null)
            return null;

        return workflowStatusRDao.selectByRFId(roleFunctionId);

    }

    @Override
    public String getRoleFunctionIdByRoleIdAndFunctionId(String roleId, String functionId) {
        if (roleId == null || functionId == null)
            return null;

        return workflowRoleFunctionDao.selectIdByRoleIdAndFunctionId(roleId, functionId);
    }

    @Override
    public String setRoleFunctionIdByRoleIdAndFunctionId(String roleId, String functionId) {
        //如果找得到RFId就找找不到就创建新的
        String roleFunctionId = this.getRoleFunctionIdByRoleIdAndFunctionId(roleId, functionId);

        if (roleFunctionId == null) {
            TAMSWorkflowRoleFunction workflowRoleFunction = new TAMSWorkflowRoleFunction();
            workflowRoleFunction.setRoleId(roleId);
            workflowRoleFunction.setWorkflowFunctionId(functionId);

            //新增roleFunctionPair
            workflowRoleFunction = workflowRoleFunctionDao.insertByEntity(workflowRoleFunction);

            roleFunctionId = workflowRoleFunction.getId();
        }

        return roleFunctionId;
    }

    @Override
    public void setWorkflowStatusRelationByRoleFunctionId(String functionId, String rfId, RelationTable rt) {
        //将原有的RFId的值删除，再加入新的值
        workflowStatusRDao.deleteTAMSWorkflowStatusRByRFId(rfId);

        List<TAMSWorkflowStatus> allStatus = workflowStatusDao.selectByFunctionId(functionId);

        int length = allStatus.size();
        CheckBoxStatus[][] matrix = rt.getData();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (matrix[i][j].isChecked()) {
                    TAMSWorkflowStatusR dataWorkflowStatusR = new TAMSWorkflowStatusR();
                    dataWorkflowStatusR.setStatusId1(allStatus.get(i).getId());
                    dataWorkflowStatusR.setStatusId2(allStatus.get(j).getId());
                    dataWorkflowStatusR.setRoleFunctionId(rfId);

                    workflowStatusRDao.saveTAMSWorkflowStatusR(dataWorkflowStatusR);
                }
            }
        }
    }


    @Override
    public List<TAMSClassFunding> getFundingByClass() {
        User user = (User) getUserSession().retrieveObject("user");

        /**如果是教务处管理员或者系统管理员则显示草稿表的内容，在下拉框里显示发布的数据
         */
        if (userInfoService.isAcademicAffairsStaff(user.getCode()) || userInfoService.isSysAdmin(user.getCode()) || userInfoService.isCollegeStaff(user.getCode())) {
            return tamsClassFundingDraftDao.selectAll();
        }
        //如果是老师，待定
        else {
            return tamsClassFundingDao.selectAll(user);
        }
    }

    @Override
    public boolean addTimeSetting(User user, String typeId, String startTime, String endTime) {
        if (!userInfoService.hasPermission(user, "ViewConsolePage")) {
            logger.warn("未授权用户请求：(新增时间段)addTimeSetting(User user, String typeId, String startTime, String endTime)\n");
            logger.warn("未授权用户信息："+user.toString()+"\n");
            return false;
        }
        TAMSTimeSettings isExist = timeSettingsDao.selectByTypeId(typeId);
        if (isExist != null) {
            logger.warn("管理员请求新增时间段失败："+user.toString()+"\n");
            logger.warn("本学期对应类型的时间段已设置\n");
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                isExist.setStartTime(
                        output.format(
                                input.parse(startTime)
                        )
                );
                isExist.setEndTime(
                        output.format(
                                input.parse(endTime)
                        )
                );
            } catch (ParseException e) {
                logger.error("输入日期格式不正确！");
                return false;
            }

            isExist.setEditTime(output.format(new Date()));
            return timeSettingsDao.updateOneByEntity(isExist);


        }
        TAMSTimeSettings timeSetting = new TAMSTimeSettings();
        timeSetting.setTimeSettingTypeId(typeId);
        timeSetting.setSessionId(sessionDao.getCurrentSession().getId().toString());

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            timeSetting.setStartTime(
                    output.format(
                            input.parse(startTime)
                    )
            );
            timeSetting.setEndTime(
                    output.format(
                            input.parse(endTime)
                    )
            );
        } catch (ParseException e) {
            logger.error("输入日期格式不正确！");
            return false;
        }
        timeSetting.setEditTime(output.format(new Date()));

        return timeSettingsDao.insetOneByEntity(timeSetting);
    }


    @Override
    public boolean deleteOneTimeSetting(TAMSTimeSettings tamsTimeSettings) {
        return timeSettingsDao.deleteOneByEntity(tamsTimeSettings);
    }

    @Override
    public List<TAMSTimeSettings> getallTimeSettings() {
        return timeSettingsDao.selectAllCurrentSession();
    }


    @Override
    public Long releaseDeptFunding(List<DepartmentFundingViewObject> departmentFundingViewObjects) {
        UTSession curSession = sessionDao.getCurrentSession();
        Long result = 0l;
        for (DepartmentFundingViewObject per : departmentFundingViewObjects) {
            TAMSDeptFunding exist = deptFundingDao.selectDeptFundsByDeptIdAndSession(per.getDepartmentId(), curSession.getId());
            TAMSDeptFundingDraft existDraft = tamsDeptFundingDraftDao.selectDeptDraftFundsByDeptIdAndSession(per.getDepartmentId(), curSession.getId());
            //将管理员设置的数额保存到正式部门经费表
            if (exist == null) {
                TAMSDeptFunding tamsDeptFunding = new TAMSDeptFunding();
                tamsDeptFunding.setSessionId(curSession.getId());
                tamsDeptFunding.setDepartmentId(per.getDepartmentId());
                tamsDeptFunding.setActualFunding(per.getActualFunding());
                tamsDeptFunding.setBonus(per.getBonus());
                tamsDeptFunding.setApplyFunding(per.getApplyFunding());
                tamsDeptFunding.setPhdFunding(per.getPhdFunding());
                tamsDeptFunding.setPlanFunding(per.getPlanFunding());
                tamsDeptFunding.setTravelSubsidy(per.getTrafficFunding());
                deptFundingDao.saveOneByEntity(tamsDeptFunding);

                //保存批准经费变更到批次经费 如果之前没有发布过，那么直接将金额加到现有的批准经费上面
                List<TAMSUniversityFunding> tamsUniversityFundings = tamsUniversityFundingDao.selectCurrBySession();
                if(tamsUniversityFundings!=null){
                    if(tamsUniversityFundings.get(0)!=null){
                        TAMSUniversityFunding tamsUniversityFundingExist = tamsUniversityFundings.get(0);
                        Long oldActualFunds = Long.valueOf(tamsUniversityFundingExist.getActualFunding());
                        Long newActualFunds = oldActualFunds + Long.valueOf(per.getActualFunding());
                        result += Long.valueOf(per.getActualFunding());
                        tamsUniversityFundingExist.setActualFunding(newActualFunds.toString());
                        tamsUniversityFundingDao.insertOneByEntity(tamsUniversityFundingExist);
                    }
                }

            } else {
                if (!per.getActualFunding().equals(exist.getActualFunding()) || !per.getPlanFunding().equals(exist.getPlanFunding())) { //如果金额有变化
                    Long changedFunds = Long.valueOf(per.getActualFunding())-Long.valueOf(exist.getActualFunding()); //变化额度等于新设置的金额减去旧金额
                    result += changedFunds;
                    exist.setActualFunding(per.getActualFunding()); //保存批准经费
                    exist.setPlanFunding(per.getPlanFunding());//保存计划经费
                    deptFundingDao.saveOneByEntity(exist);

                    //保存批准经费变更到批次经费 如果之前发布过，那么是加上变化的额度
                    List<TAMSUniversityFunding> tamsUniversityFundings = tamsUniversityFundingDao.selectCurrBySession();
                    if(tamsUniversityFundings!=null){
                        if(tamsUniversityFundings.get(0)!=null){
                            TAMSUniversityFunding tamsUniversityFundingExist = tamsUniversityFundings.get(0);
                            Long oldActualFunds = Long.valueOf(tamsUniversityFundingExist.getActualFunding());
                            Long newActualFunds = oldActualFunds + changedFunds;
                            tamsUniversityFundingExist.setActualFunding(newActualFunds.toString());
                            tamsUniversityFundingDao.insertOneByEntity(tamsUniversityFundingExist);
                        }
                    }
                }
            }
            //保存草稿表
            existDraft.setActualFunding(per.getActualFunding());//保存批准经费
            existDraft.setPlanFunding(per.getPlanFunding());//保存计划经费
            tamsDeptFundingDraftDao.saveOneByEntity(existDraft);
        }
        return result;
    }

    //发布课程经费
    @Override
    public boolean releaseClassFunding(List<ClassFundingViewObject> classFundingViewObjects) {
        UTSession curSession = sessionDao.getCurrentSession();
        for (ClassFundingViewObject per : classFundingViewObjects) {
            TAMSClassFunding exist = tamsClassFundingDao.getOneByClassIdAndSessionId(per.getClassId(), curSession.getId().toString());
            TAMSClassFundingDraft existDraft = tamsClassFundingDraftDao.selectOneByClassIdAndSessionId(per.getClassId(), curSession.getId().toString());
            //若为空，则直接将classfundingdraft中的经费添加到classfunding里面去
            if (exist == null) {
                TAMSClassFunding tamsClassFunding = new TAMSClassFunding();
                tamsClassFunding.setSessionId(curSession.getId().toString());
                tamsClassFunding.setClassId(per.getClassId());
                tamsClassFunding.setApplyFunding(per.getApplyFunding());
                tamsClassFunding.setAssignedFunding(per.getAssignedFunding());
                tamsClassFunding.setPhdFunding(per.getPhdFunding());
                tamsClassFunding.setBonus(per.getBonus());
                tamsClassFunding.setTravelSubsidy(per.getTravelSubsidy());
                tamsClassFundingDao.saveOneByEntity(tamsClassFunding);
            }
            //若不为空，则修改对应的classfunding
            else {
                if (!per.getAssignedFunding().equals(exist.getAssignedFunding())) {
                    exist.setAssignedFunding(per.getAssignedFunding());
                    tamsClassFundingDao.saveOneByEntity(exist);
                }
            }
            //保存草稿
            existDraft.setAssignedFunding(per.getAssignedFunding());
            tamsClassFundingDraftDao.insertOneByEntity(existDraft);
        }
        return true;
    }

    //保存时计算学院经费
    @Override
    public Integer countDeptFunding(List<DepartmentFundingViewObject> departmentFundingViewObjects) {
        UTSession curSession = sessionDao.getCurrentSession();
        List<Integer> departmentIds = new ArrayList<>();
        for(DepartmentFundingViewObject per : departmentFundingViewObjects) {
            Integer departmentId = per.getDepartmentId();
            departmentIds.add(departmentId);
        }
        //差值
        Integer subSetTotalPlan = 0;
        Integer subSetTotalAssigned = 0;
        for(int i=0; i<departmentIds.size(); i++) {
            Integer data = Integer.parseInt(tamsDeptFundingDraftDao.selectDeptDraftFundsByDeptIdAndSession(departmentIds.get(i), curSession.getId()).getPlanFunding());
            Integer data1 = Integer.parseInt(tamsDeptFundingDraftDao.selectDeptDraftFundsByDeptIdAndSession(departmentIds.get(i), curSession.getId()).getActualFunding());
            subSetTotalPlan = subSetTotalPlan +
                    Integer.parseInt(departmentFundingViewObjects.get(i).getPlanFunding()) - data
            ;
            subSetTotalAssigned = subSetTotalAssigned +
                    Integer.parseInt(departmentFundingViewObjects.get(i).getActualFunding()) - data1
            ;
        }
        //原来的值
        Integer setTotalPlan = 0;
        Integer setTotalAssigned = 0;
        List<TAMSDeptFundingDraft> tamsDeptFundingDraft = tamsDeptFundingDraftDao.selectAll();
        for(TAMSDeptFundingDraft per : tamsDeptFundingDraft)  {
            setTotalPlan  = setTotalPlan + Integer.parseInt(per.getPlanFunding());
            setTotalAssigned = setTotalAssigned + Integer.parseInt(per.getActualFunding());
        }
        if ((setTotalPlan + subSetTotalPlan )> Integer.parseInt(tamsUniversityFundingDao.selectCurrBySession().get(0).getPlanFunding()))
            return 1;
        if ((setTotalAssigned + subSetTotalAssigned) > Integer.parseInt(tamsUniversityFundingDao.selectCurrBySession().get(0).getActualFunding()))
            return 2;
        else
            return 3;
    }

    //计算课程经费
    @Override
    public Integer countClassFunding (List<ClassFundingViewObject> classFundingViewObjects, String totalAssignedClass) {
        UTSession curSession = sessionDao.getCurrentSession();
        List<String> classIds = new ArrayList<>();
        for(ClassFundingViewObject per : classFundingViewObjects) {
            String classId = per.getClassId();
            classIds.add(classId);
        }
        //差值
        Integer subSetTotalAssigned = 0;
        for(int i=0; i<classIds.size(); i++){
            Integer data = Integer.parseInt(tamsClassFundingDraftDao.selectOneByClassIdAndSessionId(classIds.get(i), curSession.getId().toString()).getAssignedFunding());
            subSetTotalAssigned = subSetTotalAssigned +
                    Integer.parseInt(classFundingViewObjects.get(i).getAssignedFunding()) - data;
        }
        //原来的值
        Integer setTotalAssigned = 0;
        List<TAMSClassFunding> tamsClassFundings = tamsClassFundingDraftDao.selectAll();
        for(TAMSClassFunding per : tamsClassFundings) {
            setTotalAssigned = setTotalAssigned + Integer.parseInt(per.getAssignedFunding());
        }

        if((setTotalAssigned + subSetTotalAssigned) > Integer.parseInt(totalAssignedClass))
            return 1;
        else
            return 2;

    }

    @Override
    public  boolean TravelSubsidyToCertainStatus(String uid,String ta_id,String workflowStatusId){
        if (uid == null)
            return false;

        return tamsTaTravelSubsidyDao.setTravelSubsidyToCertainStatus(ta_id,workflowStatusId);
    }

    //计算助教经费
    @Override
    public Integer countTaFunding (List<TaFundingViewObject> taFundingViewObjects) {
        UTSession curSession = sessionDao.getCurrentSession();
        List<String> classIds = new ArrayList<>();
        for(TaFundingViewObject per : taFundingViewObjects) {
            classIds.add(per.getClassId());
        }
        //获取不重复的classids
        for(int i=0; i<classIds.size(); i++) {
            for(int j=i+1; j<classIds.size(); j++) {
                if(classIds.get(i).toString().equals(classIds.get(j).toString()))
                    classIds.remove(j);
            }
        }
        //获取原来的值
        List<Integer> setTotalAssigneds = new ArrayList<>();
        for(int i=0; i<classIds.size(); i++) {
            List<TAMSTa> tamsTas = tamsTaDao.selectByClassId(classIds.get(i));
            Integer setTotalAssigned = 0;
            for(TAMSTa per : tamsTas) {
                setTotalAssigned = setTotalAssigned + Integer.parseInt(per.getAssignedFunding());
            }
            setTotalAssigneds.add(setTotalAssigned);
        }
        //获取课程总经费
        List<Integer> classFundingAssigned = new ArrayList<>();
        List<TAMSClassFunding> classFunding = tamsClassFundingDao.selectByClassIds(classIds);
        if(classFunding != null) {
            for (TAMSClassFunding per : classFunding) {
                classFundingAssigned.add(Integer.parseInt(per.getAssignedFunding()));
            }
        }
        //获取差值
        List<Integer> subs = new ArrayList<>();
        for(int i=0; i<classIds.size(); i++) {
            Integer sub =0;
            for(int j=0; j<taFundingViewObjects.size(); j++) {
                if(classIds.get(i).toString().equals(taFundingViewObjects.get(j).getClassId()))
                    sub = sub + (Integer.parseInt(taFundingViewObjects.get(j).getAssignedFunding()) -
                            Integer.parseInt(tamsTaDao.selectByStudentIdAndClassIdAndSessionId(
                                    taFundingViewObjects.get(j).getStuId(), classIds.get(i), curSession.getId().toString()).getAssignedFunding()
                            )
                    );
            }
            subs.add(sub);
        }

        Integer code = 0;
        for(int i=0; i<classFundingAssigned.size(); i++) {
            if((subs.get(i) + setTotalAssigneds.get(i)) > classFundingAssigned.get(i)){
                code = 1;
                break;
            }else
                code = 2;
        }
        return code;
    }


    //计算课程总预算经费
    @Override
    public void saveDeptFunding(List<DepartmentFundingViewObject> departmentFundingViewObjects) {
        UTSession curSession = sessionDao.getCurrentSession();
        for (DepartmentFundingViewObject per : departmentFundingViewObjects) {
            TAMSDeptFundingDraft existDraft = tamsDeptFundingDraftDao.selectDeptDraftFundsByDeptIdAndSession(per.getDepartmentId(), curSession.getId());
            existDraft.setActualFunding(per.getActualFunding());//保存批准经费
            existDraft.setPlanFunding(per.getPlanFunding());//保存计划经费
            tamsDeptFundingDraftDao.saveOneByEntity(existDraft);
        }
    }

    @Override
    public void saveClassFunding(List<ClassFundingViewObject> classFundingViewObjects) {
        UTSession curSession = sessionDao.getCurrentSession();
        for (ClassFundingViewObject per : classFundingViewObjects) {
            TAMSClassFundingDraft existDraft = tamsClassFundingDraftDao.selectOneByClassIdAndSessionId(per.getClassId(), curSession.getId().toString());
            existDraft.setAssignedFunding(per.getAssignedFunding());
            tamsClassFundingDraftDao.insertOneByEntity(existDraft);
        }
    }

    @Override
    public void saveTaFunding(List<TaFundingViewObject> taFundingViewObjects) {
        UTSession curSession = sessionDao.getCurrentSession();
        for (TaFundingViewObject per : taFundingViewObjects) {
            if(per.getAssignedFunding()!=null) {
                TAMSTa exist = tamsTaDao.selectByStudentIdAndClassIdAndSessionId(per.getStuId(), per.getClassId(), curSession.getId().toString());
                //初始化博士津贴
                if (exist.getType()!=null&&exist.getType().equals("2")) {
                    Integer assignedFunds = Integer.valueOf(per.getAssignedFunding());
                    int phdFunds = (int)(assignedFunds*0.2);
                    exist.setPhdFunding(String.valueOf(phdFunds));
                }
                exist.setAssignedFunding(per.getAssignedFunding());
                tamsTaDao.insertByEntity(exist);
            }
        }
    }


    @Override
    public void saveSessionFunding(List<SessionFundingViewObject> sessionFundingViewObjects) {
        for (SessionFundingViewObject per : sessionFundingViewObjects) {
            TAMSUniversityFunding existFunding = tamsUniversityFundingDao.selectCurrBySession().get(0);
            existFunding.setPlanFunding(per.getPlanFunding());
            existFunding.setActualFunding(per.getActualFunding());
            tamsUniversityFundingDao.insertOneByEntity(existFunding);
        }
    }

    //获取工作时间
    @Override
    public short getWorkTime() {
        TAMSTimeSettingType timeSettingType = timeSettingTypeDao.selectByName("批次经费和学院经费设置");
        TAMSTimeSettingType tamsTimeSettingType = timeSettingTypeDao.selectByName("课程经费设置");
        TAMSTimeSettingType timeSettingTypeTaFunds = timeSettingTypeDao.selectByName("助教经费设置");
        TimeUtil timeUtil = new TimeUtil();
        if (timeSettingType == null || tamsTimeSettingType == null || timeSettingTypeTaFunds == null) {
            return 10;
        }
        if (!timeUtil.isBetweenPeriod(timeSettingType.getId(), sessionDao.getCurrentSession().getId().toString()))
            return 1;
        if (!timeUtil.isBetweenPeriod(tamsTimeSettingType.getId(), sessionDao.getCurrentSession().getId().toString()))
            return 2;
        if (!timeUtil.isBetweenPeriod(timeSettingTypeTaFunds.getId(), sessionDao.getCurrentSession().getId().toString()))
            return 3;
        else
            return 0;
    }

    /*
    @Override
    public void saveClassFunding(List<ClassFundingViewObject> classFundingViewObjects){
        UTSession curSession = sessionDao.getCurrentSession();

        for(ClassFundingViewObject classFundingViewObject:classFundingViewObjects){
            TAMSClassFunding exitFunding = tamsClassFundingDao.getOneByClassIdAndSessionId(classFundingViewObject.getClassId(),curSession.getId().toString());
            exitFunding.setAssignedFunding(classFundingViewObject.getAssignedFunding());
            tamsClassFundingDao.saveOneByEntity(exitFunding);
        }

    }
    */


    @Override
    public List<TAMSTimeSettingType> getAllTimeCategory() {
        return timeSettingTypeDao.selectAll();
    }

    @Override
    public boolean saveTimeCategory(TAMSTimeSettingType timeSettingType) {
        return timeSettingTypeDao.insertOneByEntity(timeSettingType);
    }

    @Override
    public boolean deleteTimeCategory(TAMSTimeSettingType timeSettingType) {
        return timeSettingTypeDao.deleteOneByEntity(timeSettingType);
    }

    //计算学院总预算经费
    @Override
    public String getSessionFundingTotalApply() {
        UTSession curSession = new UTSessionDaoJpa().getCurrentSession();
        User user = (User) getUserSession().retrieveObject("user");
        List<TAMSDeptFundingDraft> deptFundingDrafts = new ArrayList<>();
        List<TAMSDeptFunding> deptFundings = new ArrayList<>();
        Integer sessionFundingTotalApply = 0;
        if(userInfoService.isSysAdmin(user.getCode()) || userInfoService.isAcademicAffairsStaff(user.getCode())) {
            deptFundingDrafts = tamsDeptFundingDraftDao.selectAll();
            if(deptFundingDrafts != null) {
                for (TAMSDeptFundingDraft per : deptFundingDrafts) {
                    sessionFundingTotalApply = sessionFundingTotalApply + Integer.parseInt(per.getApplyFunding());
                }
            }
        }
        else {
            deptFundings.add(
                    tamsDeptFundingDao.selectDeptFundsByDeptIdAndSession(user.getDepartmentId(), curSession.getId())
            );
            if(deptFundings != null&&deptFundings.get(0)!=null)
                sessionFundingTotalApply = Integer.parseInt(deptFundings.get(0).getApplyFunding());
        }
        return sessionFundingTotalApply.toString();
    }

    //计算课程总申报经费
    @Override
    public String getClassFundingTotalApply() {
        User user = (User) getUserSession().retrieveObject("user");
        Integer classFundingTotalApply = 0;
        List<TAMSClassFunding> classFundings = new ArrayList<>();
        if(userInfoService.isSysAdmin(user.getCode()) || userInfoService.isAcademicAffairsStaff(user.getCode()) || userInfoService.isCollegeStaff(user.getCode()))
            classFundings = tamsClassFundingDraftDao.selectAll();
        else
            classFundings = tamsClassFundingDao.selectAll(user);
        if(classFundings !=null) {
            for(TAMSClassFunding per : classFundings) {
                classFundingTotalApply = classFundingTotalApply + Integer.parseInt(per.getApplyFunding());
            }
        }
        return classFundingTotalApply.toString();
    }
    @Override
    public String getSessionFundingStatistics() {
        List<TAMSDeptFunding> deptFunds = tamsDeptFundingDraftDao.selectDepartmentCurrDraftBySession();
        String totalPlan = "";
        Long setted = 0l;
        for (TAMSDeptFunding deptFunding : deptFunds) {
            setted = setted+Integer.parseInt(deptFunding.getPlanFunding());
        }
        if (tamsUniversityFundingDao.selectCurrBySession() == null || tamsUniversityFundingDao.selectCurrBySession().size() == 0)
            totalPlan = "0";
        else
            totalPlan = tamsUniversityFundingDao.selectCurrBySession().get(0).getPlanFunding();

        return setted+"("+totalPlan+")";
    }


    @Override
    public String getSessionFundingStatistics(String totalPlan) {
        List<TAMSDeptFunding> deptFundings = tamsDeptFundingDraftDao.selectDepartmentCurrDraftBySession();
        Long setted = 0l;
        for (TAMSDeptFunding deptFunding : deptFundings) {
            setted = setted+Integer.parseInt(deptFunding.getPlanFunding());
        }

        return setted+"/"+totalPlan;
    }

    @Override
    public String getSessionFundingTotalApprove(String totalAssigned) {
        List<TAMSDeptFunding> deptFundings = tamsDeptFundingDraftDao.selectDepartmentCurrDraftBySession();
        Long setted = 0l;
        for (TAMSDeptFunding deptFunding : deptFundings) {
            setted = setted+Integer.parseInt(deptFunding.getActualFunding());
        }
        return setted.toString() + "/" + totalAssigned;
    }

    @Override
    public String getSessionFundingTotalApprove(List<DepartmentFundingViewObject> departmentFundingViewObjects) {
        Long totalApproved = 0l;
        for (DepartmentFundingViewObject departmentFundingViewObject : departmentFundingViewObjects) {
            totalApproved += Integer.parseInt(departmentFundingViewObject.getActualFunding());
        }
        String totalAssigned = "";
        if (tamsUniversityFundingDao.selectCurrBySession() == null || tamsUniversityFundingDao.selectCurrBySession().size() == 0)
            totalAssigned = "0";
        else
            totalAssigned = tamsUniversityFundingDao.selectCurrBySession().get(0).getActualFunding();
        return totalApproved.toString() + "/" + totalAssigned;
    }

    //从学院经费的表查询出各个学院批准的经费，作为课程的总批准经费
    @Override
    public String getClassTotalAssignedFunding() {
        User user = (User) getUserSession().retrieveObject("user");
        TAMSDeptFunding deptFunding;
        if (userInfoService.isSysAdmin(user.getCode()) || userInfoService.isAcademicAffairsStaff(user.getCode()))
            return tamsUniversityFundingDao.selectCurrBySession() != null ?
                    tamsUniversityFundingDao.selectCurrBySession().get(0).getActualFunding() : "0";
            //二级单位管理员看到该学院的批准经费
        else
            deptFunding = deptFundingDao.selectDeptFundsByDeptIdAndSession(user.getDepartmentId(), sessionDao.getCurrentSession().getId());
        if (deptFunding == null || deptFunding.getActualFunding().toString().equals("0"))
            return "经费未分配";  //表明该学院还未分配经费
        else
            return deptFunding.getActualFunding();
    }


    @Override
    public boolean initCourseManagerData() {
        int i = 0;

        List<String> curCourseMangerCourseId = new ArrayList<>();
        List<String> curSessionCourseIds = new ArrayList<>();
        List<UTClassInformation> curSessionClassInformation = utClassInfoDao.getAllCurrentClassInformation();//找到所有的课程信息

        if(curSessionClassInformation!=null){
            for(UTClassInformation utClassInformation:curSessionClassInformation){
                if(!curSessionCourseIds.contains(utClassInformation.getCourseId().toString()))
                    curSessionCourseIds.add(utClassInformation.getCourseId().toString());  //开课的课程UNIQEUID
            }
        }
/*        List<TAMSCourseManager> allCourseManager = tamsCourseManagerDao.getAllCourseManager();
        if(allCourseManager!=null){
            for(TAMSCourseManager tamsCourseManager:allCourseManager){
                curCourseMangerCourseId.add(tamsCourseManager.getCourseId());
            }

        }*/
        for(String needToAdd:curSessionCourseIds) {
            if(!curCourseMangerCourseId.contains(needToAdd)) {
                curCourseMangerCourseId.add(needToAdd);
                List<UTClassInformation> classes = utClassInfoDao.getClassesByCourseId(needToAdd);
                if(classes!=null&&classes.size()==1){
                    List<UTClassInstructor> classIns = utClassInstructorDao.selectByClassId(classes.get(0).getId());
                    if(classIns!=null&&classIns.size()==1){
                        TAMSCourseManager tamsCourseManager = new TAMSCourseManager();
                        tamsCourseManager.setCourseId(needToAdd);
                        tamsCourseManager.setCourseManagerId(classIns.get(0).getInstructorId());
                        tamsCourseManagerDao.saveCourseManager(tamsCourseManager);

                        //添加课程负责人角色
                        KRIM_ROLE_T krim_role_t = krim_role_t_dao.getKrimRoleTByName(COURSE_MANAGER_ROLE_NAME);
                        krim_role_t.setChecked(true);
                        List<KRIM_ROLE_T> needToAddRoleList = new ArrayList<>();
                        needToAddRoleList.add(krim_role_t);
                        KRIM_PRNCPL_T currentEntity = krim_prncpl_t_dao.getKrimEntityEntTypTByPrncplId(classIns.get(0).getInstructorId()==null?"0":classIns.get(0).getInstructorId());
                        if (krim_role_t != null && currentEntity != null) {
                            krim_role_mbr_t_dao.saveKrimRoleMbrTByPrncpltAndRoles(currentEntity, needToAddRoleList);
                        }

                    }else {
                        TAMSCourseManager tamsCourseManager = new TAMSCourseManager();
                        tamsCourseManager.setCourseId(needToAdd);
                        tamsCourseManager.setCourseManagerId(null);
                        tamsCourseManagerDao.saveCourseManager(tamsCourseManager);
                    }
                }else {
                    TAMSCourseManager tamsCourseManager = new TAMSCourseManager();
                    tamsCourseManager.setCourseId(needToAdd);
                    tamsCourseManager.setCourseManagerId(null);
                    tamsCourseManagerDao.saveCourseManager(tamsCourseManager);
                }
            }
        }
        return true;
    }

    @Override
    public boolean initTeacherData(){
        List<UTClassInstructor> allClassInstructors = utClassInstructorDao.getAllClassInstructor();
        List<String> instructors = new ArrayList<>();
        if(allClassInstructors!=null){
            for(UTClassInstructor utClassInstructor:allClassInstructors){
                if(utClassInstructor.getInstructorId()!=null) {
                    if (!instructors.contains(utClassInstructor.getInstructorId())){
                        instructors.add(utClassInstructor.getInstructorId());
                    }

                }
            }
        }

        KRIM_ROLE_T krim_role_t = krim_role_t_dao.getKrimRoleTByName(TEACHER_ROLE_NAME);
        krim_role_t.setChecked(true);
        List<KRIM_ROLE_T> needToAddRoleList = new ArrayList<>();
        needToAddRoleList.add(krim_role_t);
        for(String s : instructors) {
            KRIM_PRNCPL_T currentEntity = krim_prncpl_t_dao.getKrimEntityEntTypTByPrncplId(s);
            if (krim_role_t != null && currentEntity != null) {
                krim_role_mbr_t_dao.saveKrimRoleMbrTByPrncpltAndRoles(currentEntity, needToAddRoleList);
            }
        }

        return true;
    }



    @Override
    public List<UTInstructor> getInstructorByNameAndCodeAndDepartmentId(String name, String code,String departmentId) {
        if (name == null)
            name = "";
        if (code == null)
            code = "";
        if(departmentId==null)
            departmentId = "";

        List<UTInstructor> result = utInstructorDao.getInstructorByCodeAndNameAndDept(name, code,departmentId);
        if (result == null) {
            List<UTInstructor> emplyList = new ArrayList<>();
            emplyList.add(new UTInstructor());  //填一个空对象
            return emplyList;
        }
        return result;
    }


    @Override
    public List<UTInstructor> getInstructorByNameAndCode(String name, String code) {
        if (name == null)
            name = "";
        if (code == null)
            code = "";
        List<UTInstructor> result = utInstructorDao.getInstructorByCode(name, code);
        if (result == null)
            result.add(new UTInstructor());  //填一个空对象
        return result;
    }

    @Override
    public boolean addCourseManagerByInsIdAndCourseId(String instructorId, String courseId) {
        TAMSCourseManager tamsCourseManagerExit = tamsCourseManagerDao.getCourseManagerByCourseId(courseId);
        if (tamsCourseManagerExit != null) {
            /**
             * 将该人添加为课程负责人角色
             */
            KRIM_ROLE_T krim_role_t = krim_role_t_dao.getKrimRoleTByName(COURSE_MANAGER_ROLE_NAME);
            krim_role_t.setChecked(true);
            List<KRIM_ROLE_T> needToAddRoleList = new ArrayList<>();
            needToAddRoleList.add(krim_role_t);
            KRIM_PRNCPL_T currentEntity = krim_prncpl_t_dao.getKrimEntityEntTypTByPrncplId(instructorId);
            if (krim_role_t != null && currentEntity != null) {
                krim_role_mbr_t_dao.saveKrimRoleMbrTByPrncpltAndRoles(currentEntity, needToAddRoleList);
                tamsCourseManagerExit.setCourseManagerId(instructorId);
                return tamsCourseManagerDao.saveCourseManager(tamsCourseManagerExit);   //将该用户保存到对应的course上
            }
        }
        return false;
    }

    /*
    @Override
    public void countAssignedFunding (List<String> classids) {
        //当前学期
        UTSession curSession = new UTSessionDaoJpa().getCurrentSession();
        List<TAMSClassFunding> tamsClassFunding = tamsClassFundingDao.selectByClassIds(classids);
        List<Integer> assignedFundingClass = new ArrayList<>();
        List<Integer> depts = new ArrayList<>();
        for(TAMSClassFunding per : tamsClassFunding) {
            Integer assigned = Integer.parseInt(per.getAssignedFunding());
            assignedFundingClass.add(assigned);
            Integer dept = per.getClassInformation().getDepartmentId();
            depts.add(dept);
        }

        for(int i=0; i<tamsClassFunding.size(); i++) {
            for(int j=i+1; j<tamsClassFunding.size(); j++) {
                if(tamsClassFunding.get(i).getClassInformation().getDepartmentId().toString().equals(
                        tamsClassFunding.get(j).getClassInformation().getDepartmentId().toString())
                        ) {
                    Integer sum = Integer.parseInt(tamsClassFunding.get(i).getAssignedFunding())
                            + Integer.parseInt(tamsClassFunding.get(j).getAssignedFunding());
                    tamsClassFunding.get(i).setAssignedFunding(sum.toString());
                    tamsClassFunding.remove(j);
                }
            }
        }

        List<TAMSDeptFunding> tamsDeptFundings = tamsDeptFundingDao.selectByDeptAndSessionId(
                depts, curSession.getId()
        );

        List<Integer> assignedFundingDept = new ArrayList<>();
            for (TAMSDeptFunding per : tamsDeptFundings) {
                Integer assigned = Integer.parseInt(per.getActualFunding());
                for(TAMSClassFunding)
            }
        }
*/

    @Override
    public List<TAMSTaBlackList> getAllBlackList(){
        List<TAMSTaBlackList> result = tamsTaBlackListDao.getAllBlackList();
        return result;
    }



    @Override
    public List<TAMSCourseManager> getCourseManagerByUid(String uId){
        /*
         本学期开课课程
         */
        List<String> curSessionCourseIds = new ArrayList<>();
        List<UTClassInformation> curSessionClassInformation = utClassInfoDao.getAllCurrentClassInformation();
        if(curSessionClassInformation!=null){
            for(UTClassInformation utClassInformation:curSessionClassInformation){
                curSessionCourseIds.add(utClassInformation.getCourseId().toString());  //当前学期开课的课程UNIQEUID
            }
        }

        List<TAMSCourseManager> result = new ArrayList<>();
        /*
            只显示本学期开课的课程
         */
        if(iUserInfoService.isSysAdmin(uId)||iUserInfoService.isAcademicAffairsStaff(uId)){
            List<TAMSCourseManager> tamsCourseManagers = tamsCourseManagerDao.getAllCourseManager();
            for(TAMSCourseManager tamsCourseManager:tamsCourseManagers){
                if(curSessionCourseIds.contains(tamsCourseManager.getCourseId())){
                    result.add(tamsCourseManager);
                }
            }
            return result;
        }
        if(iUserInfoService.isCollegeStaff(uId)){
            UTInstructor utInstructor = utInstructorDao.getInstructorByIdWithoutCache(uId);
            if(utInstructor!=null){
                String departmentId = utInstructor.getDepartmentId().toString();
                List<TAMSCourseManager> tamsCourseManagers =  tamsCourseManagerDao.getCourseManagerByDeptId(departmentId);
                for(TAMSCourseManager tamsCourseManager:tamsCourseManagers){
                    if(curSessionCourseIds.contains(tamsCourseManager.getCourseId())){
                        result.add(tamsCourseManager);
                    }
                }
                return result;
            }
            return null;
        }
        return null;
    }

    /**
     *封装的方法，用于获取导出的PDF文件的文件路径
     * @param courseManagerViewObjectList 传入的参数
     * @return filePath                   返回的PDF文件路径
     */
    @Override
    public String prepareCourseManagerToPDF(List<CourseManagerViewObject> courseManagerViewObjectList){
        SimpleDateFormat curTime = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "课程管理人员列表" + "-" + getUserSession().getLoggedInUserPrincipalId() + "-" + curTime.format(new Date());
        String filePath="";
        try{
            String[] header = {"课程名称", "课程代码", "课程负责人", "职工号"};
            List<String[]> Content = new ArrayList(courseManagerViewObjectList.size());
            for(CourseManagerViewObject courseMVOList : courseManagerViewObjectList) {
                String courseName = courseMVOList.getCourseNm() == null ? "" : courseMVOList.getCourseNm();
                String courseNmb = courseMVOList.getCourseNmb() == null ? "" : courseMVOList.getCourseNmb();
                String courseManager = courseMVOList.getCourseManager() == null ? "" : courseMVOList.getCourseManager();
                String InstructorCode = courseMVOList.getInstructorCode() == null ? "" : courseMVOList.getInstructorCode();
                String[] content = {
                        courseName, courseNmb, courseManager, InstructorCode
                };
                Content.add(content);
            }
            filePath = PDFService.printNormalTable("课程信息列表", header, Content, fileName);

        }catch(DocumentException | IOException e){
//            e.printStackTrace();
//            infoForm.setErrMsg("系统导出PDF文件错误！");
//            return this.showDialog("refreshPageViewDialog", true, infoForm);
            String errorMsg="exception";
            return errorMsg;
        }
        return filePath;
    }

    /**
     *获取经费明细PDF格式导出的路径
     * @param  JingFeiManager
     * @return filePath
     */
@Override
    public String prepareJingFeiToPDF(List<DetailFundingViewObject> JingFeiManager){
        SimpleDateFormat curTime = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "经费明细列表" + "-" + getUserSession().getLoggedInUserPrincipalId() + "-" + curTime.format(new Date());
        String filePath="";
        try{
            String[] header = {"助教","学号","银行","银行卡号","身份证号","课程","课程代码","教学班号","3月实发","4月实发","5月实发","6月实发","7月实发","8月实发","实发总额"};
            List<String[]> Content = new ArrayList( JingFeiManager.size());
            for(DetailFundingViewObject jingfeiMVOList :  JingFeiManager) {
                String taName = jingfeiMVOList.getTaName()   == null ? "" : jingfeiMVOList.getTaName();
                String stuId = jingfeiMVOList.getStuId()     == null ? "" : jingfeiMVOList.getStuId();
                String bankName= jingfeiMVOList.getBankName()== null ? "" : jingfeiMVOList.getBankName();
                String bankId = jingfeiMVOList.getBankId()   == null ? "" : jingfeiMVOList.getBankId();
                String identity=jingfeiMVOList.getIdentity() == null ? "" : jingfeiMVOList.getIdentity();
                String classNbr=jingfeiMVOList.getClassNbr() == null ? "" : jingfeiMVOList.getClassNbr();
                String instructorName=jingfeiMVOList.getInstructorName()   == null ? "" :jingfeiMVOList.getInstructorName() ;
                String courseName=jingfeiMVOList.getCourseName() == null ? "" :jingfeiMVOList.getCourseName();
                String courseCode=jingfeiMVOList.getCourseCode() == null ? "" :jingfeiMVOList.getCourseCode();
                String monthlySalary3=jingfeiMVOList.getMonthlySalary3() == null ? "" :jingfeiMVOList.getMonthlySalary3();
                String monthlySalary4=jingfeiMVOList.getMonthlySalary4() == null ? "" :jingfeiMVOList.getMonthlySalary4();
                String monthlySalary5=jingfeiMVOList.getMonthlySalary5() == null ? "" :jingfeiMVOList.getMonthlySalary5();
                String monthlySalary6=jingfeiMVOList.getMonthlySalary6() == null ? "" :jingfeiMVOList.getMonthlySalary6();
                String monthlySalary7=jingfeiMVOList.getMonthlySalary7() == null ? "" :jingfeiMVOList.getMonthlySalary7();
                String monthlySalary8=jingfeiMVOList.getMonthlySalary8() == null ? "" :jingfeiMVOList.getMonthlySalary8();
                String total=jingfeiMVOList.getTotal() == null ? "" :jingfeiMVOList.getTotal();
                String[] content = {
                        taName, stuId, bankName, bankId,identity,courseName,
                        courseCode,classNbr,monthlySalary3,monthlySalary4,                                   monthlySalary5,monthlySalary6,monthlySalary7,monthlySalary8,total
                };
                Content.add(content);
            }
            filePath = PDFService.printNormalTable("经费明细列表", header, Content, fileName);

        }catch(DocumentException | IOException e){
//            e.printStackTrace();
//            infoForm.setErrMsg("系统导出PDF文件错误！");
//            return this.showDialog("refreshPageViewDialog", true, infoForm);
            String errorMsg="exception";
            return errorMsg;
        }
        return filePath;
    }

    /**
     *获取学校历史经费PDF格式导出的路径
     * @param  SchoolHistoryFundsManager
     * @return filePath
     */
    @Override
    public String prepareSchoolHistoryFundsPDF(List<SessionFundingViewObject> SchoolHistoryFundsManager){
     SimpleDateFormat curTime = new SimpleDateFormat("yyyy-MM-dd");
     String fileName = "学校历史经费列表" + "-" + getUserSession().getLoggedInUserPrincipalId() + "-" + curTime.format(new Date());
       String filePath="";
        try{
            String[] header = {"批次","预算经费","申报经费","批次经费","博士津贴","奖励经费","交通补贴","总经费"};
            List<String[]> Content = new ArrayList( SchoolHistoryFundsManager.size());
            for(SessionFundingViewObject SchoolHistoryFundsMVOList :  SchoolHistoryFundsManager) {
                String sessionName = SchoolHistoryFundsMVOList.getSessionName() == null ? "" :SchoolHistoryFundsMVOList.getSessionName() ;
                String planFunding = SchoolHistoryFundsMVOList.getPlanFunding() == null ? "" : SchoolHistoryFundsMVOList.getPlanFunding();
                String applyFunding=SchoolHistoryFundsMVOList.getApplyFunding() == null ? "" : SchoolHistoryFundsMVOList.getApplyFunding();
                String actualFunding = SchoolHistoryFundsMVOList.getActualFunding()   == null ? "" : SchoolHistoryFundsMVOList.getActualFunding();
                String PhdFunding=SchoolHistoryFundsMVOList.getPhdFunding() == null ? "" :SchoolHistoryFundsMVOList.getPhdFunding();
                String bonus=SchoolHistoryFundsMVOList.getBonus() == null ? "" : SchoolHistoryFundsMVOList.getBonus();
                String trafficFunding=SchoolHistoryFundsMVOList.getTrafficFunding()   == null ? "" :SchoolHistoryFundsMVOList.getTrafficFunding() ;
                String total=SchoolHistoryFundsMVOList.getTotal() == null ? "" :SchoolHistoryFundsMVOList.getTotal();

                String[] content = {
                        sessionName, planFunding, applyFunding, actualFunding,
                        PhdFunding,bonus, trafficFunding,total
                };
                Content.add(content);
            }
            filePath = PDFService.printNormalTable("学校历史经费列表", header, Content, fileName);

        }catch(DocumentException | IOException e){
//            e.printStackTrace();
//            infoForm.setErrMsg("系统导出PDF文件错误！");
//            return this.showDialog("refreshPageViewDialog", true, infoForm);
            String errorMsg="exception";
            return errorMsg;
        }
     return filePath;
    }
    /**
     *获取学院经费PDF格式导出的路径
     * @param  CollegeFundsManager
     * @return filePath
     */
    @Override
    public String prepareCollegeFundsPDF(List<DepartmentFundingViewObject> CollegeFundsManager){
        SimpleDateFormat curTime = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "学院经费列表" + "-" + getUserSession().getLoggedInUserPrincipalId() + "-" + curTime.format(new Date());
        String filePath="";
        try{
            String[] header = {"批次","学院","预算经费","申报经费","批准经费","博士经费","奖励经费","交通补贴","总经费"};
            List<String[]> Content = new ArrayList( CollegeFundsManager.size());
            for(DepartmentFundingViewObject CollegeFundsMVOList :  CollegeFundsManager) {
                String sessionName = CollegeFundsMVOList.getSessionName() == null ? "" :CollegeFundsMVOList.getSessionName() ;
                String planFunding = CollegeFundsMVOList.getPlanFunding() == null ? "" : CollegeFundsMVOList.getPlanFunding();
                String applyFunding=CollegeFundsMVOList.getApplyFunding() == null ? "" : CollegeFundsMVOList.getApplyFunding();
                String actualFunding = CollegeFundsMVOList.getActualFunding()   == null ? "" : CollegeFundsMVOList.getActualFunding();
                String PhdFunding=CollegeFundsMVOList.getPhdFunding() == null ? "" :CollegeFundsMVOList.getPhdFunding();
                String bonus=CollegeFundsMVOList.getBonus() == null ? "" : CollegeFundsMVOList.getBonus();
                String trafficFunding=CollegeFundsMVOList.getTrafficFunding()   == null ? "" :CollegeFundsMVOList.getTrafficFunding() ;
                String total=CollegeFundsMVOList.getTotal() == null ? "" :CollegeFundsMVOList.getTotal();
                String department=CollegeFundsMVOList.getDepartment() == null ? "" :CollegeFundsMVOList.getDepartment();
                String[] content = {
                        sessionName,department, planFunding, applyFunding, actualFunding,
                        PhdFunding,bonus, trafficFunding,total
                };
                Content.add(content);
            }
            filePath = PDFService.printNormalTable("学院经费列表", header, Content, fileName);

        }catch(DocumentException | IOException e){
//            e.printStackTrace();
//            infoForm.setErrMsg("系统导出PDF文件错误！");
//            return this.showDialog("refreshPageViewDialog", true, infoForm);
            String errorMsg="exception";
            return errorMsg;
        }
        return filePath;
    }
    /**
     *获取学院历史经费PDF格式导出的路径
     * @param  CollegeHistoryFundsManager
     * @return filePath
     */
    @Override
    public String prepareCollegeHistoryFundsPDF(List<DepartmentFundingViewObject> CollegeHistoryFundsManager){
        SimpleDateFormat curTime = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "学院历史经费列表" + "-" + getUserSession().getLoggedInUserPrincipalId() + "-" + curTime.format(new Date());
        String filePath="";
        try{
            String[] header = {"批次","学院","预算经费","申报经费","批次经费","博士津贴","奖励经费","交通补贴","总经费"};
            List<String[]> Content = new ArrayList( CollegeHistoryFundsManager.size());
            for(DepartmentFundingViewObject CollegeHistoryFundsMVOList :  CollegeHistoryFundsManager) {
                String sessionName = CollegeHistoryFundsMVOList.getSessionName() == null ? "" :CollegeHistoryFundsMVOList.getSessionName() ;
                String planFunding = CollegeHistoryFundsMVOList.getPlanFunding() == null ? "" : CollegeHistoryFundsMVOList.getPlanFunding();
                String applyFunding=CollegeHistoryFundsMVOList.getApplyFunding() == null ? "" : CollegeHistoryFundsMVOList.getApplyFunding();
                String actualFunding = CollegeHistoryFundsMVOList.getActualFunding()   == null ? "" : CollegeHistoryFundsMVOList.getActualFunding();
                String PhdFunding=CollegeHistoryFundsMVOList.getPhdFunding() == null ? "" :CollegeHistoryFundsMVOList.getPhdFunding();
                String bonus=CollegeHistoryFundsMVOList.getBonus() == null ? "" : CollegeHistoryFundsMVOList.getBonus();
                String trafficFunding=CollegeHistoryFundsMVOList.getTrafficFunding()   == null ? "" :CollegeHistoryFundsMVOList.getTrafficFunding() ;
                String total=CollegeHistoryFundsMVOList.getTotal() == null ? "" :CollegeHistoryFundsMVOList.getTotal();
                String department=CollegeHistoryFundsMVOList.getDepartment() == null ? "" :CollegeHistoryFundsMVOList.getDepartment();
                String[] content = {
                        sessionName,department, planFunding, applyFunding, actualFunding,
                        PhdFunding,bonus, trafficFunding,total
                };
                Content.add(content);
            }
            filePath = PDFService.printNormalTable("学院历史经费列表", header, Content, fileName);

        }catch(DocumentException | IOException e){
//            e.printStackTrace();
//            infoForm.setErrMsg("系统导出PDF文件错误！");
//            return this.showDialog("refreshPageViewDialog", true, infoForm);
            String errorMsg="exception";
            return errorMsg;
        }
        return filePath;
    }

    /**
     *获取黑名单PDF格式导出的路径
     * @param  BlacklistManager
     * @return filePath
     */
    @Override
    public String prepareBlacklistPDF(List<BlackListViewObject> BlacklistManager){
        SimpleDateFormat curTime = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = "黑名单列表" + "-" + getUserSession().getLoggedInUserPrincipalId() + "-" + curTime.format(new Date());
        String filePath="";
        try{
            String[] header = {"名称","学号","加入时间","操作执行者名称"};
            List<String[]> Content = new ArrayList( BlacklistManager.size());
            for(BlackListViewObject BlacklistMVOList : BlacklistManager) {
                String stuName = BlacklistMVOList.getStuName() == null ? "" :BlacklistMVOList.getStuName() ;
                String stuId=BlacklistMVOList.getStuId() == null ? "" : BlacklistMVOList.getStuId();
                String joinTime=BlacklistMVOList.getJoinTime() == null ? "" : BlacklistMVOList.getJoinTime();
                String executorName=BlacklistMVOList.getExecutorName()   == null ? "" : BlacklistMVOList.getExecutorName();

                String[] content = {
                        stuName,stuId,joinTime,executorName
                };
                Content.add(content);
            }
            filePath = PDFService.printNormalTable("黑名单列表", header, Content, fileName);

        }catch(DocumentException | IOException e){
//            e.printStackTrace();
//            infoForm.setErrMsg("系统导出PDF文件错误！");
//            return this.showDialog("refreshPageViewDialog", true, infoForm);
            String errorMsg="exception";
            return errorMsg;
        }
        return filePath;
    }
}


