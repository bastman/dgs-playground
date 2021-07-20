package com.example.demo.service

import com.example.demo.db.ShowsRecord
import com.example.demo.db.ShowsTable
import com.example.demo.db.toShowDto
import com.example.demo.generated.types.AddShowInput
import com.example.demo.generated.types.Show
import com.netflix.graphql.dgs.InputArgument
import mu.KLogging
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ShowsService {
    companion object : KLogging()


    @Transactional(readOnly = true)
    fun allShows(): List<Show> {

        val table = ShowsTable
        logger.info { "thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()}" }

        val records =

            table.selectAll()
                .map(table::mapRowToRecord)


        val dtos = records.map { it.toShowDto() }
        return dtos
    }


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
