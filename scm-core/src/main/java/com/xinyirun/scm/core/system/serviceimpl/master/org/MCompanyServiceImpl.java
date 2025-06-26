package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MCompanyEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.org.MCompanyMapper;
import com.xinyirun.scm.core.system.service.master.enterprise.IMEnterpriseService;
import com.xinyirun.scm.core.system.service.master.org.IMCompanyService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MCompanyAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 公司主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class MCompanyServiceImpl extends BaseServiceImpl<MCompanyMapper, MCompanyEntity> implements IMCompanyService {

    @Autowired
    private MCompanyMapper mapper;

    @Autowired
    private MCompanyAutoCodeServiceImpl autoCode;

    @Autowired
    private IMEnterpriseService enterpriseService;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MCompanyVo> selectPage(MCompanyVo searchCondition) {
        // 分页条件
        Page<MCompanyVo> pageCondition =
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
    public List<MCompanyVo> select(MCompanyVo searchCondition) {
        // 查询 数据
        List<MCompanyVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MCompanyVo> selectIdsInForExport(List<MCompanyVo> searchCondition) {
        // 查询 数据
        List<MCompanyVo> list = mapper.selectIdsInForExport(searchCondition);
        return list;
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsIn(List<MCompanyVo> searchCondition) {
        List<MCompanyEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCompanyEntity entity : list) {
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
//            entity.setTenant_id(getUserSessionTenantId());
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MCompanyEntity entity) {
//        entity.setTenant_id(getUserSessionTenantId());
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
        Integer rtn = mapper.insert(entity);
        enterpriseService.insertSystemEnterpriseByOrgCompany(entity);
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(MCompanyEntity entity) {
        // 更新前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);
//        entity.setTenant_id(getUserSessionTenantId());
        Integer rtn = mapper.updateById(entity);
        enterpriseService.updateSystemEnterpriseByOrgCompany(entity);
        return UpdateResultUtil.OK(rtn);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param code
     * @return
     */
    public List<MCompanyEntity> selectByCode(String code, Long equal_id) {
        // 查询 数据
        List<MCompanyEntity> list = mapper.selectByCode(code, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MCompanyEntity> selectByName(String name, Long equal_id) {
        // 查询 数据
        List<MCompanyEntity> list = mapper.selectByName(name, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MCompanyEntity> selectBySimpleName(String name, Long equal_id) {
        // 查询 数据
        List<MCompanyEntity> list = mapper.selectBySimpleName(name, equal_id);
        return list;
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(MCompanyEntity entity, String moduleType){
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                List<MCompanyEntity> codeList_insertCheck = selectByCode(entity.getCode(), null);
                List<MCompanyEntity> nameList_insertCheck = selectByName(entity.getName(),  null);
                List<MCompanyEntity> simple_name_insertCheck = selectBySimpleName(entity.getSimple_name(), null);
                if (codeList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：企业编号【"+ entity.getCode() +"】出现重复!", entity.getCode());
                }
                if (nameList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：企业全称【"+ entity.getName() +"】出现重复!", entity.getName());
                }
                if (simple_name_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：企业简称【"+ entity.getSimple_name() +"】出现重复!", entity.getSimple_name());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                List<MCompanyEntity> codeList_updCheck = selectByCode(entity.getCode(), entity.getId());
                List<MCompanyEntity> nameList_updCheck = selectByName(entity.getName(), entity.getId());
                List<MCompanyEntity> simple_name_updCheck = selectBySimpleName(entity.getSimple_name(), entity.getId());

                if (codeList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：企业编号【"+ entity.getCode() +"】出现重复!", entity.getCode());
                }
                if (nameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：企业全称【"+ entity.getName() +"】出现重复!", entity.getName());
                }
                if (simple_name_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：企业简称【"+ entity.getSimple_name() +"】出现重复!", entity.getSimple_name());
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
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】在组织机构中正在被使用，不能删除！", count);
                }
                break;
            case CheckResultAo.UNDELETE_CHECK_TYPE:
//                /** 如果逻辑删除为true，表示为：页面点击了删除操作 */
//                if(!entity.getIs_del()) {
//                    return CheckResultUtil.OK();
//                }
//                // 更新场合，不能重复设置
//                List<MCompanyEntity> codeList_delCheck = selectByCode(entity.getCode(), null, entity.getId());
//                List<MCompanyEntity> nameList_delCheck = selectByName(entity.getName(), null, entity.getId());
//                List<MCompanyEntity> simple_name_delCheck = selectBySimpleName(entity.getSimple_name(), null, entity.getId());
//
//                if (codeList_delCheck.size() >= 1) {
//                    return CheckResultUtil.NG("复原出错：复原企业编号【"+ entity.getCode() +"】这条数据会造成数据重复！", entity.getCode());
//                }
//                if (nameList_delCheck.size() >= 1) {
//                    return CheckResultUtil.NG("复原出错：复原企业全称【"+ entity.getName() +"】这条数据会造成数据重复！", entity.getName());
//                }
//                if (simple_name_delCheck.size() >= 1) {
//                    return CheckResultUtil.NG("复原出错：复原企业简称【"+ entity.getSimple_name() +"】这条数据会造成数据重复！", entity.getSimple_name());
//                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }


    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    @Override
    public MCompanyVo selectByid(Long id) {
        // 查询 数据
        return mapper.selectId(id);
    }
}
