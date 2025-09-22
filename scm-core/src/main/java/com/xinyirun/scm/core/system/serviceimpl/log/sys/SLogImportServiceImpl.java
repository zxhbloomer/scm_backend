//package com.xinyirun.scm.core.system.serviceimpl.log.sys;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.xinyirun.scm.bean.entity.log.sys.SLogImportEntity;
//import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
//import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
//import com.xinyirun.scm.bean.system.vo.sys.log.SLogImportVo;
//import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
//import com.xinyirun.scm.core.system.mapper.log.sys.SLogImportMapper;
//import com.xinyirun.scm.core.system.service.log.sys.ISLogImportService;
//import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
//import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
//import com.xinyirun.scm.mq.rabbitmq.producer.business.log.excelimport.LogImportProducer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * <p>
// *  服务实现类
// * </p>
// *
// * @author wwl
// * @since 2022-04-07
// */
//@Service
//public class SLogImportServiceImpl extends BaseServiceImpl<SLogImportMapper, SLogImportEntity> implements ISLogImportService {
//
//    @Autowired
//    private SLogImportMapper mapper;
//
//    @Autowired
//    private LogImportProducer producer;
//
//    @Override
//    public IPage<SLogImportVo> selectPage(SLogImportVo searchCondition) {
//        // 分页条件
//        Page<SLogImportEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize(), false);
//        // 通过page进行排序
//        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
//        // 查询page
//        IPage<SLogImportVo> logImportVoIPage = mapper.selectPage(pageCondition, searchCondition);
//
//        // 动态计算最大的limit
//        searchCondition.getPageCondition().setLimit_count((int) (searchCondition.getPageCondition().getSize() * 10));
//        // 根据动态计算的最大limit，计算count
//        Integer count = mapper.getLimitCount(searchCondition) ;
//        // 计算pages，加上之当前页前的pages
//        if(count > searchCondition.getPageCondition().getSize()) {
//            logImportVoIPage.setTotal(count + searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
//        } else {
//            logImportVoIPage.setTotal( searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
//        }
//        return logImportVoIPage;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public InsertResultAo<Integer> insert(SLogImportVo vo) {
//        SLogImportEntity entity = new SLogImportEntity();
//        BeanUtilsSupport.copyProperties(vo, entity);
//        int rtn = mapper.insert(entity);
//        return InsertResultUtil.OK(rtn);
//
////        // 改保存 mongodb
////        SLogImportMongoEntity entity = (SLogImportMongoEntity) BeanUtilsSupport.copyProperties(vo, SLogImportMongoEntity.class);
////        producer.mqSendMq(entity);
////        return InsertResultUtil.OK(1);
//    }
//
//}
