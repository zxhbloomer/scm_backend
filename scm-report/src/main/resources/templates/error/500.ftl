<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>服务器错误 - 500</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            color: #333;
        }
        
        .error-container {
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            padding: 60px 40px;
            text-align: center;
            max-width: 500px;
            width: 90%;
        }
        
        .error-code {
            font-size: 120px;
            font-weight: bold;
            color: #ff6b6b;
            margin: 0;
            line-height: 1;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
        }
        
        .error-title {
            font-size: 24px;
            font-weight: bold;
            margin: 20px 0 15px;
            color: #333;
        }
        
        .error-message {
            font-size: 16px;
            color: #666;
            margin-bottom: 20px;
            line-height: 1.6;
        }
        
        .error-details {
            background: #f8f9fa;
            border-left: 4px solid #ff6b6b;
            padding: 15px;
            margin: 20px 0;
            border-radius: 0 4px 4px 0;
            text-align: left;
        }
        
        .error-details-title {
            font-weight: bold;
            color: #333;
            margin-bottom: 8px;
        }
        
        .error-details-content {
            font-size: 14px;
            color: #666;
            font-family: monospace;
            word-break: break-all;
            max-height: 100px;
            overflow-y: auto;
        }
        
        .error-description {
            font-size: 14px;
            color: #999;
            margin-bottom: 40px;
            line-height: 1.5;
        }
        
        .action-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            text-decoration: none;
            cursor: pointer;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }
        
        .btn-primary {
            background: #ff6b6b;
            color: #fff;
        }
        
        .btn-primary:hover {
            background: #ff5252;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(255, 107, 107, 0.4);
        }
        
        .btn-secondary {
            background: #f7fafc;
            color: #ff6b6b;
            border: 1px solid #e2e8f0;
        }
        
        .btn-secondary:hover {
            background: #edf2f7;
            transform: translateY(-2px);
        }
        
        .icon {
            width: 16px;
            height: 16px;
        }
        
        .footer-info {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e2e8f0;
            font-size: 12px;
            color: #a0aec0;
        }
        
        .error-id {
            font-size: 12px;
            color: #a0aec0;
            margin-top: 15px;
        }
        
        @media (max-width: 480px) {
            .error-container {
                padding: 40px 20px;
            }
            
            .error-code {
                font-size: 80px;
            }
            
            .error-title {
                font-size: 20px;
            }
            
            .action-buttons {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-code">500</div>
        <div class="error-title">服务器内部错误</div>
        
        <div class="error-message">
            服务器在处理您的请求时发生了意外错误
        </div>
        
        <#if errorMessage??>
            <div class="error-details">
                <div class="error-details-title">错误详情:</div>
                <div class="error-details-content">${errorMessage}</div>
            </div>
        </#if>
        
        <div class="error-description">
            我们已经记录了这个错误，技术团队将尽快修复此问题。
            您可以稍后重试，或者联系系统管理员获取帮助。
        </div>
        
        <div class="action-buttons">
            <button class="btn btn-primary" onclick="location.reload()">
                <svg class="icon" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd"/>
                </svg>
                重新加载
            </button>
            
            <a href="/jmreport/list" class="btn btn-secondary">
                <svg class="icon" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"/>
                </svg>
                返回首页
            </a>
        </div>
        
        <div class="error-id">
            错误ID: ERR-<span id="errorId"></span>
        </div>
        
        <div class="footer-info">
            SCM供应链管理系统 - 积木报表模块
        </div>
    </div>

    <script>
        // 页面加载完成后的处理
        document.addEventListener('DOMContentLoaded', function() {
            // 生成唯一的错误ID
            var errorId = 'JM' + Date.now().toString(36).toUpperCase();
            document.getElementById('errorId').textContent = errorId;
            
            // 记录500错误
            console.error('500错误 - 服务器内部错误:', {
                errorId: errorId,
                url: window.location.href,
                userAgent: navigator.userAgent,
                timestamp: new Date().toISOString()
            });
            
            // 可以发送错误报告到服务器（可选）
            /*
            fetch('/scm/report/error/log', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    errorId: errorId,
                    errorType: '500',
                    url: window.location.href,
                    userAgent: navigator.userAgent,
                    timestamp: new Date().toISOString()
                })
            }).catch(function(err) {
                console.warn('无法发送错误报告:', err);
            });
            */
        });
    </script>
</body>
</html>