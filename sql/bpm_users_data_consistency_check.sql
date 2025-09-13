-- =====================================================
-- BPM用户数据一致性检查SQL脚本
-- 用于验证m_staff和bpm_users表之间的数据同步状态
-- 
-- 数据库: scm_tenant_20250519_001
-- 创建时间: 2025-01-12
-- 作者: xinyirun
-- =====================================================

-- 1. 检查m_staff中存在但bpm_users中缺失的记录
SELECT 
    '缺失BPM用户记录' AS check_type,
    s.id AS staff_id,
    s.code AS staff_code,
    s.name AS staff_name,
    s.is_del AS staff_is_del,
    'BPM用户表中缺失对应记录' AS issue_description
FROM m_staff s
LEFT JOIN bpm_users b ON s.id = b.staff_id AND b.is_del = false
WHERE s.is_del = false  -- 只检查未删除的员工
  AND b.staff_id IS NULL;

-- 2. 检查bpm_users中存在但m_staff中不存在或已删除的记录  
SELECT 
    '孤立BPM用户记录' AS check_type,
    b.id AS bpm_user_id,
    b.staff_id,
    b.user_code,
    b.user_name,
    s.is_del AS staff_is_del,
    CASE 
        WHEN s.id IS NULL THEN '员工记录不存在'
        WHEN s.is_del = true THEN '员工已删除但BPM用户未同步删除'
        ELSE '其他问题'
    END AS issue_description
FROM bpm_users b
LEFT JOIN m_staff s ON b.staff_id = s.id
WHERE b.is_del = false  -- 只检查未删除的BPM用户
  AND (s.id IS NULL OR s.is_del = true);

-- 3. 检查字段值不一致的记录
SELECT 
    '字段值不一致' AS check_type,
    s.id AS staff_id,
    s.code AS staff_code,
    s.name AS staff_name,
    CONCAT_WS('; ', 
        CASE WHEN s.code != b.user_code THEN CONCAT('编码不一致: ', s.code, ' vs ', b.user_code) END,
        CASE WHEN s.name != b.user_name THEN CONCAT('姓名不一致: ', s.name, ' vs ', b.user_name) END,
        CASE WHEN s.name_py != b.pingyin THEN CONCAT('拼音不一致: ', s.name_py, ' vs ', b.pingyin) END,
        CASE WHEN s.entry_date != b.entry_date THEN CONCAT('入职日期不一致: ', s.entry_date, ' vs ', b.entry_date) END,
        CASE WHEN s.leave_date != b.leave_date THEN CONCAT('离职日期不一致: ', s.leave_date, ' vs ', b.leave_date) END
    ) AS field_differences
FROM m_staff s
INNER JOIN bpm_users b ON s.id = b.staff_id
WHERE s.is_del = false AND b.is_del = false
  AND (s.code != b.user_code 
       OR s.name != b.user_name 
       OR IFNULL(s.name_py, '') != IFNULL(b.pingyin, '')
       OR IFNULL(s.entry_date, '1900-01-01') != IFNULL(b.entry_date, '1900-01-01')
       OR IFNULL(s.leave_date, '1900-01-01') != IFNULL(b.leave_date, '1900-01-01'));

-- 4. 检查性别字段转换是否正确
SELECT 
    '性别转换错误' AS check_type,
    s.id AS staff_id,
    s.code AS staff_code,
    s.name AS staff_name,
    s.sex AS staff_sex_varchar,
    b.sex AS bpm_sex_bit,
    CASE 
        WHEN s.sex IN ('1', '男') AND b.sex != 1 THEN '男性转换错误'
        WHEN s.sex IN ('0', '女') AND b.sex != 0 THEN '女性转换错误'
        WHEN s.sex NOT IN ('0', '1', '男', '女') AND b.sex IS NOT NULL THEN '无效性别值'
        ELSE '其他性别转换问题'
    END AS conversion_issue
