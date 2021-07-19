package com.example.demo.service

import com.example.demo.generated.types.AddReviewInput
import com.example.demo.generated.types.Review
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

/**
 * This service emulates a data store.
 * For convenience in the demo we just generate Reviews in memory, but imagine this would be backed by for example a database.
 * If this was indeed backed by a database, it would be very important to avoid the N+1 problem, which means we need to use a DataLoader to call this class.
 */
@Service
class ReviewsService(private val showsService: ShowsService) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val reviews = mutableListOf<Review>()

    /**
     * Hopefully nobody calls this for multiple shows within a single query, that would indicate the N+1 problem!
     */
    /*
    fun reviewsForShow(showId: Int): List<Review>? {
        return reviews[showId]
    }

     */

    fun reviews():List<Review> {
        val out = reviews.toList()
        return out
    }

    /**
     * This is the method we want to call when loading reviews for multiple shows.
     * If this code was backed by a relational database, it would select reviews for all requested shows in a single SQL query.
     */
    fun reviewsForShows(showIds: List<Int>): List<Review> {
        logger.info("Loading reviews for shows ${showIds.joinToString()}")

        val out:List<Review> = reviews.filter {
            it.showId in showIds
        }

        return out
    }


    fun addReview(reviewInput: AddReviewInput):Review {
        val review = Review(
            id = (0..Int.MAX_VALUE).random(),
            showId= reviewInput.showId,
            username = reviewInput.username,
            starScore = reviewInput.starScore,
            submittedDate = OffsetDateTime.now()
        )

        reviews.add(review)

        logger.info("Review added {}", review)

        return review
    }



}
