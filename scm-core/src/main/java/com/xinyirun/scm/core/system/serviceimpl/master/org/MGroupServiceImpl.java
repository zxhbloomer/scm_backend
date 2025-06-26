package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MGroupEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MGroupVo;
import com.xinyirun.scm.common.annotations.LogByIdAnnotion;
import com.xinyirun.scm.common.annotations.LogByIdsAnnotion;
import com.xinyirun.scm.common.annotations.OperationLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.OperationEnum;
import com.xinyirun.scm.common.enums.ParameterEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MGroupAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.mapper.master.org.MGroupMapper;
import com.xinyirun.scm.core.system.service.master.org.IMGroupService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  集团主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class MGroupServiceImpl extends BaseServiceImpl<MGroupMapper, MGroupEntity> implements IMGroupService {

    @Autowired
    private MGroupMapper mapper;

    @Autowired
    private MGroupAutoCodeServiceImpl autoCode;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MGroupVo> selectPage(MGroupVo searchCondition) {
        // 分页条件
        Page<MGroupEntity> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MGroupVo> select(MGroupVo searchCondition) {
        // 查询 数据
        List<MGroupVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MGroupVo> selectIdsInForExport(List<MGroupVo> searchCondition) {
        // 查询 数据
        List<MGroupVo> list = mapper.selectIdsInForExport(searchCondition);
        return list;
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @OperationLogAnnotion(
        name = SystemConstants.OPERATION.M_GROUP.OPER_LOGIC_DELETE,
        type = OperationEnum.LOGIC_DELETE,
        logByIds = @LogByIdsAnnotion(
            name = SystemConstants.OPERATION.M_GROUP.OPER_LOGIC_DELETE,
            type = OperationEnum.LOGIC_DELETE,
            oper_info = "",
            table_name = SystemConstants.OPERATION.M_GROUP.TABLE_NAME,
            id_position = ParameterEnum.FIRST,
            ids = "#{searchCondition.id}"
        )
    )
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsIn(List<MGroupVo> searchCondition) {
        List<MGroupEntity> list = mapper.selectIdsIn(searchCondition);
        for(MGroupEntity entity : list) {
            CheckResultAo cr;
            if(entity.getIs_del()){
                /** 如果逻辑删除为true，表示为：页面点击了复原操作 */
                cr = checkLogic(entity, CheckResultAo.UNDELETE_CHECK_TYPE);
            } else {
                /** 如果逻辑删除为false，表示为：页面点击了删除操作 */
                cr = checkLogic(entity, CheckResultAo.DELETE_CHECK_TYPE);
            }
            if (cr.isSuccess() == false) {
                throw new BusinessException(cr.getMessage());
            }
            entity.setIs_del(!entity.getIs_del());
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    @OperationLogAnnotion(
        name = SystemConstants.OPERATION.M_GROUP.OPER_INSERT,
        type = OperationEnum.ADD,
        logById = @LogByIdAnnotion(
            name = SystemConstants.OPERATION.M_GROUP.OPER_INSERT,
            type = OperationEnum.ADD,
            oper_info = "",
            table_name = SystemConstants.OPERATION.M_GROUP.TABLE_NAME,
            id = "#{entity.id}"
        )
    )
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MGroupEntity entity) {
        // 编号为空则自动生成编号
        if(StringUtils.isEmpty(entity.getCode())){
            entity.setCode(autoCode.autoCode().getCode());
        }
        // 插入前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 插入逻辑保存
        entity.setIs_del(false);
        return InsertResultUtil.OK(mapper.insert(entity));
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    @OperationLogAnnotion(
        name = SystemConstants.OPERATION.M_GROUP.OPER_UPDATE,
        type = OperationEnum.UPDATE,
        logById = @LogByIdAnnotion(
            name = SystemConstants.OPERATION.M_GROUP.OPER_UPDATE,
            type = OperationEnum.UPDATE,
            oper_info = "",
            table_name = SystemConstants.OPERATION.M_GROUP.TABLE_NAME,
            id = "#{entity.id}"
        )
    )
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(MGroupEntity entity) {
        // 更新前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);
        return UpdateResultUtil.OK(mapper.updateById(entity));
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param code
     * @return
     */
    public List<MGroupEntity> selectByCode(String code, Long equal_id) {
        // 查询 数据
        List<MGroupEntity> list = mapper.selectByCode(code, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MGroupEntity> selectByName(String name, Long equal_id) {
        // 查询 数据
        List<MGroupEntity> list = mapper.selectByName(name, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MGroupEntity> selectBySimpleName(String name, Long equal_id) {
        // 查询 数据
        List<MGroupEntity> list = mapper.selectBySimpleName(name, equal_id);
        return list;
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(MGroupEntity entity, String moduleType){
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                List<MGroupEntity> codeList_insertCheck = selectByCode(entity.getCode(), null);
                List<MGroupEntity> nameList_insertCheck = selectByName(entity.getName(), null);
                List<MGroupEntity> simple_name_insertCheck = selectBySimpleName(entity.getSimple_name(), null);
                if (codeList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：集团编号【"+ entity.getCode() +"】出现重复", entity.getCode());
                }
                if (nameList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：集团名称【"+ entity.getName() +"】出现重复", entity.getName());
                }
                if (simple_name_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：集团简称【"+ entity.getSimple_name() +"】出现重复", entity.getSimple_name());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                List<MGroupEntity> codeList_updCheck = selectByCode(entity.getCode(), entity.getId());
                List<MGroupEntity> nameList_updCheck = selectByName(entity.getName(), entity.getId());
                List<MGroupEntity> simple_name_updCheck = selectBySimpleName(entity.getSimple_name(), entity.getId());

                if (codeList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：集团编号【"+ entity.getCode() +"】出现重复！", entity.getCode());
                }
                if (nameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：集团全称【"+ entity.getName() +"】出现重复！", entity.getName());
                }
                if (simple_name_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：集团简称【"+ entity.getSimple_name() +"】出现重复！", entity.getSimple_name());
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                /** 如果逻辑删除为false，表示为：页面点击了删除操作 */
                if(entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                // 是否被使用的check，如果被使用则不能删除
                int count = mapper.isExistsInOrg(entity);
                if(count > 0){
                    return CheckResultUtil.NG("删除出错：该集团【"+ entity.getSimple_name() +"】在组织机构中正在被使用，不能删除！", count);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     *
     * 根据id获取数据
     *
     * @param id
     * @return
     */
    @Override
    public MGroupVo selectByid(Long id) {
        // 查询 数据
        return mapper.selectId(id);
    }
}
