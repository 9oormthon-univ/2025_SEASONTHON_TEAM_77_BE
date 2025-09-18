package com.teachtouch.backend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 잘못된 입력
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("요청하신 정보에 문제가 있어 처리를 완료할 수 없습니다.\n입력 내용을 다시 확인해 주세요.");
        // 예: 잘못된 가이드 번호 등
    }

    // 2. NullPointerException 발생 시
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointer(NullPointerException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("처리 중 문제가 발생했습니다.\n잠시 후 다시 시도해 주세요.");
    }

    // 3. 그 외 모든 예외 상황
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {

        if (ex instanceof MissingServletRequestPartException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("이미지 파일이 첨부되지 않아 처리를 진행할 수 없습니다.\n이미지를 꼭 선택해서 다시 시도해 주세요.");
        }

        if (ex instanceof MultipartException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("이미지를 전송하는 과정에서 문제가 발생했습니다.\n파일 형식이나 크기를 다시 확인해 주세요.");
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("예상치 못한 오류가 발생했습니다.\n지속될 경우 관리자에게 문의해 주세요.");
    }
}
