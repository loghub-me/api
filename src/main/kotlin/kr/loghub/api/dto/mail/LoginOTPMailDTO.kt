package kr.loghub.api.dto.mail

class LoginOTPMailDTO(
    override val to: String,
    override val subject: String = "[LogHub] 로그인 인증코드",
    val token: String,
    override val html: String = """
        <html>
            <body>
                <h1>로그인 인증 코드</h1>
                <p>$token</p>
            </body>
        </html>
    """,  // TODO: Refactor
) : MailDTO