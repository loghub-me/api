package kr.loghub.api.constant

object ResponseMessage {
    const val IM_GOOD = "I'm good!"

    const val UNAUTHORIZED = "인증 정보를 찾을 수 없습니다."
    const val FORBIDDEN = "접근 권한이 없습니다."
    const val INVALID_TOKEN = "유효하지 않은 토큰입니다."
    const val EXPIRED_TOKEN = "만료된 토큰입니다."

    const val USER_NOT_FOUND = "사용자를 찾을 수 없습니다."
}