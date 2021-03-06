package cn.edu.cqu.ngtl.service.riceservice.impl;

import cn.edu.cqu.ngtl.bo.StuIdClassIdPair;
import cn.edu.cqu.ngtl.bo.User;
import cn.edu.cqu.ngtl.dao.cm.impl.CMProgramCourseDaoJpa;
import cn.edu.cqu.ngtl.dao.krim.impl.KRIM_ROLE_MBR_T_DaoJpa;
import cn.edu.cqu.ngtl.dao.tams.*;
import cn.edu.cqu.ngtl.dao.ut.UTClassInfoDao;
import cn.edu.cqu.ngtl.dao.ut.UTClassInstructorDao;
import cn.edu.cqu.ngtl.dao.ut.UTInstructorDao;
import cn.edu.cqu.ngtl.dao.ut.UTSessionDao;
import cn.edu.cqu.ngtl.dao.ut.impl.UTInstructorDaoJpa;
import cn.edu.cqu.ngtl.dao.ut.impl.UTSessionDaoJpa;
import cn.edu.cqu.ngtl.dao.ut.impl.UTStudentDaoJpa;
import cn.edu.cqu.ngtl.dataobject.cm.CMProgram;
import cn.edu.cqu.ngtl.dataobject.cm.CMProgramCourse;
import cn.edu.cqu.ngtl.dataobject.krim.KRIM_ROLE_MBR_T;
import cn.edu.cqu.ngtl.dataobject.tams.*;
import cn.edu.cqu.ngtl.dataobject.ut.*;
import cn.edu.cqu.ngtl.dataobject.view.UTClassInformation;
import cn.edu.cqu.ngtl.form.classmanagement.ClassInfoForm;
import cn.edu.cqu.ngtl.service.classservice.IClassInfoService;
import cn.edu.cqu.ngtl.service.courseservice.ICourseInfoService;
import cn.edu.cqu.ngtl.service.riceservice.ITAConverter;
import cn.edu.cqu.ngtl.service.taservice.ITAService;
import cn.edu.cqu.ngtl.service.userservice.IUserInfoService;
import cn.edu.cqu.ngtl.tools.converter.StringDateConverter;
import cn.edu.cqu.ngtl.viewobject.adminInfo.*;
import cn.edu.cqu.ngtl.viewobject.classinfo.*;
import cn.edu.cqu.ngtl.viewobject.common.FileViewObject;
import cn.edu.cqu.ngtl.viewobject.tainfo.AppraisalDetailViewObject;
import cn.edu.cqu.ngtl.viewobject.tainfo.MyClassViewObject;
import cn.edu.cqu.ngtl.viewobject.tainfo.TaInfoViewObject;
import cn.edu.cqu.ngtl.viewobject.tainfo.WorkBenchViewObject;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tangjing on 16-10-19.
 */
@Service
public class TAConverterimpl implements ITAConverter {

    static final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
    static final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Logger logger = Logger.getRootLogger();

    @Autowired
    private ICourseInfoService courseInfoService;

    @Autowired
    private IClassInfoService iClassInfoService;

    @Autowired
    private ITAService taService;

    @Autowired
    private UTSessionDao sessionDao;

    @Autowired
    private TAMSClassTaApplicationDao tamsClassTaApplicationDao;

    @Autowired
    private TAMSWorkflowStatusDao workflowStatusDao;

    @Autowired
    private TAMSActivityDao activityDao;

    @Autowired
    private TAMSTaCategoryDao taCategoryDao;

    @Autowired
    private TAMSTaDao taDao;

    @Autowired
    private TAMSActivityDao tamsActivityDao;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private UTClassInstructorDao classInstructorDao;

    @Autowired
    private UTInstructorDao utInstructorDao;

    @Autowired
    private UTClassInfoDao utClassInfoDao;

    @Autowired
    private UTClassInstructorDao utClassInstructorDao;

    @Autowired
    private TAMSTeachCalendarDao tamsTeachCalendarDao;

    @Autowired
    private TAMSTaDao tamsTaDao;

    @Autowired
    private TAMSClassFundingDraftDao tamsClassFundingDraftDao;

    @Override
    public List<ClassTeacherViewObject> classInfoToViewObject(List<UTClassInformation> informationlist) {
        if (informationlist == null || informationlist.isEmpty()) {
            List<ClassTeacherViewObject> viewObjects = new ArrayList<>(1);
            viewObjects.add(new ClassTeacherViewObject());
            return viewObjects;
        }
        List<ClassTeacherViewObject> viewObjects = new ArrayList<>(informationlist.size());
        if (informationlist.size() > 10) { //需要取出全部数据的阈值，具体数值待调整。
            /**
             * 取出教师ID和姓名的组合
             */
            System.out.println(System.currentTimeMillis());
            Map timeAndFundsAndTaNumberOfApplications = tamsClassTaApplicationDao.getAllClassAndHourAndFundsAndTaCounts();
            Map InstructorMap = utInstructorDao.getAllInstructorNameIdMap();
            Map hiredTa = taDao.getAllHiredTaNumberMap();
            System.out.println(System.currentTimeMillis());
            /**
             * 取出classID和教师ID的组合
             */
//            List<UTClassInstructor> utClassInstructors = classInstructorDao.getAllClassInstructor();
            System.out.println(System.currentTimeMillis());
            List<Map> result = classInstructorDao.getAllClassIdAndInstructorId(InstructorMap);
            Map classInstructorMap = result.get(0);
            Map classInsIdMap = result.get(1);

//            for (UTClassInstructor utClassInstructor : utClassInstructors) {
//                if (classInstructorMap.get(utClassInstructor.getClassId()) != null) //如果一门课有多个教师，则将教师名字进行组合
//                    classInstructorMap.put(utClassInstructor.getClassId(), InstructorMap.get(utClassInstructor.getInstructorId()) + " " + classInstructorMap.get(utClassInstructor.getClassId()));
//                else
//                    classInstructorMap.put(utClassInstructor.getClassId(), InstructorMap.get(utClassInstructor.getInstructorId()));
//            }
            System.out.println(System.currentTimeMillis());
            //没有数据的话返回一行空数据，否则表格消失


            for (UTClassInformation information : informationlist) {
                ClassTeacherViewObject viewObject = new ClassTeacherViewObject();
                String hiredTaNumber = hiredTa.get(information.getId()) == null ? "0" :hiredTa.get(information.getId()).toString();
                String timeAndFunds = timeAndFundsAndTaNumberOfApplications.get(information.getId()) == null ? null :timeAndFundsAndTaNumberOfApplications.get(information.getId()).toString();
                if (timeAndFunds != null) {
                    viewObject.setTaCount(hiredTaNumber+"/"+timeAndFunds.split(",")[2]);
                    viewObject.setWorkTime(timeAndFunds.split(",")[1]);
                    viewObject.setAppFunds(timeAndFunds.split(",")[0]);
                } else {
                    viewObject.setTaCount(hiredTaNumber+"/无数据");
                    viewObject.setWorkTime("");
                    viewObject.setAppFunds("");
                }
                viewObject.setStuNbr(information.getLimit()==null?"缺失":information.getLimit().toString());
                viewObject.setId(information.getId());
                viewObject.setClassNumber(information.getClassNumber());
                viewObject.setDepartmentName(information.getDeptName());
                viewObject.setCourseName(information.getCourseName());
                viewObject.setCourseCode(information.getCourseCode());
                viewObject.setClassType(information.getClassType());
                viewObject.setStatus(information.getStatusName());
                viewObject.setOrder(information.getOrder());
                viewObject.setInstructorName((String) classInstructorMap.get(information.getId()));
                String[] instructorIdList = classInsIdMap.get(information.getId()).toString().split(" ");
                if(instructorIdList!=null){
                    viewObject.setInstructorList(Arrays.asList(instructorIdList));
                }
                viewObjects.add(viewObject);
            }
            return viewObjects;
        } else {

            if (informationlist == null || informationlist.size() == 0) {
                List<ClassTeacherViewObject> nullObject = new ArrayList<>(1);
                nullObject.add(new ClassTeacherViewObject());
                return nullObject;
            }

            for (UTClassInformation information : informationlist) {
                String instructorname = "";
                List<String> instructorIds = new ArrayList<>();
                List<UTClassInstructor> instructorName = classInstructorDao.selectByClassId(information.getId());
                if (instructorName != null)
                    for (UTClassInstructor utClassInstructor : instructorName) {
                        if(utClassInstructor.getInstructorId() != null) {
                            instructorname += utClassInstructor.getUtInstructor().getName() + " ";
                            instructorIds.add(utClassInstructor.getInstructorId());
                        }
                }
//                String workTime = tamsTeachCalendarDao.countWorkTimeByClassId(information.getId());
                ClassTeacherViewObject viewObject = new ClassTeacherViewObject();
                viewObject.setId(information.getId());
                viewObject.setClassNumber(information.getClassNumber());
                viewObject.setStuNbr(information.getLimit()==null?"缺失":information.getLimit().toString());
                viewObject.setDepartmentName(information.getDeptName());
                viewObject.setCourseName(information.getCourseName());
                viewObject.setCourseCode(information.getCourseCode());
                viewObject.setClassType(information.getClassType());
                viewObject.setStatus(information.getStatusName());
                viewObject.setOrder(information.getOrder());
                viewObject.setInstructorName(instructorname);
                viewObject.setInstructorList(instructorIds);
                TAMSClassTaApplication tamsClassTaApplication = tamsClassTaApplicationDao.selectByClassId(information.getId());
                String hiredTaNumber = taDao.countHiredTa(information.getId());
                hiredTaNumber = hiredTaNumber == null ? "0" :hiredTaNumber;
                viewObject.setTaCount(tamsClassTaApplication == null ? hiredTaNumber+"/无数据" :hiredTaNumber+"/"+tamsClassTaApplication.getTaNumber().toString());
                viewObject.setWorkTime(tamsClassTaApplication == null ? "" :tamsClassTaApplication.getWorkHour());
                viewObject.setAppFunds(tamsClassTaApplication == null ? "" :tamsClassTaApplication.getApplicationFunds());
                viewObjects.add(viewObject);
            }
            return viewObjects;
        }
    }

