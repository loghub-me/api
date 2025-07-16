package kr.loghub.api.service.user

import kr.loghub.api.entity.user.User
import kr.loghub.api.proxy.TaskAPIProxy
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserImageService(private val taskAPIProxy: TaskAPIProxy) {
    fun uploadImage(file: MultipartFile, user: User): String {
        val response = taskAPIProxy.uploadImage(file, user.id!!)
        return response.path
    }
}