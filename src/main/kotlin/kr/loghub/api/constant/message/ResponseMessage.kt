package kr.loghub.api.constant.message

object ResponseMessage {
    const val IM_GOOD = "I'm good!"

    object Default {
        const val INVALID_REQUEST = "유효하지 않은 요청입니다."
        const val INVALID_FIELD = "유효하지 않은 필드입니다."
        const val NOT_FOUND = "해당하는 정보를 찾을 수 없습니다."
        const val ALREADY_EXISTS = "이미 존재하는 정보입니다."
        const val CONFLICT_FIELD = "필드 값이 충돌합니다."
        const val MISSING_COOKIE = "쿠키가 전달되지 않았습니다."
        const val INTERNAL_SERVER_ERROR = "알 수 없는 문제가 발생했습니다. 잠시 후 다시 시도해주세요."
    }

    object Page {
        const val MUST_BE_POSITIVE = "페이지는 1 이상이어야 합니다."
    }

    object Auth {
        const val UNAUTHORIZED = "인증 정보를 찾을 수 없습니다."
        const val FORBIDDEN = "접근 권한이 없습니다."
        const val BAD_CREDENTIALS = "인증 정보가 유효하지 않습니다."
        const val INVALID_OTP = "유효하지 않은 OTP입니다."
        const val INVALID_TOKEN = "유효하지 않은 토큰입니다."
        const val AUTHENTICATION_FAILED = "인증에 실패했습니다."
        const val UNSUPPORTED_OAUTH2_PROVIDER = "지원하지 않는 공급자입니다."
    }

    object Join {
        const val REQUEST_SUCCESS = "이메일로 가입 확인 코드가 발송되었습니다!"
        const val CONFIRM_SUCCESS = "가입이 성공적으로 완료되었습니다!"
    }

    object Login {
        const val REQUEST_SUCCESS = "이메일로 로그인 확인 코드가 발송되었습니다!"
        const val CONFIRM_SUCCESS = "로그인이 성공적으로 완료되었습니다!"
    }

    object Logout {
        const val SUCCESS = "로그아웃이 성공적으로 완료되었습니다!"
    }

    object Token {
        const val REFRESH_SUCCESS = "토큰이 갱신되었습니다!"
    }

    object User {
        const val NOT_FOUND = "사용자를 찾을 수 없습니다."
        const val BOT_NOT_FOUND = "봇 사용자를 찾을 수 없습니다."
        const val EMAIL_ALREADY_EXISTS = "이미 사용중인 이메일입니다."
        const val USERNAME_ALREADY_EXISTS = "이미 사용중인 아이디입니다."
        const val AVATAR_UPDATE_SUCCESS = "아바타가 성공적으로 업데이트되었습니다!"
        const val PROFILE_UPDATE_SUCCESS = "프로필이 성공적으로 업데이트되었습니다!"
        const val PRIVACY_UPDATE_SUCCESS = "정보 공개 설정이 성공적으로 업데이트되었습니다!"
        const val USERNAME_UPDATE_SUCCESS = "아이디가 성공적으로 변경되었습니다!"
        const val USERNAME_NOT_CHANGED = "유저네임이 변경되지 않았습니다."
        const val INVALID_DATE_RANGE = "날짜 범위가 유효하지 않습니다."
    }

    object Article {
        const val NOT_FOUND = "아티클을 찾을 수 없습니다."
        const val POST_SUCCESS = "아티클이 성공적으로 등록되었습니다!"
        const val EDIT_SUCCESS = "아티클이 성공적으로 수정되었습니다!"
        const val DELETE_SUCCESS = "아티클이 성공적으로 삭제되었습니다!"
        const val PERMISSION_DENIED = "이 아티클에 대한 권한이 없습니다!"

        object Comment {
            const val NOT_FOUND = "아티클의 댓글을 찾을 수 없습니다."
            const val POST_SUCCESS = "댓글이 성공적으로 등록되었습니다!"
            const val DELETE_SUCCESS = "댓글이 성공적으로 삭제되었습니다!"
            const val PERMISSION_DENIED = "이 댓글에 대한 권한이 없습니다!"
        }
    }

    object Series {
        const val NOT_FOUND = "책을 찾을 수 없습니다."
        const val POST_SUCCESS = "책이 성공적으로 등록되었습니다!"
        const val EDIT_SUCCESS = "책이 성공적으로 수정되었습니다!"
        const val DELETE_SUCCESS = "책이 성공적으로 삭제되었습니다!"
        const val PERMISSION_DENIED = "이 책에 대한 권한이 없습니다!"

        object Chapter {
            const val NOT_FOUND = "책 챕터를 찾을 수 없습니다."
            const val POST_SUCCESS = "책 챕터가 성공적으로 등록되었습니다!"
            const val EDIT_SUCCESS = "책 챕터가 성공적으로 수정되었습니다!"
            const val DELETE_SUCCESS = "책 챕터가 성공적으로 삭제되었습니다!"
            const val CHANGE_SEQUENCE_SUCCESS = "책 챕터 순서가 성공적으로 변경되었습니다!"
            const val SEQUENCE_MUST_BE_DIFF = "챕터 순서는 서로 다른 값이어야 합니다!"
        }

        object Review {
            const val NOT_FOUND = "책 리뷰를 찾을 수 없습니다."
            const val POST_SUCCESS = "책 리뷰가 성공적으로 등록되었습니다!"
            const val DELETE_SUCCESS = "책 리뷰가 성공적으로 삭제되었습니다!"
            const val PERMISSION_DENIED = "이 책 리뷰에 대한 권한이 없습니다!"
            const val ALREADY_EXISTS = "이미 작성한 책 리뷰가 존재합니다!"
        }
    }

    object Question {
        const val NOT_FOUND = "질문을 찾을 수 없습니다."
        const val POST_SUCCESS = "질문이 성공적으로 등록되었습니다!"
        const val CLOSE_SUCCESS = "질문이 성공적으로 닫혔습니다!"
        const val EDIT_SUCCESS = "질문이 성공적으로 수정되었습니다!"
        const val DELETE_SUCCESS = "질문이 성공적으로 삭제되었습니다!"
        const val PERMISSION_DENIED = "이 질문에 대한 권한이 없습니다!"
        const val STATUS_MUST_BE_OPEN = "질문 상태는 OPEN이어야 합니다!"

        object Answer {
            const val NOT_FOUND = "답변을 찾을 수 없습니다."
            const val POST_SUCCESS = "답변이 성공적으로 등록되었습니다!"
            const val ACCEPT_SUCCESS = "답변이 성공적으로 채택되었습니다!"
            const val EDIT_SUCCESS = "답변이 성공적으로 수정되었습니다!"
            const val DELETE_SUCCESS = "답변이 성공적으로 삭제되었습니다!"
            const val CANNOT_POST_SELF = "자신의 질문에 답변을 작성할 수 없습니다."
            const val ALREADY_ACCEPTED = "이미 채택된 답변입니다!"
            const val REQUEST_GENERATE_SUCCESS = "답변 생성 요청이 성공적으로 처리되었습니다!"
            const val GENERATE_COOLDOWN = "답변 생성 요청은 1분에 한 번만 가능합니다!"
        }
    }

    object Topic {
        const val NOT_FOUND = "토픽을 찾을 수 없습니다."
    }

    object Star {
        const val NOT_FOUND = "스타를 찾을 수 없습니다."
        const val ALREADY_EXISTS = "이미 스타가 존재합니다."
        const val ADD_SUCCESS = "스타가 성공적으로 추가되었습니다!"
        const val DELETE_SUCCESS = "스타가 성공적으로 삭제되었습니다!"
    }
}