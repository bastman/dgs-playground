package com.example.demo.domain.review

import com.example.demo.domain.show.ShowTable
import com.example.demo.generated.types.Review
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object ReviewTable : Table("review") {
    val review_id: Column<UUID> = uuid("review_id")
    override val primaryKey: PrimaryKey = PrimaryKey(review_id, name = "xxx_pkey")

    val show_id: Column<UUID> = uuid("show_id")
        .references(ShowTable.show_id)

    val submitted_at: Column<LocalDateTime> = datetime("submitted_at")

    val username: Column<String> = varchar("username", 255)
    val star_rating: Column<Int?> = integer("star_rating")
        .nullable()
    val comment: Column<String?> = varchar("comment", 1024)
        .nullable()

    fun mapRowToRecord(row: ResultRow): ReviewRecord =
        ReviewRecord(
            review_id = row[review_id],
            show_id = row[show_id],
            submitted_at = row[submitted_at],
            username = row[username],
            star_rating = row[star_rating],
            comment = row[comment],
        )


    fun insertRecord(record: ReviewRecord): ReviewRecord {
        this.insert {
            it[review_id] = record.review_id
            it[show_id] = record.show_id
            it[submitted_at] = record.submitted_at
            it[username] = record.username
            it[star_rating] = record.star_rating
            it[comment] = record.comment
        }
        return getRecordById(reviewId = record.review_id)
    }

    fun findRecordById(reviewId: UUID): ReviewRecord? {
        return this.select { review_id eq reviewId }
            .limit(n = 1, offset = 0)
            .map { mapRowToRecord(it) }
            .firstOrNull()
    }

    fun getRecordById(reviewId: UUID): ReviewRecord = findRecordById(reviewId = reviewId)
        ?: error("Record not found (table: ${this.tableName} reviewId: $reviewId)")

}

data class ReviewRecord(
    val review_id: UUID,
    val show_id: UUID,
    val submitted_at: LocalDateTime,
    val username: String,
    val star_rating: Int?,
    val comment: String?,
)


fun ReviewRecord.toReviewDto(): Review = Review(
    reviewId = review_id,
    showId = show_id,
    username = username,
    starScore = star_rating,
    submittedDate = submitted_at.atZone(ZoneId.of("UTC")).toOffsetDateTime(),
    comment = comment
)

