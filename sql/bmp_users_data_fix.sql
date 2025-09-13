-- =====================================================
-- BPM用户数据修复SQL脚本  
-- 用于修复m_staff和bpm_users表之间的数据不一致问题
-- 
-- 警告：执行前请先运行检查脚本确认问题，并做好数据备份！
-- 数据库: scm_tenant_20250519_001
-- 创建时间: 2025-01-12
-- 作者: xinyirun
-- =====================================================

-- 使用说明：
-- 1. 先运行bpm_users_data_consistency_check.sql检查问题
-- 2. 根据检查结果选择性执行下面的修复SQL
-- 3. 每个修复操作前建议先备份相关数据

-- ==========================================
-- 1. 为缺失的员工创建BPM用户记录
-- ==========================================

-- 预览将要创建的BPM用户记录
SELECT 
    s.id AS staff_id,
    s.code AS user_code,
    s.name AS user_name,
    s.name_py AS pingyin,
    s.name AS alisa,
    CASE 
        WHEN s.sex IN ('1', '男') THEN 1
        WHEN s.sex IN ('0', '女') THEN 0
        ELSE NULL
    END AS sex,
    s.entry_date,
    s.leave_date,
    CASE 
        WHEN IFNULL(u.is_biz_admin, 0) = 1 THEN 2  -- 业务管理员
        WHEN IFNULL(s.is_admin, 0) = 1 THEN 1      -- 系统管理员  
        ELSE 0                                      -- 普通员工
    END AS admin,
    s.c_time,
    s.u_time,
    CAST('scm_tenant_20250519_001' AS BINARY) AS tenant_code,
    IFNULL(s.user_id, NULL) AS user_id,
    IFNULL(u.avatar, NULL) AS avatar,
    0 AS is_del
FROM m_staff s
LEFT JOIN m_user u ON s.user_id = u.id
LEFT JOIN bpm_users b ON s.id = b.staff_id AND b.is_del = false
WHERE s.is_del = false 
  AND b.staff_id IS NULL;

-- 执行创建BPM用户记录（请确认预览结果后再执行）
/*
INSERT INTO bpm_users (staff_id, user_code, user_name, pingyin, alisa, sex, entry_date, leave_date, admin, c_time, u_time, tenant_code, user_id, avatar, is_del)
SELECT 
    s.id,
    s.code,
    s.name,
    s.name_py,
    s.name,
    CASE 
        WHEN s.sex IN ('1', '男') THEN 1
        WHEN s.sex IN ('0', '女') THEN 0
        ELSE NULL
    END,
    s.entry_date,
    s.leave_date,
    CASE 
        WHEN IFNULL(u.is_biz_admin, 0) = 1 THEN 2
        WHEN IFNULL(s.is_admin, 0) = 1 THEN 1
        ELSE 0
    END,
    s.c_time,
    s.u_time,
    CAST('scm_tenant_20250519_001' AS BINARY),
    s.user_id,
    u.avatar,
    0
FROM m_staff s
LEFT JOIN m_user u ON s.user_id = u.id
LEFT JOIN bpm_users b ON s.id = b.staff_id AND b.is_del = false
WHERE s.is_del = false 
  AND b.staff_id IS NULL;
*/

-- ==========================================
-- 2. 清理孤立的BPM用户记录（逻辑删除）
-- ==========================================

-- 预览将要删除的孤立BPM用户记录
SELECT 
    b.id AS bpm_user_id,
    b.staff_id,
    b.user_code,
    b.user_name,
    CASE 
        WHEN s.id IS NULL THEN '员工记录不存在'
        WHEN s.is_del = 1 THEN '员工已删除'
        ELSE '其他原因'
    END AS deletion_reason
FROM bpm_users b
LEFT JOIN m_staff s ON b.staff_id = s.id
WHERE b.is_del = false 
  AND (s.id IS NULL OR s.is_del = true);

-- 执行逻辑删除孤立的BPM用户记录（请确认预览结果后再执行）
/*
UPDATE bpm_users b
LEFT JOIN m_staff s ON b.staff_id = s.id
SET b.is_del = true,
    b.u_time = NOW()
WHERE b.is_del = false 
  AND (s.id IS NULL OR s.is_del = true);
*/

-- ==========================================
-- 3. 修复字段值不一致的记录
-- ==========================================

-- 预览将要修复的字段不一致记录
SELECT 
    s.id AS staff_id,
    '修复前' AS status,
    b.user_code AS old_user_code, s.code AS new_user_code,
    b.user_name AS old_user_name, s.name AS new_user_name,
    b.pingyin AS old_pingyin, s.name_py AS new_pingyin,
    b.entry_date AS old_entry_date, s.entry_date AS new_entry_date,
    b.leave_date AS old_leave_date, s.leave_date AS new_leave_date
FROM m_staff s
INNER JOIN bpm_users b ON s.id = b.staff_id
WHERE s.is_del = false AND b.is_del = false
  AND (s.code != b.user_code 
       OR s.name != b.user_name 
       OR IFNULL(s.name_py, '') != IFNULL(b.pingyin, '')
       OR IFNULL(s.entry_date, '1900-01-01') != IFNULL(b.entry_date, '1900-01-01')
       OR IFNULL(s.leave_date, '1900-01-01') != IFNULL(b.leave_date, '1900-01-01'));

