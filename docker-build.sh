#!/bin/bash

# 学習進捗トラッカー Docker ビルドスクリプト

echo "🐳 学習進捗トラッカー Docker イメージをビルド中..."

# イメージ名とタグを設定
IMAGE_NAME="java-study-app"
TAG="latest"

# 既存のイメージを削除（オプション）
if [ "$1" = "--clean" ]; then
    echo "🧹 既存のイメージを削除中..."
    docker rmi $IMAGE_NAME:$TAG 2>/dev/null || true
fi

# Dockerイメージをビルド
echo "🔨 Dockerイメージをビルド中..."
docker build -t $IMAGE_NAME:$TAG .

# ビルド結果を確認
if [ $? -eq 0 ]; then
    echo "✅ Dockerイメージのビルドが完了しました！"
    echo "📦 イメージ名: $IMAGE_NAME:$TAG"
    echo ""
    echo "🚀 アプリケーションを起動するには:"
    echo "   docker-compose up -d"
    echo ""
    echo "🔍 ログを確認するには:"
    echo "   docker-compose logs -f app"
    echo ""
    echo "🛑 アプリケーションを停止するには:"
    echo "   docker-compose down"
else
    echo "❌ Dockerイメージのビルドに失敗しました。"
    exit 1
fi 