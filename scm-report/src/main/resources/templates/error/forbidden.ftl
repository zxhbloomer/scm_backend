<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>访问被拒绝 - 403</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: linear-gradient(135deg, #ffa726 0%, #ff7043 100%);
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
        
        .error-icon {
            font-size: 80px;
            color: #ffa726;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
        }
        
        .error-code {
            font-size: 120px;
            font-weight: bold;
            color: #ffa726;
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
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .error-description {
            font-size: 14px;
            color: #999;
            margin-bottom: 40px;
            line-height: 1.5;
        }
        
        .reason-list {
            background: #fff8e1;
            border-left: 4px solid #ffa726;
            padding: 20px;
            margin: 20px 0;
            border-radius: 0 6px 6px 0;
            text-align: left;
        }
        
        .reason-title {
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }
        
        .reason-list ul {
            margin: 0;
            padding-left: 20px;
            color: #666;
            font-size: 14px;
        }
        
        .reason-list li {
            margin-bottom: 8px;
            line-height: 1.4;
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
            background: #ffa726;
            color: #fff;
        }
        
        .btn-primary:hover {
            background: #ff9800;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(255, 167, 38, 0.4);
        }
        
        .btn-secondary {
            background: #f7fafc;
            color: #ffa726;
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
        <div class="error-icon">🔒</div>
        <div class="error-code">403</div>
        <div class="error-title">访问被拒绝</div>
        
        <div class="error-message">
            <#if errorMessage??>
                ${errorMessage}
            <#else>
                抱歉，您没有权限访问此资源
            </#if>
        </div>
        
        <div class="reason-list">
            <div class="reason-title">可能的原因:</div>
            <ul>
                <li>报表尚未发布，仍处于设计状态</li>
                <li>您的账户没有访问此报表的权限</li>
                <li>报表已被设置为私有或受限访问</li>
                <li>您的会话已过期，需要重新登录</li>
            </ul>
        </div>
        
        <div class="error-description">
            如果您认为这是一个错误，请联系系统管理员或报表负责人。
        </div>
        
        <div class="action-buttons">
            <a href="/jmreport/list" class="btn btn-primary">
                <svg class="icon" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"/>
                </svg>
                返回首页
            </a>
            
            <button class="btn btn-secondary" onclick="history.back()">
                <svg class="icon" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M7.707 14.707a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l2.293 2.293a1 1 0 010 1.414z" clip-rule="evenodd"/>
                </svg>
                返回上页
            </button>
            
            <button class="btn btn-secondary" onclick="location.reload()">
                <svg class="icon" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd"/>
                </svg>
                重试
            </button>
        </div>
        
        <div class="footer-info">
            SCM供应链管理系统 - 积木报表模块
        </div>
    </div>

    <script>
        // 页面加载完成后的处理
        document.addEventListener('DOMContentLoaded', function() {
            // 记录403错误
            console.warn('403错误 - 访问被拒绝:', {
                url: window.location.href,
                referrer: document.referrer,
                timestamp: new Date().toISOString()
            });
        });
    </script>
</body>
</html>