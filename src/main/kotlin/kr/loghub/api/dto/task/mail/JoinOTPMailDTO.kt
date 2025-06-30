package kr.loghub.api.dto.task.mail

class JoinOTPMailDTO(
    override val to: String,
    override val subject: String = "[LogHub] 가입 절차를 완료해주세요!",
    val otp: String,
    override val html: String = """
        <html>
            <body>
                <h1>가입 인증 코드</h1>
                <p>$otp</p>
            </body>
        </html>
    """,  // TODO: Refactor
) : MailDTO