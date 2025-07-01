package kr.loghub.api.dto.task.mail

class LoginMailSendRequest(
    override val to: String,
    override val subject: String = "[LogHub] 로그인 인증코드",
    val otp: String,
    override val html: String = """
        <html>
            <body>
                <h1>로그인 인증 코드</h1>
                <p>$otp</p>
            </body>
        </html>
    """,  // TODO: Refactor
) : MailSendRequest