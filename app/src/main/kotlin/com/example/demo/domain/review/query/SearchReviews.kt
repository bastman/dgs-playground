package com.example.demo.domain.review.query

import com.example.demo.domain.review.ReviewRecord
import com.example.demo.domain.review.ReviewTable
import com.example.demo.domain.review.toReviewDto
import com.example.demo.generated.types.Review
import mu.KLogging
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class SearchReviews {
    companion object : KLogging()

    @Transactional(readOnly = true)
    fun handle(): List<Review> {
        //logger.info("Loading all reviews")
        //logger.info { "thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }
        val table = ReviewTable
        val records: List<ReviewRecord> = table.selectAll()
            .map(table::mapRowToRecord)

        val out = records.map { it.toReviewDto() }
        return out
    }


}
