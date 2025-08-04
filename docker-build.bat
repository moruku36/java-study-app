@echo off
chcp 65001 >nul

REM 学習進捗トラッカー Docker ビルドスクリプト (Windows)

echo 🐳 学習進捗トラッカー Docker イメージをビルド中...

REM イメージ名とタグを設定
set IMAGE_NAME=java-study-app
set TAG=latest

REM 既存のイメージを削除（オプション）
if "%1"=="--clean" (
    echo 🧹 既存のイメージを削除中...
    docker rmi %IMAGE_NAME%:%TAG% 2>nul
)

REM Dockerイメージをビルド
echo 🔨 Dockerイメージをビルド中...
docker build -t %IMAGE_NAME%:%TAG% .

REM ビルド結果を確認
if %ERRORLEVEL% EQU 0 (
    echo ✅ Dockerイメージのビルドが完了しました！
    echo 📦 イメージ名: %IMAGE_NAME%:%TAG%
    echo.
    echo 🚀 アプリケーションを起動するには:
    echo    docker-compose up -d
    echo.
    echo 🔍 ログを確認するには:
    echo    docker-compose logs -f app
    echo.
    echo 🛑 アプリケーションを停止するには:
    echo    docker-compose down
) else (
    echo ❌ Dockerイメージのビルドに失敗しました。
    exit /b 1
) 