package com.example.demo.service

import com.example.demo.db.ReviewsRecord
import com.example.demo.db.ReviewsTable
import com.example.demo.db.toReviewDto
import com.example.demo.generated.types.AddReviewInput
import com.example.demo.generated.types.Review
import com.example.demo.util.spring.transaction.SpringTransactionTemplate
import com.example.demo.util.spring.transaction.inTransaction
import com.example.demo.util.spring.transaction.springTransactionTemplate
import mu.KLogging
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant
import java.time.ZoneId
import java.util.*

/**
 * This service emulates a data store.
 * For convenience in the demo we just generate Reviews in memory, but imagine this would be backed by for example a database.
 * If this was indeed backed by a database, it would be very important to avoid the N+1 problem, which means we need to use a DataLoader to call this class.
 */
@Service

class ReviewsService(
    private val showsService: ShowsService,

    private val stm: SpringTransactionManager
    ) {
    companion object : KLogging()

    //private val reviews = mutableListOf<Review>()




    @Transactional(readOnly = true)
    fun allReviews(): List<Review> {
        logger.info("Loading all reviews")
        logger.info { "thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }
        //val out = reviews.toList()
        val table = ReviewsTable
        val records: List<ReviewsRecord> = table.selectAll()
            .map(table::mapRowToRecord)

        //val out = reviewsService.reviews()
        val out = records.map { it.toReviewDto() }
        return out
    }


    /**
     * This is the method we want to call when loading reviews for multiple shows.
     * If this code was backed by a relational database, it would select reviews for all requested shows in a single SQL query.
     */
    //@Transactional(readOnly = true) // works
    fun reviewsForShows(showIds: List<UUID>): List<Review> {
        logger.info("reviewsForShows - Loading reviews for shows ${showIds.joinToString()}")
        logger.info { "reviewsForShows START - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        val table = ReviewsTable


        val records =inTransaction(stm= stm) {
            logger.info { "reviewsForShows IN TRANS - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

            table.select {
                table.show_id inList showIds
            }.map(table::mapRowToRecord)
        }
/*
        val records = transaction {
            logger.info { "reviewsForShows - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

            table.select {
                table.show_id inList showIds
            }.map(table::mapRowToRecord)
        }

 */
        logger.info { "reviewsForShows END - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        val dtos = records.map { it.toReviewDto() }

        return dtos
    }

    /**
     * Hopefully nobody calls this for multiple shows within a single query, that would indicate the N+1 problem!
     */

    @Transactional(readOnly = true)
    fun reviewsForShowNPlus1(showId: UUID): List<Review>? {
        logger.info("(n+1) Loading reviews for show ${showId}")
        logger.info { "thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        val table = ReviewsTable
        val dto = table.select {
            table.show_id.eq(showId)
        }.map(table::mapRowToRecord)
            .firstOrNull()
            ?.toReviewDto()
        logger.info { "thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        return when(dto) {
            null-> null
            else-> listOf(dto)
        }
    }


    fun addReview(input: AddReviewInput): Review {
        val table = ReviewsTable
        val recordNew = ReviewsRecord(
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

        return dto
    }


}
