package com.example.demo.dataloader

import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Review
import com.example.demo.service.ReviewsService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/*
@DgsDataLoader(name = DgsConstants.SHOW.Reviews)
class ReviewsDataLoader(val reviewsService: ReviewsService): MappedBatchLoader<Int, List<Review>> {
    /**
     * This method will be called once, even if multiple datafetchers use the load() method on the DataLoader.
     * This way reviews can be loaded for all the Shows in a single call instead of per individual Show.
     */
    override fun load(keys: Set<Int>): CompletionStage<Map<Int, List<Review>>> {
        return CompletableFuture.supplyAsync {
            reviewsService.reviewsForShows(showIds = keys.toList())
                .groupBy { it.showId }
        }
    }

}

 */
