package com.xinyirun.scm.core.system.serviceimpl.business.todo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.todo.BTodoEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.bean.system.vo.business.todo.BTodoVo;
import com.xinyirun.scm.bean.system.vo.business.todo.TodoCountVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.todo.BTodoMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MStaffOrgMapper;
import com.xinyirun.scm.core.system.service.business.todo.IBAlreadyDoService;
import com.xinyirun.scm.core.system.service.business.todo.IBTodoService;
import com.xinyirun.scm.core.system.service.master.org.IMPositionService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * <p>
 * 待办事项 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-11-20
 */
@Service
public class BTodoServiceImpl extends BaseServiceImpl<BTodoMapper, BTodoEntity> implements IBTodoService {

    @Autowired
    private BTodoMapper mapper;

    @Autowired
    private MStaffOrgMapper staffOrgMapper;

    @Autowired
    private IBAlreadyDoService ibAlreadyDoService;

    @Autowired
    private IMPositionService imPositionService;

    @Override
    public IPage<BTodoVo> selectPage(BTodoVo searchCondition) {
        // 分页条件
        Page<BTodoEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序

        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<BTodoVo> selectList(BTodoVo searchCondition) {
        return mapper.selectTodoList(searchCondition);
    }

    @Override
    public List<Integer> selectTodoIdList(String serial_type,Long[] position_id) {
        return mapper.selectTodoIdList(serial_type, position_id);
    }

    @Override
    public BTodoEntity selectById(int id) {
        return mapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BTodoVo vo) {
        // 插入逻辑保存
        BTodoEntity entity = (BTodoEntity) BeanUtilsSupport.copyProperties(vo, BTodoEntity.class);

        entity.setStatus(DictConstant.DICT_B_TODO_STATUS_TODO);
        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> save(BTodoVo vo) {
        // 修改逻辑保存
        BTodoEntity entity = (BTodoEntity) BeanUtilsSupport.copyProperties(vo, BTodoEntity.class);

        int rtn = mapper.updateById(entity);
        return InsertResultUtil.OK(rtn);
    }

    @Override
    public TodoCountVo selectTodoCount(String serial_type) {
        return mapper.selectTodoCount(serial_type, SecurityUtil.getStaff_id());
    }

    /**
     * 生成待办事项
     * @param vo
     */
    @Override
    public void insertTodo(BTodoVo vo) {
        List<MPositionVo> list = imPositionService.selectPositionByPerms(vo.getPerms());
        for (MPositionVo position : list) {
            BTodoVo todoVo = new BTodoVo();
            BeanUtilsSupport.copyProperties(vo, todoVo);
            todoVo.setStatus(DictConstant.DICT_B_TODO_STATUS_TODO);
            todoVo.setPosition_code(position.getCode());
            todoVo.setPosition_id(position.getId());
            this.insert(todoVo);
        }
    }

    /**
     * 根据 详情 serial_ids 和 serial_type 删除数据
     *
     * @param serial_ids  serial_id 集合
     * @param serial_type 类型
     */
    @Override
    public void deleteByIdsAndSerialType(@NotEmpty(message = "id集合不能为空") List<Integer> serial_ids,
                                         @NotBlank(message = "serial_type不能为空") String serial_type) {
        mapper.deleteByIdsAndSerialType(serial_ids, serial_type);
    }

    /**
     * 获取当前登录用户的session数据
     * @return
     */
    public UserSessionBo getUserSession(){
        UserSessionBo bo = ServletUtil.getUserSession();
        return bo;
    }

}
