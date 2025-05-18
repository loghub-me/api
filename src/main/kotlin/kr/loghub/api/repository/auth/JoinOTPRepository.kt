package kr.loghub.api.repository.auth

import kr.loghub.api.entity.auth.JoinOTP
import org.springframework.data.repository.CrudRepository

interface JoinOTPRepository : CrudRepository<JoinOTP, String> {
    fun findByOtp(otp: String): JoinOTP?
}