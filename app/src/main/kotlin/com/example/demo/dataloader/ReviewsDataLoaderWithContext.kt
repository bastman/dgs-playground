package com.example.demo.dataloader

import com.example.demo.generated.types.Review
import com.example.demo.service.ReviewsService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Supplier


@DgsDataLoader(name = "reviewsWithContext")
class ReviewsDataLoaderWithContext(private val reviewsService: ReviewsService) :
    MappedBatchLoaderWithContext<Int, List<Review>> {

    override fun load(keys: Set<Int>, environment: BatchLoaderEnvironment): CompletionStage<Map<Int, List<Review>>> {
        return CompletableFuture.supplyAsync {
            reviewsService.reviewsForShows(showIds = keys.toList())
                .groupBy { it.showId }
        }
    }

}
