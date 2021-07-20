package com.example.demo.domain.show

import com.example.demo.domain.show.query.SearchShowsQuery
import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Show
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import mu.KLogging

@DgsComponent
class ShowQueries(
    private val searchShows: SearchShowsQuery,
) {
    companion object : KLogging()

    /**
     * This datafetcher resolves the shows field on Query.
     * It uses an @InputArgument to get the titleFilter from the Query if one is defined.
     */
    @DgsQuery(field = DgsConstants.QUERY.Shows)
    fun searchShows(@InputArgument titleFilter: String?): List<Show> {
        val out = searchShows.handle(titleFilter = titleFilter)
        return out
    }

}
