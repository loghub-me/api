package kr.loghub.api.entity.common

import jakarta.persistence.Embeddable

@Embeddable
class RowMetadata {
    @Transient
    val tableoid: String = ""

    @Transient
    val ctid: String = ""
}