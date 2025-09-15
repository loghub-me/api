package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.article.ArticleDTO
import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.question.QuestionFilter
import me.loghub.api.dto.question.QuestionSort
import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.article.ArticleMapper
import me.loghub.api.mapper.question.QuestionMapper
import me.loghub.api.mapper.series.SeriesMapper
import me.loghub.api.mapper.user.UserMapper
import me.loghub.api.repository.article.ArticleCustomRepository
import me.loghub.api.repository.question.QuestionCustomRepository
import me.loghub.api.repository.series.SeriesCustomRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.checkField
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val articleCustomRepository: ArticleCustomRepository,
    private val seriesCustomRepository: SeriesCustomRepository,
    private val questionCustomRepository: QuestionCustomRepository,
) {
    private companion object {
        private const val PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun getUser(username: String) = userRepository.findByUsername(username)
        ?.let { UserMapper.mapDetail(it) }
        ?: throw EntityNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional(readOnly = true)
    fun getUserProfile(username: String) = userRepository.findByUsername(username)
        ?.let { UserMapper.mapProfile(it) }
        ?: throw EntityNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional(readOnly = true)
    fun searchUserArticles(username: String, query: String, sort: ArticleSort, page: Int): Page<ArticleDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return articleCustomRepository.search(
            username = username,
            query = query.trim(),
            sort = sort,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(ArticleMapper::map)
    }

    @Transactional(readOnly = true)
    fun searchUserSeries(username: String, query: String, sort: SeriesSort, page: Int): Page<SeriesDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return seriesCustomRepository.search(
            username = username,
            query = query.trim(),
            sort = sort,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(SeriesMapper::map)
    }

    @Transactional(readOnly = true)
    fun searchUserQuestions(
        username: String,
        query: String,
        sort: QuestionSort,
        filter: QuestionFilter,
        page: Int
    ): Page<QuestionDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return questionCustomRepository.search(
            username = username,
            query = query.trim(),
            sort = sort,
            filter = filter,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(QuestionMapper::map)
    }
}