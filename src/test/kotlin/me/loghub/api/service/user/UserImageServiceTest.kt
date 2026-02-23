package me.loghub.api.service.user

import me.loghub.api.dto.task.image.ImageUploadResponse
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.multipart.MultipartFile
import kotlin.test.assertEquals

class UserImageServiceTest {
    private lateinit var taskAPIProxy: TaskAPIProxy

    private lateinit var userImageService: UserImageService

    @BeforeEach
    fun setUp() {
        taskAPIProxy = mock()
        userImageService = UserImageService(taskAPIProxy)
    }

    @Test
    fun `should upload image and return uploaded path`() {
        val user = AuthFixtures.user(id = 1L)
        val file = mock<MultipartFile>()
        whenever(taskAPIProxy.uploadImage(file, 1L)).thenReturn(ImageUploadResponse("1/uploaded.webp"))

        val result = userImageService.uploadImage(file, user)

        assertEquals("1/uploaded.webp", result)
        verify(taskAPIProxy).uploadImage(file, 1L)
    }
}
