package com.xinyirun.scm.core.system.mapper.master.bankaccounts;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.bank.MBankAccountsEntity;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsExportVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 企业银行账户表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-24
 */
@Repository
public interface MBankAccountsMapper extends BaseMapper<MBankAccountsEntity> {



    /**
     * 分页查询
     */
@Select("""
            	<script>
            	SELECT
            		tab1.*,
            		IF(tab1.link_status,'成功','未开通') as link_status_name,
            		tab2.name as enterprise_name,
            		tab3.name as c_name,
            		tab4.name as u_name,
            		GROUP_CONCAT(tab5.name) as bank_type_name
            	FROM
            		m_bank_accounts tab1
            		LEFT JOIN m_enterprise tab2 ON tab1.enterprise_id = tab2.id
            		LEFT JOIN m_staff tab3 ON tab1.c_id = tab3.id
            		LEFT JOIN m_staff tab4 ON tab1.u_id = tab4.id
            		LEFT JOIN m_bank_accounts_type tab5 ON tab5.bank_id = tab1.id
            		-- 状态：0-禁用、1-可用、-1-删除 (排除已删除数据)
            		WHERE true AND tab1.status != '-1'
            		-- 账户名称模糊查询
            		AND (tab1.name like concat('%', #{p1.name}, '%') or #{p1.name} is null or #{p1.name} = '')
            		-- 开户行模糊查询
            		AND (tab1.bank_name like concat('%', #{p1.bank_name}, '%') or #{p1.bank_name} is null or #{p1.bank_name} = '')
            		<if test="p1.bank_type != null and p1.bank_type.length > 0">
            		AND EXISTS (
            		    SELECT 1 FROM m_bank_accounts_type tab6 
            		    WHERE tab6.bank_id = tab1.id 
            		    AND tab6.code IN 
            		    <foreach collection="p1.bank_type" item="item" open="(" separator="," close=")">
            		        #{item}
            		    </foreach>
            		    -- 状态：0-禁用、1-可用、-1-删除 (银行账户类型状态为可用)
            		    AND tab6.status = '1'
            		)
            		</if>
            		GROUP BY tab1.id
            	</script>
            """)
    IPage<MBankAccountsVo> selectPage(Page page, @Param("p1") MBankAccountsVo searchCondition);

    /**
     * id查询
     */
@Select("""
            	SELECT
            		tab1.*,
            		IF(tab1.link_status,'成功','未开通') as link_status_name,
            		tab2.name as enterprise_name,
            		tab3.name as c_name,
            		tab4.name as u_name,
            		GROUP_CONCAT(tab5.name) as bank_type_name
            	FROM
            		m_bank_accounts tab1
            		LEFT JOIN m_enterprise tab2 ON tab1.enterprise_id = tab2.id
            		LEFT JOIN m_staff tab3 ON tab1.c_id = tab3.id
            		LEFT JOIN m_staff tab4 ON tab1.u_id = tab4.id
            		LEFT JOIN m_bank_accounts_type tab5 ON tab5.bank_id = tab1.id
            		WHERE true AND tab1.id = #{p1}
            		GROUP BY tab1.id
            """)
    MBankAccountsVo selById(@Param("p1") Integer id);

    /**
     * 校验
     */
@Select("""
            -- 编号唯一性校验
            select * from m_bank_accounts where true and code = #{p1.code} and status!= '-1'
            AND (id != #{p1.id} or #{p1.id} is null or #{p1.id} = '')
            """)
    List<MBankAccountsVo> validateDuplicateCode(@Param("p1")MBankAccountsVo mBankAccountsVo);

    /**
     * 查询默认企业默认银行账户
     */
@Select("""
            -- 查询企业默认账户：主体企业id匹配 + 状态非删除 + 默认账户
            select * from m_bank_accounts where true and enterprise_id = #{p1} and status!=-1 and is_default = 1
            """)
    MBankAccountsVo selByEnterpriseIdAndStatus(@Param("p1")Integer enterpriseId);

