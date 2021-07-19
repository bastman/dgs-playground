package com.example.demo.db

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

object ShowsTable : Table("shows") {
    val show_id = uuid("show_id")
    override val primaryKey: PrimaryKey = PrimaryKey(show_id, name = "xxx_pkey")

    val title = text("title")
    val release_year = integer("release_year").nullable()

    fun insertShow(record:ShowsRecord) {
        this.insert {
            it[show_id]=record.show_id
            it[title]=record.title
            it[release_year]=record.release_year
        }
    }

    fun findOne(showId:UUID):ShowsRecord? {
        return this.select { show_id eq showId}
            .limit(n=1, offset = 0)
            .map { mapRowToRecord(it) }
            .firstOrNull()
    }

    fun getOne(showId:UUID):ShowsRecord = findOne(showId=showId)
        ?: error("show not found (showId: $showId")


    fun mapRowToRecord(row:ResultRow):ShowsRecord =
        ShowsRecord(
            show_id=row[show_id],
            title=row[title],
            release_year=row[release_year],
        )

}

data class ShowsRecord(
    val show_id: UUID,
    val title: String,
    val release_year: Int?
)
