//package com.xinyirun.scm.core.system.serviceimpl.master.goods.unit;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.xinyirun.scm.bean.entity.master.goods.unit.MGoodsUnitConvertEntity;
//import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
//import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
//import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
//import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitConvertVo;
//import com.xinyirun.scm.bean.system.vo.master.goods.unit.MUnitConvertUpdateVo;
//import com.xinyirun.scm.common.exception.system.BusinessException;
//import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
//import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitConvertService;
//import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * <p>
// *  服务实现类
// * </p>
// *
// * @author htt
// * @since 2021-09-23
// */
//@Service
//public class MGoodsUnitConvertServiceImpl extends BaseServiceImpl<MGoodsUnitConvertMapper, MGoodsUnitConvertEntity> implements IMGoodsUnitConvertService {
//
//    @Autowired
//    private MGoodsUnitConvertMapper mapper;
//    /**
//     * 查询分页列表
//     * @param searchCondition
//     * @return
//     */
//    @Override
//    public IPage<MGoodsUnitConvertVo> selectPage(MGoodsUnitConvertVo searchCondition) {
//        // 分页条件
//        Page<MGoodsUnitConvertEntity> pageCondition =
//                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
//        // 通过page进行排序
//        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
//
//        return mapper.selectPage(pageCondition, searchCondition);
//    }
//
//    @Override
//    public List<MGoodsUnitConvertVo> selectList(MGoodsUnitConvertVo searchCondition) {
//        return mapper.selectList(searchCondition);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void enableByIdsIn(List<MGoodsUnitConvertVo> searchCondition) {
//        List<MGoodsUnitConvertEntity> list = mapper.selectIdsIn(searchCondition);
//        for(MGoodsUnitConvertEntity entity : list) {
//            entity.setEnable(!entity.getEnable());
//        }
//        saveOrUpdateBatch(list, 500);
//    }
//
//
//    /**
//     * 插入集合list记录（选择字段，策略插入）
//     *
//     * @param vos 实体对象
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public InsertResultAo<Integer> insert(List<MGoodsUnitConvertVo> vos) {
//
//        int rtn = 0;
//        for(MGoodsUnitConvertVo vo:vos){
//            checkLogic(vo);
//            // 插入逻辑保存
//            MGoodsUnitConvertEntity entity = (MGoodsUnitConvertEntity) BeanUtilsSupport.copyProperties(vo, MGoodsUnitConvertEntity.class);
//            rtn = mapper.insert(entity);
//        }
//        // 插入逻辑保存
//        return InsertResultUtil.OK(rtn);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void update(MUnitConvertUpdateVo vo) {
//        // 全删全插
//        mapper.deleteBySkuId(vo.getSku_id());
////        for(MGoodsUnitConvertVo v:vo.getUnitList()){
////            v.setSku_id(vo.getSku_id());
////            checkLogic(v);
////
////            // 插入逻辑保存
////            MGoodsUnitConvertEntity entity = (MGoodsUnitConvertEntity) BeanUtilsSupport.copyProperties(v, MGoodsUnitConvertEntity.class);
////            mapper.insert(entity);
////        }
//    }
//
//    @Override
//    public DeleteResultAo<Integer> deleteByIdsIn(List<MGoodsUnitConvertVo> searchCondition) {
//        return null;
//    }
//
//    /**
//     * 启用
//     * @param searchCondition
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void enabledByIdsIn(List<MGoodsUnitConvertVo> searchCondition) {
//        List<MGoodsUnitConvertEntity> list = mapper.selectIdsIn(searchCondition);
//        for(MGoodsUnitConvertEntity entity : list) {
//            entity.setEnable(Boolean.TRUE);
//        }
//        saveOrUpdateBatch(list, 500);
//    }
//
//    @Override
//    public void updateOrSave(List<MGoodsUnitConvertVo> searchCondition) {
//
//        List<MGoodsUnitConvertEntity> list = new ArrayList<>();
//        for(MGoodsUnitConvertVo vo:searchCondition){
//            // 插入逻辑保存
//            MGoodsUnitConvertEntity entity = (MGoodsUnitConvertEntity) BeanUtilsSupport.copyProperties(vo, MGoodsUnitConvertEntity.class);
//            list.add(entity);
//        }
//        saveOrUpdateBatch(list, 500);
//    }
//
//    /**
//     * 停用
//     * @param searchCondition
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void disSabledByIdsIn(List<MGoodsUnitConvertVo> searchCondition) {
//        List<MGoodsUnitConvertEntity> list = mapper.selectIdsIn(searchCondition);
//        for(MGoodsUnitConvertEntity entity : list) {
//            entity.setEnable(Boolean.FALSE);
//        }
//        saveOrUpdateBatch(list, 500);
//    }
//
//    @Override
//    public MGoodsUnitConvertVo selectById(int id) {
//        return mapper.selectId(id);
//    }
//
//    @Override
//    public List<MGoodsUnitConvertEntity> selectByName(String name) {
//        // 查询 数据
//        return mapper.selectByName(name);
//    }
//
//    @Override
//    public List<MGoodsUnitConvertEntity> selectByBuisness(int buisnessTypeId) {
//        // 查询 数据
//        return mapper.selectByBuisness(buisnessTypeId);
//    }
//
//    private void checkLogic(MGoodsUnitConvertVo vo) {
//        MGoodsUnitConvertVo searchCondition = new MGoodsUnitConvertVo();
//        searchCondition.setSku_id(vo.getSku_id());
//        searchCondition.setJl_unit_id(vo.getJl_unit_id());
//        List<MGoodsUnitConvertVo> list = mapper.selectList(searchCondition);
//        if (list.size() > 0) {
//            throw new BusinessException("该物料已存在"+vo.getJl_unit()+"->"+vo.getHs_unit()+"关系,无法重复添加");
//        }
//    }
//
//}
