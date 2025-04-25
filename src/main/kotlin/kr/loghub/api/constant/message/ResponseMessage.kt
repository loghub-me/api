package kr.loghub.api.constant.message

object ResponseMessage {
    const val IM_GOOD = "I'm good!"

    const val INVALID_REQUEST = "잘못된 요청입니다."
    const val NOT_FOUND: String = "해당하는 정보를 찾을 수 없습니다."

    const val UNAUTHORIZED = "인증 정보를 찾을 수 없습니다."
    const val FORBIDDEN = "접근 권한이 없습니다."
    const val BAD_CREDENTIALS = "인증 정보가 유효하지 않습니다."
    const val INVALID_OTP = "유효하지 않은 OTP입니다."
    const val INVALID_TOKEN = "유효하지 않은 토큰입니다."
    const val AUTHENTICATION_FAILED = "인증에 실패했습니다."

    const val JOIN_REQUEST_SUCCESS = "이메일로 가입 확인 코드가 발송되었습니다!"
    const val LOGIN_REQUEST_SUCCESS = "이메일로 로그인 확인 코드가 발송되었습니다!"
    const val JOIN_CONFIRM_SUCCESS = "가입이 성공적으로 완료되었습니다!"
    const val LOGIN_CONFIRM_SUCCESS = "로그인이 성공적으로 완료되었습니다!"
    const val TOKEN_REFRESH_SUCCESS = "토큰이 갱신되었습니다!"

    const val UNSUPPORTED_OAUTH2_PROVIDER = "지원하지 않는 공급자입니다."

    const val USER_NOT_FOUND = "사용자를 찾을 수 없습니다."
    const val USER_EMAIL_ALREADY_EXISTS = "이미 사용중인 이메일입니다."
    const val USER_USERNAME_ALREADY_EXISTS = "이미 사용중인 아이디입니다."
}