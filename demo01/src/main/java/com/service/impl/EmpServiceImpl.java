package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dao.EmpDao;
import com.dao.EmpRepository;
import com.pojo.Emp;
import com.service.EmpService;
import com.utils.RedisUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Service
public class EmpServiceImpl extends ServiceImpl<EmpDao, Emp> implements EmpService {
//    @Cacheable(cacheNames = "user", key = "#id",
//            condition = "#id%2==1", unless = "#result==null")

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedisUtil redisUtil;
    @Cacheable(cacheNames = "emp", key = "#id",
            unless = "#result==null")
    @Override
    public Emp findById(int id){
        System.out.println("findById"+id);
        return super.baseMapper.selectById(id);
    }
//    @Cacheable(cacheNames = "newEmp",key = "#emp.empno")
    @Override
    public boolean insert1(Emp emp) {
        System.out.println("insert1");
        return super.baseMapper.insert(emp)>0;
    }
    @Cacheable(cacheNames = "newEmp",key = "#emp.empno")
    @Override
    public Emp modifyById(Emp emp) {
        System.out.println("update"+emp.getEmpno());
        super.baseMapper.updateById(emp);
        return emp;
    }
    @CacheEvict(cacheNames = "emp", key = "#id")
    public boolean delete(int id){
        System.out.println("delete"+id);
        return super.baseMapper.deleteById(id)>0;
    }

    @Override
    public List<Emp> findCondition(IPage<Emp> page, QueryWrapper<Emp> wrapper) {
        return super.baseMapper.select(page,wrapper);
    }

    @Override
    public Emp findByNo(int id) {
        System.out.println("findByNo");
        Object obj=redisTemplate.opsForValue().get("emp"+id);
        if(obj==null){
            Emp emp=super.baseMapper.selectById(id);
            if(emp==null){
                return null;
            }
            redisTemplate.opsForValue().set("emp"+emp.getEmpno(),emp);
            return emp;
        }
        return (Emp)obj;
    }

    @Override
    public boolean insert3(Emp emp) {
        System.out.println("insert3");
        return super.baseMapper.insert(emp)>0;
    }

    @Override
    public Emp update3(Emp emp) {
        System.out.println("update3");
        super.baseMapper.updateById(emp);
        redisUtil.set("emp"+emp.getEmpno(),emp);
        return emp;
    }

    @Override
    public boolean remove3(int id) {
        boolean is_delete=false;
        if(super.baseMapper.deleteById(id)>0){
            if(redisUtil.exists("emp"+id)){
                redisUtil.remove("emp"+id);
            }
            is_delete=true;
        }
        return is_delete;
    }

    /**
     * mongo
     * repository
     */
    @Resource
    private EmpRepository empRepository;

    @Override
    public List<Emp> findMongoRepository(int id){
        List<Emp> emps=empRepository.findAll();
        if(emps.size()==0){
            emps=super.baseMapper.selectList(new QueryWrapper<>());
            empRepository.saveAll(emps);
            return emps;
        }
        return emps;
    }

    @Override
    public Emp saveOrUpdateMongoRepository(Emp emp) {
        if(emp.getEmpno()!=null&&emp.getEmpno()>0){
            return empRepository.save(emp);
        }
        if(super.baseMapper.insert(emp)>0){
            return empRepository.save(emp);
        }
        return null;
    }

    @Override
    public Emp findByEnameAndSal(String ename, Integer sal) {
        return empRepository.findByEnameAndSal(ename, sal);
    }

    @Override
    public boolean removeMongoRepository(int id) {
        empRepository.deleteById(id);
        return super.baseMapper.deleteById(id)>0;
    }

}
