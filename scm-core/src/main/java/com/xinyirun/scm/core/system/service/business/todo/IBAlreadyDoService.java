package com.xinyirun.scm.core.system.service.business.todo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.entity.business.todo.BAlreadyDoEntity;
import com.xinyirun.scm.bean.system.vo.business.todo.BAlreadyDoVo;

import java.util.List;

/**
 * <p>
 * 待办事项服务类
 * </p>
 *
 * @author wwl
 * @since 2021-11-20
 */
public interface IBAlreadyDoService extends IService<BAlreadyDoEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BAlreadyDoVo> selectPage(BAlreadyDoVo searchCondition) ;

    /**
     * 根据类圆形查询待办id
     */
    List<Integer> selectAlreadyDoIdList(String serial_type, Long staff_id) ;

    /**
     * 插入一条记录
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BAlreadyDoVo vo);

    /**
     * 修改一条记录
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> save(BAlreadyDoVo vo);

    /**
     * 生成已办事项
     * @param vo 已办事项
     */
    public void insertAlreadyDo(BAlreadyDoVo vo);

}
