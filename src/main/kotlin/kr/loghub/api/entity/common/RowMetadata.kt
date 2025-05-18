package kr.loghub.api.entity.common

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class RowMetadata {
    @Column(nullable = false)
    val tableoid: String = ""

    @Column(nullable = false)
    val ctid: String = ""
}