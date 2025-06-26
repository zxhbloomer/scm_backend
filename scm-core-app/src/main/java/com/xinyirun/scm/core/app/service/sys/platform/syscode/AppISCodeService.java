package com.xinyirun.scm.core.app.service.sys.platform.syscode;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.app.ao.result.AppInsertResultAo;
import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.app.vo.sys.platform.syscode.AppSCodeVo;
import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface AppISCodeService extends IService<SCodeEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<AppSCodeVo> selectPage(AppSCodeVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<AppSCodeVo> select(AppSCodeVo searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    AppSCodeVo selectByid(Long id);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    AppInsertResultAo<Integer> insert(SCodeEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    AppUpdateResultAo<Integer> update(SCodeEntity entity);

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
    AppUpdateResultAo<SCodeEntity> createCode(String type);
}
