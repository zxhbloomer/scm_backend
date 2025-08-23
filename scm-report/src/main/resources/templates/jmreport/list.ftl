<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>积木报表工作台</title>
    <link rel="stylesheet" href="/jmreport/css/bootstrap.min.css">
    <link rel="stylesheet" href="/jmreport/css/font-awesome.min.css">
    <style>
        body {
            background-color: #f5f5f5;
            font-family: "Microsoft YaHei", Arial, sans-serif;
        }
        
        .navbar-brand {
            font-size: 20px;
            font-weight: bold;
        }
        
        .main-container {
            padding: 20px;
            max-width: 1400px;
            margin: 0 auto;
        }
        
        .toolbar {
            background: #fff;
            padding: 15px 20px;
            border-radius: 6px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .search-box {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        
        .search-input {
            padding: 8px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            width: 250px;
        }
        
        .btn {
            padding: 8px 16px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            background: #fff;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn:hover {
            background: #f0f0f0;
            text-decoration: none;
        }
        
        .btn-primary {
            background: #1890ff;
            border-color: #1890ff;
            color: #fff;
        }
        
        .btn-primary:hover {
            background: #40a9ff;
            border-color: #40a9ff;
            color: #fff;
        }
        
        .btn-success {
            background: #52c41a;
            border-color: #52c41a;
            color: #fff;
        }
        
        .btn-success:hover {
            background: #73d13d;
            border-color: #73d13d;
            color: #fff;
        }
        
        .report-grid {
            background: #fff;
            border-radius: 6px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .grid-header {
            background: #fafafa;
            border-bottom: 1px solid #e8e8e8;
            padding: 15px 20px;
            font-weight: bold;
            font-size: 16px;
            color: #333;
        }
        
        .report-list {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            padding: 20px;
        }
        
        .report-card {
            border: 1px solid #e8e8e8;
            border-radius: 6px;
            padding: 20px;
            background: #fff;
            transition: all 0.3s;
            cursor: pointer;
            position: relative;
        }
        
        .report-card:hover {
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            transform: translateY(-2px);
        }
        
        .report-title {
            font-size: 16px;
            font-weight: bold;
            color: #333;
            margin-bottom: 8px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .report-code {
            color: #666;
            font-size: 12px;
            margin-bottom: 8px;
        }
        
        .report-desc {
            color: #888;
            font-size: 14px;
            line-height: 1.4;
            margin-bottom: 15px;
            height: 42px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
        }
        
        .report-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            font-size: 12px;
            color: #999;
        }
        
        .report-status {
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: bold;
        }
        
        .status-published {
            background: #f6ffed;
            color: #52c41a;
        }
        
        .status-draft {
            background: #fff7e6;
            color: #fa8c16;
        }
        
        .report-actions {
            display: flex;
            gap: 8px;
        }
        
        .action-btn {
            padding: 4px 8px;
            font-size: 12px;
            border-radius: 3px;
            text-decoration: none;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-icon {
            font-size: 48px;
            margin-bottom: 16px;
            color: #d9d9d9;
        }
        
        .empty-text {
            font-size: 16px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" href="#"><i class="fa fa-cube"></i> 积木报表工作台</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="/scm/report/datasource"><i class="fa fa-database"></i> 数据源管理</a>
                <a class="nav-link" href="#"><i class="fa fa-cog"></i> 系统设置</a>
            </div>
        </div>
    </nav>

    <!-- 主容器 -->
    <div class="main-container">
        <!-- 工具栏 -->
        <div class="toolbar">
            <div class="search-box">
                <input type="text" class="search-input" id="searchInput" placeholder="搜索报表名称或编码...">
                <button class="btn btn-primary" onclick="searchReports()">
                    <i class="fa fa-search"></i> 搜索
                </button>
            </div>
            <div class="action-buttons">
                <a href="/jmreport/designer/new" class="btn btn-success">
                    <i class="fa fa-plus"></i> 新建报表
                </a>
                <button class="btn" onclick="refreshList()">
                    <i class="fa fa-refresh"></i> 刷新
                </button>
            </div>
        </div>

        <!-- 报表网格 -->
        <div class="report-grid">
            <div class="grid-header">
                <i class="fa fa-list"></i> 报表列表 (${(reportList?size)!0} 个报表)
            </div>
            
            <#if reportList?? && (reportList?size > 0)>
                <div class="report-list">
                    <#list reportList as report>
                        <div class="report-card" onclick="viewReport('${report.id}')">
                            <div class="report-title" title="${report.name!"无标题"}">
                                ${report.name!"无标题"}
                            </div>
                            <div class="report-code">编码: ${report.code!"--"}</div>
                            <div class="report-desc" title="${report.note!"暂无描述"}">
                                ${report.note!"暂无描述"}
                            </div>
                            <div class="report-meta">
                                <span>浏览: ${(report.viewCount)!0} 次</span>
                                <span class="report-status <#if report.status == '1'>status-published<#else>status-draft</#if>">
                                    <#if report.status == '1'>已发布<#else>设计中</#if>
                                </span>
                            </div>
                            <div class="report-actions" onclick="event.stopPropagation()">
                                <#if report.status == '1'>
                                    <a href="/jmreport/view/${report.id}" class="action-btn btn-primary" target="_blank">
                                        <i class="fa fa-eye"></i> 预览
                                    </a>
                                </#if>
                                <a href="/jmreport/index/${report.id}" class="action-btn btn" target="_blank">
                                    <i class="fa fa-edit"></i> 编辑
                                </a>
                                <a href="/jmreport/config/${report.id}" class="action-btn btn">
                                    <i class="fa fa-cog"></i> 配置
                                </a>
                                <button class="action-btn btn" onclick="deleteReport('${report.id}', '${report.name!""}')">
                                    <i class="fa fa-trash"></i> 删除
                                </button>
                            </div>
                        </div>
                    </#list>
                </div>
            <#else>
                <div class="empty-state">
                    <div class="empty-icon">
                        <i class="fa fa-file-text-o"></i>
                    </div>
                    <div class="empty-text">暂无报表数据</div>
                    <a href="/jmreport/designer/new" class="btn btn-primary">
                        <i class="fa fa-plus"></i> 创建第一个报表
                    </a>
                </div>
            </#if>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="/jmreport/js/jquery.min.js"></script>
    <script src="/jmreport/js/bootstrap.bundle.min.js"></script>
    <script>
        // 查看报表
        function viewReport(reportId) {
            window.open('/jmreport/view/' + reportId, '_blank');
        }

        // 搜索报表
        function searchReports() {
            var keyword = $('#searchInput').val();
            if (keyword.trim()) {
                // 重新加载页面并带上搜索参数
                window.location.href = '/jmreport/list?search=' + encodeURIComponent(keyword);
            } else {
                window.location.href = '/jmreport/list';
            }
        }

        // 刷新列表
        function refreshList() {
            window.location.reload();
        }

        // 删除报表
        function deleteReport(reportId, reportName) {
            if (confirm('确定要删除报表 "' + reportName + '" 吗？\n删除后不可恢复！')) {
                $.ajax({
                    url: '/scm/report/delete/' + reportId,
                    type: 'DELETE',
                    success: function(result) {
                        if (result && result.success) {
                            alert('删除成功');
                            window.location.reload();
                        } else {
                            alert('删除失败: ' + (result.message || '未知错误'));
                        }
                    },
                    error: function(xhr, status, error) {
                        alert('删除失败: ' + error);
                    }
                });
            }
        }

        // 页面加载完成后
        $(document).ready(function() {
            // 搜索框回车事件
            $('#searchInput').keypress(function(e) {
                if (e.which == 13) {
                    searchReports();
                }
            });
            
            // 获取URL中的搜索参数
            var urlParams = new URLSearchParams(window.location.search);
            var searchKeyword = urlParams.get('search');
            if (searchKeyword) {
                $('#searchInput').val(searchKeyword);
            }
        });
    </script>
</body>
</html>