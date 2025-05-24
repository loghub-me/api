package kr.loghub.api.util

fun String.toSlug() = this
    .lowercase()
    .replace("%20", "-") // "%20" -> "-"
    .replace(Regex("[^가-힣ㄱ-ㅎㅏ-ㅣa-z0-9-_]"), "-") // 허용 문자 외에는 "-"로 치환
    .replace(Regex("-{2,}"), "-") // 연속된 "-"는 하나로
    .replace(Regex("^-|-$"), "") // 앞뒤 "-" 제거