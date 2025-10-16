@echo off
REM 自动打开Claude Code的批处理脚本
REM 目标目录: D:\2025_project\20_project_in_github\00_scm_backend\scm_backend

cd /d "D:\2025_project\20_project_in_github\00_scm_backend\scm_backend"
claude -c

REM 如果Claude Code未安装或命令不存在，显示错误信息
if errorlevel 1 (
    echo.
    echo 错误：无法启动Claude Code
    echo 请确认Claude Code已正确安装且环境变量已配置
    pause
)
