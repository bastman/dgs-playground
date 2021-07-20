package com.example.demo.dataloader

import com.example.demo.generated.types.Review
import com.example.demo.service.ReviewsService
import com.netflix.graphql.dgs.DgsDataLoader
import mu.KLogging
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.math.log


@DgsDataLoader(name = "reviewsWithContext")
class ReviewsDataLoaderWithContext(private val reviewsService: ReviewsService) :
    MappedBatchLoaderWithContext<UUID, List<Review>> {

    companion object:KLogging()

    //@Transactional(readOnly = true)
    override fun load(keys: Set<UUID>, environment: BatchLoaderEnvironment): CompletionStage<Map<UUID, List<Review>>> {
        logger.info { "thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        return CompletableFuture.supplyAsync {
            reviewsService.reviewsForShows(showIds = keys.toList())
                .groupBy { it.showId }
        }
    }

}
