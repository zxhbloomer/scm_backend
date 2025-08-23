<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>打印 - ${reportName!"报表"}</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: "SimSun", "Microsoft YaHei", Arial, sans-serif;
            font-size: 12px;
            line-height: 1.4;
            color: #333;
            background: #fff;
        }
        
        .print-container {
            width: 100%;
            max-width: none;
            margin: 0;
            padding: 20px;
        }
        
        .report-header {
            text-align: center;
            margin-bottom: 20px;
            border-bottom: 2px solid #333;
            padding-bottom: 10px;
        }
        
        .report-title {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 8px;
            color: #000;
        }
        
        .report-subtitle {
            font-size: 14px;
            color: #666;
            margin-bottom: 5px;
        }
        
        .print-info {
            font-size: 10px;
            color: #999;
            text-align: right;
        }
        
        .report-params {
            margin-bottom: 15px;
            padding: 10px;
            background: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        
        .params-title {
            font-weight: bold;
            margin-bottom: 5px;
            color: #333;
        }
        
        .params-list {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            font-size: 11px;
        }
        
        .param-item {
            display: flex;
            align-items: center;
        }
        
        .param-label {
            font-weight: bold;
            margin-right: 5px;
        }
        
        .param-value {
            color: #666;
        }
        
        .report-content {
            width: 100%;
            min-height: 400px;
            border: 1px solid #ddd;
        }
        
        #jm-print-sheet {
            width: 100%;
            height: auto;
            min-height: 400px;
        }
        
        .report-footer {
            margin-top: 20px;
            border-top: 1px solid #ddd;
            padding-top: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 10px;
            color: #666;
        }
        
        .footer-left {
            text-align: left;
        }
        
        .footer-right {
            text-align: right;
        }
        
        .page-break {
            page-break-before: always;
        }
        
        /* 打印样式 */
        @media print {
            body {
                font-size: 11px;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }
            
            .print-container {
                padding: 0;
                max-width: none;
            }
            
            .no-print {
                display: none !important;
            }
            
            .report-content {
                border: none;
            }
            
            .page-break {
                page-break-before: always;
            }
            
            @page {
                margin: 1cm;
                size: A4;
            }
        }
        
        /* 自定义样式 */
        ${cssStr!""}
    </style>
</head>
<body>
    <div class="print-container">
        <!-- 报表头部 -->
        <div class="report-header">
            <div class="report-title">${reportName!"报表"}</div>
            <div class="report-subtitle">报表编码: ${reportCode!"--"}</div>
            <div class="print-info">
                打印时间: <span id="printTime"></span> | 
                报表ID: ${reportId!"--"}
            </div>
        </div>
        
        <!-- 查询参数 -->
        <#if params?? && (params?size > 0)>
            <div class="report-params">
                <div class="params-title">查询条件:</div>
                <div class="params-list">
                    <#list params?keys as key>
                        <div class="param-item">
                            <span class="param-label">${key}:</span>
                            <span class="param-value">${params[key]!"--"}</span>
                        </div>
                    </#list>
                </div>
            </div>
        </#if>
        
        <!-- 报表内容 -->
        <div class="report-content">
            <div id="jm-print-sheet"></div>
        </div>
        
        <!-- 报表页脚 -->
        <div class="report-footer">
            <div class="footer-left">
                <div>系统名称: SCM供应链管理系统</div>
                <div>报表引擎: 积木报表 v1.7.4</div>
            </div>
            <div class="footer-right">
                <div>第 <span id="pageNumber">1</span> 页</div>
                <div>共 <span id="totalPages">1</span> 页</div>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="/jmreport/js/jquery.min.js"></script>
    <script src="/jmreport/js/luckysheet/luckysheet.umd.js"></script>
    <script>
        // 报表配置
        window.reportConfig = {
            reportId: '${reportId!""}',
            reportCode: '${reportCode!""}',
            reportName: '${reportName!""}',
            jsonStr: '${jsonStr!"{}"}',
            params: <#if params??>${params?js_string}<#else>{}</#if>
        };

        // 页面加载完成后初始化
        $(document).ready(function() {
            initPrintReport();
            updatePrintTime();
        });

        // 初始化打印报表
        function initPrintReport() {
            try {
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
                
                // 配置Luckysheet选项（针对打印优化）
                var options = {
                    container: 'jm-print-sheet',
                    title: window.reportConfig.reportName,
                    lang: 'zh',
                    data: jsonData.data || [getDefaultSheet()],
                    allowCopy: false,
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
                    pointEditUpdate: function() { return false; },
                    showGridLines: true,
                    showRowBar: false,
                    showColumnBar: false
                };
                
                luckysheet.create(options);
                
                // 加载报表数据
                setTimeout(function() {
                    loadPrintData();
                }, 500);
                
            } catch (error) {
                console.error('初始化打印报表失败:', error);
                showPrintError('报表初始化失败: ' + error.message);
            }
        }
        
        // 获取默认工作表（针对打印优化）
        function getDefaultSheet() {
            return {
                name: "PrintSheet",
                color: "",
                status: 1,
                order: 0,
                data: [],
                config: {
                    borderInfo: [],
                    columnlen: {},
                    rowlen: {},
                    merge: {},
                    authority: {}
                },
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

        // 加载打印数据
        function loadPrintData() {
            // 调用报表执行接口获取数据
            $.ajax({
                url: '/scm/report/execute/' + window.reportConfig.reportId,
                type: 'POST',
                data: JSON.stringify(window.reportConfig.params),
                contentType: 'application/json',
                success: function(result) {
                    if (result && result.status === 'success') {
                        updatePrintData(result.dataList || []);
                    } else {
                        showPrintError(result.errorMessage || '报表执行失败');
                    }
                },
                error: function(xhr, status, error) {
                    console.error('执行打印报表失败:', error);
                    showPrintError('执行报表失败: ' + error);
                }
            });
        }

        // 更新打印数据
        function updatePrintData(data) {
            // TODO: 将数据转换为适合打印的格式
            console.log('打印数据:', data);
            
            // 数据加载完成后自动打印
            setTimeout(function() {
                window.print();
            }, 1000);
        }

        // 显示打印错误
        function showPrintError(message) {
            $('#jm-print-sheet').html(
                '<div style="text-align: center; padding: 50px; color: #ff4d4f; font-size: 16px;">' +
                '<div style="margin-bottom: 20px;"><i class="fa fa-exclamation-triangle"></i></div>' +
                '<div>' + message + '</div>' +
                '</div>'
            );
        }

        // 更新打印时间
        function updatePrintTime() {
            var now = new Date();
            var timeStr = now.getFullYear() + '-' + 
                         String(now.getMonth() + 1).padStart(2, '0') + '-' +
                         String(now.getDate()).padStart(2, '0') + ' ' +
                         String(now.getHours()).padStart(2, '0') + ':' +
                         String(now.getMinutes()).padStart(2, '0') + ':' +
                         String(now.getSeconds()).padStart(2, '0');
            $('#printTime').text(timeStr);
        }

        // 自定义脚本
        ${jsStr!""}
    </script>
</body>
</html>