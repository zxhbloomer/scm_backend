package com.xinyirun.scm.core.system.serviceimpl.master.carrier;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerInfoEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.carrier.MCarrierInfoVo;
import com.xinyirun.scm.bean.system.vo.master.carrier.MCarrierVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.carrier.MCarrierInfoMapper;
import com.xinyirun.scm.core.system.mapper.master.carrier.MCarrierMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.master.carrier.IMCarrierService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MCarrierServiceImpl extends BaseServiceImpl<MCarrierMapper, MCustomerEntity> implements IMCarrierService {
    @Autowired
    private MCarrierMapper mapper;

    @Autowired
    private MCarrierInfoMapper customerInfoMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    /**
     * 查询承运商分页
     */
    @Override
    public IPage<MCarrierVo> selectPage(MCarrierVo searchCondition) {
        // 分页条件
        Page<MCustomerEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取承运商
     */
    @Override
    public MCarrierVo getDetail(MCarrierVo searchCondition) {
        MCarrierVo resultVo = mapper.getDetail(searchCondition);
        if(resultVo != null) {
            // set附件信息返回
            return getFile(resultVo);
        }
        return null;
    }

    /**
     * 新增承运商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MCarrierVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(), CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MCustomerEntity entity = (MCustomerEntity) BeanUtilsSupport.copyProperties(vo, MCustomerEntity.class);

        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称简拼
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin(str.toString());

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        MCustomerInfoEntity infoEntity = new MCustomerInfoEntity();
        infoEntity.setCustomer_id(entity.getId());
        // 新增承运商明细
        customerInfoMapper.insert(infoEntity);
        // 附件新增逻辑
        insertFile(vo.getInfoVo(),infoEntity);
        customerInfoMapper.updateById(infoEntity);
        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MCarrierVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo.getName(), vo.getCode(), CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        MCustomerEntity entity = (MCustomerEntity) BeanUtilsSupport.copyProperties(vo, MCustomerEntity.class);

        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称简拼
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin(str.toString());

        MCustomerInfoEntity infoEntity = customerInfoMapper.getByCustomerId(entity.getId());
        // 先删除附件再新增
        deleteFile(vo);
        insertFile(vo.getInfoVo(),infoEntity);

        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    /**
     * id查询
     */
    @Override
    public MCarrierVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * name查询
     */
    @Override
    public List<MCustomerEntity> selectByName(String name) {
        // 查询 数据
        return mapper.selectByName(name);
    }

    /**
     * 删除承运商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MCarrierVo searchCondition) {
        int result = mapper.deleteById(searchCondition.getId());
        if(result != 2){
            throw new UpdateErrorException("您删除的数据不存在，请查询后重新编辑更新。");
        }
    }

    /**
     * code查询
     */
    @Override
    public List<MCustomerEntity> selectByCode(String code) {
        // 查询 数据
        return mapper.selectByCode(code);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(String name, String code, String moduleType) {
        List<MCustomerEntity> selectByName = selectByName(name);
        List<MCustomerEntity> selectByKey = selectByCode(code);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                if (selectByKey.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", name);
                }
                if (selectByKey.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：编码出现重复", code);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 附件新增逻辑
     */
    public void insertFile(MCarrierInfoVo vo, MCustomerInfoEntity entity) {
        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_CUSTOMER);
        // 法人身份正面附件新增
        if(vo.getId_front_file() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getId_front_file().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getId_front_file(),fileInfoEntity);
            fileInfoEntity.setFile_name(vo.getId_front_file().getFileName());
            fileInfoMapper.insert(fileInfoEntity);
            // 法人身份正面附件id
            entity.setId_front_file_id(fileEntity.getId());
            fileEntity.setId(null);
        }
        // 法人身份反面附件新增
        if(vo.getId_back_file() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getId_back_file().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getId_back_file(),fileInfoEntity);
            fileInfoEntity.setFile_name(vo.getId_back_file().getFileName());
            fileInfoMapper.insert(fileInfoEntity);
            // 法人身份反面附件id
            entity.setId_back_file_id(fileEntity.getId());
            fileEntity.setId(null);
        }
        // 企业认证附件新增
        if(vo.getConfirm_file() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getConfirm_file().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getConfirm_file(),fileInfoEntity);
            fileInfoEntity.setFile_name(vo.getConfirm_file().getFileName());
            fileInfoMapper.insert(fileInfoEntity);
            // 企业认证附件id
            entity.setConfirm_file_id(fileEntity.getId());
            fileEntity.setId(null);
        }
        // 经营许可认证附件新增
        if(vo.getLicence_confirm_file() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getLicence_confirm_file().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getLicence_confirm_file(),fileInfoEntity);
            fileInfoEntity.setFile_name(vo.getLicence_confirm_file().getFileName());
            fileInfoMapper.insert(fileInfoEntity);
            // 经营许可认证附件id
            entity.setLicence_confirm_file_id(fileEntity.getId());
            fileEntity.setId(null);
        }
    }

    /**
     * 删除附件
     */
    public void deleteFile(MCarrierVo vo) {
        MCustomerInfoEntity entity = customerInfoMapper.getByCustomerId(vo.getId());
        if(entity.getId_front_file_id() != null) {
            // 删除附件主从表、法人认证字段置空
            fileMapper.deleteById(entity.getId_front_file_id());
            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getId_front_file_id()));
            entity.setId_front_file_id(null);
        }
        if(entity.getId_back_file_id() != null) {
            // 删除附件主从表、法人认证字段置空
            fileMapper.deleteById(entity.getId_back_file_id());
            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getId_back_file_id()));
            entity.setId_back_file_id(null);
        }
        if(entity.getConfirm_file_id() != null) {
            // 删除附件主从表、企业认证字段置空
            fileMapper.deleteById(entity.getConfirm_file_id());
            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getConfirm_file_id()));
            entity.setConfirm_file_id(null);
        }
        if(entity.getLicence_confirm_file_id() != null) {
            // 删除附件主从表、经营许可认证字段置空
            fileMapper.deleteById(entity.getLicence_confirm_file_id());
            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getLicence_confirm_file_id()));
            entity.setLicence_confirm_file_id(null);
        }
    }

    /**
     * 查询附件
     */
    public MCarrierVo getFile(MCarrierVo vo){
        // 查询附件逻辑：先查询承运商明细数据对象,4个附件id字段不为空的话去查询附件表数据
        MCustomerInfoEntity infoEntity = customerInfoMapper.getByCustomerId(vo.getId());
        MCarrierInfoVo infoVo = (MCarrierInfoVo) BeanUtilsSupport.copyProperties(infoEntity, MCarrierInfoVo.class);
        if(infoVo.getId_front_file_id() != null) {
            SFileEntity file = fileMapper.selectById(infoVo.getId_front_file_id());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo)BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                infoVo.setId_front_file(fileInfoVo);
            }
        }
        if(infoVo.getId_back_file_id() != null) {
            SFileEntity file = fileMapper.selectById(infoVo.getId_back_file_id());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo)BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                infoVo.setId_back_file(fileInfoVo);
            }
        }
        if(infoVo.getConfirm_file_id() != null) {
            SFileEntity file = fileMapper.selectById(infoVo.getConfirm_file_id());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo)BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                infoVo.setConfirm_file(fileInfoVo);
            }
        }
        if(infoVo.getLicence_confirm_file_id() != null) {
            SFileEntity file = fileMapper.selectById(infoVo.getLicence_confirm_file_id());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo)BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                infoVo.setLicence_confirm_file(fileInfoVo);
            }
        }
        vo.setInfoVo(infoVo);
        return vo;
    }

}
