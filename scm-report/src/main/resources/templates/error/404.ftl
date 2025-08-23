<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>页面未找到 - 404</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
            color: #667eea;
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
            background: #667eea;
            color: #fff;
        }
        
        .btn-primary:hover {
            background: #5a67d8;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #f7fafc;
            color: #667eea;
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
        <div class="error-code">404</div>
        <div class="error-title">页面未找到</div>
        
        <div class="error-message">
            <#if errorMessage??>
                ${errorMessage}
            <#else>
                抱歉，您访问的页面不存在或已被删除
            </#if>
        </div>
        
        <div class="error-description">
            请检查网址是否正确，或者返回首页继续浏览。
            如果您认为这是一个错误，请联系系统管理员。
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
        </div>
        
        <div class="footer-info">
            SCM供应链管理系统 - 积木报表模块
        </div>
    </div>

    <script>
        // 页面加载完成后的处理
        document.addEventListener('DOMContentLoaded', function() {
            // 记录404错误（可选）
            console.warn('404错误 - 页面未找到:', window.location.href);
            
            // 如果是从其他页面跳转过来的，可以提供更具体的返回链接
            if (document.referrer) {
                console.log('来源页面:', document.referrer);
            }
        });
    </script>
</body>
</html>