    @Override
    public ClassTaApplyViewObject classInfoToApplyObject(User user, UTClass clazz) {
        ClassTaApplyViewObject viewObject = new ClassTaApplyViewObject();

        viewObject.setTeacherName(user.getName());

        viewObject.setTeacherType(user.getType());

        viewObject.setClassNumber(clazz.getClassNumber());

        UTCourse course = clazz.getCourseOffering() != null ? clazz.getCourseOffering().getCourse() :null;

        UTSession session = clazz.getCourseOffering() != null ? clazz.getCourseOffering().getSession() :null;

        CMProgramCourse programCourse = new CMProgramCourse();

        if (course != null) {
            viewObject.setCourseName(course.getName());

            viewObject.setCourseNumber(course.getCodeR());

            viewObject.setCourseHour(course.getHour());

            viewObject.setCredit(course.getCredit()+"");

            programCourse = courseInfoService.getProgramCourseByCourseId(course.getId());
        }

        if (programCourse != null) {

            viewObject.setIsRequired((programCourse.getRequired() == 1) ? "必修" :"选修");

            if (programCourse.getClassification() != null)
                viewObject.setCourseType(programCourse.getClassification().getName());

            viewObject.setProgramName(programCourse.getProgram() != null ?
                    programCourse.getProgram().getName() :null);
        }

        if (session != null) {

            Integer year = (Integer.parseInt(programCourse.getSemester())+1) / 2;

            viewObject.setGrade((Integer.parseInt(session.getYear())-year)+"");

        }
        //--------------------------目前没有值的加了默认---------------------------------------
        viewObject.setStudentNumber(100+"");

        viewObject.setAssistantNumber(1+"");

        return viewObject;
    }

    public ApplyAssistantViewObject applyAssistantToTableViewObject(UTStudent student, UTClass clazz) {

        ApplyAssistantViewObject viewObject = new ApplyAssistantViewObject();

        if (student != null) {
            viewObject.setUsername(student.getName());
            viewObject.setStudentId(student.getId());
//            viewObject.setUg_Major(student.getProgram() != null ?
//                    student.getProgram().getName() : null);
            /** 需要修改 */
            viewObject.setG_Major(student.getProgram() != null ? student.getProgram().getName() :null);
        }

        if (clazz != null) {
            viewObject.setStudentNumber(clazz.getLimit()==null?"缺失":clazz.getLimit().toString());
            viewObject.setClassId(clazz.getId());
            viewObject.setApplyCourseType(clazz.getClassType()==null?null:clazz.getClassType());
            viewObject.setCourseName(clazz.getCourseOffering().getCourse().getName());
            viewObject.setClassNbr(clazz.getClassNumber());
            viewObject.setCredit(clazz.getCourseOffering().getCourse().getCredit());
            StringBuilder sb = new StringBuilder();
            if (clazz.getUtInstructors() != null) {
                for (UTInstructor instructor : clazz.getUtInstructors()) {
                    sb.append(instructor.getName()+",\n");
                }
            }
            viewObject.setApplyTeacher(sb.toString());
        }

        UTCourse course = clazz.getCourseOffering() != null ? clazz.getCourseOffering().getCourse() :null;

        CMProgramCourse programCourse = new CMProgramCourse();

        if (course != null) {
            programCourse = courseInfoService.getProgramCourseByCourseId(course.getId());

            viewObject.setClassHour(course.getHour());
        }

        if (programCourse != null) {
            viewObject.setApplyCourseType(programCourse.getProgram() != null ?
                    programCourse.getProgram().getName() :null);
        }

        return viewObject;
    }

