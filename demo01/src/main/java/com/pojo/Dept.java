package com.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName
@Document
public class Dept implements Serializable {
    @TableId
    @Id
    @MongoId
    @Indexed
    private Integer deptno;
    private String dname;
    private String loc;
}
