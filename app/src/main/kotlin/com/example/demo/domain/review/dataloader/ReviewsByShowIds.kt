package com.example.demo.domain.review.dataloader

import com.example.demo.config.threadpools.GqlThreadPools
import com.example.demo.domain.review.ReviewTable
import com.example.demo.domain.review.toReviewDto
import com.example.demo.generated.types.Review
import com.netflix.graphql.dgs.DgsDataLoader
import mu.KLogging
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@DgsDataLoader(name = "reviewsWithContext")
class ReviewsByShowIdsDataLoader(private val reviewsByShowIds: ReviewsByShowIds) :
    MappedBatchLoaderWithContext<UUID, List<Review>> {

    companion object : KLogging()

    override fun load(keys: Set<UUID>, environment: BatchLoaderEnvironment): CompletionStage<Map<UUID, List<Review>>> {
        logger.info { "START - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        val out = executeBlockingAsync(GqlThreadPools.IO) {
            logger.info { "executeBlockingAsync - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

            // blocking call to db
            reviewsByShowIds.handle(showIds = keys.toList())
                .groupBy { it.showId }
        }
        logger.info { "END - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }
        return out
    }

    // Note: the common forkjoin pool is used by default.
    // TBD: let's use a dedicated pool for blocking calls to our db
    // https://www.graphql-java.com/blog/threads/
    private fun <T>executeBlockingAsync(executorService: ExecutorService, block:()->T):CompletableFuture<T> {
        return CompletableFuture.supplyAsync( block, executorService)
    }

}

@Component
class ReviewsByShowIds {

    companion object : KLogging()

    /**
     * This is the method we want to call when loading reviews for multiple shows.
     * If this code was backed by a relational database, it would select reviews for all requested shows in a single SQL query.
     */
    @Transactional(readOnly = true)
    fun handle(showIds: List<UUID>): List<Review> {

        logger.info("reviewsForShows - Loading reviews for shows ${showIds.joinToString()}")

        logger.info { "reviewsForShows START - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        val table = ReviewTable

        val records = table.select {
            table.show_id inList showIds
        }.map(table::mapRowToRecord)


        logger.info { "reviewsForShows END - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        val dtos = records.map { it.toReviewDto() }

        return dtos
    }

}


