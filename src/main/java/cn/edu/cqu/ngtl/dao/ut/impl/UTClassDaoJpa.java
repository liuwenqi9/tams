package cn.edu.cqu.ngtl.dao.ut.impl;

import cn.edu.cqu.ngtl.dao.ut.UTClassDao;
import cn.edu.cqu.ngtl.dataobject.ut.UTClass;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by tangjing on 16-10-15.
 */
@Repository
@Component("UTClassDaoJpa")
public class UTClassDaoJpa implements UTClassDao {

    @Override
    public UTClass selectByClassId(String id) {
        return KradDataServiceLocator.getDataObjectService().find(UTClass.class, id);
    }

    @Override
    public void saveUTClassesByList(List<UTClass> utClasses){
        for(UTClass utClass : utClasses){
            KradDataServiceLocator.getDataObjectService().save(utClass);
        }
    }

    @Override
    public List<UTClass> getAllClasses(){
       return  KradDataServiceLocator.getDataObjectService().findAll(UTClass.class).getResults();

    }

    @Override
    public void insertOneByEntity(UTClass utClass){

        KradDataServiceLocator.getDataObjectService().save(utClass);
    }



}
