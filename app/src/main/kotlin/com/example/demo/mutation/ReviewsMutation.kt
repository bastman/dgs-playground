package com.example.demo.mutation

import com.example.demo.generated.types.AddReviewInput
import com.example.demo.generated.types.Review
import com.example.demo.service.ReviewsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.InputArgument


@DgsComponent
class ReviewsMutation(
    private val reviewsService: ReviewsService
) {

    @DgsData(parentType = "Mutation", field = "addReview") // field={{name_of_mutation}}
    fun addReview(@InputArgument("review") input: AddReviewInput): Review {
        val review:Review = reviewsService.addReview(reviewInput = input)
        return review
    }
}
