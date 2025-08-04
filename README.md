# 学習進捗トラッカー

Spring Boot + Thymeleaf + H2データベースを使用した学習進捗管理Webアプリケーションです。

## 機能

### 主要機能
- **ダッシュボード**: 週次学習進捗の可視化、統計情報の表示
- **学習目標管理**: 科目別の学習目標の設定・管理
- **学習記録**: 日々の学習時間とメモの記録
- **学習履歴**: 過去の学習記録の閲覧・検索
- **週次レポート**: Spring Batchによる自動レポート生成

### 技術スタック
- **Backend**: Spring Boot 3.2.0, Spring Data JPA, Spring Security, Spring Batch
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js, Font Awesome
- **Database**: H2 Database (開発用)
- **Build Tool**: Maven
- **Java Version**: 17

## セットアップ手順

### 前提条件
- Java 17以上
- Maven 3.6以上（またはMaven Wrapper使用）

### ローカル環境での実行

#### 1. プロジェクトのクローン
```bash
git clone https://github.com/moruku36/java-study-app.git
cd java-study-app
```

#### 2. アプリケーションの起動
```bash
# Maven Wrapperを使用（推奨）
./mvnw spring-boot:run

# またはMavenがインストールされている場合
mvn spring-boot:run
```

#### 3. アクセス
- アプリケーション: http://localhost:8080
- H2コンソール: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:studydb`
  - ユーザー名: `sa`
  - パスワード: (空)

### Dockerでの実行

#### 1. 前提条件
- Docker Desktop がインストールされていること
- Docker Compose が利用可能であること

#### 2. アプリケーションの起動
```bash
# 本番環境用（PostgreSQL + アプリケーション）
docker-compose up -d

# 開発環境用（PostgreSQL + pgAdmin）
docker-compose -f docker-compose.dev.yml up -d
```

#### 3. アクセス
- **アプリケーション**: http://localhost:8080
- **pgAdmin（開発環境）**: http://localhost:5050
  - メール: `admin@studyapp.com`
  - パスワード: `admin123`

#### 4. 便利なコマンド
```bash
# ログを確認
docker-compose logs -f app

# アプリケーションを停止
docker-compose down

# データベースも含めて完全に削除
docker-compose down -v

# イメージを再ビルド
docker-compose build --no-cache
```

### Render.comでのデプロイ

#### 1. Render.comアカウント作成
- [Render.com](https://render.com)にアクセスしてアカウント作成
- GitHubアカウントと連携

#### 2. 新しいWeb Service作成
- Dashboard → "New +" → "Web Service"
- GitHubリポジトリを選択: `moruku36/java-study-app`

#### 3. サービス設定
- **Name**: `java-study-app`
- **Environment**: `Java`
- **Build Command**: `./mvnw clean package -DskipTests`
- **Start Command**: `java -jar target/java-study-app-1.0.0.jar`
- **Java Version**: `17`

#### 4. 環境変数設定（オプション）
- `SPRING_PROFILES_ACTIVE`: `production`
- `DDL_AUTO`: `update`
- `SHOW_SQL`: `false`
- `THYMELEAF_CACHE`: `true`
- `LOG_LEVEL`: `INFO`

#### 5. PostgreSQLデータベース追加（推奨）
- "New +" → "PostgreSQL"
- データベース名を設定
- 環境変数`DATABASE_URL`が自動設定される

#### 6. デプロイ
- "Create Web Service"をクリック
- ビルドとデプロイが自動実行される

## 使用方法

### 初期データ
アプリケーション起動時に以下のサンプルデータが自動生成されます：
- ユーザー: `sample_user` (パスワード: `password123`)
- 学習目標: Java (60分/日), 数学 (45分/日), 英語 (30分/日)
- 過去7日間のサンプル学習記録

### 基本的な使い方
1. **ダッシュボード**: 学習進捗の概要を確認
2. **学習目標**: 新しい学習目標を作成・管理
3. **学習記録**: 日々の学習時間を記録
4. **学習履歴**: 過去の記録を確認

## API エンドポイント

### 学習目標 API
- `POST /api/goals` - 学習目標の作成
- `GET /api/goals/{userId}` - ユーザーの学習目標一覧
- `GET /api/goals/{userId}/active` - アクティブな学習目標
- `PUT /api/goals/{id}` - 学習目標の更新
- `DELETE /api/goals/{id}` - 学習目標の削除

### 学習記録 API
- `POST /api/logs` - 学習記録の作成
- `GET /api/logs/{userId}` - ユーザーの学習記録一覧
- `GET /api/logs/{userId}/weekly` - 週次進捗データ
- `GET /api/logs/{userId}/range` - 期間指定での学習記録
- `PUT /api/logs/{id}` - 学習記録の更新
- `DELETE /api/logs/{id}` - 学習記録の削除

## プロジェクト構造

```
src/
├── main/
│   ├── java/com/studyapp/
│   │   ├── config/          # 設定クラス
│   │   ├── controller/      # コントローラー
│   │   ├── domain/          # エンティティ
│   │   ├── dto/            # DTOクラス
│   │   ├── repository/     # リポジトリ
│   │   ├── service/        # サービス
│   │   └── batch/          # Spring Batch設定
│   └── resources/
│       ├── templates/      # Thymeleafテンプレート
│       └── application.yml # アプリケーション設定
└── test/                   # テストコード
```

## 今後の拡張予定

### 機能拡張
- [ ] ユーザー認証・認可の実装
- [ ] 学習記録の編集機能
- [ ] 学習目標の編集機能
- [ ] 学習履歴ページの実装
- [ ] 月次・年次レポート機能
- [ ] 学習アドバイス機能（ChatGPT API連携）
- [ ] 通知機能（Slack連携）
- [ ] モバイルアプリ対応

### 技術的改善
- [ ] PostgreSQLへの移行
- [ ] Docker化
- [ ] CI/CDパイプラインの構築
- [ ] ユニットテスト・統合テストの追加
- [ ] API仕様書（OpenAPI）の作成
- [ ] ログ機能の強化
- [ ] パフォーマンス最適化

## 開発環境

### 推奨IDE
- IntelliJ IDEA
- Eclipse (Spring Tool Suite)
- VS Code (Java Extension Pack)

### デバッグ
- H2コンソールでデータベース内容を確認可能
- Spring Boot DevToolsによりホットリロード対応
- ログレベル: DEBUG (application.ymlで設定)

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## 貢献

プルリクエストやイシューの報告を歓迎します。貢献する前に、以下の点を確認してください：

1. コードスタイルの統一
2. 適切なテストの追加
3. ドキュメントの更新

## サポート

問題や質問がある場合は、GitHubのイシューを作成してください。 