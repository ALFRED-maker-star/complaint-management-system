package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.PriorityUrgent
import com.example.ui.theme.StatusEscalatedBg
import com.example.ui.theme.StatusEscalatedBorder
import com.example.ui.theme.StatusEscalatedText
import com.example.ui.theme.StatusInProgressBg
import com.example.ui.theme.StatusInProgressBorder
import com.example.ui.theme.StatusInProgressText
import com.example.ui.theme.StatusNewBg
import com.example.ui.theme.StatusNewBorder
import com.example.ui.theme.StatusNewText
import com.example.ui.theme.StatusResolvedBg
import com.example.ui.theme.StatusResolvedBorder
import com.example.ui.theme.StatusResolvedText

enum class ComplaintStatus(
    val displayName: String,
    val bgColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val stepIndex: Int
) {
    NEW(
        displayName = "New",
        bgColor = StatusNewBg,
        textColor = StatusNewText,
        borderColor = StatusNewBorder,
        stepIndex = 0
    ),
    IN_PROGRESS(
        displayName = "In Progress",
        bgColor = StatusInProgressBg,
        textColor = StatusInProgressText,
        borderColor = StatusInProgressBorder,
        stepIndex = 1
    ),
    RESOLVED(
        displayName = "Resolved",
        bgColor = StatusResolvedBg,
        textColor = StatusResolvedText,
        borderColor = StatusResolvedBorder,
        stepIndex = 2
    ),
    ESCALATED(
        displayName = "Escalated",
        bgColor = StatusEscalatedBg,
        textColor = StatusEscalatedText,
        borderColor = StatusEscalatedBorder,
        stepIndex = 2
    )
}

enum class Priority(
    val displayName: String,
    val color: Color
) {
    LOW("Low", PriorityLow),
    MEDIUM("Medium", PriorityMedium),
    HIGH("High", PriorityHigh),
    URGENT("Urgent", PriorityUrgent);

    companion object {
        fun suggestPriority(subject: String, description: String): Priority {
            val text = "$subject $description".lowercase()
            return when {
                text.contains("urgent") || text.contains("emergency") || text.contains("leak") ||
                        text.contains("outage") || text.contains("breach") || text.contains("safety") -> URGENT
                text.contains("broken") || text.contains("cannot access") || text.contains("blocked") ||
                        text.contains("charged twice") || text.contains("overcharged") || text.contains("fraud") -> HIGH
                text.contains("delayed") || text.contains("slow") || text.contains("missing") ||
                        text.contains("refund") || text.contains("incorrect") -> MEDIUM
                else -> LOW
            }
        }
    }
}

enum class SenderRole {
    USER,
    STAFF,
    SYSTEM
}

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val slaHours: Int,
    val defaultPriority: Priority = Priority.MEDIUM,
    val isActive: Boolean = true
)

data class StaffMember(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val department: String,
    val avatarInitials: String
)

data class MessageComment(
    val id: String,
    val complaintId: String,
    val senderName: String,
    val senderRole: SenderRole,
    val isInternal: Boolean,
    val content: String,
    val formattedTime: String
)

data class FeedbackRating(
    val complaintId: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val submittedAt: String
)

data class TimelineStep(
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val isDone: Boolean,
    val isCurrent: Boolean
)

data class Complaint(
    val id: String,
    val subject: String,
    val description: String,
    val categoryId: String,
    val categoryName: String,
    val priority: Priority,
    val status: ComplaintStatus,
    val submittedByName: String,
    val submittedByEmail: String,
    val assignedStaff: StaffMember?,
    val createdAt: String,
    val updatedAt: String,
    val attachmentNames: List<String> = emptyList(),
    val messages: List<MessageComment> = emptyList(),
    val internalNotes: List<MessageComment> = emptyList(),
    val feedback: FeedbackRating? = null
)
