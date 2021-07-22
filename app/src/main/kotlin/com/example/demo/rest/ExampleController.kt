package com.example.demo.rest

import com.example.demo.domain.review.ReviewTable
import com.example.demo.domain.review.toReviewDto
import com.example.demo.domain.show.ShowTable
import com.example.demo.domain.show.toShowDto
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExampleController {


    @GetMapping("/api/foo")
    fun foo(): Any? {
        // the rest api is faster under load

        // datasource: (reviews: 460, shows: 116)
        // api requests: -n 1000, -c 30
        // gql: 1600 ms (p95)
        // rest single query, single transaction: 700 ms (p95)
        // rest 2 queries, 1 transactions: 800 ms
        // rest 2 queries, 2 transactions: 950 ms


        return fooTwoQueriesTwoTransactions()
    }

    private fun fooSingleQuery(): Any? {
        val src = transaction {
            ReviewTable.innerJoin(ShowTable)
                .select { Op.TRUE }
                .map {
                    Pair(ShowTable.mapRowToRecord(it), ReviewTable.mapRowToRecord(it))
                }
        }

        val showRecords = src.map { it.first }
        val reviewDtosByShowId = src
            .map { it.second }
            .map { it.toReviewDto() }
            .groupBy { it.showId }
        val showDtos = showRecords.map {
            it.toShowDto()
                .copy(reviews = reviewDtosByShowId.get(it.show_id))
        }
        val out = mapOf(
            "data" to showDtos
        )

        return out

    }

    private fun fooTwoQueriesTwoTransactions(): Any? {

        val showRecords = transaction {
            ShowTable.select { Op.TRUE }
                .map { ShowTable.mapRowToRecord(it) }
        }

        val showIds = showRecords.map { it.show_id }.distinct()


        val reviewRecords = transaction {
            ReviewTable.select { ReviewTable.show_id inList showIds }
                .map { ReviewTable.mapRowToRecord(it) }
        }

        val reviewDtosByShowId = reviewRecords.map { it.toReviewDto() }
            .groupBy { it.showId }
        val showDtos = showRecords.map {
            it.toShowDto()
                .copy(reviews = reviewDtosByShowId.get(it.show_id))
        }
        val out = mapOf(
            "data" to showDtos
        )

        return out
    }

    private fun fooTwoQueriesOneTransaction(): Any? {

        val src = transaction {
            val showRecords =
                ShowTable.select { Op.TRUE }
                    .map { ShowTable.mapRowToRecord(it) }


            val showIds = showRecords.map { it.show_id }.distinct()

            val reviewRecords =
                ReviewTable.select { ReviewTable.show_id inList showIds }
                    .map { ReviewTable.mapRowToRecord(it) }
            Pair(showRecords, reviewRecords)
        }

        val showRecords = src.first
        val reviewRecords = src.second

        val reviewDtosByShowId = reviewRecords.map { it.toReviewDto() }
            .groupBy { it.showId }
        val showDtos = showRecords.map {
            it.toShowDto()
                .copy(reviews = reviewDtosByShowId.get(it.show_id))
        }
        val out = mapOf(
            "data" to showDtos
        )

        return out
    }
}
