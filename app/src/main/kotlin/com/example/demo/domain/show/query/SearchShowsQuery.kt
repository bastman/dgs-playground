package com.example.demo.domain.show.query

import com.example.demo.domain.show.ShowTable
import com.example.demo.domain.show.toShowDto
import com.example.demo.generated.types.Show
import mu.KLogging
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class SearchShowsQuery {
    companion object : KLogging()

    @Transactional(readOnly = true)
    fun handle(titleFilter: String?): List<Show> {
        val table = ShowTable
     //   logger.info { "thread: ${Thread.currentThread().name} - tx: ${TransactionManager.currentOrNull()} active: ${TransactionSynchronizationManager.isActualTransactionActive()}" }

        val records = table.select {
            when (titleFilter) {
                null -> Op.TRUE
                else -> table.title.lowerCase().like("%${titleFilter.lowercase()}%")
            }
        }.map(table::mapRowToRecord)

        val dtos = records.map { it.toShowDto() }
        return dtos
    }
}
