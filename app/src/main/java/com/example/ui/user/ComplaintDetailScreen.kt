package com.example.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Complaint
import com.example.model.ComplaintStatus
import com.example.model.MessageComment
import com.example.model.SenderRole
import com.example.ui.components.CategoryChip
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StaffAvatar
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AppBackground
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceVariant
import com.example.ui.theme.SteadyBlue
import com.example.ui.theme.SteadyBlueContainer
import com.example.ui.theme.SteadyBlueOnContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ComplaintDetailScreen(
    complaint: Complaint,
    onAddComment: (String) -> Unit,
    onSubmitFeedback: (rating: Int, comment: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Ticket Overview Header Card
                TicketOverviewCard(complaint = complaint)
            }

            item {
                // Status Timeline
                StatusTimelineCard(status = complaint.status, assigned = complaint.assignedStaff != null)
            }

            // Feedback Rating Section if Resolved
            if (complaint.status == ComplaintStatus.RESOLVED) {
                item {
                    FeedbackRatingCard(
                        complaint = complaint,
                        onSubmitFeedback = onSubmitFeedback
                    )
                }
            }

            // Thread Messages Divider & Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Activity & Discussion",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppSurfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${complaint.messages.size} messages",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Message Bubbles
            items(complaint.messages, key = { it.id }) { message ->
                MessageBubble(message = message)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Input Bar
        Surface(
            color = AppSurface,
            shadowElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Write a message or reply...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_user_comment"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SteadyBlue,
                        unfocusedBorderColor = AppBorder,
                        focusedContainerColor = AppSurfaceVariant,
                        unfocusedContainerColor = AppSurfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(commentText)
                            commentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (commentText.isNotBlank()) SteadyBlue else AppSurfaceVariant)
                        .testTag("send_comment_button"),
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (commentText.isNotBlank()) Color.White else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TicketOverviewCard(complaint: Complaint) {
    Surface(
        color = AppSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ticket ID, Date & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = complaint.id,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SteadyBlue
                    )
                    Text(
                        text = "Submitted: ${complaint.createdAt}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                StatusBadge(status = complaint.status)
            }

            // Subject
            Text(
                text = complaint.subject,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Category & Priority
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(categoryName = complaint.categoryName)
                PriorityBadge(priority = complaint.priority)
            }

            HorizontalDivider(color = AppBorder)

            // Description
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Description:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = complaint.description,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 19.sp
                )
            }

            // Attached Files
            if (complaint.attachmentNames.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Attached Files (${complaint.attachmentNames.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        complaint.attachmentNames.forEach { file ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AppSurfaceVariant)
                                    .border(1.dp, AppBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = null,
                                    tint = SteadyBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = file,
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Assigned Staff
            if (complaint.assignedStaff != null) {
                HorizontalDivider(color = AppBorder)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StaffAvatar(
                        initials = complaint.assignedStaff.avatarInitials,
                        name = complaint.assignedStaff.name,
                        role = "Assigned Support Agent • ${complaint.assignedStaff.department}",
                        size = 34
                    )
                }
            }
        }
    }
}

@Composable
fun StatusTimelineCard(
    status: ComplaintStatus,
    assigned: Boolean
) {
    Surface(
        color = AppSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Resolution Timeline",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            val lastStepLabel = if (status == ComplaintStatus.ESCALATED) "Escalated" else "Resolved"
            val lastStepDone = status == ComplaintStatus.RESOLVED || status == ComplaintStatus.ESCALATED

            val steps: List<Pair<String, Boolean>> = listOf(
                "Submitted" to true,
                "Assigned" to (assigned || status != ComplaintStatus.NEW),
                "In Progress" to (status == ComplaintStatus.IN_PROGRESS || status == ComplaintStatus.RESOLVED || status == ComplaintStatus.ESCALATED),
                lastStepLabel to lastStepDone
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, (label, isCompleted) ->
                    val isLast = index == steps.size - 1
                    val isEscalatedStep = label == "Escalated"

                    val stepColor = when {
                        isCompleted && isEscalatedStep -> Color(0xFFEF4444)
                        isCompleted -> SteadyBlue
                        else -> Color(0xFFCBD5E1)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(stepColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = if (isEscalatedStep) Icons.Default.ErrorOutline else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isCompleted) TextPrimary else TextMuted
                        )
                    }

                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .weight(0.6f)
                                .height(2.dp)
                                .background(if (steps[index + 1].second) SteadyBlue else Color(0xFFE2E8F0))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: MessageComment) {
    when (message.senderRole) {
        SenderRole.SYSTEM -> {
            // Centered subtle system message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${message.content} • ${message.formattedTime}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        SenderRole.USER -> {
            // Right-aligned User Bubble
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    color = SteadyBlue,
                    shape = RoundedCornerShape(14.dp, 14.dp, 2.dp, 14.dp),
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = message.content,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
                Text(
                    text = "${message.senderName} • ${message.formattedTime}",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }

        SenderRole.STAFF -> {
            // Left-aligned Staff Bubble
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp, start = 4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified Staff",
                        tint = SteadyBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = message.senderName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SteadyBlue
                    )
                    Text(
                        text = "• Support Team",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = AppSurface,
                    shape = RoundedCornerShape(14.dp, 14.dp, 14.dp, 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = message.content,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
                Text(
                    text = message.formattedTime,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun FeedbackRatingCard(
    complaint: Complaint,
    onSubmitFeedback: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(complaint.feedback?.rating ?: 5) }
    var comment by remember { mutableStateOf(complaint.feedback?.comment ?: "") }
    var isEditing by remember { mutableStateOf(complaint.feedback == null) }

    val ratingLabels = mapOf(
        1 to "Terrible - Unresolved issues",
        2 to "Poor - Slow or unhelpful",
        3 to "Average - Adequate service",
        4 to "Good - Swift and helpful",
        5 to "Excellent - Outstanding resolution!"
    )

    Surface(
        color = Color(0xFFF0FDF4),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("feedback_rating_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Resolution Feedback",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534)
                    )
                }

                if (complaint.feedback != null && !isEditing) {
                    Row(
                        modifier = Modifier
                            .clickable { isEditing = true }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Feedback",
                            tint = Color(0xFF166534),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Edit",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (complaint.feedback != null && !isEditing) {
                // Read-only feedback view
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StarRatingBar(rating = complaint.feedback.rating, starSize = 20)
                        Text(
                            text = "${complaint.feedback.rating}/5 Stars",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                    if (complaint.feedback.comment.isNotBlank()) {
                        Text(
                            text = "\"${complaint.feedback.comment}\"",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 17.sp
                        )
                    }
                    Text(
                        text = "Submitted on ${complaint.feedback.submittedAt}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            } else {
                // Interactive Feedback Form
                Text(
                    text = "How satisfied are you with how this complaint was handled?",
                    fontSize = 12.sp,
                    color = TextPrimary
                )

                // Interactive Star Bar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StarRatingBar(
                        rating = rating,
                        onRatingChanged = { rating = it },
                        starSize = 30
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ratingLabels[rating] ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF15803D)
                    )
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Share any comments on resolution speed or support staff...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .testTag("input_feedback_comment"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF16A34A),
                        unfocusedBorderColor = Color(0xFF86EFAC),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = {
                        onSubmitFeedback(rating, comment)
                        isEditing = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_feedback_button")
                ) {
                    Text(
                        text = "Submit Feedback Rating",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
