package com.example.demo.domain.show

import com.example.demo.domain.show.mutation.AddSampleShowsMutation
import com.example.demo.domain.show.mutation.AddShowMutation
import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.AddShowInput
import com.example.demo.generated.types.Show
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import mu.KLogging

@DgsComponent
class ShowMutations(
    private val addShow: AddShowMutation,
    private val addSampleShows: AddSampleShowsMutation,
) {
    companion object : KLogging()

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME,
        field = DgsConstants.MUTATION.AddShow) // field={{name_of_mutation}}
    fun addShow(@InputArgument("input") input: AddShowInput): Show {
        return addShow.handle(input)
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME,
        field = DgsConstants.MUTATION.AddSampleShows) // field={{name_of_mutation}}
    fun addSampleShows(): List<Show> {
        return addSampleShows.handle()
    }
}
