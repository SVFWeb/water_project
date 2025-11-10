package com.example.demo02.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo02.domain.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TransactionRepository extends BaseMapper<Transaction> {
    // 使用MyBatis-Plus的BaseMapper，已经包含了基本的CRUD操作

}