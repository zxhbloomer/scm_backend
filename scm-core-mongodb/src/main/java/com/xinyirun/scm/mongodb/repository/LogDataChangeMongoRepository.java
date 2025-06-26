package com.xinyirun.scm.mongodb.repository;

import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMongoEntity;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogDataChangeMongoRepository extends MongoRepository<SLogDataChangeMongoEntity, String> {
    // 这里可以添加更多的查询方法，如果需要

    /**
     * 添加按request_id删除数据的方法,注意void是删除
     * @param requestId
     */
    @DeleteQuery("{ 'request_id' : ?0 }")
    void deleteByRequestId(String requestId);

    @Query("{ 'order_code' : ?0 }")
    Optional<SLogDataChangeMongoEntity> findByOrderCode(String order_code);
}