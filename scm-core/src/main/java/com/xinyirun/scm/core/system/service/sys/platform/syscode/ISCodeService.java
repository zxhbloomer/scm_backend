package com.xinyirun.scm.core.system.service.sys.platform.syscode;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.bean.system.vo.sys.platform.syscode.SCodeVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISCodeService extends IService<SCodeEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<SCodeVo> selectPage(SCodeVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<SCodeVo> select(SCodeVo searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    SCodeVo selectByid(Long id);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(SCodeEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(SCodeEntity entity);

    /**
     * 通过type查询
     *
     */
    List<SCodeEntity> selectByType(String type, Long equal_id);

    /**
     * 获取编号
     * @param type
     * @return
     */
    UpdateResultAo<SCodeEntity> createCode(String type);
}
