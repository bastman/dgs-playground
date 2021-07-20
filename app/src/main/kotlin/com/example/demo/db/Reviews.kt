package com.example.demo.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime
import java.util.*

object ReviewsTable : Table("reviews") {
    val review_id: Column<UUID> = uuid("review_id")
    override val primaryKey: PrimaryKey = PrimaryKey(review_id, name = "xxx_pkey")

    val show_id: Column<UUID> = uuid("show_id")
        .references(ShowsTable.show_id)

    val submitted_at: Column<LocalDateTime> = datetime("submitted_at")

    val username: Column<String> = varchar("username", 255)
    val star_rating: Column<Int?> = integer("star_rating")
        .nullable()
    val comment: Column<String?> = varchar("comment", 1024)
        .nullable()

    fun mapRowToRecord(row: ResultRow): ReviewsRecord =
        ReviewsRecord(
            review_id = row[review_id],
            show_id = row[show_id],
            submitted_at = row[submitted_at],
            username = row[username],
            star_rating = row[star_rating],
            comment = row[comment],
        )


    fun insertRecord(record:ReviewsRecord):ReviewsRecord {
        this.insert {
            it[review_id]=record.review_id
            it[show_id]=record.show_id
            it[submitted_at]=record.submitted_at
            it[username]=record.username
            it[star_rating]=record.star_rating
            it[comment]=record.comment
        }
        return this.getRecordById(reviewId = record.review_id)
    }

    fun findRecordById(reviewId:UUID):ReviewsRecord? {
        return this.select { this@ReviewsTable.review_id eq reviewId}
            .limit(n=1, offset = 0)
            .map { mapRowToRecord(it) }
            .firstOrNull()
    }

    fun getRecordById(reviewId:UUID):ReviewsRecord = findRecordById(reviewId=reviewId)
        ?: error("Record not found (table: ${this.tableName} reviewId: $reviewId)")

}

data class ReviewsRecord(
    val review_id: UUID,
    val show_id: UUID,
    val submitted_at: LocalDateTime,
    val username: String,
    val star_rating: Int?,
    val comment: String?,
)
