package com.example.demo.datafetcher

import com.example.demo.generated.types.Review
import com.example.demo.generated.types.Show
import com.example.demo.service.ReviewsService
import com.example.demo.service.ShowsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class ReviewsDataFetcher(private val reviewsService: ReviewsService) {
    /**
     * This datafetcher resolves the shows field on Query.
     * It uses an @InputArgument to get the titleFilter from the Query if one is defined.
     */
    @DgsQuery
    fun reviews(): List<Review> {
        val out = reviewsService.reviews()
        return out
    }
}
