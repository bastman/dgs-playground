package com.example.demo.datafetcher

import com.example.demo.db.ShowsTable
import com.example.demo.db.toShowDto
import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Show
import com.example.demo.service.ShowsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import mu.KLogging
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@DgsComponent
class ShowsDataFetcher(private val showsService: ShowsService) {
    companion object:KLogging()
    /**
     * This datafetcher resolves the shows field on Query.
     * It uses an @InputArgument to get the titleFilter from the Query if one is defined.
     */
    @DgsQuery(field = DgsConstants.QUERY.Shows)
    fun shows(@InputArgument titleFilter: String?): List<Show> {
        val out = showsService.allShows()
        return out

    }


}
