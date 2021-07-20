package com.example.demo.domain.show

import com.example.demo.generated.types.Show
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

object ShowTable : Table("show") {

    val show_id = uuid("show_id")
    override val primaryKey: PrimaryKey = PrimaryKey(show_id, name = "xxx_pkey")

    val title = text("title")
    val release_year = integer("release_year").nullable()

    fun insertRecord(record: ShowRecord): ShowRecord {
        this.insert {
            it[show_id] = record.show_id
            it[title] = record.title
            it[release_year] = record.release_year
        }
        return getRecordById(showId = record.show_id)
    }

    fun findRecordById(showId: UUID): ShowRecord? {
        return this.select { show_id eq showId }
            .limit(n = 1, offset = 0)
            .map { mapRowToRecord(it) }
            .firstOrNull()
    }

    fun getRecordById(showId: UUID): ShowRecord = findRecordById(showId = showId)
        ?: error("Record not found (table: ${this.tableName} showId: $showId)")


    fun mapRowToRecord(row: ResultRow): ShowRecord =
        ShowRecord(
            show_id = row[show_id],
            title = row[title],
            release_year = row[release_year],
        )

}

data class ShowRecord(
    val show_id: UUID,
    val title: String,
    val release_year: Int?,
)

fun ShowRecord.toShowDto(): Show = Show(
    showId = show_id,
    title = title,
    releaseYear = release_year,
    someUUID = null
)
