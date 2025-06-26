//package com.xinyirun.scm.core.system.service.master.goods.unit;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
//import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
//import com.xinyirun.scm.bean.entity.master.goods.unit.MGoodsUnitConvertEntity;
//import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitConvertVo;
//import com.xinyirun.scm.bean.system.vo.master.goods.unit.MUnitConvertUpdateVo;
//
//import java.util.List;
//
///**
// * <p>
// *  服务类
// * </p>
// *
// * @author htt
// * @since 2021-09-23
// */
//public interface IMGoodsUnitConvertService extends IService<MGoodsUnitConvertEntity> {
//
//    /**
//     * 获取列表，页面查询
//     */
//    IPage<MGoodsUnitConvertVo> selectPage(MGoodsUnitConvertVo searchCondition) ;
//
//    /**
//     * 获取列表，页面查询
//     */
//    List<MGoodsUnitConvertVo> selectList(MGoodsUnitConvertVo searchCondition) ;
//
//    /**
//     * 查询by id，返回结果
//     */
//    MGoodsUnitConvertVo selectById(int id);
//
//    /**
//     * 插入一条记录（选择字段，策略插入）
//     */
//    InsertResultAo<Integer> insert(List<MGoodsUnitConvertVo> vo);
//
//    /**
//     * 修改单位换算数据
//     */
//    void update(MUnitConvertUpdateVo vo);
//
//
//    /**
//     * 批量删除
//     */
//    DeleteResultAo<Integer> deleteByIdsIn(List<MGoodsUnitConvertVo> searchCondition);
//
//    /**
//     * 批量启用
//     */
//    void enabledByIdsIn(List<MGoodsUnitConvertVo> searchCondition);
//
//    /**
//     * 批量启用
//     */
//    void updateOrSave(List<MGoodsUnitConvertVo> searchCondition);
//
//    /**
//     * 批量禁用
//     */
//    void disSabledByIdsIn(List<MGoodsUnitConvertVo> searchCondition);
//
//    /**
//     * 通过name查询
//     *
//     */
//    List<MGoodsUnitConvertEntity> selectByName(String name);
//
//    /**
//     * 通过key查询
//     *
//     */
//    List<MGoodsUnitConvertEntity> selectByBuisness(int buisnessTypeId);
//
//    /**
//     * 批量启用/停用
//     */
//    void enableByIdsIn(List<MGoodsUnitConvertVo> searchCondition);
//}
