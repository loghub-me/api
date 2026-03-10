package me.loghub.api.dto.notification

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.util.checkField

data class LastEventId(val value: Long) {
    object Header {
        const val NAME = "Last-Event-ID"

        fun from(str: String): LastEventId {
            val value = str.toLongOrNull()
            checkField(Header.NAME, value != null) { ResponseMessage.Default.INVALID_FIELD }
            return LastEventId(value)
        }
    }
}