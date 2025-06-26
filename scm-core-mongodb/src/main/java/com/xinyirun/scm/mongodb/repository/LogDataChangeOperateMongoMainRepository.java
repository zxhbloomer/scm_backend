package com.xinyirun.scm.mongodb.repository;

import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeOperateMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogDataChangeOperateMongoMainRepository extends MongoRepository<SLogDataChangeOperateMongoEntity, String> {
    /**
     * 根据order_code查找SLogDataChangeOperateMongoEntity
     *
     * @param request_id
     * @return 包含订单编码的SLogDataChangeOperateMongoEntity对象的Optional
     */
    @Query("{ 'request_id' : ?0 }")
    Optional<SLogDataChangeOperateMongoEntity> findByRequestId(String request_id);
}