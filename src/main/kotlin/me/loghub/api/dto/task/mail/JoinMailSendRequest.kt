package me.loghub.api.dto.task.mail

import me.loghub.api.config.ClientConfig

class JoinMailSendRequest(
    override val to: String,
    override val subject: String = "[LogHub] 가입을 완료해주세요!",
    val otp: String,
    override val html: String = """
<!DOCTYPE html>
<html dir="ltr" lang="en">
  <head>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
    <meta name="x-apple-disable-message-reformatting" />
  </head>
  <body style="background-color:#ffffff">
    <table border="0" width="100%" cellpadding="0" cellspacing="0" role="presentation" align="center">
      <tbody>
        <tr>
          <td style='background-color:#ffffff;color:#24292e;font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Helvetica,Arial,sans-serif,"Apple Color Emoji","Segoe UI Emoji"'>
            <div style="display:none;overflow:hidden;line-height:1px;opacity:0;max-height:0;max-width:0" data-skip-in-text="true">
              $subject
            </div>
            <table align="center" width="100%" border="0" cellpadding="0" cellspacing="0" role="presentation" style="max-width:480px;margin:0 auto;padding:20px 0 48px">
              <tbody>
                <tr style="width:100%">
                  <td>
                    <p style="font-size:24px;line-height:1.25;margin-top:16px;margin-bottom:16px">
                      <b>$subject</b>
                    </p>
                    <table align="center" width="100%" border="0" cellpadding="0" cellspacing="0" role="presentation" style="padding:24px;border:solid 1px #dedede;border-radius:5px;text-align:center">
                      <tbody>
                        <tr>
                          <td>
                            <p style="font-size:14px;line-height:24px;margin:0 0 10px 0;text-align:left;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0">
                              OTP: <strong>$otp</strong>
                            </p>
                            <p
                              style="font-size:14px;line-height:24px;margin:0 0 10px 0;text-align:left;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0">
                              가입 인증을 위해 OTP를 입력해주세요. OTP는 3분 동안 유효합니다.
                            </p>
                            <a
                              style="line-height:1.5;text-decoration:none;display:inline-block;max-width:100%;mso-padding-alt:0px;font-size:14px;background-color:#3a97fa;color:#fff;border-radius:0.5em;padding:12px 24px;padding-top:12px;padding-right:24px;padding-bottom:12px;padding-left:24px"
                              href="${ClientConfig.HOST}/join/confirm?email=$to&otp=$otp"
                              target="_blank"
                              ><span
                                ><!--[if mso]><i style="mso-font-width:400%;mso-text-raise:18" hidden>&#8202;&#8202;&#8202;</i><![endif]--></span
                              ><span
                                style="max-width:100%;display:inline-block;line-height:120%;mso-padding-alt:0px;mso-text-raise:9px"
                                >인증하기</span
                              ><span
                                ><!--[if mso]><i style="mso-font-width:400%" hidden>&#8202;&#8202;&#8202;&#8203;</i><![endif]--></span
                              ></a
                            >
                          </td>
                        </tr>
                      </tbody>
                    </table>
                    <p
                      style="font-size:14px;line-height:24px;text-align:center;margin-top:16px;margin-bottom:16px">
                      <a
                        href="${ClientConfig.HOST}/legal#terms"
                        target="_blank"
                        style="color:#0366d6;text-decoration-line:none;font-size:12px"
                        >이용약관</a
                      >
                      ・<!-- -->
                      <a
                        href="${ClientConfig.HOST}/support"
                        target="_blank"
                        style="color:#0366d6;text-decoration-line:none;font-size:12px"
                        >문의하기</a
                      >
                    </p>
                    <p
                      style="font-size:12px;line-height:24px;color:#6a737d;text-align:center;margin-top:60px;margin-bottom:16px">
                      LogHub
                    </p>
                  </td>
                </tr>
              </tbody>
            </table>
          </td>
        </tr>
      </tbody>
    </table>
    <!--/$-->
  </body>
</html>
""",
) : MailSendRequest