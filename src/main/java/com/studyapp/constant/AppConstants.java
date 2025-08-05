package com.studyapp.constant;

/**
 * アプリケーション全体で使用する定数
 */
public final class AppConstants {
    
    private AppConstants() {
        // インスタンス化を防ぐ
    }
    
    // デフォルトユーザー設定
    public static final String DEFAULT_USERNAME = "sample_user";
    public static final String DEFAULT_EMAIL = "sample@example.com";
    public static final String DEFAULT_PASSWORD = "password123";
    
    // アプリケーション情報
    public static final String APP_NAME = "java-study-app";
    public static final String APP_VERSION = "1.0.0";
    
    // エラーメッセージ
    public static final String ERROR_USER_NOT_FOUND = "ユーザーが見つかりません";
    public static final String ERROR_DASHBOARD_LOAD = "ダッシュボードの読み込み中にエラーが発生しました";
    public static final String ERROR_STUDY_LOG_SAVE = "学習記録の保存中にエラーが発生しました";
    public static final String ERROR_HISTORY_LOAD = "学習履歴の取得中にエラーが発生しました";
    
    // 成功メッセージ
    public static final String SUCCESS_STUDY_LOG_SAVE = "学習記録を保存しました";
    public static final String SUCCESS_USER_REGISTER = "ユーザー登録が完了しました。ログインしてください。";
    
    // バリデーションメッセージ
    public static final String VALIDATION_USERNAME_LENGTH = "ユーザー名は3文字以上で入力してください";
    public static final String VALIDATION_EMAIL_FORMAT = "有効なメールアドレスを入力してください";
    public static final String VALIDATION_PASSWORD_LENGTH = "パスワードは6文字以上で入力してください";
    public static final String VALIDATION_PASSWORD_MISMATCH = "パスワードが一致しません";
    public static final String VALIDATION_USERNAME_EXISTS = "このユーザー名は既に使用されています";
    public static final String VALIDATION_EMAIL_EXISTS = "このメールアドレスは既に使用されています";
} 