# Render.com デプロイガイド

## 概要
このドキュメントでは、学習進捗トラッカーアプリケーションをRender.comにデプロイする手順を説明します。

## 前提条件
- GitHubアカウント
- Render.comアカウント
- PostgreSQLデータベース（Render.comで作成）

## デプロイ手順

### 1. PostgreSQLデータベースの作成

1. Render.comのダッシュボードにログイン
2. 「New +」→「PostgreSQL」を選択
3. 以下の設定でデータベースを作成：
   - **Name**: `study-app-db`
   - **Database**: `studydb`
   - **User**: `studyuser`
   - **Region**: 最寄りのリージョン（例：Oregon）
   - **Plan**: Free

### 2. データベース接続情報の取得

データベース作成後、以下の情報をメモしてください：
- **Internal Database URL**: `postgresql://studyuser:password@host:port/studydb`
- **External Database URL**: `postgresql://studyuser:password@host.region.render.com/studydb`
- **Username**: `studyuser`
- **Password**: 作成時に設定したパスワード

### 3. Webサービスの作成

1. 「New +」→「Web Service」を選択
2. GitHubリポジトリを接続：`https://github.com/moruku36/java-study-app`
3. 以下の設定を入力：

#### 基本設定
- **Name**: `java-study-app`
- **Environment**: `Java`
- **Region**: データベースと同じリージョン
- **Branch**: `main`
- **Plan**: Free

#### ビルド設定
- **Build Command**: `./mvnw clean package -DskipTests`
- **Start Command**: `java -jar target/java-study-app-1.0.0.jar`

#### 環境変数
以下の環境変数を設定してください：

| キー | 値 | 説明 |
|------|-----|------|
| `JAVA_VERSION` | `17` | Javaのバージョン |
| `SPRING_PROFILES_ACTIVE` | `production` | 本番環境プロファイル |
| `DATABASE_URL` | `postgresql://studyuser:password@host.region.render.com/studydb?sslmode=require` | 外部データベースURL |
| `DATABASE_USERNAME` | `studyuser` | データベースユーザー名 |
| `DATABASE_PASSWORD` | `your_password` | データベースパスワード |
| `DDL_AUTO` | `update` | データベーススキーマ更新モード |
| `SHOW_SQL` | `false` | SQLログ出力無効化 |
| `THYMELEAF_CACHE` | `true` | Thymeleafキャッシュ有効化 |
| `LOG_LEVEL` | `INFO` | ログレベル |

### 4. デプロイの実行

1. 「Create Web Service」をクリック
2. ビルドとデプロイが自動的に開始されます
3. デプロイ完了まで5-10分程度待機

### 5. 動作確認

デプロイ完了後、以下のURLでアプリケーションにアクセスできます：
- **アプリケーション**: `https://your-app-name.onrender.com`
- **ヘルスチェック**: `https://your-app-name.onrender.com/health`
- **Actuator**: `https://your-app-name.onrender.com/actuator/health`

## トラブルシューティング

### よくあるエラーと対処法

#### 1. データベース接続エラー
```
Driver org.postgresql.Driver claims to not accept jdbcUrl
```
**対処法**: 
- 環境変数 `DATABASE_URL` が正しく設定されているか確認
- SSLモードが含まれているか確認（`?sslmode=require`）

#### 2. 起動タイムアウト
```
Application failed to start within the time limit
```
**対処法**:
- データベース接続設定を確認
- ログレベルを `INFO` に設定
- 不要な初期化処理を無効化

#### 3. メモリ不足エラー
```
OutOfMemoryError
```
**対処法**:
- JVMオプションを追加：`-Xmx512m -Xms256m`
- ログレベルを `WARN` に設定

### ログの確認方法

1. Render.comダッシュボードでサービスを選択
2. 「Logs」タブをクリック
3. リアルタイムログを確認

### 環境変数の確認方法

1. サービス設定画面で「Environment」タブを確認
2. 必要な環境変数が正しく設定されているか確認

## パフォーマンス最適化

### 起動時間の短縮
- `lazy-initialization: true` を設定
- 不要なログ出力を無効化
- データ初期化を無効化

### メモリ使用量の最適化
- HikariCP接続プールサイズを調整
- JVMヒープサイズを設定
- 不要なBeanの初期化を遅延

## セキュリティ設定

### 本番環境での推奨設定
- データベースパスワードを強力なものに変更
- HTTPS通信の強制
- 不要なエンドポイントの無効化
- ログに機密情報が出力されないよう注意

## 監視とメンテナンス

### ヘルスチェック
- `/health` エンドポイントでアプリケーション状態を監視
- `/actuator/health` で詳細なヘルス情報を確認

### ログ監視
- エラーログの定期的な確認
- パフォーマンスログの監視
- セキュリティログの確認

## 更新手順

1. GitHubリポジトリにコードをプッシュ
2. Render.comで自動デプロイが開始
3. デプロイ完了を確認
4. 動作確認を実施

## サポート

問題が発生した場合は、以下を確認してください：
1. Render.comのログ
2. アプリケーションのログ
3. データベース接続状態
4. 環境変数の設定 