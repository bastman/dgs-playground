package com.example.demo.domain.review.dataloader

import com.example.demo.config.threadpools.GqlThreadPools
import com.example.demo.domain.review.ReviewTable
import com.example.demo.domain.review.toReviewDto
import com.example.demo.generated.types.Review
import com.example.demo.util.time.durationToNowInMillis
import com.netflix.graphql.dgs.DgsDataLoader
import mu.KLogging
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant
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
       // logger.info { "START - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        val startedAt = Instant.now()

        val out = executeBlockingAsync() {
           // logger.info { "executeBlockingAsync - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

            // blocking call to db
            val out = reviewsByShowIds.handle(showIds = keys.toList())
                .also {
                    logger.info { "DgsDataLoader 'reviewsByShowIds' duration (ms): ${startedAt.durationToNowInMillis()} items.count: ${it.size}" }
                }
                .groupBy { it.showId }
             //
            out
        }
       // logger.info { "END - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }
        return out
    }

    // Note: the common forkjoin pool is used by default.
    // TBD: let's use a dedicated pool for blocking calls to our db
    // https://www.graphql-java.com/blog/threads/
    private fun <T>executeBlockingAsync(block:()->T):CompletableFuture<T> {
        return CompletableFuture.supplyAsync( block,GqlThreadPools.IO)
        //return CompletableFuture.supplyAsync( block)
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
        val startedAt=Instant.now()
       // logger.info("reviewsForShows - Loading reviews for shows ${showIds.joinToString()}")
       // logger.info { "reviewsForShows START - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        val table = ReviewTable

        val sl = table
            .slice(table.review_id)
            .select {
                table.show_id inList showIds
            }.map {
                //it[table.review_id]
                "foo"
            }
        logger.info { "ReviewsByShowIds.handle(): ID's only duration (ms): ${startedAt.durationToNowInMillis()} items.count: ${sl.size}" }


        val x = table.select {
            table.show_id inList showIds
        }.count()
        logger.info { "ReviewsByShowIds.handle(): COUNT duration (ms): ${startedAt.durationToNowInMillis()} count: ${x}" }


        val records = table.select {
            table.show_id inList showIds
        }

            .map(table::mapRowToRecord)
        logger.info { "ReviewsByShowIds.handle(): records duration (ms): ${startedAt.durationToNowInMillis()} items.count: ${records.size}" }



       // logger.info { "reviewsForShows END - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        val dtos = records.map { it.toReviewDto() }

        return dtos.also {
            logger.info { "ReviewsByShowIds.handle(): END dtos duration (ms): ${startedAt.durationToNowInMillis()} items.count: ${it.size}" }

        }
    }

}


