package com.example.demo.domain.review.mutation

import com.example.demo.domain.review.ReviewRecord
import com.example.demo.domain.review.ReviewTable
import com.example.demo.domain.review.toReviewDto
import com.example.demo.generated.types.AddReviewInput
import com.example.demo.generated.types.Review
import mu.KLogging
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant
import java.time.ZoneId
import java.util.*

@Component
class AddReviewMutation {
    companion object : KLogging()

    @Transactional(readOnly = false)
    fun handle(input: AddReviewInput): Review {
        logger.info { "addReview START - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        val table = ReviewTable
        val recordNew = ReviewRecord(
            review_id = UUID.randomUUID(),
            show_id = input.showId,
            submitted_at = Instant.now().atZone(ZoneId.of("UTC")).toLocalDateTime(),
            username = input.username,
            star_rating = input.starScore,
            comment = input.comment
        )
        val recordInserted = table.insertRecord(recordNew)
        val dto = recordInserted.toReviewDto()
        logger.info("record added: $recordInserted")

        logger.info { "addReview END - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        return dto
    }

}
