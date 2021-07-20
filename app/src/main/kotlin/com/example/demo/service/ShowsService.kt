package com.example.demo.service

import com.example.demo.db.ShowsRecord
import com.example.demo.db.ShowsTable
import com.example.demo.db.toShowDto
import com.example.demo.generated.types.AddShowInput
import com.example.demo.generated.types.Show
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShowsService {
    companion object : KLogging()
    /*
    private val shows:MutableList<Show> = mutableListOf(
        Show(id = 1, title = "Stranger Things", releaseYear = 2016, someUUID= UUID.randomUUID()),

        Show(id = 2, title = "Ozark", releaseYear = 2017),
        Show(id = 3, title = "The Crown", releaseYear = 2016),
        Show(id = 4, title = "Dead to Me", releaseYear = 2019),
        Show(id = 5, title = "Orange is the New Black", releaseYear = 2013)


    )

     */


    fun addShow(input: AddShowInput): Show {
        val table = ShowsTable
        val recordNew = ShowsRecord(
            show_id = UUID.randomUUID(),
            title = input.title,
            release_year = input.releaseYear
        )
        val recordInserted = table.insertRecord(recordNew)
        logger.info { "inserted record: $recordInserted " }
        val dto = recordInserted.toShowDto()
        return dto
    }
}
