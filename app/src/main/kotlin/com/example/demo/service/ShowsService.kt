package com.example.demo.service

import com.example.demo.db.ShowsTable
import com.example.demo.generated.types.AddShowInput
import com.example.demo.generated.types.Show
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ShowsService {
    private val shows:MutableList<Show> = mutableListOf(
        Show(id = 1, title = "Stranger Things", releaseYear = 2016, someUUID= UUID.randomUUID()),

        Show(id = 2, title = "Ozark", releaseYear = 2017),
        Show(id = 3, title = "The Crown", releaseYear = 2016),
        Show(id = 4, title = "Dead to Me", releaseYear = 2019),
        Show(id = 5, title = "Orange is the New Black", releaseYear = 2013)


    )

    @Transactional
    fun shows(): List<Show> {
        val records = ShowsTable.selectAll()
            .map { ShowsTable.mapRowToRecord(it) }



        return shows.toList()
    }

    fun addShow(input: AddShowInput):Show {
        val show = Show(
            id = (0..Int.MAX_VALUE).random(),
            title = input.title,
            releaseYear = input.releaseYear,
            someUUID = input.someUUID
        )
        shows.add(show)
        return show
    }
}
