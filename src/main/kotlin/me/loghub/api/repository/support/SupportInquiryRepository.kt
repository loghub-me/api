package me.loghub.api.repository.support

import me.loghub.api.entity.support.SupportInquiry
import org.springframework.data.jpa.repository.JpaRepository

interface SupportInquiryRepository : JpaRepository<SupportInquiry, Long>