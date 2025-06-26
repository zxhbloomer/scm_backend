package com.xinyirun.scm.mongodb.repository;

import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogDataChangeMongoMainRepository extends MongoRepository<SLogDataChangeMainMongoEntity, String> {
    /**
     * 根据order_code查找SLogDataChangeMainMongoEntity
     *
     * @param order_code 订单编码
     * @return 包含订单编码的SLogDataChangeMainMongoEntity对象的Optional
     */
    @Query("{ 'order_code' : ?0 }")
    Optional<SLogDataChangeMainMongoEntity> findByOrderCode(String order_code);

    /**
     * 添加按request_id删除数据的方法,注意void是删除
     * @param requestId
     */
    @DeleteQuery("{ 'request_id' : ?0 }")
    void deleteByRequestId(String requestId);

    @Query("{ 'order_code' : ?0, 'request_id' : ?1 }")
    Page<SLogDataChangeMainMongoEntity> findAllByOrderCodeAndRequestId(String order_code, String request_id, Pageable pageable);

    @Query("{ 'order_code' : ?0 }")
    Page<SLogDataChangeMainMongoEntity> findAllByOrderCode(String order_code, Pageable pageable);

    @Query("{ 'request_id' : ?0 }")
    Page<SLogDataChangeMainMongoEntity> findAllByRequestId(String request_id, Pageable pageable);

    Page<SLogDataChangeMainMongoEntity> findAll(Pageable pageable);
}