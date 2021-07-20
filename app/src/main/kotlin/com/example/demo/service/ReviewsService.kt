package com.example.demo.service

import com.example.demo.db.ReviewsRecord
import com.example.demo.db.ReviewsTable
import com.example.demo.db.toReviewDto
import com.example.demo.generated.types.AddReviewInput
import com.example.demo.generated.types.Review
import mu.KLogging
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneId
import java.util.*

/**
 * This service emulates a data store.
 * For convenience in the demo we just generate Reviews in memory, but imagine this would be backed by for example a database.
 * If this was indeed backed by a database, it would be very important to avoid the N+1 problem, which means we need to use a DataLoader to call this class.
 */
@Service

class ReviewsService(private val showsService: ShowsService) {
    companion object : KLogging()

    //private val reviews = mutableListOf<Review>()

    /**
     * Hopefully nobody calls this for multiple shows within a single query, that would indicate the N+1 problem!
     */
    /*
    fun reviewsForShow(showId: Int): List<Review>? {
        return reviews[showId]
    }

     */
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
    @Transactional(readOnly = true) // works
    fun reviewsForShows(showIds: List<UUID>): List<Review> {
        logger.info("Loading reviews for shows ${showIds.joinToString()}")
        logger.info { "thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        val table = ReviewsTable
        val records = table.select {
            table.show_id inList showIds
        }.map(table::mapRowToRecord)
        val dtos = records.map { it.toReviewDto() }

        return dtos
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
