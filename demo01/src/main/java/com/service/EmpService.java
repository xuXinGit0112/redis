package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pojo.Emp;

import java.util.List;

public interface EmpService extends IService<Emp> {
    public Emp findById(int id);
    boolean insert1(Emp emp);
    Emp modifyById(Emp emp);
    boolean delete(int id);

    List<Emp> findCondition(IPage<Emp> page, QueryWrapper<Emp> wrapper);

    public Emp findByNo(int id);
    boolean insert3(Emp emp);
    Emp update3(Emp emp);
    boolean remove3(int id);


    /**
     * mongo
     * repository
     */
    List<Emp> findMongoRepository(int id);
    Emp saveOrUpdateMongoRepository(Emp emp);
    boolean removeMongoRepository(int id);
    Emp findByEnameAndSal(String ename,Integer sal);
}
