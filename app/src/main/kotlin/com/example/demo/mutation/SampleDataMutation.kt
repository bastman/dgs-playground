package com.example.demo.mutation

import com.example.demo.db.*
import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.AddShowInput
import com.example.demo.generated.types.Show
import com.example.demo.service.ReviewsService
import com.example.demo.service.ShowsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*

@DgsComponent
class SampleDataMutation(
    private val showsService: ShowsService,
    private val reviewsService: ReviewsService
) {

    @DgsData(parentType = "Mutation", field = DgsConstants.MUTATION.AddSampleData) // field={{name_of_mutation}}
    @Transactional(readOnly = false)
    fun addSampleData(): List<Show> {
        val showTable = ShowsTable
        val showRecords = (0..3).map {
            ShowsRecord(
                show_id = UUID.randomUUID(),
                title = "title-${UUID.randomUUID()}",
                release_year = (1900..2021).random()
            ).let(showTable::insertRecord)
        }

        val reviewRecords = showRecords.map {
            val showId=it.show_id
            ReviewsRecord(
                review_id = UUID.randomUUID(),
                submitted_at = (Instant.now()-Duration.ofSeconds((0L..100_000L).random()))
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime(),
                show_id = showId,
                username = "username-${UUID.randomUUID()}",
                comment = "comment-${UUID.randomUUID()}",
                star_rating = (0..5).random()
            ).let(ReviewsTable::insertRecord)
        }

        val dtos = showRecords.map { it.toShowDto() }

        return dtos
    }
}
