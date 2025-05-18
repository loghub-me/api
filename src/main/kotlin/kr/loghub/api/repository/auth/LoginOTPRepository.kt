package kr.loghub.api.repository.auth

import kr.loghub.api.entity.auth.LoginOTP
import org.springframework.data.repository.CrudRepository

interface LoginOTPRepository : CrudRepository<LoginOTP, String> {
    fun findByOtp(otp: String): LoginOTP?
}