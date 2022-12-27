package com.dao;

import com.pojo.Emp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpRepository extends MongoRepository<Emp,Integer> {
    public Emp findByEnameAndSal(String ename,Integer sal);

//    public List<Emp> findByEnameLike(String ename, Pageable page);
    public Page<Emp> findByEnameLike(String ename, Pageable page);

}
