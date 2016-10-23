package cn.edu.cqu.ngtl.dao.tams.impl;

import cn.edu.cqu.ngtl.dao.tams.TAMSTaApplicationDao;
import cn.edu.cqu.ngtl.dataobject.tams.TAMSTaApplication;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by tangjing on 16-10-23.
 */
@Repository
@Component("TAMSTaApplicationDaoJpa")
public class TAMSTaApplicationDaoJpa implements TAMSTaApplicationDao {

    @Override
    public boolean insertOne(TAMSTaApplication taApplication) {

        KRADServiceLocator.getDataObjectService().save(taApplication);

        return true;
    }

}
