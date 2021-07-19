package com.example.demo.datafetcher

import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.Review
import com.example.demo.generated.types.Show
import com.example.demo.service.ShowsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class ShowsDataFetcher(private val showsService: ShowsService) {
    /**
     * This datafetcher resolves the shows field on Query.
     * It uses an @InputArgument to get the titleFilter from the Query if one is defined.
     */
    @DgsQuery(field = DgsConstants.QUERY.Shows)
    fun shows(@InputArgument titleFilter : String?): List<Show> {
        return if(titleFilter != null) {
            showsService.shows().filter { it.title.contains(titleFilter) }
        } else {
            showsService.shows()
        }
    }


}
