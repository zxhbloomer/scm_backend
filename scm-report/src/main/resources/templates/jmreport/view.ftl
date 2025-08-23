<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${reportName!"报表预览"}</title>
    <link rel="stylesheet" href="/jmreport/css/luckysheet.css">
    <link rel="stylesheet" href="/jmreport/css/view.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background-color: #f5f5f5;
        }
        
        .report-header {
            background: #fff;
            border-bottom: 1px solid #e8e8e8;
            padding: 15px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .report-title {
            font-size: 18px;
            font-weight: bold;
            color: #333;
        }
        
        .report-actions {
            display: flex;
            gap: 10px;
        }
        
        .btn {
            padding: 6px 16px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            background: #fff;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s;
        }
        
        .btn:hover {
            background: #f0f0f0;
        }
        
        .btn-primary {
            background: #1890ff;
            border-color: #1890ff;
            color: #fff;
        }
        
        .btn-primary:hover {
            background: #40a9ff;
            border-color: #40a9ff;
        }
        
        .report-container {
            background: #fff;
            margin: 20px;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .query-panel {
            background: #fafafa;
            border-bottom: 1px solid #e8e8e8;
            padding: 15px 20px;
            display: none;
        }
        
        .query-panel.active {
            display: block;
        }
        
        .query-form {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            align-items: center;
        }
        
        .query-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .query-label {
            font-size: 14px;
            color: #666;
            white-space: nowrap;
        }
        
        .query-input {
            padding: 4px 8px;
            border: 1px solid #d9d9d9;
            border-radius: 4px;
            font-size: 14px;
            min-width: 120px;
        }
        
        .report-content {
            position: relative;
            min-height: 500px;
        }
        
        #jm-sheet-wrapper {
            width: 100%;
            height: 600px;
            position: relative;
        }
        
        .loading-mask {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(255,255,255,0.8);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        
        .loading-spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #1890ff;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .error-message {
            text-align: center;
            padding: 50px 20px;
            color: #ff4d4f;
            font-size: 16px;
        }
        
        ${cssStr!""}
        
        @media print {
            .report-header,
            .query-panel,
            .no-print {
                display: none !important;
            }
            
            .report-container {
                margin: 0;
                box-shadow: none;
                border-radius: 0;
            }
            
            body {
                background: #fff;
            }
        }
    </style>
</head>
<body>
    <!-- 报表头部 -->
    <div class="report-header no-print">
        <div class="report-title">${reportName!"报表预览"}</div>
        <div class="report-actions">
            <button class="btn" onclick="toggleQueryPanel()">查询条件</button>
            <button class="btn" onclick="refreshReport()">刷新</button>
            <button class="btn" onclick="exportExcel()">导出Excel</button>
            <button class="btn" onclick="exportPdf()">导出PDF</button>
            <button class="btn btn-primary" onclick="printReport()">打印</button>
        </div>
    </div>

    <!-- 报表容器 -->
    <div class="report-container">
        <!-- 查询面板 -->
        <div id="queryPanel" class="query-panel">
            <div class="query-form">
                <!-- 动态生成查询条件 -->
                <div class="query-actions" style="margin-left: auto;">
                    <button class="btn btn-primary" onclick="executeQuery()">查询</button>
                    <button class="btn" onclick="resetQuery()">重置</button>
                </div>
            </div>
        </div>
        
        <!-- 报表内容 -->
        <div class="report-content">
            <div id="jm-sheet-wrapper">
                <div class="loading-mask" id="loadingMask">
                    <div class="loading-spinner"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="/jmreport/js/jquery.min.js"></script>
    <script src="/jmreport/js/luckysheet/luckysheet.umd.js"></script>
    <script>
        // 全局变量
        window.reportConfig = {
            reportId: '${reportId!""}',
            reportCode: '${reportCode!""}',
            reportName: '${reportName!""}',
            jsonStr: '${jsonStr!"{}"}',
            apiUrl: '${apiUrl!""}',
            params: <#if params??>${params?js_string}<#else>{}</#if>
        };

        // 页面加载完成后初始化
        $(document).ready(function() {
            initReport();
        });

        // 初始化报表
        function initReport() {
            try {
                showLoading();
                
                // 解析报表配置
                var jsonData = {};
                if (window.reportConfig.jsonStr) {
                    try {
                        jsonData = JSON.parse(window.reportConfig.jsonStr);
                    } catch (e) {
                        console.error('解析报表JSON配置失败:', e);
                        jsonData = {};
                    }
                }
                
                // 初始化Luckysheet
                var options = {
                    container: 'jm-sheet-wrapper',
                    title: window.reportConfig.reportName,
                    lang: 'zh',
                    data: jsonData.data || [getDefaultSheet()],
                    allowCopy: true,
                    showtoolbar: false,
                    showinfobar: false,
                    showsheetbar: false,
                    showstatisticBar: false,
                    enableAddRow: false,
                    enableAddCol: false,
                    userEdit: false,
                    cellRightClickConfig: {},
                    sheetRightClickConfig: {},
                    pointEdit: false,
                    pointEditUpdate: function() { return false; }
                };
                
                luckysheet.create(options);
                
                // 加载报表数据
                setTimeout(function() {
                    loadReportData();
                }, 500);
                
            } catch (error) {
                console.error('初始化报表失败:', error);
                showError('报表初始化失败: ' + error.message);
            }
        }
        
        // 获取默认工作表
        function getDefaultSheet() {
            return {
                name: "Sheet1",
                color: "",
                status: 1,
                order: 0,
                data: [],
                config: {},
                scrollLeft: 0,
                scrollTop: 0,
                luckysheet_select_save: [],
                calcChain: [],
                isPivotTable: false,
                pivotTable: {},
                filter_select: {},
                filter: null,
                luckysheet_alternateformat_save: [],
                luckysheet_alternateformat_save_modelCustom: [],
                luckysheet_conditionformat_save: {},
                frozen: {},
                chart: [],
                zoomRatio: 1,
                image: {},
                showGridLines: 1,
                dataVerification: {}
            };
        }

        // 加载报表数据
        function loadReportData() {
            if (window.reportConfig.apiUrl) {
                // 如果配置了API地址，调用API获取数据
                $.ajax({
                    url: window.reportConfig.apiUrl,
                    type: 'POST',
                    data: JSON.stringify(window.reportConfig.params),
                    contentType: 'application/json',
                    success: function(data) {
                        updateReportData(data);
                        hideLoading();
                    },
                    error: function(xhr, status, error) {
                        console.error('加载报表数据失败:', error);
                        showError('加载报表数据失败: ' + error);
                        hideLoading();
                    }
                });
            } else {
                // 使用默认数据执行接口
                $.ajax({
                    url: '/scm/report/execute/' + window.reportConfig.reportId,
                    type: 'POST',
                    data: JSON.stringify(window.reportConfig.params),
                    contentType: 'application/json',
                    success: function(result) {
                        if (result && result.status === 'success') {
                            updateReportData(result.dataList || []);
                        } else {
                            showError(result.errorMessage || '报表执行失败');
                        }
                        hideLoading();
                    },
                    error: function(xhr, status, error) {
                        console.error('执行报表失败:', error);
                        showError('执行报表失败: ' + error);
                        hideLoading();
                    }
                });
            }
        }

        // 更新报表数据
        function updateReportData(data) {
            // TODO: 将数据转换为Luckysheet格式
            console.log('报表数据:', data);
        }

        // 显示加载状态
        function showLoading() {
            $('#loadingMask').show();
        }

        // 隐藏加载状态
        function hideLoading() {
            $('#loadingMask').hide();
        }

        // 显示错误信息
        function showError(message) {
            $('#jm-sheet-wrapper').html('<div class="error-message">' + message + '</div>');
        }

        // 切换查询面板
        function toggleQueryPanel() {
            $('#queryPanel').toggleClass('active');
        }

        // 执行查询
        function executeQuery() {
            // 收集查询参数
            var params = {};
            $('.query-input').each(function() {
                var name = $(this).attr('name');
                var value = $(this).val();
                if (name && value) {
                    params[name] = value;
                }
            });
            
            window.reportConfig.params = params;
            loadReportData();
        }

        // 重置查询
        function resetQuery() {
            $('.query-input').val('');
            window.reportConfig.params = {};
            loadReportData();
        }

        // 刷新报表
        function refreshReport() {
            showLoading();
            loadReportData();
        }

        // 导出Excel
        function exportExcel() {
            var url = '/scm/report/export/' + window.reportConfig.reportId + '?exportType=excel';
            downloadFile(url, window.reportConfig.params);
        }

        // 导出PDF
        function exportPdf() {
            var url = '/scm/report/export/' + window.reportConfig.reportId + '?exportType=pdf';
            downloadFile(url, window.reportConfig.params);
        }

        // 打印报表
        function printReport() {
            window.print();
        }

        // 下载文件
        function downloadFile(url, params) {
            var form = $('<form method="post" action="' + url + '">');
            if (params) {
                for (var key in params) {
                    form.append('<input type="hidden" name="' + key + '" value="' + params[key] + '">');
                }
            }
            $('body').append(form);
            form.submit();
            form.remove();
        }

        // 自定义脚本
        ${jsStr!""}
    </script>
</body>
</html>