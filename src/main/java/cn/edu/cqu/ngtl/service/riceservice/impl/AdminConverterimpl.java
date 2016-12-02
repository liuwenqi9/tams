package cn.edu.cqu.ngtl.service.riceservice.impl;

import cn.edu.cqu.ngtl.dao.ut.impl.UTCourseDaoJpa;
import cn.edu.cqu.ngtl.dataobject.tams.TAMSCourseManager;
import cn.edu.cqu.ngtl.dataobject.tams.TAMSTa;
import cn.edu.cqu.ngtl.dataobject.ut.UTCourse;
import cn.edu.cqu.ngtl.dataobject.ut.UTStudent;
import cn.edu.cqu.ngtl.service.riceservice.IAdminConverter;
import cn.edu.cqu.ngtl.viewobject.adminInfo.CourseManagerViewObject;
import cn.edu.cqu.ngtl.viewobject.adminInfo.DetailFundingViewObject;
import cn.edu.cqu.ngtl.viewobject.adminInfo.TaFundingViewObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by awake on 2016-10-26.
 */
@Service
public class AdminConverterimpl implements IAdminConverter {

    private static final Logger logger = Logger.getRootLogger();

    @Autowired
    private UTCourseDaoJpa utCourseDao;

    @Override
    public List<CourseManagerViewObject> getCourseManagerToTableViewObject(List<TAMSCourseManager> tamsCourseManagerList) {
        List<CourseManagerViewObject> courseManagerViewObjectList = new ArrayList(tamsCourseManagerList.size());
        for (TAMSCourseManager tamsCourseManager : tamsCourseManagerList) {
            UTCourse utCourse = new UTCourseDaoJpa().selectOneById(tamsCourseManager.getCourseId());
            if(utCourse!=null) {
                CourseManagerViewObject courseManagerViewObject = new CourseManagerViewObject();
                courseManagerViewObject.setId(tamsCourseManager.getCourseManagerId());
                courseManagerViewObject.setCourseNm(utCourse.getName());
                courseManagerViewObject.setCourseNmb(utCourse.getCodeR());
                courseManagerViewObject.setCourseManager(tamsCourseManager.getUtInstructor().getName());
                courseManagerViewObject.setInstructorCode(tamsCourseManager.getUtInstructor().getCode());
                courseManagerViewObjectList.add(courseManagerViewObject);
            }
        }

        return  courseManagerViewObjectList;
    }




    @Override
    public List<TaFundingViewObject> taFundingToViewObject(List<TAMSTa> tamsTas){
        if(tamsTas == null || tamsTas.size() == 0) {
            logger.error("数据为空！");
            return null;
        }
        List<TaFundingViewObject> taFundingViewObjects = new ArrayList<>(tamsTas.size());
        for(TAMSTa ta : tamsTas) {
            TaFundingViewObject taFundingViewObject = new TaFundingViewObject();
            UTCourse course = null;
            if(ta.getTaClass() != null) {
                taFundingViewObject.setClassNbr(ta.getTaClass().getClassNumber());
                if (ta.getTaClass().getCourseOffering() != null) {
                    course = ta.getTaClass().getCourseOffering().getCourse();
                    if(course != null) {
                        taFundingViewObject.setCourseName(course.getName());
                    }
                }
            }
            UTStudent taStu = ta.getTa();
            if(taStu != null) {
                taFundingViewObject.setTaName(taStu.getName());
            }

            if (ta.getCurSession() != null ){
                taFundingViewObject.setSessionName(ta.getCurSession().getYear() + "年" +
                        ta.getCurSession().getTerm() + "季");
            }

            //暂时缺失的属性
            taFundingViewObject.setAssignedFunding(ta.getAssignedFunding());
            taFundingViewObject.setBonus(ta.getBonus());
            taFundingViewObject.setPhdFunding(ta.getPhdFunding());
            taFundingViewObject.setTravelSubsidy(ta.getTravelSubsidy());
            taFundingViewObject.setTaType("博士");
            Integer total =   (Integer.parseInt(ta.getAssignedFunding())+
                    Integer.parseInt(ta.getAssignedFunding())+
                    Integer.parseInt(ta.getAssignedFunding())+
                    Integer.parseInt(ta.getAssignedFunding())+
                    Integer.parseInt(ta.getAssignedFunding()));
            taFundingViewObject.setTotal(total.toString());
            taFundingViewObjects.add(taFundingViewObject);
        }
        return taFundingViewObjects;
    }


    @Override
    public List<DetailFundingViewObject> detailFundingToViewObject(List<TAMSTa> tamsTas){
        if(tamsTas == null || tamsTas.size() == 0) {
            logger.error("数据为空！");
            return null;
        }
        List<DetailFundingViewObject> detailFundingViewObjects = new ArrayList<>(tamsTas.size());
        for(TAMSTa ta : tamsTas) {
            DetailFundingViewObject detailFundingViewObject = new DetailFundingViewObject();
            UTCourse course = null;
            if(ta.getTaClass() != null) {
                detailFundingViewObject.setClassNbr(ta.getTaClass().getClassNumber());
                if (ta.getTaClass().getCourseOffering() != null) {
                    course = ta.getTaClass().getCourseOffering().getCourse();
                    if(course != null) {
                        detailFundingViewObject.setCourseName(course.getName());
                    }
                }
            }
            UTStudent taStu = ta.getTa();
            if(taStu != null) {
                detailFundingViewObject.setTaName(taStu.getName());
            }
            //暂时缺失的属性
            detailFundingViewObject.setBankId("8888888888888888888");
            detailFundingViewObject.setBankName("宇宙第一银行");
            detailFundingViewObject.setIdentity("51300000000000023X");
            detailFundingViewObject.setAssignedFunding(ta.getAssignedFunding());
            detailFundingViewObject.setBonus(ta.getBonus());
            detailFundingViewObject.setPhdFunding(ta.getPhdFunding());
            detailFundingViewObject.setTravelSubsidy(ta.getTravelSubsidy());
            detailFundingViewObject.setMonthlySalary1("100");
            detailFundingViewObject.setMonthlySalary2("200");
            detailFundingViewObject.setMonthlySalary3("300");
            detailFundingViewObject.setMonthlySalary4("400");
            detailFundingViewObject.setMonthlySalary5("500");
            detailFundingViewObject.setMonthlySalary6("600");


            Integer total = (Integer.parseInt(detailFundingViewObject.getMonthlySalary1())+
                    Integer.parseInt(detailFundingViewObject.getMonthlySalary2())+
                    Integer.parseInt(detailFundingViewObject.getMonthlySalary3())+
                    Integer.parseInt(detailFundingViewObject.getMonthlySalary4())+
                    Integer.parseInt(detailFundingViewObject.getMonthlySalary5())+
                    Integer.parseInt(detailFundingViewObject.getMonthlySalary6()));
            detailFundingViewObject.setTotal(total.toString());
            detailFundingViewObjects.add(detailFundingViewObject);
        }

    return  detailFundingViewObjects;
    }
}