    /**
     * 导出
     */
@Select("""
            	<script>
            	SELECT
            		@row_num:= @row_num+ 1 as no,
            		tab1.*,
            		IF(tab1.link_status,'成功','未开通') as link_status_name,
            		IF(tab1.status,'启用','禁用') as status_name,
            		IF(tab1.is_default,'是','否') as is_default_name,
            		tab2.name as enterprise_name,
            		tab3.name as c_name,
            		tab4.name as u_name,
            		GROUP_CONCAT(tab6.name) as bank_type_name
            	FROM
            		m_bank_accounts tab1
            		LEFT JOIN m_enterprise tab2 ON tab1.enterprise_id = tab2.id
            		LEFT JOIN m_staff tab3 ON tab1.c_id = tab3.id
            		LEFT JOIN m_staff tab4 ON tab1.u_id = tab4.id
            		LEFT JOIN m_bank_accounts_type tab6 ON tab6.bank_id = tab1.id
            	-- 状态：0-禁用、1-可用、-1-删除 (排除已删除数据)
            	WHERE true AND tab1.status != '-1'
            		-- 账户名称模糊查询
            		AND (tab1.name like concat('%', #{p1.name}, '%') or #{p1.name} is null or #{p1.name} = '')
            		-- 开户行模糊查询
            		AND (tab1.bank_name like concat('%', #{p1.bank_name}, '%') or #{p1.bank_name} is null or #{p1.bank_name} = '')
               <if test='p1.ids != null and p1.ids.length != 0' >
                and tab1.id in
                    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
               <if test="p1.bank_type != null and p1.bank_type.length > 0">
               AND EXISTS (
                   SELECT 1 FROM m_bank_accounts_type tab7 
                   WHERE tab7.bank_id = tab1.id 
                   AND tab7.code IN 
                   <foreach collection="p1.bank_type" item="item" open="(" separator="," close=")">
                       #{item}
                   </foreach>
                   -- 状态：0-禁用、1-可用、-1-删除 (银行账户类型状态为可用)
                   AND tab7.status = '1'
               )
               </if>
            	GROUP BY tab1.id
            	 </script>
            """)
    List<MBankAccountsExportVo> selectExportList(@Param("p1")MBankAccountsVo searchCondition);

    /**
     * 导出
     */
@Select("""
            	<script>
            	SELECT
            		count(tab1.id)
            	FROM
            		m_bank_accounts tab1
            		LEFT JOIN m_enterprise tab2 ON tab1.enterprise_id = tab2.id
            		LEFT JOIN m_staff tab3 ON tab1.c_id = tab3.id
            		LEFT JOIN m_staff tab4 ON tab1.u_id = tab4.id,(select @row_num:=0) tab5
            	-- 状态：0-禁用、1-可用、-1-删除 (排除已删除数据)
            	WHERE true AND tab1.status != '-1'
            		-- 账户名称模糊查询
            		AND (tab1.name like concat('%', #{p1.name}, '%') or #{p1.name} is null or #{p1.name} = '')
            		-- 开户行模糊查询
            		AND (tab1.bank_name like concat('%', #{p1.bank_name}, '%') or #{p1.bank_name} is null or #{p1.bank_name} = '')
               <if test='p1.ids != null and p1.ids.length != 0' >
                and tab1.id in
                    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
               <if test="p1.bank_type != null and p1.bank_type.length > 0">
               AND EXISTS (
                   SELECT 1 FROM m_bank_accounts_type tab6 
                   WHERE tab6.bank_id = tab1.id 
                   AND tab6.code IN 
                   <foreach collection="p1.bank_type" item="item" open="(" separator="," close=")">
                       #{item}
                   </foreach>
                   -- 状态：0-禁用、1-可用、-1-删除 (银行账户类型状态为可用)
                   AND tab6.status = '1'
               )
               </if>
            	 </script>
            """)
    Long selectExportCount(MBankAccountsVo searchCondition);

@Select("""
            	<script>
            	SELECT
            		tab1.*,
            		tab2.name as enterprise_name,
            		GROUP_CONCAT(tab5.name) as bank_type_name
            	FROM
            		m_bank_accounts tab1
            		LEFT JOIN m_enterprise tab2 ON tab1.enterprise_id = tab2.id
            		LEFT JOIN m_bank_accounts_type tab5 ON tab5.bank_id = tab1.id
            		WHERE TRUE
            		      -- 主体企业id匹配
            		      AND(tab1.enterprise_id = #{p1.enterprise_id} or #{p1.enterprise_id} is null or #{p1.enterprise_id} ='' )
            		      -- 主体企业code匹配
            		      AND(tab1.enterprise_code = #{p1.enterprise_code} or #{p1.enterprise_code} is null or #{p1.enterprise_code} ='' )
            		      -- 是否默认(0-否 1-是) 查询默认账户
            		      AND tab1.is_default = '1'
            		      -- 状态：0-禁用、1-可用、-1-删除 (查询可用状态)
            		      AND tab1.status = '1'
            		      <if test="p1.bank_type != null and p1.bank_type.length > 0">
            		      AND EXISTS (
            		          SELECT 1 FROM m_bank_accounts_type tab3 
            		          WHERE tab3.bank_id = tab1.id 
            		          AND tab3.code IN 
            		          <foreach collection="p1.bank_type" item="item" open="(" separator="," close=")">
            		              #{item}
            		          </foreach>
            		          -- 状态：0-禁用、1-可用、-1-删除 (银行账户类型状态为可用)
            		          AND tab3.status = '1'
            		      )
            		      </if>
            	GROUP BY tab1.id
            	</script>
            """)
    MBankAccountsVo getPurchaser(@Param("p1") MBankAccountsVo searchCondition);

