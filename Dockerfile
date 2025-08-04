# マルチステージビルドを使用
FROM maven:3.9.5-openjdk-17 AS build

# 作業ディレクトリを設定
WORKDIR /app

# Maven Wrapperとpom.xmlをコピー
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Maven Wrapperに実行権限を付与
RUN chmod +x mvnw

# 依存関係をダウンロード
RUN ./mvnw dependency:go-offline -B

# ソースコードをコピー
COPY src src

# アプリケーションをビルド
RUN ./mvnw clean package -DskipTests

# 実行用イメージ
FROM openjdk:17-jre-slim

# 作業ディレクトリを設定
WORKDIR /app

# ビルドしたJARファイルをコピー
COPY --from=build /app/target/java-study-app-1.0.0.jar app.jar

# ポート8080を公開
EXPOSE 8080

# ヘルスチェックを追加
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"] 