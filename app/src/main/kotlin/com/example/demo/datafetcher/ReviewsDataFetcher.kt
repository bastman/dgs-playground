package com.example.demo.datafetcher

import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Review
import com.example.demo.generated.types.Show
import com.example.demo.service.ReviewsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery


@DgsComponent
class ReviewsDataFetcher(private val reviewsService: ReviewsService) {
    /**
     * This datafetcher resolves the shows field on Query.
     * It uses an @InputArgument to get the titleFilter from the Query if one is defined.
     */
    @DgsQuery(field = DgsConstants.QUERY.Reviews)
    fun reviews(): List<Review> {
        val out = reviewsService.reviews()
        return out
    }

    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = DgsConstants.SHOW.Reviews)
    fun reviewsForShows(dfe: DgsDataFetchingEnvironment): List<Review>? {
        val show = dfe.getSource<Show>()
        //Load the reviews from the pre-loaded localContext.
        val reviewsForShows: Map<Int, List<Review>> = dfe.getLocalContext()
        val out = reviewsForShows.get(show.id);
        return out
    }
}