    @Override
    public ClassDetailInfoViewObject classInfoToViewObject(UTClass clazz) {
        ClassDetailInfoViewObject classDetailInfoViewObject = new ClassDetailInfoViewObject();
        TAMSClassTaApplication tamsClassTaApplication = iClassInfoService.getClassApplicationByClassId(clazz.getId());
        if (tamsClassTaApplication != null) {
            classDetailInfoViewObject.setTaNumber(tamsClassTaApplication.getTaNumber().toString());
            classDetailInfoViewObject.setWorkHour(tamsClassTaApplication.getWorkHour());
        }
        User user = (User) GlobalVariables.getUserSession().retrieveObject("user");

//        List<TAMSTeachCalendar> tamsTeachCalendars = iClassInfoService.getAllTaTeachCalendarFilterByUidAndClassId(user.getCode(),clazz.getId());
//        if(tamsTeachCalendars!=null){
//            Integer workHour = 0;
//            for(TAMSTeachCalendar tamsTeachCalendar : tamsTeachCalendars){
//                workHour+=Integer.valueOf(tamsTeachCalendar.getElapsedTime());
//            }
//            classDetailInfoViewObject.setWorkHour(workHour.toString());
//        }else{
//            classDetailInfoViewObject.setWorkHour("待定");
//        }

        UTCourse course = new UTCourse();

        if (clazz != null) {
            classDetailInfoViewObject.setClassNumber(clazz.getClassNumber());
            classDetailInfoViewObject.setCourseClassification(clazz.getClassType()==null?"":clazz.getClassType());
            classDetailInfoViewObject.setCourseDepartment(clazz.getCourseOffering().getCourse().getDepartment().getName());
            course = clazz.getCourseOffering() != null ? clazz.getCourseOffering().getCourse() :null;

            //classInstructor为空的数据库，无法查询
            StringBuilder sb = new StringBuilder();
            Integer countTeacherNum = 0;
            if (clazz.getUtInstructors() != null) {

                for (UTInstructor instructor : clazz.getUtInstructors()) {
                    countTeacherNum++;
                    sb.append(instructor.getName()+" ");
                }
            }
            classDetailInfoViewObject.setStudentNumber(clazz.getLimit()==null?"缺失":clazz.getLimit().toString());
            classDetailInfoViewObject.setInstructor(sb.toString());
            classDetailInfoViewObject.setInstructorNumber(countTeacherNum.toString());
        }

        UTSession session = clazz.getCourseOffering() != null ? clazz.getCourseOffering().getSession() :null;
        CMProgramCourse programCourse = new CMProgramCourse();
        CMProgram program = new CMProgram();

        if (course != null) {
            classDetailInfoViewObject.setCourseName(course.getName() == null ? " " :course.getName());
            classDetailInfoViewObject.setCourseId(course.getCodeR() == null ? " " :course.getCodeR());
            classDetailInfoViewObject.setClassHour(course.getHour() == null ? " " :course.getHour());   //目前为空
            classDetailInfoViewObject.setCredit(course.getCredit() == null ? " " :course.getCredit());
            programCourse = courseInfoService.getProgramCourseByCourseId(course.getId());
        }

        if (programCourse != null) {

            classDetailInfoViewObject.setRequired((programCourse.getRequired() == 1) ? "必修" :"选修");
            if (programCourse.getClassification() != null)
                classDetailInfoViewObject.setCourseClassification(programCourse.getClassification().getName());


            program = programCourse.getProgram() != null ? programCourse.getProgram() :null;


            UTDepartment department = new UTDepartment();
            if (session != null) {

                Integer year = (Integer.parseInt(programCourse.getSemester())+1) / 2;


                if (program != null) {

                    classDetailInfoViewObject.setDepartmentAndGrade((Integer.parseInt(session.getYear())-year)+"/"+program.getName());
                    department = program.getDepartment() != null ? program.getDepartment() :null;
                }
                if (department != null) {
                    classDetailInfoViewObject.setCourseDepartment(department.getName());
                }

            }
        }


        return classDetailInfoViewObject;
    }

    @Override
    public List<ClassTeacherViewObject> classToViewObject(List<UTClass> informationlist) {
        if (informationlist == null || informationlist.size() == 0)
            return null;
        List<ClassTeacherViewObject> viewObjects = new ArrayList<>(informationlist.size());

        for (UTClass information : informationlist) {
            ClassTeacherViewObject viewObject = new ClassTeacherViewObject();

            //if(clazz.getUtInstructors() != null && clazz.getUtInstructors().size() != 0)
            viewObject.setId(information.getId());
            viewObject.setInstructorName("test");
            viewObject.setStudentCounts(information.getMinPerWeek().toString());

            viewObject.setClassNumber(information.getClassNumber());

            UTCourse course = information.getCourseOffering() != null ? information.getCourseOffering().getCourse() :null;
            information.getCourseOffering().getSession().getYear();
            if (course != null) {
                viewObject.setSessionYear(information.getCourseOffering().getSession() != null ?
                        information.getCourseOffering().getSession().getYear() :null);

                viewObject.setDepartmentName(course.getDepartment() != null ? course.getDepartment().getName() :null);
                viewObject.setCourseName(course.getName());
                viewObject.setCourseHour(course.getHour());
                viewObject.setCourseCode(course.getCodeR());
                viewObject.setCourseCredit(course.getCredit().toString());
            }

            CMProgramCourse programCourse = information.getProgramCourse();
            if (programCourse != null) {
                viewObject.setCourseClassification(programCourse.getClassification() != null ? programCourse.getClassification().getName() :null);
                viewObject.setIsRequired(programCourse.getRequired() == 1 ? "必修" :"选修");
                viewObject.setProgramName(programCourse.getProgram() != null ? programCourse.getProgram().getName() :null);
            }
            viewObjects.add(viewObject);
        }

        return viewObjects;
    }

    @Override
    public TAMSTaApplication submitInfoToTaApplication(ClassInfoForm form) {
        TAMSTaApplication application = new TAMSTaApplication();
        application.setPhoneNbr(form.getApplicationPhoneNbr());
        application.setApplicationId(form.getApplyAssistantViewObject().getStudentId());
        application.setApplicationClassId(form.getApplyAssistantViewObject().getClassId().toString());
        application.setApplicationTime(new StringDateConverter().convertToEntityAttribute(new Date()));
        application.setBankName(form.getBankName());
        application.setBankNbr(form.getBankNbr());
        application.setNote(form.getApplyReason());

        return application;
    }

    @Override
    public List<TermManagerViewObject> termInfoToViewObject(List<UTSession> sessions) {

        List<TermManagerViewObject> viewObjects = new ArrayList<>(sessions.size());

        for (UTSession session : sessions) {
            TermManagerViewObject viewObject = new TermManagerViewObject();
            viewObject.setTermName(session.getYear()+"年"+session.getTerm()+"季");
            viewObject.setEndDate(session.getEndDate());
            viewObject.setStartDate(session.getBeginDate());
            viewObject.setActive(session.getActive());

            //// FIXME: 16-10-27 日后需要加上预算信息
            viewObject.setBudget(100000);

            viewObjects.add(viewObject);
        }

        return viewObjects;
    }

    @Override
    public UTSession termToDataObject(TermManagerViewObject term) throws ParseException {
        UTSession session = sessionDao.selectByYearAndTerm(term.getTermYear(),
                term.getTermTerm().substring(0, 1)); //去掉"季度"的'度'

        if (session == null) {
            session = new UTSession();
            //新建的预处理
            session.setYear(term.getTermYear());
            session.setTerm(term.getTermTerm().substring(0, 1));  //去掉"季度"的'度'
        }

        //新建和编辑的公共部分
        session.setActive(term.getActive());
        if (term.getStartDate() != null) {
            session.setBeginDate(
                    outputFormat.format(
                            inputFormat.parse(
                                    term.getStartDate()
                            )
                    )
            );
        }
        if (term.getEndDate() != null) {
            session.setEndDate(
                    outputFormat.format(
                            inputFormat.parse(
                                    term.getEndDate()
                            )
                    )
            );
        }

        return session;
    }


