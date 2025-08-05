package com.studyapp.exception;

/**
 * アプリケーション固有の例外クラス
 */
public class StudyAppException extends RuntimeException {
    
    public StudyAppException(String message) {
        super(message);
    }
    
    public StudyAppException(String message, Throwable cause) {
        super(message, cause);
    }
} 