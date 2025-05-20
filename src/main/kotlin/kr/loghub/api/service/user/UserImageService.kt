package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.entity.user.User
import kr.loghub.api.util.checkField
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.IOException
import java.nio.file.Paths
import javax.imageio.ImageIO

@Service
class UserImageService(
    private val r2Client: S3Client,
    @Value("\${r2.bucket}") val bucket: String
) {
    fun uploadFile(file: MultipartFile, uploader: User): String {
        checkField("file", isImageFile(file)) {
            ResponseMessage.Image.INVALID_IMAGE
        }

        val key = createKey(file, uploader.username);
        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.contentType)
            .build()
        r2Client.putObject(request, RequestBody.fromBytes(file.bytes))
        return key;
    }

    private fun isImageFile(file: MultipartFile): Boolean {
        val contentType = file.contentType ?: return false
        if (!contentType.startsWith("image/")) {
            return false
        }

        try {
            file.inputStream.use { input ->
                val image = ImageIO.read(input) ?: return false
                if (image.width <= 0 || image.height <= 0) {
                    return false
                }
            }
        } catch (_: IOException) {
            return false
        }

        return true
    }

    private fun createKey(file: MultipartFile, username: String): String {
        val original = file.originalFilename ?: "unnamed"
        val sanitized = Paths.get(original).fileName.toString()
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
        return "$username/${System.currentTimeMillis()}_$sanitized"
    }
}