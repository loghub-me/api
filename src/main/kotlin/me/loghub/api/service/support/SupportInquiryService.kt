package me.loghub.api.service.support

import me.loghub.api.dto.support.PostSupportInquiryDTO
import me.loghub.api.entity.support.SupportInquiry
import me.loghub.api.repository.support.SupportInquiryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SupportInquiryService(private val supportInquiryRepository: SupportInquiryRepository) {
    @Transactional
    fun postInquiry(requestBody: PostSupportInquiryDTO): SupportInquiry {
        val inquiry = requestBody.toEntity()
        return supportInquiryRepository.save(inquiry)
    }
}