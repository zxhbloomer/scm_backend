package com.xinyirun.scm.mongodb.repository.v1;

import com.xinyirun.scm.bean.entity.mongo.monitor.v1.BMonitorDataMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 类似mybatis 的 dao,直接继承 MongoRepository 提供 insert,delete,find基本操作
 */
@Repository
public interface MonitorMongoRepository extends MongoRepository<BMonitorDataMongoEntity, String> {

}