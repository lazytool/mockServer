package com.letv.mocker.ui.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.letv.mocker.framework.exception.ServiceException;
import com.letv.mocker.ui.dao.SysDao;
import com.letv.mocker.ui.vo.SystemContext;

@Service("sysService")
@Scope("singleton")
public class SystemServiceImpl implements SystemService {

    @Resource
    private SysDao sysDao;

    @Override
    public String getVal(String name) throws ServiceException {
        try {
            final List<String> vals = this.sysDao.findValByName(name);
            return vals.get(0);
        } catch (final Exception e) {
            e.printStackTrace();
            throw new ServiceException("error:" + e.getMessage());
        }

    }

    @Override
    public List<SystemContext> getAll() throws Exception {
        return this.sysDao.findAll();
    }

    @Override
    public void update(String name, String value) throws Exception {
        this.sysDao.update(name, value);
    }

}