-- 执行字段值同步（请确认预览结果后再执行）
/*
UPDATE bpm_users b
INNER JOIN m_staff s ON s.id = b.staff_id
SET b.user_code = s.code,
    b.user_name = s.name,
    b.pingyin = s.name_py,
    b.alisa = s.name,
    b.entry_date = s.entry_date,
    b.leave_date = s.leave_date,
    b.u_time = s.u_time
WHERE s.is_del = false AND b.is_del = false
  AND (s.code != b.user_code 
       OR s.name != b.user_name 
       OR IFNULL(s.name_py, '') != IFNULL(b.pingyin, '')
       OR IFNULL(s.entry_date, '1900-01-01') != IFNULL(b.entry_date, '1900-01-01')
       OR IFNULL(s.leave_date, '1900-01-01') != IFNULL(b.leave_date, '1900-01-01'));
*/

-- ==========================================
-- 4. 修复性别字段转换错误
-- ==========================================

-- 预览性别字段修复
SELECT 
    s.id AS staff_id,
    s.code AS staff_code,
    s.name AS staff_name,
    s.sex AS staff_sex_varchar,
    b.sex AS old_bpm_sex_bit,
    CASE 
        WHEN s.sex IN ('1', '男') THEN 1
        WHEN s.sex IN ('0', '女') THEN 0
        ELSE NULL
    END AS new_bpm_sex_bit
FROM m_staff s
INNER JOIN bpm_users b ON s.id = b.staff_id
WHERE s.is_del = false AND b.is_del = false
  AND NOT (
    (s.sex IN ('1', '男') AND b.sex = 1) OR
    (s.sex IN ('0', '女') AND b.sex = 0) OR
    (s.sex NOT IN ('0', '1', '男', '女') AND b.sex IS NULL)
  );

-- 执行性别字段修复（请确认预览结果后再执行）
/*
UPDATE bpm_users b
INNER JOIN m_staff s ON s.id = b.staff_id
SET b.sex = CASE 
    WHEN s.sex IN ('1', '男') THEN 1
    WHEN s.sex IN ('0', '女') THEN 0
    ELSE NULL
END,
b.u_time = NOW()
WHERE s.is_del = false AND b.is_del = false
  AND NOT (
    (s.sex IN ('1', '男') AND b.sex = 1) OR
    (s.sex IN ('0', '女') AND b.sex = 0) OR
    (s.sex NOT IN ('0', '1', '男', '女') AND b.sex IS NULL)
  );
*/

-- ==========================================
-- 5. 修复管理员标识错误
-- ==========================================

-- 预览管理员标识修复
SELECT 
    s.id AS staff_id,
    s.code AS staff_code,
    s.name AS staff_name,
    s.is_admin AS staff_is_admin,
    IFNULL(u.is_biz_admin, 0) AS user_is_biz_admin,
    b.admin AS old_bmp_admin_flag,
    CASE 
        WHEN IFNULL(u.is_biz_admin, 0) = 1 THEN 2  -- 业务管理员优先
        WHEN IFNULL(s.is_admin, 0) = 1 THEN 1      -- 系统管理员
        ELSE 0                                      -- 普通员工
    END AS new_bpm_admin_flag
FROM m_staff s
INNER JOIN bmp_users b ON s.id = b.staff_id
LEFT JOIN m_user u ON s.user_id = u.id
WHERE s.is_del = false AND b.is_del = false
  AND NOT (
    (IFNULL(u.is_biz_admin, 0) = 1 AND b.admin = 2) OR
    (IFNULL(u.is_biz_admin, 0) != 1 AND IFNULL(s.is_admin, 0) = 1 AND b.admin = 1) OR
    (IFNULL(u.is_biz_admin, 0) != 1 AND IFNULL(s.is_admin, 0) != 1 AND b.admin = 0)
  );

-- 执行管理员标识修复（请确认预览结果后再执行）
/*
UPDATE bpm_users b
INNER JOIN m_staff s ON s.id = b.staff_id
LEFT JOIN m_user u ON s.user_id = u.id
SET b.admin = CASE 
    WHEN IFNULL(u.is_biz_admin, 0) = 1 THEN 2
    WHEN IFNULL(s.is_admin, 0) = 1 THEN 1
    ELSE 0
END,
b.u_time = NOW()
WHERE s.is_del = false AND b.is_del = false
  AND NOT (
    (IFNULL(u.is_biz_admin, 0) = 1 AND b.admin = 2) OR
    (IFNULL(u.is_biz_admin, 0) != 1 AND IFNULL(s.is_admin, 0) = 1 AND b.admin = 1) OR
    (IFNULL(u.is_biz_admin, 0) != 1 AND IFNULL(s.is_admin, 0) != 1 AND b.admin = 0)
  );
*/

-- ==========================================
-- 6. 数据修复后的验证查询
-- ==========================================

-- 验证修复结果汇总
SELECT 
    '修复后验证' AS check_phase,
    '总计' AS category,
    CONCAT(
        '员工总数(未删除): ', (SELECT COUNT(*) FROM m_staff WHERE is_del = false), '; ',
        'BPM用户总数(未删除): ', (SELECT COUNT(*) FROM bpm_users WHERE is_del = false), '; ',
        '已同步记录数: ', (SELECT COUNT(*) FROM m_staff s INNER JOIN bpm_users b ON s.id = b.staff_id WHERE s.is_del = false AND b.is_del = false), '; ',
        '同步率: ', ROUND((SELECT COUNT(*) FROM m_staff s INNER JOIN bpm_users b ON s.id = b.staff_id WHERE s.is_del = false AND b.is_del = false) * 100.0 / (SELECT COUNT(*) FROM m_staff WHERE is_del = false), 2), '%'
    ) AS validation_summary;