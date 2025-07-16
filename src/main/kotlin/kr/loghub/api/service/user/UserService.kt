package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.article.ArticleSort
import kr.loghub.api.dto.series.SeriesDTO
import kr.loghub.api.dto.series.SeriesSort
import kr.loghub.api.dto.question.QuestionDTO
import kr.loghub.api.dto.question.QuestionFilter
import kr.loghub.api.dto.question.QuestionSort
import kr.loghub.api.mapper.article.ArticleMapper
import kr.loghub.api.mapper.series.SeriesMapper
import kr.loghub.api.mapper.question.QuestionMapper
import kr.loghub.api.mapper.user.UserMapper
import kr.loghub.api.repository.article.ArticleCustomRepository
import kr.loghub.api.repository.series.SeriesCustomRepository
import kr.loghub.api.repository.question.QuestionCustomRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.util.checkField
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val articleCustomRepository: ArticleCustomRepository,
    private val seriesCustomRepository: SeriesCustomRepository,
    private val questionCustomRepository: QuestionCustomRepository,
) {
    companion object {
        private const val PAGE_SIZE = 20
    }

    fun getUser(username: String) = userRepository.findByUsername(username)
        ?.let { UserMapper.mapDetail(it) }
        ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)

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