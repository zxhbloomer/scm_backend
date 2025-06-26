package com.xinyirun.scm.core.system.serviceimpl.sys.pages;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.sys.pages.SPagesFunctionEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesFunctionExportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesFunctionVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.sys.pages.SPagesFunctionMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesFunctionService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 页面按钮表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-06-05
 */
@Service
public class SPagesFunctionServiceImpl extends ServiceImpl<SPagesFunctionMapper, SPagesFunctionEntity> implements
        ISPagesFunctionService {

    @Autowired
    private SPagesFunctionMapper mapper;

    @Autowired
    private ISConfigService configService;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SPagesFunctionVo> selectPage(SPagesFunctionVo searchCondition) {
        // 分页条件
        Page<SPagesFunctionVo> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        if("sort".equals(searchCondition.getPageCondition().getSort())) {
            PageUtil.setSort(pageCondition, "page_code");
            PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        } else if("-sort".equals(searchCondition.getPageCondition().getSort())) {
            PageUtil.setSort(pageCondition, "page_code");
            PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        } else {
            PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        }
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<SPagesFunctionVo> select(SPagesFunctionVo searchCondition) {
        // 查询 数据
        List<SPagesFunctionVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<SPagesFunctionVo> insert(SPagesFunctionVo entity) {
        // 插入前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 插入逻辑保存
        SPagesFunctionEntity sf = (SPagesFunctionEntity) BeanUtilsSupport.copyProperties(entity, SPagesFunctionEntity.class);
        sf.setId(null); // 插入时，id置空
        int count = mapper.insert(sf);
        if(count == 0){
            throw new InsertErrorException("保存失败，请查询后重新再试。");
        }
        return InsertResultUtil.OK(selectByid(sf.getId()));
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<SPagesFunctionVo> update(SPagesFunctionVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        vo.setC_id(null);
        vo.setC_time(null);

        SPagesFunctionEntity sf = (SPagesFunctionEntity) BeanUtilsSupport.copyProperties(vo, SPagesFunctionEntity.class);
        int count = mapper.updateById(sf);
        if(count == 0){
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(selectByid(sf.getId()));
    }

    /**
     * 更新一条记录（选择字段，策略更新），指定字段
     *
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<SPagesFunctionVo> update_assign(SPagesFunctionVo vo) {
         int updCount = mapper.update(null, new UpdateWrapper<SPagesFunctionEntity>()
            .eq("id", vo.getId())
            .eq("dbversion", vo.getDbversion())
            .set("sort", vo.getSort())
            .set("u_id", SecurityUtil.getStaff_id())
            .set("u_time", LocalDateTime.now())
            .set("dbversion", vo.getDbversion() + 1)
         );
         if(updCount == 0){
             throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
         }
        return UpdateResultUtil.OK(selectByid(vo.getId()));
    }

    /**
     * check逻辑
     *
     * @return
     */
    public CheckResultAo checkLogic(SPagesFunctionVo vo, String moduleType) {

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                SPagesFunctionVo insertCondition = (SPagesFunctionVo) BeanUtilsSupport.copyProperties(vo, SPagesFunctionVo.class);
                insertCondition.setId(null);
                List<SPagesFunctionVo> insertRtnList = selectByPageIdAndFunctionId(insertCondition);
                if (insertRtnList.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：页面名称【"+ insertRtnList.get(0).getPage_name() +"】、"
                                                             + "按钮名称【" + insertRtnList.get(0).getFunction_name() + "】"
                                                             +"出现重复!", vo.getId());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 新增场合，不能重复
                SPagesFunctionVo updCondition = (SPagesFunctionVo) BeanUtilsSupport.copyProperties(vo, SPagesFunctionVo.class);
                updCondition.setNe_id(vo.getId());
                updCondition.setId(null);
                List<SPagesFunctionVo> updRtnList = selectByPageIdAndFunctionId(updCondition);
                if (updRtnList.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：页面名称【"+ updRtnList.get(0).getPage_name() +"】、"
                                                              + "按钮名称【" + updRtnList.get(0).getFunction_name() + "】"
                                                              +"出现重复!", vo.getId());
                }
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
    public SPagesFunctionVo selectByid(Long id) {
        SPagesFunctionVo condition = new SPagesFunctionVo();
        // 查询 数据
        condition.setId(id);
        return mapper.selectId(condition);
    }

    /**
     * 批量删除
     *
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DeleteResultAo<Integer> realDeleteByIdsIn(List<SPagesFunctionVo> searchCondition) {
        List<Long> idList = new ArrayList<>();
        searchCondition.forEach(bean -> {
            idList.add(bean.getId());
        });
        int result=mapper.deleteBatchIds(idList);
        return DeleteResultUtil.OK(result);
    }

    /**
     * 查询导出数据
     *
     * @param searchConditionList 查询参数
     * @return List<SPagesFunctionExportVo>
     */
    @Override
    public List<SPagesFunctionExportVo> selectExportList(SPagesFunctionVo searchConditionList) {
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (searchConditionList.getIds() != null && !Objects.isNull(sConfigEntity)
                && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(searchConditionList);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.exportList(searchConditionList);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @return
     */
    public List<SPagesFunctionVo> selectByPageIdAndFunctionId(SPagesFunctionVo vo) {
        return mapper.selectByPageIdAndFunctionId(vo);
    }

}
