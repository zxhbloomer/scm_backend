package com.xinyirun.scm.core.system.service.master.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffPositionVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffExportVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 员工 服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
public interface IMStaffService extends IService<MStaffEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MStaffVo> selectPage(MStaffVo searchCondition) ;

    /**
     * 获取明细，页面查询
     */
    MStaffVo getDetail(MStaffVo searchCondition) ;

    /**
     * 获取所有数据
     */
//    List<MStaffVo> select(MStaffVo searchCondition);

    /**
     * 获取所选id的数据
     */
    List<MStaffVo> selectIdsIn(List<MStaffVo> searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<MStaffVo> exportBySelectIdsIn(List<MStaffVo> searchCondition);

    /**
     * 批量物理删除
     * @param searchCondition
     * @return
     */
    DeleteResultAo<Integer> realDeleteByIdsIn(List<MStaffVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MStaffVo entity, HttpSession session);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(MStaffVo entity, HttpSession session);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> updateSelf(MStaffVo vo);

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    MStaffVo selectByid(Long id);

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<MStaffVo> searchCondition);

    /**
     * 查询岗位员工
     * @param searchCondition
     * @return
     */
    MStaffPositionVo getPositionStaffData(MStaffPositionVo searchCondition);

    /**
     * 查询岗位员工
     * @param searchCondition
     * @return
     */
    void setPositionStaff(MStaffPositionVo searchCondition);

    /**
     * 更新头像
     */
    void saveAvatar(String url);

    /**
     * 根据 用户code查询ID
     * @param code 编码
     * @return Integer
     */
    Integer selectIdByStaffCode(String code);

    /**
     * 查询所有数据导出
     * @param searchCondition
     * @return
     */
    List<MStaffExportVo> selectExportAllList(MStaffVo searchCondition);

    /**
     * 查询部分数据数据导出
     * @param searchConditionList
     * @return
     */
    List<MStaffExportVo> selectExportList(List<MStaffVo> searchConditionList);

    void initAvatar();

}
