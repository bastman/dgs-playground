package com.example.demo.domain.review

import com.example.demo.domain.review.dataloader.ReviewsByShowIdsDataLoader
import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Review
import com.example.demo.generated.types.Show
import com.example.demo.util.time.durationToNowInMillis
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import mu.KLogging
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture


@DgsComponent
class ReviewDataFetcher {
    companion object : KLogging()

    /**
     * This datafetcher will be called to resolve the "reviews" field on a Show.
     * It's invoked for each individual Show, so if we would load 10 shows, this method gets called 10 times.
     * To avoid the N+1 problem this datafetcher uses a DataLoader.
     * Although the DataLoader is called for each individual show ID, it will batch up the actual loading to a single method call to the "load" method in the ReviewsDataLoader.
     * For this to work correctly, the datafetcher needs to return a CompletableFuture.
     */


    // Show.reviews  (async)
    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = DgsConstants.SHOW.Reviews)
    fun reviewsByShow(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Review>>? {
        val startedAt = Instant.now()
      //  logger.info { "reviewsByShow START ASYNC - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        //Instead of loading a DataLoader by name, we can use the DgsDataFetchingEnvironment and pass in the DataLoader classname.
        val reviewsDataLoader = dfe.getDataLoader<UUID, List<Review>>(
            ReviewsByShowIdsDataLoader::class.java)

        //Because the reviews field is on Show, the getSource() method will return the Show instance.
        val show = dfe.getSource<Show>()

        if(show.reviews !=null) {
            return CompletableFuture.supplyAsync { show.reviews }
        }

        //Load the reviews from the DataLoader. This call is async and will be batched by the DataLoader mechanism.
        val out = reviewsDataLoader.load(show.showId)

        //  logger.info { "reviewsByShow END ASYNC - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        return out
    }


    /*
    // Show.reviews  (sync)
    // n+1 ???
    @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME, field = DgsConstants.SHOW.Reviews)
    fun reviewsNPlus1(dfe: DgsDataFetchingEnvironment): List<Review>? {
        val show = dfe.getSource<Show>()
        val out = reviewsService.reviewsForShowNPlus1(show.showId)
        return out
    }

     */


}
