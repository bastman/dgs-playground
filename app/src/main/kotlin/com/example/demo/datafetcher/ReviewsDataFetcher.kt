package com.example.demo.datafetcher

import com.example.demo.dataloader.ReviewsDataLoaderWithContext
import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Review
import com.example.demo.generated.types.Show
import com.example.demo.service.ReviewsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import java.util.concurrent.CompletableFuture


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

    /*
    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = DgsConstants.SHOW.Reviews)
    fun reviewsForShows(dfe: DgsDataFetchingEnvironment): List<Review>? {
        val show = dfe.getSource<Show>()
        //Load the reviews from the pre-loaded localContext.
        val reviewsByShowId: Map<Int, List<Review>> ? = dfe.getLocalContext()
        val showId:Int = show.id
        val out = reviewsByShowId?.get(showId);
        return out
    }

     */

    /**
     * This datafetcher will be called to resolve the "reviews" field on a Show.
     * It's invoked for each individual Show, so if we would load 10 shows, this method gets called 10 times.
     * To avoid the N+1 problem this datafetcher uses a DataLoader.
     * Although the DataLoader is called for each individual show ID, it will batch up the actual loading to a single method call to the "load" method in the ReviewsDataLoader.
     * For this to work correctly, the datafetcher needs to return a CompletableFuture.
     */
// Show.reviews  (async)
    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = DgsConstants.SHOW.Reviews)
    fun reviews(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Review>>? {
        //Instead of loading a DataLoader by name, we can use the DgsDataFetchingEnvironment and pass in the DataLoader classname.
        val reviewsDataLoader = dfe.getDataLoader<Int, List<Review>>(
            ReviewsDataLoaderWithContext::class.java)

        //Because the reviews field is on Show, the getSource() method will return the Show instance.
        val show = dfe.getSource<Show>()

        //Load the reviews from the DataLoader. This call is async and will be batched by the DataLoader mechanism.
        val out = reviewsDataLoader.load(show.id)
        return out
    }



    /*
    // Show.reviews  (sync)
    // n+1 ???
    @DgsData(parentType = "Show", field = "reviews")
    fun reviews(dfe: DgsDataFetchingEnvironment): List<Review>? {
        val show = dfe.getSource<Show>()
        val out = reviewsService.reviewsForShows(showIds = listOf(show.id))
        return out
    }

     */
}