FROM m_staff s
INNER JOIN bpm_users b ON s.id = b.staff_id
WHERE s.is_del = false AND b.is_del = false
  AND NOT (
    (s.sex IN ('1', '男') AND b.sex = 1) OR
    (s.sex IN ('0', '女') AND b.sex = 0) OR
    (s.sex NOT IN ('0', '1', '男', '女') AND b.sex IS NULL)
  );

-- 5. 检查管理员标识转换是否正确
SELECT 
    '管理员标识错误' AS check_type,
    s.id AS staff_id,
    s.code AS staff_code,
    s.name AS staff_name,
    s.is_admin AS staff_is_admin,
    u.is_biz_admin AS user_is_biz_admin,
    b.admin AS bpm_admin_flag,
    CASE 
        WHEN u.is_biz_admin = 1 AND b.admin != 2 THEN '业务管理员标识错误'
        WHEN u.is_biz_admin != 1 AND s.is_admin = 1 AND b.admin != 1 THEN '系统管理员标识错误'
        WHEN u.is_biz_admin != 1 AND s.is_admin != 1 AND b.admin != 0 THEN '普通员工标识错误'
        ELSE '其他管理员标识问题'
    END AS admin_flag_issue
FROM m_staff s
INNER JOIN bpm_users b ON s.id = b.staff_id
LEFT JOIN m_user u ON s.user_id = u.id
WHERE s.is_del = false AND b.is_del = false
  AND NOT (
    (IFNULL(u.is_biz_admin, 0) = 1 AND b.admin = 2) OR
    (IFNULL(u.is_biz_admin, 0) != 1 AND IFNULL(s.is_admin, 0) = 1 AND b.admin = 1) OR
    (IFNULL(u.is_biz_admin, 0) != 1 AND IFNULL(s.is_admin, 0) != 1 AND b.admin = 0)
  );

-- 6. 检查时间戳一致性（创建和更新时间应该合理）
SELECT 
    '时间戳异常' AS check_type,
    s.id AS staff_id,
    s.code AS staff_code,
    s.name AS staff_name,
    s.c_time AS staff_create_time,
    s.u_time AS staff_update_time,
    b.c_time AS bpm_create_time,
    b.u_time AS bpm_update_time,
    CASE 
        WHEN b.c_time < s.c_time THEN 'BPM创建时间早于员工创建时间'
        WHEN b.u_time < s.u_time THEN 'BPM更新时间早于员工更新时间'
        WHEN TIMESTAMPDIFF(HOUR, s.u_time, b.u_time) > 24 THEN 'BPM更新时间与员工更新时间相差超过24小时'
        ELSE '其他时间戳问题'
    END AS timestamp_issue
FROM m_staff s
INNER JOIN bpm_users b ON s.id = b.staff_id
WHERE s.is_del = false AND b.is_del = false
  AND (b.c_time < s.c_time 
       OR b.u_time < s.u_time 
       OR TIMESTAMPDIFF(HOUR, s.u_time, b.u_time) > 24);

-- 7. 统计汇总信息
SELECT 
    '数据统计汇总' AS check_type,
    '总计' AS category,
    CONCAT(
        '员工总数(未删除): ', (SELECT COUNT(*) FROM m_staff WHERE is_del = false), '; ',
        'BPM用户总数(未删除): ', (SELECT COUNT(*) FROM bpm_users WHERE is_del = false), '; ',
        '已同步记录数: ', (SELECT COUNT(*) FROM m_staff s INNER JOIN bpm_users b ON s.id = b.staff_id WHERE s.is_del = false AND b.is_del = false), '; ',
        '缺失BPM记录数: ', (SELECT COUNT(*) FROM m_staff s LEFT JOIN bpm_users b ON s.id = b.staff_id AND b.is_del = false WHERE s.is_del = false AND b.staff_id IS NULL), '; ',
        '孤立BPM记录数: ', (SELECT COUNT(*) FROM bpm_users b LEFT JOIN m_staff s ON b.staff_id = s.id WHERE b.is_del = false AND (s.id IS NULL OR s.is_del = true))
    ) AS summary_info,
    '' AS details,
    '' AS issue_description;