    /**
     * 企业银行账户，弹窗获取分页列表
     */
@Select("""
            	<script>
            	SELECT
            		tab1.*,
            		IF(tab1.link_status,'成功','未开通') as link_status_name,
            		tab2.name as enterprise_name,
            		tab3.name as c_name,
            		tab4.name as u_name,
            		GROUP_CONCAT(tab5.name) as bank_type_name
            	FROM
            		m_bank_accounts tab1
            		LEFT JOIN m_enterprise tab2 ON tab1.enterprise_id = tab2.id
            		LEFT JOIN m_staff tab3 ON tab1.c_id = tab3.id
            		LEFT JOIN m_staff tab4 ON tab1.u_id = tab4.id
            		LEFT JOIN m_bank_accounts_type tab5 ON tab5.bank_id = tab1.id
            		WHERE true
            		-- 状态：0-禁用、1-可用、-1-删除 (查询可用状态)
            		AND tab1.status = '1'
            		-- 主体企业code精确匹配
            		AND (tab2.code = #{p1.enterprise_code} or #{p1.enterprise_code} is null or #{p1.enterprise_code}='' )
            		-- 企业名称模糊查询
            		AND (tab2.name like concat('%', #{p1.enterprise_name}, '%') or #{p1.enterprise_name} is null or #{p1.enterprise_name} = '')
            		-- 账户名称模糊查询
            		AND (tab1.name like concat('%', #{p1.name}, '%') or #{p1.name} is null or #{p1.name} = '')
            		-- 开户行模糊查询
            		AND (tab1.bank_name like concat('%', #{p1.bank_name}, '%') or #{p1.bank_name} is null or #{p1.bank_name} = '')
            		<if test="p1.bank_type != null and p1.bank_type.length > 0">
            		AND EXISTS (
            		    SELECT 1 FROM m_bank_accounts_type tab6 
            		    WHERE tab6.bank_id = tab1.id 
            		    AND tab6.code IN 
            		    <foreach collection="p1.bank_type" item="item" open="(" separator="," close=")">
            		        #{item}
            		    </foreach>
            		    -- 状态：0-禁用、1-可用、-1-删除 (银行账户类型状态为可用)
            		    AND tab6.status = '1'
            		)
            		</if>
            		GROUP BY tab1.id
            	</script>
            """)
    IPage<MBankAccountsVo> dialogpageList(Page<MBankAccountsVo> pageCondition,@Param("p1") MBankAccountsVo searchCondition);

    /**
     * 获取销售方企业的默认银行账户
     */
@Select("""
            	<script>
            	SELECT
            		tab1.*,
            		tab2.name as enterprise_name,
            		GROUP_CONCAT(tab5.name) as bank_type_name
            	FROM
            		m_bank_accounts tab1
            		LEFT JOIN m_enterprise tab2 ON tab1.enterprise_id = tab2.id
            		LEFT JOIN m_bank_accounts_type tab5 ON tab5.bank_id = tab1.id
            		WHERE TRUE
            		      -- 主体企业id匹配
            		      AND(tab1.enterprise_id = #{p1.enterprise_id} or #{p1.enterprise_id} is null or #{p1.enterprise_id} ='' )
            		      -- 主体企业code匹配
            		      AND(tab1.enterprise_code = #{p1.enterprise_code} or #{p1.enterprise_code} is null or #{p1.enterprise_code} ='' )
            		      -- 是否默认(0-否 1-是) 查询默认账户
            		      AND tab1.is_default = '1'
            		      -- 状态：0-禁用、1-可用、-1-删除 (查询可用状态)
            		      AND tab1.status = '1'
            		      <if test="p1.bank_type != null and p1.bank_type.length > 0">
            		      AND EXISTS (
            		          SELECT 1 FROM m_bank_accounts_type tab3 
            		          WHERE tab3.bank_id = tab1.id 
            		          AND tab3.code IN 
            		          <foreach collection="p1.bank_type" item="item" open="(" separator="," close=")">
            		              #{item}
            		          </foreach>
            		          -- 状态：0-禁用、1-可用、-1-删除 (银行账户类型状态为可用)
            		          AND tab3.status = '1'
            		      )
            		      </if>
            	GROUP BY tab1.id
            	</script>
            """)
    MBankAccountsVo getSeller(@Param("p1") MBankAccountsVo searchCondition);

    /**
     * 获取银行收款账户下拉
     */
@Select("""
            	SELECT
            		tab1.id AS bank_id,
            		CONCAT_WS( ' | ', tab1.holder_name, tab1.bank_name, tab1.account_number ) AS bank_value
            	FROM
            		m_bank_accounts tab1
            		WHERE TRUE
            		-- 状态：0-禁用、1-可用、-1-删除 (查询可用状态)
            		AND tab1.status = '1'
            		-- 主体企业code精确匹配
            		AND tab1.enterprise_code = #{p1.enterprise_code}
            """)
    List<MBankAccountsVo> getBankCollection(@Param("p1")MBankAccountsVo searchCondition);
}
