package com.example.demo.domain.show.mutation

import com.example.demo.domain.review.ReviewRecord
import com.example.demo.domain.review.ReviewTable
import com.example.demo.domain.review.toReviewDto
import com.example.demo.domain.show.ShowTable
import com.example.demo.domain.show.ShowRecord
import com.example.demo.domain.show.toShowDto
import com.example.demo.generated.types.Show
import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*

@Component
class AddSampleShowsMutation() {
    companion object : KLogging()

    @Transactional(readOnly = false)
    fun handle(): List<Show> {
        val showTable = ShowTable
        val showRecords = (0..3).map {
            ShowRecord(
                show_id = UUID.randomUUID(),
                title = "title-${UUID.randomUUID()}",
                release_year = (1900..2021).random()
            ).let(showTable::insertRecord)
        }

        val fromDb = showRecords.map { showRecord ->
            val showId = showRecord.show_id

            val reviewRecords = (1..4).map {
                ReviewRecord(
                    review_id = UUID.randomUUID(),
                    submitted_at = (Instant.now() - Duration.ofSeconds((0L..100_000L).random()))
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDateTime(),
                    show_id = showId,
                    username = "username-${UUID.randomUUID()}",
                    comment = "comment-${UUID.randomUUID()}",
                    star_rating = (0..5).random()
                ).let(ReviewTable::insertRecord)
            }
            Pair(showRecord, reviewRecords)
        }

        val dtos = fromDb.map {
            val showRecord = it.first
            val reviewRecords = it.second
            val reviewDtos = reviewRecords.map { it.toReviewDto() }
            showRecord.toShowDto()
                .copy(reviews = reviewDtos)
        }

        return dtos
    }

}
