package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.EmpRepository;
import com.pojo.Emp;
import com.service.EmpService;
import com.utils.RedisUtil2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class EmpController {
    @Resource
    private EmpService empService;
    @Autowired
    private RedisUtil2 redisUtil2;

    /**
     * 使用@EnableCaching的方式对redis进行增删改查
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Emp findById(int id){
        return empService.findById(id);
    }
    @GetMapping("/insert1")
    public boolean insert1(Emp emp){
        return empService.insert1(emp);
    }
    @GetMapping("/modifyById")
    public Emp modifyById(Emp emp){
        return empService.modifyById(emp);
    }
    @GetMapping("/delete")
    public boolean delete(int id){
        return empService.delete(id);
    }
    @GetMapping("/findById2")
    public Emp findById2(int id){
        return empService.findById(id);
    }

    /**
     * 根据RedisUtil工具类分页查询员工信息保存到redis
     * @param pageIndex
     * @param pageSize
     * @param sal1
     * @param sal2
     * @return
     */
    @GetMapping("/findEmp")
    public List<Emp> find(int pageIndex, int pageSize, int sal1, int sal2){
        return empService.findCondition(
                new Page<Emp>(pageIndex,pageSize),
                new QueryWrapper<Emp>().between("sal",sal1,sal2)
        );
    }
    @GetMapping("/findEmp2")
    public List<Emp> find2(int pageIndex, int pageSize){

        List<Emp> list = (List<Emp>) redisUtil2.get("list");
        if(list == null){
            System.out.println("list == null");
            list = empService.page(new Page<Emp>(pageIndex,pageSize)).getRecords();
            list = empService.page(new Page<Emp>(pageIndex,pageSize)).getRecords();
            redisUtil2.set("list",list);
        }

        return list;
    }
    @GetMapping("/findEmp3")
    public List<Emp> find3(long pageIndex, long pageSize){
        List<Object> list = redisUtil2.lRange("mylist",(pageIndex-1)*pageSize,pageIndex*pageSize-1);
        if(list == null || list.size() == 0){
            List<Emp> emps = empService.page(new Page<Emp>(pageIndex,pageSize)).getRecords();

            redisUtil2.rPushAll("mylist",emps,30L);
            return emps;
        }

        List<Emp> myemps = list.stream().map(o->(Emp)o).collect(Collectors.toList());
        return myemps;

        //运作原理:首先查询redis数据库
        //然后产生分支
        //分支1:若redis中存在数据则通过rangAPI去划区域取值
        //分支2:若redis中不存在数据,则访问mysql数据库将所有结果缓存至redis中,然后第二次访问时执行分支1

    }

    /**
     * 使用RedisTemplate对redis进行增删改查
     * @param id
     * @return
     */
    @GetMapping("/findByNo")
    public Emp findByNo(int id){
        return empService.findByNo(id);
    }
    @GetMapping("/insert3")
    public boolean insert3(Emp emp){
        return empService.insert3(emp);
    }
    @GetMapping("/update3")
    public Emp update3(Emp emp){
        return empService.update3(emp);
    }
    @GetMapping("delete3")
    public boolean remove3(int id){
        return empService.remove3(id);
    }


    /**
     * mongo
     * repository
     */

    @GetMapping("/findMongoRepository")
    public List<Emp> findMongoRepository(int id){
        return empService.findMongoRepository(id);
    }

    @GetMapping("/saveOrUpdateMongoRepository")
    public Emp saveMongoRepository(Emp emp){
        return empService.saveOrUpdateMongoRepository(emp);
    }

    @GetMapping("removeMongoRepository")
    public boolean removeMongoRepository(int id){
        return  empService.removeMongoRepository(id);
    }

    @RequestMapping("/findByEnameAndSal")
    public Emp findByEnameAndSal(String ename,Integer sal){
        return empService.findByEnameAndSal(ename,sal);
    }
    @Resource
    private EmpRepository empRepository;
    @RequestMapping("/findByEnameLike")
    public List<Emp> findByEnameLike(String ename,int pageIndex,int pageSize){
//        return empRepository.findByEnameLike(ename,
//                PageRequest.of(pageIndex-1,pageSize,
//                        Sort.by(Sort.Direction.DESC,"empno")));
        return empRepository.findByEnameLike(ename,
                PageRequest.of(pageIndex-1,pageSize,
                        Sort.by(Sort.Direction.DESC,"empno"))).getContent();
    }

    /**
     * MongoTemplate
     */
    @Resource
    private MongoTemplate mongoTemplate;
    @RequestMapping("saveOrUpdateMongoTemplate")
    public Emp saveOrUpdateMongoTemplate(Emp emp){
        if(emp.getEmpno()!=null&&emp.getEmpno()>0){
//            empService.update(emp,new QueryWrapper<Emp>().eq("empno",emp.getEmpno()));
            empService.updateById(emp);
            return mongoTemplate.save(emp);
        }
        if(empService.save(emp)){
            return mongoTemplate.save(emp);
        }
        return null;
    }
    @RequestMapping("insertMongoTemplate")
    public Emp insertMongoTemplate(Emp emp){
//        List<Emp> list=new ArrayList<>();
        if(empService.save(emp)){
//            return (List<Emp>)mongoTemplate.insertAll(list);
            return mongoTemplate.insert(emp);
        }
        return null;
    }

    @RequestMapping("/removeMongoTemplate")
    public long removeMongoTemplate(int empno){
        if(empService.delete(empno)){
            Query query=new Query();
            query.addCriteria(Criteria.where("empno").in(empno));
            return mongoTemplate.remove(query,Emp.class).getDeletedCount();
        }
        return 0;

    }

    @RequestMapping("/findAllMongoTemplate")
    public List<Emp> findAllMongoTemplate(){
        List<Emp> emps=mongoTemplate.findAll(Emp.class);
        if(emps.size()==0){
            emps=empService.list();
            mongoTemplate.save(emps);
            return emps;
        }
        return emps;
    }

    @RequestMapping("/pageMongoTemplate")
    public List<Emp> pageMongoTemplate(String ename,int pageIndex,int pageSize){
        Query query=new Query();
        Pattern pattern=Pattern.compile("^.*" + ename + ".*$", Pattern.CASE_INSENSITIVE);
        Criteria criteria=Criteria.where("ename").regex(pattern);
        query.addCriteria(criteria);
        query.with(PageRequest.of(pageIndex-1,pageSize,Sort.by(Sort.Direction.DESC,"empno")));
                long count=mongoTemplate.count(query,Emp.class);
        System.out.println(count+"=================");
        return mongoTemplate.find(query,Emp.class);
    }
}

