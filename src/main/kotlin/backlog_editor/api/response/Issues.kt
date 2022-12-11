package backlog_editor.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class Issues(
    val id: Number,
    val projectId: Number,
    val issueKey: String,
    val keyId: Number,
    val issueType: IssueType,
    val summary: String,
    val description: String,
    val priority: Priority,
    val status: Status,
    val assignee: Assignee,
    val estimatedHours: Number,
    val actualHours: Number,
    val category: List<Category>
) {
    data class Category(
        val id: Number,
        val name: String,
        val displayOrder: Number
    )

    data class Assignee(
        val id: Number,
        val userId: String,
        val name: String,
        val roleType: Number,
        val lang: String,
        val mailAddress: String,
        val lastLoginTime: LocalDate
    )

    data class Status(
        val id: Number,
        val projectId: Number,
        val name: String,
        val color: String,
        val displayOrder: Number
    )

    data class IssueType(
        val id: Number,
        val projectId: Number,
        val name: String,
        val color: String,
        val displayOrder: Number
    )

    data class Priority(
        val id: Number,
        val name: String
    )

}