    //全校所有助教界面
    @Override
    public List<TaInfoViewObject> taCombineDetailInfo(List<TAMSTa> allTa) {
        List<TaInfoViewObject> viewObjects = new ArrayList<>();
        if (allTa == null || allTa.size() == 0) {
            //logger.error("数据为空！");
            viewObjects.add(new TaInfoViewObject());
            return viewObjects;
        }

        for (TAMSTa ta : allTa) {
            TaInfoViewObject viewObject = new TaInfoViewObject();
            viewObject.setTaId(ta.getTaId());
            viewObject.setClassid(ta.getTaClassId());
            viewObject.setApplicationReason(ta.getApplicationNote());
            viewObject.setTaCategory(ta.getTamsTaCategory() == null ? "缺失" :ta.getTamsTaCategory().getName());
            UTCourse course = null;
            List<UTInstructor> instructors = null;
            if (ta.getTaClass() != null) {
                viewObject.setClassNumber(ta.getTaClass().getClassNumber());
                viewObject.setCredit(ta.getTaClass().getCourseOffering().getCourse().getCredit());
                instructors = ta.getTaClass().getUtInstructors();
                StringBuilder sb = new StringBuilder();
                if (instructors != null)
                    for (UTInstructor instructor : instructors)
                        sb.append(instructor.getName()+"  ");
                viewObject.setInstructorName(sb.toString());
                if (ta.getTaClass().getCourseOffering() != null) {
                    course = ta.getTaClass().getCourseOffering().getCourse();
                    if (course != null) {
                        viewObject.setDeptName(course.getDepartment().getName());
                        viewObject.setCourseName(course.getName());
                        viewObject.setCourseCode(course.getCodeR());
                    }
                }
            }
            UTStudent taStu = ta.getTa();
            if (taStu != null) {
                viewObject.setTaMajorName(taStu.getProgram() == null ? "缺失" :taStu.getProgram().getName());
                viewObject.setTaName(taStu.getName());
                viewObject.setTaIDNumber(taStu.getId());
                viewObject.setTaGender(taStu.getGender());
                //viewObject.setTaMajorName(taStu.getProgram() != null ? taStu.getProgram().getName() :null);
            }

            viewObject.setId(ta.getId());
            viewObject.setStatusId(ta.getOutStandingTaWorkflowStatusId());
            viewObject.setStatus(ta.getOutStandingTaWorkflowStatus() != null ? ta.getOutStandingTaWorkflowStatus().getWorkflowStatus() :"缺失");
            viewObject.setTeacherAppraise(ta.getEvaluation());
            viewObject.setStuAppraise(ta.getStuEva());
            //暂时缺失的属性
            viewObject.setTaMajorName("缺失");
            viewObject.setContactPhone("缺失");
            viewObject.setAdvisorName("缺失");
            viewObject.setVitality("缺失");
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

    //我的助教界面助教列表
    @Override
    public List<MyTaViewObject> myTaCombinePayDay(List<TAMSTa> allTaFilteredByUid) {

        List<MyTaViewObject> viewObjects = new ArrayList<>(allTaFilteredByUid.size());

        if (allTaFilteredByUid == null || allTaFilteredByUid.size() == 0) {
            viewObjects.add(new MyTaViewObject());
            return viewObjects;
        }
        for (TAMSTa ta : allTaFilteredByUid) {
            MyTaViewObject viewObject = new MyTaViewObject();
            UTStudent taStu = ta.getTa();
            if (taStu != null) {
                viewObject.setTaName(taStu.getName());
                viewObject.setTaIdNumber(taStu.getId());
                viewObject.setTaGender(taStu.getGender());
                viewObject.setTaMajorName(taStu.getProgram() != null ? taStu.getProgram().getName() :null);
            }

            viewObject.setStatus(ta.getStatus());
            //暂时缺失的属性
            viewObject.setContactPhone("缺失");
            viewObject.setAdvisorName("缺失");
            viewObject.setPayDay("暂未设置");

            viewObjects.add(viewObject);
        }

        return viewObjects;
    }

    //工作台界面相关
    @Override
    public List<WorkBenchViewObject> taCombineWorkbench(List<WorkBenchViewObject> list) {

        //List<WorkBenchViewObject> viewObject = new ArrayList<>();
        //WorkBenchViewObject workbenchviewobject = new WorkBenchViewObject();
        if (list == null || list.size() == 0) {
            List<WorkBenchViewObject> nullObject = new ArrayList<>(1);
            nullObject.add(new WorkBenchViewObject());
            return nullObject;
        }
        User user = (User) GlobalVariables.getUserSession().retrieveObject("user");
        UTSession curSession = new UTSessionDaoJpa().getCurrentSession();
        for(WorkBenchViewObject per : list) {
            per.setStatus(tamsTaDao.selectByStudentIdAndClassIdAndSessionId(user.getCode(), per.getClassId(), curSession.getId().toString()).getStatus());
        }

        for (int i = 0; i < list.size(); i++) {
            for (int j = i+1; j < list.size(); j++) {
                //System.out.println(list.get(i).getClassNbr());
                //System.out.println(list.get(j).getClassNbr());
                if (list.get(i).getClassNbr().toString().equals(list.get(j).getClassNbr().toString())) {
                    list.get(i).setTeacher(list.get(i).getTeacher()+','+list.get(j).getTeacher());
                    list.remove(j);
                }
                //viewObject.set(i,list.get(i));
                //break;
            }
        }
        //viewObject.set(i,list.get(i));

        return list;
    }

    @Override
    public List<WorkBenchViewObject> taCombineMyApplicationClass(List<WorkBenchViewObject> list) {

        if (list == null || list.size() == 0) {
            List<WorkBenchViewObject> nullObject = new ArrayList<>(1);
            nullObject.add(new WorkBenchViewObject());
            return nullObject;
        }

        for (int i = 0; i < list.size(); i++) {
            for (int j = i+1; j < list.size(); j++) {
                if (list.get(i).getClassNbr().toString().equals(list.get(j).getClassNbr().toString())) {
                    list.get(i).setTeacher(list.get(i).getTeacher()+','+list.get(j).getTeacher());
                    list.remove(j);
                }
            }
        }
        return list;
    }


    //FIXME 迁移后删掉
    @Override
    public List<cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject> myTaCombinePayDayClass(List<TAMSTa> allTaFilteredByUid) {


        if (allTaFilteredByUid == null || allTaFilteredByUid.size() == 0) {
            List<cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject> nullObject = new ArrayList<>(1);
            nullObject.add(new cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject());
            return nullObject;
        }

        List<cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject> viewObjects = new ArrayList<>(allTaFilteredByUid.size());

        for (TAMSTa ta : allTaFilteredByUid) {
            cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject viewObject = new cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject();
            UTStudent taStu = ta.getTa();
            if (taStu != null) {
                viewObject.setDepartmentName(taStu.getDepartment().getName());
                viewObject.setApplicationClassId(ta.getTaClassId());
                viewObject.setTaName(taStu.getName());
                viewObject.setTaIdNumber(taStu.getId());
                viewObject.setTaGender(taStu.getGender());
                viewObject.setTaMajorName(taStu.getProgram() != null ? taStu.getProgram().getName() :null);
            }
            viewObject.setStatus(ta.getStatus());
            //viewObject.setContactPhone(ta.getPhoneNbr());
            //暂时缺失的属性
            viewObject.setContactPhone("缺失");
            viewObject.setAdvisorName("缺失");
            viewObject.setPayDay("暂未设置");

            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

    //我的助教界面申请人助教列表
    @Override
    public List<cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject> applicationToViewObjectClass(List<TAMSTaApplication> allApplicationFilterByUid) {

        if (allApplicationFilterByUid == null || allApplicationFilterByUid.size() == 0) {
            List<cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject> nullObject = new ArrayList<>(1);
            nullObject.add(new cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject());
            return nullObject;
        }

        List<cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject> viewObjects = new ArrayList<>(allApplicationFilterByUid.size());

        for (TAMSTaApplication application : allApplicationFilterByUid) {
            cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject viewObject = new cn.edu.cqu.ngtl.viewobject.classinfo.MyTaViewObject();
            UTStudent applicant = application.getApplicant();
            if (applicant != null) {
                viewObject.setDepartmentName(applicant.getDepartment().getName());
                viewObject.setTaName(applicant.getName());
                viewObject.setTaIdNumber(applicant.getId());
                viewObject.setTaGender(applicant.getGender());
                viewObject.setTaMajorName(applicant.getProgram() != null ? applicant.getProgram().getName() :null);
            }
            
            viewObject.setApplicationNote(application.getNote());
            viewObject.setApplicationClassId(application.getApplicationClassId());
            viewObject.setContactPhone(application.getPhoneNbr()==null?null:application.getPhoneNbr());
            //暂时缺失的属性

            viewObject.setAdvisorName("缺失");

            viewObjects.add(viewObject);
        }

        return viewObjects;
    }


    //我的助教界面申请人助教列表
    @Override
    public List<MyTaViewObject> applicationToViewObject(List<TAMSTaApplication> allApplicationFilterByUid) {
        List<MyTaViewObject> viewObjects = new ArrayList<>(allApplicationFilterByUid.size());

        if (allApplicationFilterByUid == null || allApplicationFilterByUid.size() == 0) {
            //return null;
            viewObjects.add(new MyTaViewObject());
            return viewObjects;
        }
        for (TAMSTaApplication application : allApplicationFilterByUid) {
            MyTaViewObject viewObject = new MyTaViewObject();
            UTStudent applicant = application.getApplicant();
            if (applicant != null) {
                viewObject.setTaName(applicant.getName());
                viewObject.setTaIdNumber(applicant.getId());
                viewObject.setTaGender(applicant.getGender());
                viewObject.setTaMajorName(applicant.getProgram() != null ? applicant.getProgram().getName() :null);
            }

            viewObject.setApplicationClassId(application.getApplicationClassId());

            //暂时缺失的属性
            viewObject.setContactPhone("缺失");
            viewObject.setAdvisorName("缺失");

            viewObjects.add(viewObject);
        }

        return viewObjects;
    }

    //添加申请人对话框，显示查询出符合条件的候选人列表
    @Override
    public List<MyTaViewObject> studentInfoToMyTaViewObject(List<UTStudent> studentList) {
        if (studentList == null || studentList.size() == 0) {
            logger.error("数据为空！");
            return null;
        }
        List<MyTaViewObject> viewObjects = new ArrayList<>();
        for (UTStudent listone : studentList) {
            MyTaViewObject viewObject = new MyTaViewObject();
            viewObject.setTaName(listone.getName());
            viewObject.setTaIdNumber(listone.getId());
            viewObject.setTaGender(listone.getGender());
            //点击查看详细信息会用到的
            CMProgram program = listone.getProgram();
            if (program == null)
                viewObject.setTaMajorName("缺失");
            else
                viewObject.setTaMajorName(listone.getProgram().getName().toString());
            viewObject.setAdvisorName("缺失");
            viewObject.setContactPhone("缺失");
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

    //添加申请人点击确定。将MyTaViewObject对象转化为TAMSTaApplication对象
    @Override
    public TAMSTaApplication TaViewObjectToTaApplication(MyTaViewObject application, String classid) {
        TAMSTaApplication tamsTaApplication = new TAMSTaApplication();
        tamsTaApplication.setApplicationClassId(classid);
        tamsTaApplication.setApplicationId(application.getTaIdNumber());
        //tamsTaApplication.setApplicationStatus("1");
        //tamsTaApplication.setApplicationTime(new StringDateConverter().convertToEntityAttribute(new Date()));
        return tamsTaApplication;
    }

    @Override
    public List<ClassFundingViewObject> classFundingToViewObject(List<TAMSClassFunding> allFundingByClass,String uId) {
        List<ClassFundingViewObject> viewObjects = new ArrayList<>();

        if (allFundingByClass == null || allFundingByClass.size() == 0) {
            viewObjects.add(new ClassFundingViewObject());
            return viewObjects;
        }
        for (TAMSClassFunding classFunding : allFundingByClass) {
            ClassFundingViewObject viewObject = new ClassFundingViewObject();
            viewObject.setCourseName(classFunding.getClassInformation().getCourseName());
            viewObject.setCourseCode(classFunding.getClassInformation().getCourseCode());
            viewObject.setDepartment(classFunding.getClassInformation().getDeptName());
            viewObject.setClassNumber(classFunding.getClassInformation().getClassNumber());
            viewObject.setClassId(classFunding.getClassInformation().getId());
            //设定教师名称
            List<UTClassInstructor> classInstructors = utClassInstructorDao.selectByClassId(classFunding.getClassId());
            if (classInstructors == null || classInstructors.size() == 0) {
                viewObject.setInstructorName("缺失");
            } else if (classInstructors.size() == 1) {
                viewObject.setInstructorName(classInstructors.get(0).getUtInstructor().getName());
            } else {
                for (int i = 0; i < classInstructors.size(); i++) {
                    for (int j = i+1; j < classInstructors.size(); j++) {
                        if (classInstructors.get(i).getClassId().toString().equals(classInstructors.get(j).getClassId().toString())) {
                            /*classInstructors.get(i).getUtInstructor().setName(
                                    classInstructors.get(i).getUtInstructor().getName() + ',' +
                                            classInstructors.get(j).getUtInstructor().getName()
                            );
                            */
                            String name = classInstructors.get(i).getUtInstructor().getName()+','+classInstructors.get(j).getUtInstructor().getName();
                            //classInstructors.remove(j);
                            viewObject.setInstructorName(name);
                        }
                    }
                }
            }

            viewObject.setApplyFunding(classFunding.getApplyFunding());
            if(userInfoService.isCollegeStaff(uId)||userInfoService.isAcademicAffairsStaff(uId)||userInfoService.isSysAdmin(uId)) {
                if (classFunding.getAssignedFunding().equals("0")) //如果为0则设置一个默认值
                    viewObject.setAssignedFunding(classFunding.getApplyFunding());
                else
                    viewObject.setAssignedFunding(classFunding.getAssignedFunding());
            }else{
                viewObject.setAssignedFunding(classFunding.getAssignedFunding());
            }
            viewObject.setPhdFunding(classFunding.getPhdFunding());
            viewObject.setBonus(classFunding.getBonus());
            viewObject.setTravelSubsidy(classFunding.getTravelSubsidy());
            Integer total = Integer.valueOf(classFunding.getAssignedFunding())
                    +Integer.valueOf(classFunding.getPhdFunding())
                    +Integer.valueOf(classFunding.getBonus())
                    +Integer.valueOf(classFunding.getTravelSubsidy());
            viewObject.setTotal(total.toString());
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

    //学院经费
    @Override
    public List<DepartmentFundingViewObject> departmentFundingToViewObject(List<TAMSDeptFunding> allFundingBySession) {
        if (allFundingBySession == null || allFundingBySession.size() == 0) {
            //logger.error("数据为空！");
            //return null;
            List<DepartmentFundingViewObject> nullObject = new ArrayList<>();
            nullObject.add(new DepartmentFundingViewObject());
            return nullObject;
        }
        List<DepartmentFundingViewObject> viewObjects = new ArrayList<>();
        for (TAMSDeptFunding deptFunding : allFundingBySession) {
            DepartmentFundingViewObject viewObject = new DepartmentFundingViewObject();
            /*
            String zero = "0";
            if(deptFunding.getTravelSubsidy().equals("null"))
                deptFunding.setTravelSubsidy(zero);
            if(deptFunding.getPlanFunding().equals("null"))
                deptFunding.setPlanFunding(zero);
            if(deptFunding.getActualFunding().equals("null"))
                deptFunding.setActualFunding(zero);
            if(deptFunding.getApplyFunding().equals("null"))
                deptFunding.setApplyFunding(zero);
            if(deptFunding.getBonus().equals("null"))
                deptFunding.setBonus(zero);
            if(deptFunding.getPhdFunding().equals("null"))
                deptFunding.setPhdFunding(zero);
            */
            viewObject.setDepartmentId(deptFunding.getDepartmentId());
            viewObject.setBonus(deptFunding.getBonus());
            viewObject.setApplyFunding(deptFunding.getApplyFunding());
            viewObject.setPhdFunding(deptFunding.getPhdFunding());
            viewObject.setActualFunding(deptFunding.getActualFunding());
            viewObject.setPlanFunding(deptFunding.getPlanFunding());
            viewObject.setDepartment(deptFunding.getDepartment().getName());
            viewObject.setTrafficFunding(deptFunding.getTravelSubsidy());
            Integer total = Integer.valueOf(deptFunding.getBonus())+Integer.valueOf(deptFunding.getActualFunding())+
                    Integer.valueOf(deptFunding.getTravelSubsidy())+Integer.valueOf(deptFunding.getPhdFunding());
            viewObject.setTotal(total.toString());
            if (deptFunding.getSession() != null) {
                viewObject.setSessionName(deptFunding.getSession().getYear()+"年"+
                        deptFunding.getSession().getTerm()+"季");
            }
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

    //学校经费
    @Override
    public List<SessionFundingViewObject> sessionFundingToViewObject(List<TAMSUniversityFunding> allFundingBySession) {
        if (allFundingBySession == null || allFundingBySession.size() == 0) {
            //logger.error("数据为空！");
            //return null;
            List<SessionFundingViewObject> nullObject = new ArrayList<>();
            nullObject.add(new SessionFundingViewObject());
            return nullObject;
        }
        List<SessionFundingViewObject> viewObjects = new ArrayList<>(allFundingBySession.size());

        for (TAMSUniversityFunding universityFunding : allFundingBySession) {
            SessionFundingViewObject viewObject = new SessionFundingViewObject();
            viewObject.setBonus(universityFunding.getBonus());
            viewObject.setApplyFunding(universityFunding.getApplyFunding());
            viewObject.setPhdFunding(universityFunding.getPhdFunding());
            viewObject.setActualFunding(universityFunding.getActualFunding());
            viewObject.setPlanFunding(universityFunding.getPlanFunding());
            viewObject.setTrafficFunding(universityFunding.getTravelSubsidy());
            Integer total = Integer.valueOf(universityFunding.getTravelSubsidy())+Integer.valueOf(universityFunding.getBonus())+
                    Integer.valueOf(universityFunding.getActualFunding())+Integer.valueOf(universityFunding.getPhdFunding());
            viewObject.setTotal(total.toString());
            if (universityFunding.getUtSession() != null)
                viewObject.setSessionName(universityFunding.getUtSession().getYear()+"年"+
                        universityFunding.getUtSession().getTerm()+"季");

            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

    @Override
    public RelationTable workflowStatusRtoJson(String functionId, List<TAMSWorkflowStatusR> workflowStatusRelations) {
        List<TAMSWorkflowStatus> allStatus = workflowStatusDao.selectByFunctionId(functionId);
        //如果连header都没有返回完全的空
        if (allStatus == null) {
            RelationTable rt = new RelationTable("default");
            return rt;
        }

        List<String> headers = new ArrayList<>(allStatus.size());
        for (TAMSWorkflowStatus status : allStatus) {
            headers.add(status.getWorkflowStatus());
        }

        CheckBoxStatus[][] matrix = new CheckBoxStatus[allStatus.size()][allStatus.size()];

        int length = allStatus.size();
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++) {
                matrix[i][j] = new CheckBoxStatus();
                if (i == j)
                    matrix[i][j].setDisabled(true);
            }
        //如果有header没数据则返回默认的全空
        if (workflowStatusRelations == null || workflowStatusRelations.size() == 0) {
            RelationTable rt = new RelationTable();
            rt.setHeader(headers);
            rt.setData(matrix);

            return rt;
        }

        for (TAMSWorkflowStatusR workflowStatusR : workflowStatusRelations) {
            int i = allStatus.indexOf(workflowStatusR.getStatus1());
            int j = allStatus.indexOf(workflowStatusR.getStatus2());
            matrix[i][j].setChecked(true);
        }
        RelationTable rt = new RelationTable();
        rt.setHeader(headers);
        rt.setData(matrix);

        return rt;
    }

    @Override
    public List<String> extractIdsFromTaInfo(List<TaInfoViewObject> checkedlist) {
        List<String> ids = new ArrayList<>();

        for (TaInfoViewObject per : checkedlist) {
            ids.add(per.getId());
        }

        return ids;
    }

    @Override
    public List<String> extractIdsFromMyTaInfo(List<MyTaViewObject> checkedList) {
        List<String> ids = new ArrayList<>();

        for (MyTaViewObject per : checkedList) {
            ids.add(per.getTaIdNumber());
        }

        return ids;
    }


    @Override
    public List<StuIdClassIdPair> extractIdsFromApplication(List<MyTaViewObject> checkedList) {
        List<StuIdClassIdPair> pairs = new ArrayList<>(checkedList.size());

        for (MyTaViewObject per : checkedList) {
            pairs.add(new StuIdClassIdPair(per.getTaIdNumber(), per.getApplicationClassId()));
        }

        return pairs;
    }

    @Override
    public List<TeachCalendarViewObject> TeachCalendarToViewObject(List<TAMSTeachCalendar> calendars, boolean needCount) {
        if (calendars == null || calendars.size() == 0)
            return null;

        List<TeachCalendarViewObject> viewObjects = new ArrayList<>(calendars.size());

        for (TAMSTeachCalendar calendar : calendars) {
            TeachCalendarViewObject viewObject = new TeachCalendarViewObject();

            /** 不确定是否应该用id作为编号 **/
            viewObject.setCode(calendar.getId());
            viewObject.setTheme(calendar.getTheme());
            viewObject.setDescription(calendar.getDescription());
            viewObject.setElapsedTime(calendar.getElapsedTime());
            viewObject.setStartTime(calendar.getStartTime());
            viewObject.setWeek(calendar.getWeek()==null?"0":calendar.getWeek());
            viewObject.setEndTime(calendar.getEndTime());
            viewObject.setTaTask(calendar.getTaTask());
            if (needCount) {
                List temp = activityDao.selectAllByCalendarId(calendar.getId());
                viewObject.setTaTaskTimes(temp != null ? String.valueOf(temp.size()) :"0");
                Integer hourlyWage;
                String budget;
                try {
                    hourlyWage = Integer.parseInt(taCategoryDao.selectOneByName("硕士").getHourlyWage());
                    budget = String.valueOf(Integer.parseInt(calendar.getElapsedTime()) * hourlyWage);
                } catch (NumberFormatException e) { //格式转换异常
                    budget = "数据异常，请联系管理员";
                } catch (RuntimeException e) { //数据库访问异常
                    e.printStackTrace();
                    budget = "数据异常，请联系管理员";
                }
                viewObject.setBudget(budget);
            }

            viewObjects.add(viewObject);
        }

        return viewObjects;
    }

    @Override
    public String countCalendarTotalElapsedTime(List<TeachCalendarViewObject> allCalendar) {
        Integer count = 0;
        if (allCalendar == null || allCalendar.size() == 0)
            return count.toString();
        else
            for (TeachCalendarViewObject calendar : allCalendar) {
                try {
                    count += Integer.valueOf(calendar.getElapsedTime());
                } catch (NumberFormatException e) {
                    count += 0;
                } finally {
                    // do nothing
                }
            }
        return count.toString();
    }

    @Override
    public String countCalendarTotalBudget(List<TeachCalendarViewObject> allCalendar) {
        Integer count = 0;
//        NumberFormat nf = new DecimalFormat(",###.00元");
        if (allCalendar == null || allCalendar.size() == 0)
            return count.toString();
        else
            for (TeachCalendarViewObject calendar : allCalendar) {
                try {
                    Integer budget = Integer.valueOf(calendar.getBudget());
                    count += budget;

//                    calendar.setBudget(nf.format(budget));
                    calendar.setBudget(budget.toString());
                } catch (NumberFormatException e) {
                    count += 0;
                } finally {
                    // do nothing
                }
            }
//        return nf.format(count);
        return count.toString();
    }

    @Override
    public List<TeachCalendarViewObject> activitiesToViewObject(List<TAMSTeachCalendar> calendarsContainActivities) {
        List<TeachCalendarViewObject> readyContainActivities = this.TeachCalendarToViewObject(calendarsContainActivities, false);
        if (readyContainActivities == null || readyContainActivities.size() == 0)
            return null;

        for (int i = 0; i < readyContainActivities.size(); i++) {
            List<TAMSActivity> activities = calendarsContainActivities.get(i).getActivityList();
            List<ActivityViewObject> activityViewObjects = new ArrayList<>(activities.size());
            for (TAMSActivity per : activities) {
                ActivityViewObject viewObject = new ActivityViewObject();
                viewObject.setDescription(per.getDescription());
                viewObject.setCreateTime(per.getCreateTime());
                viewObject.setLastUpdateTime(per.getLastUpdateTime());

                activityViewObjects.add(viewObject);
            }
            readyContainActivities.get(i).setActivityViewObjects(activityViewObjects);
        }

        return readyContainActivities;
    }

    @Override
    public ClassTaApplyViewObject instructorAndClassInfoToViewObject(UTClass classInfo) {
        ClassTaApplyViewObject viewObject = new ClassTaApplyViewObject();
        List<UTClassInstructor> utClassInstructors = utClassInstructorDao.selectByClassId(classInfo.getId());
        String instructorName = "";
        String instructorCode = "";
        if (utClassInstructors != null) {
            for (UTClassInstructor utClassInstructor : utClassInstructors) {
                instructorName += utClassInstructor.getUtInstructor().getName()+" ";
                instructorCode += utClassInstructor.getUtInstructor().getCode()+" ";
            }
        }
        viewObject.setTeacherName(instructorName);
        viewObject.setTeacherType(instructorCode);
        if (classInfo != null) {
            UTCourse course = classInfo.getCourseOffering() != null ? classInfo.getCourseOffering().getCourse() :null;
            viewObject.setStudentNumber(classInfo.getLimit()==null?"缺失":classInfo.getLimit().toString());
            viewObject.setCourseType(classInfo.getClassType()==null?"":classInfo.getClassType());
            viewObject.setCredit(classInfo.getCourseOffering().getCourse().getCredit());
            viewObject.setCourseHour(course.getHour());
            viewObject.setClassNumber(classInfo.getClassNumber());
            viewObject.setStudentNumber(classInfo.getMinPerWeek() == null ? " " :classInfo.getMinPerWeek().toString());
            if (course != null) {
                UTSession session = classInfo.getCourseOffering().getSession();
                viewObject.setCourseName(course.getName());
                viewObject.setCourseNumber(course.getCodeR());

                CMProgramCourse programCourse = new CMProgramCourseDaoJpa().selectByCourseId(course.getId());
                if (programCourse != null) {
                    viewObject.setCourseType(programCourse.getClassification() != null ? programCourse.getClassification().getName() :null);
                    viewObject.setIsRequired(programCourse.getRequired() == 1 ? "必修" :"选修");
                    CMProgram program = programCourse.getProgram();
                    if (program != null) {
                        viewObject.setProgramName(program.getName());
                        //viewObject.setCredit(program.getCredit().toString());
                        if (session != null) {
                            Integer year = (Integer.parseInt(programCourse.getSemester())+1) / 2;
                            Integer grade = Integer.parseInt(session.getYear())-year;
                            viewObject.setGrade(grade.toString());
                        }
                    }

                }
            }
        }
        return viewObject;
    }


    @Override
    public List<AppraisalDetailViewObject> teachCalendarToAppraisalViewObject(List<TAMSTeachCalendar> teachCalendars) {
        List<AppraisalDetailViewObject> result = new ArrayList<>();
        if (teachCalendars != null) {
            for (TAMSTeachCalendar tamsTeachCalendar : teachCalendars) {
                AppraisalDetailViewObject appraisalDetailViewObject = new AppraisalDetailViewObject();
                appraisalDetailViewObject.setCalendarName(tamsTeachCalendar.getTheme());
                List<TAMSActivity> tamsActivity = tamsActivityDao.selectAllByCalendarId(tamsTeachCalendar.getId());
                appraisalDetailViewObject.setActivityNumber(tamsActivity == null ? "0" :String.valueOf(tamsActivity.size()));
                /**
                 * 缺失数据填入固定值
                 */
                appraisalDetailViewObject.setCompletion("80%");
                appraisalDetailViewObject.setEngagement("99%");
                appraisalDetailViewObject.setGrade("95");
                result.add(appraisalDetailViewObject);
            }
        } else {
            AppraisalDetailViewObject appraisalDetailViewObject = new AppraisalDetailViewObject();
            appraisalDetailViewObject.setCalendarName("");
            appraisalDetailViewObject.setActivityNumber("");
            appraisalDetailViewObject.setCompletion("");
            appraisalDetailViewObject.setEngagement("");
            appraisalDetailViewObject.setGrade("");
            result.add(appraisalDetailViewObject);
        }
        return result;
    }

    @Override
    public List<FileViewObject> attachmentsToFileViewObject(List<TAMSAttachments> attachments) {
        if (attachments == null || attachments.size() == 0)
            return null;

        List<FileViewObject> viewObjects = new ArrayList<>(attachments.size());
        for (TAMSAttachments attachment : attachments) {
            FileViewObject viewObject = new FileViewObject();
            viewObject.setId(attachment.getId());
            viewObject.setName(attachment.getFileName());
            try {
                long size = Integer.parseInt(attachment.getFileSize());
                viewObject.setSize(size);
            } catch (NumberFormatException e) {

            }
            String authorId = attachment.getAuthorId();
            String name;
            if (userInfoService.isInstructor(authorId)) {
                name = new UTInstructorDaoJpa().getInstructorByIdWithoutCache(authorId).getName();
            } else if (userInfoService.isStudent(authorId)) {
                name = new UTStudentDaoJpa().getUTStudentById(authorId).getName();
            } else
                name = "缺失";
            viewObject.setUploaderName(name);
            viewObject.setUploadDate(attachment.getCreateTime());
            viewObject.setDownloadTimes(attachment.getDownloadTimes() != null ? attachment.getDownloadTimes() :"0");

            viewObjects.add(viewObject);
        }
        return viewObjects;
    }


    @Override
    public List<TaInfoViewObject> getTaInfoListByConditions(Map<String, String> conditions, String uId) {
        int countNull = 0;
        for (Map.Entry<String, String> entry : conditions.entrySet()) {
            if (entry.getValue() == null) {
                conditions.put(entry.getKey(), "%");
                countNull++;
            } else
                conditions.put(entry.getKey(), "%"+entry.getValue()+"%");
        }

        if (countNull != 11) {
            return taService.seachTainfoListByConditions(conditions);
        } else {
            return this.taCombineDetailInfo(
                    taService.getAllTaFilteredByUid(uId));
        }

    }


    @Override
    public List<MyClassViewObject> MyClassViewObject(List<Object> myClassIdList) {
        List<MyClassViewObject> myClassViewObjects = new ArrayList<>();
        if (myClassIdList == null || myClassIdList.size() == 0) {
            myClassViewObjects.add(new MyClassViewObject());
            return myClassViewObjects;
        }
        /*
        List<Object> classIdList = new ArrayList<>();
        for(UTStudentTimetable utStudentTimetable : utStudentTimetables){
            classIdList.add(utStudentTimetable.getClassId());
        }
        */

        List<UTClassInformation> utClassInformations = utClassInfoDao.selectBatchByIds(myClassIdList);

        for (UTClassInformation utClassInformation : utClassInformations) {
            MyClassViewObject myClassViewObject = new MyClassViewObject();
            myClassViewObject.setClassNbr(utClassInformation.getClassNumber());
            myClassViewObject.setCourseCode(utClassInformation.getCourseCode());
            myClassViewObject.setCourseName(utClassInformation.getCourseName());
            myClassViewObject.setCredit(utClassInformation.getCredit());
            myClassViewObject.setDepartmentName(utClassInformation.getDeptName());
            List<UTClassInstructor> utClassInstructors = utClassInstructorDao.selectByClassId(utClassInformation.getId());
            String instructorName = "";
            for (UTClassInstructor utClassInstructor : utClassInstructors) {
                instructorName += utClassInstructor.getUtInstructor().getName()+"  ";
            }
            myClassViewObject.setInstructorName(instructorName);
            myClassViewObjects.add(myClassViewObject);

        }
        return myClassViewObjects;
    }

    @Override
    public String countClassFunding(List<ClassFundingViewObject> classFundings, String totalAssignedFunding, String totalsetted) {
        if (classFundings == null || classFundings.size() == 0)
            return "0/"+totalAssignedFunding;
//        if(classFundings != null && !classFundings.isEmpty()) {
//            for (ClassFundingViewObject classFunding : classFundings) {
//                if(classFunding.getApplyFunding() == null)
//                    totalSetted += Long.parseLong("0");
//                else
//                    totalSetted += Long.parseLong(classFunding.getApplyFunding());
//            }
//        }
        return totalsetted+"/"+totalAssignedFunding;
    }

    /*
    @Override
    public String countClassFundingTotalApproved(List<ClassFundingViewObject> classFundings) {
        if (classFundings == null || classFundings.isEmpty())
            return "0";
        Long totalApproved = 0l;
        for (ClassFundingViewObject classFunding : classFundings) {
            if (classFunding.getAssignedFunding() == null)
                totalApproved += Long.parseLong("0");
            else
                totalApproved += Long.parseLong(classFunding.getAssignedFunding());
        }

        return totalApproved.toString();
    }
    */
    @Override
    public String countClassFundingTotalApproved(List<ClassFundingViewObject> classFundings) {
        if (classFundings == null || classFundings.isEmpty())
            return "0";
        Long totalApproved = 0l;
        List<TAMSClassFunding> tamsClassFundings = tamsClassFundingDraftDao.selectAll();
        if(tamsClassFundings!=null) {
            for (TAMSClassFunding per : tamsClassFundings) {
                totalApproved = totalApproved+Long.parseLong(per.getAssignedFunding());
            }
            return totalApproved.toString();
        }
        return "0";
    }

    @Override
    public List<ClassApplyFeedBackViewObject> feedBackToViewObject(List<TAMSClassApplyFeedback> tamsClassApplyFeedbacks) {
        List<ClassApplyFeedBackViewObject> classApplyFeedBackViewObjects = new ArrayList<>();
        for (TAMSClassApplyFeedback tamsClassApplyFeedback : tamsClassApplyFeedbacks) {
            ClassApplyFeedBackViewObject classApplyFeedBackViewObject = new ClassApplyFeedBackViewObject();
            classApplyFeedBackViewObject.setNewStatus(tamsClassApplyFeedback.getNewStatus());
            classApplyFeedBackViewObject.setOldStatue(tamsClassApplyFeedback.getOldStatus());
            classApplyFeedBackViewObject.setFeedBacks(tamsClassApplyFeedback.getFeedback() == null ? "无" :tamsClassApplyFeedback.getFeedback());
            classApplyFeedBackViewObject.setFeedBackTime(tamsClassApplyFeedback.getFeedbackTime());
            List<KRIM_ROLE_MBR_T> roles = new KRIM_ROLE_MBR_T_DaoJpa().getKrimEntityEntTypTsByMbrId(tamsClassApplyFeedback.getFeedbackUid());
            if (roles.size() > 1) {
                for (KRIM_ROLE_MBR_T krim_role_mbr_t : roles) {
                    if (krim_role_mbr_t.getKrimRoleT().getName().contains("管理员"))
                        classApplyFeedBackViewObject.setFeedBackRole(krim_role_mbr_t.getKrimRoleT().getName());
                }
            } else {
                classApplyFeedBackViewObject.setFeedBackRole(roles.get(0).getKrimRoleT().getName());
            }
            classApplyFeedBackViewObjects.add(classApplyFeedBackViewObject);
        }
        return classApplyFeedBackViewObjects;
    }
}
