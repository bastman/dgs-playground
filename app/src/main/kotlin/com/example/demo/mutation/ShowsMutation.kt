package com.example.demo.mutation

import com.example.demo.generated.types.AddShowInput
import com.example.demo.generated.types.Show
import com.example.demo.service.ShowsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import org.springframework.transaction.annotation.Transactional

@DgsComponent
class ShowsMutation(
    private val showsService: ShowsService,
) {

    @DgsData(parentType = "Mutation", field = "addShow") // field={{name_of_mutation}}
    @Transactional(readOnly = false)
    fun addShow(@InputArgument("show") input: AddShowInput): Show {
        val show: Show = showsService.addShow(input = input)
        return show
    }
}
