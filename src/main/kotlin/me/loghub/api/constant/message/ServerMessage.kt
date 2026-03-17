package me.loghub.api.constant.message

object ServerMessage {
    const val FAILED_BUILD_JWT_ALGORITHM = "Failed to build JWT Algorithm"
    const val FAILED_BUILD_JWT_VERIFIER = "Failed to build JWT Verifier"
    const val FAILED_BUILD_SECURITY_FILTER_CHAIN = "Failed to build SecurityFilterChain"

    const val AUTHENTICATION_NOT_FOUND = "Authentication not found in SecurityContext"
    const val PRINCIPAL_NOT_USER = "Principal is not of type User"

    const val ENTITY_NOT_PERSISTED = "Entity is not persisted yet"
}