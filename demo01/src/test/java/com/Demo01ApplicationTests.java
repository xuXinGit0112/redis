package com;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Demo01ApplicationTests {

    @Test
    void contextLoads() {
//        System.out.println(null instanceof Object);
        int a=10;
        double b=10;
        long c=10;
        System.out.println(a==b);//true
        System.out.println(a==c);//true
        System.out.println(b==c);//true


    }

}
