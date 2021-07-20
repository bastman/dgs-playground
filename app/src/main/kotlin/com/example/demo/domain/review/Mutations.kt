package com.example.demo.domain.review

import com.example.demo.domain.review.mutation.AddReviewMutation
import com.example.demo.generated.DgsConstants
import com.example.demo.generated.types.AddReviewInput
import com.example.demo.generated.types.Review
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument
import org.springframework.transaction.annotation.Transactional


@DgsComponent
class ReviewMutations(
    private val addReview: AddReviewMutation,
) {

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME,
        field = DgsConstants.MUTATION.AddReview) // field={{name_of_mutation}}
    fun addReview(@InputArgument("input") input: AddReviewInput): Review {
        return addReview.handle(input)
    }
}
