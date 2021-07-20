package com.example.demo.domain.show.mutation

import com.example.demo.domain.show.ShowTable
import com.example.demo.domain.show.ShowsRecord
import com.example.demo.domain.show.toShowDto
import com.example.demo.generated.types.AddShowInput
import com.example.demo.generated.types.Show
import mu.KLogging
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.*

@Component
class AddShowMutation {
    companion object : KLogging()

    @Transactional(readOnly = false)
    fun handle(input: AddShowInput): Show {
        logger.info { "addShow START - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        val table = ShowTable
        val recordNew = ShowsRecord(
            show_id = UUID.randomUUID(),
            title = input.title,
            release_year = input.releaseYear
        )
        val recordInserted = table.insertRecord(recordNew)
        logger.info { "inserted record: $recordInserted " }
        val dto: Show = recordInserted.toShowDto()

        logger.info { "addShow END - thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        return dto
    }
}
