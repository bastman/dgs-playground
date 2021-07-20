package com.example.demo.domain.review

import com.example.demo.domain.review.query.SearchReviews
import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Review
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import mu.KLogging

@DgsComponent
class ReviewQueries(
    private val searchReviews: SearchReviews,
) {
    companion object : KLogging()

    // Query.reviews
    @DgsQuery(field = DgsConstants.QUERY.Reviews)
    fun searchReviews(): List<Review> {
        val out = searchReviews.handle()
        return out
    }

}
