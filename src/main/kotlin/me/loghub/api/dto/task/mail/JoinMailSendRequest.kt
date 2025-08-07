package me.loghub.api.dto.task.mail

import me.loghub.api.constant.http.HttpDomain

class JoinMailSendRequest(
    override val to: String,
    override val subject: String = "[LogHub] 가입을 완료해주세요!",
    val otp: String,
    override val html: String = """
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>LogHub | 로그인 인증 OTP</title>
  </head>
  <body style="font-family: Arial, Helvetica, sans-serif">
    <header style="padding: 12px; background-color: #3a97fa">
        <img src="${HttpDomain.WEB}/logo.png" alt="logo" width="128" height="128" />
    </header>
    <section style="padding: 12px">
      <h2>로그인 인증 OTP</h2>
      <div style="display: inline-block; padding: 8px 12px; border-radius: 6px; color: #fff; background-color: #3a97fa">
        $otp
      </div>
      <a
        href="${HttpDomain.WEB}/login/confirm?email=$to&otp=$otp"
        style="display: inline-block; padding: 8px 12px; border-radius: 6px; color: #3a97fa; background-color: #fff"
      >
        인증하기
      </a>
      <p style="color: #666">로그인 인증을 위해 OTP를 입력해주세요. OTP는 3분 동안 유효합니다.</p>
    </section>
  </body>
</html>
""",
) : MailSendRequest