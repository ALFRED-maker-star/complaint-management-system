package com.example.ui.admin

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.StaffMember
import com.example.ui.components.CategoryChip
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StaffAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AppBackground
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceVariant
import com.example.ui.theme.SteadyBlue
import com.example.ui.theme.SteadyBlueContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.user.MessageBubble

@Composable
fun AssignUpdateComplaintScreen(
    complaint: Complaint,
    staffList: List<StaffMember>,
    onAssignStaff: (StaffMember?) -> Unit,
    onUpdateStatus: (ComplaintStatus) -> Unit,
    onAddInternalNote: (String) -> Unit,
    onAddPublicResponse: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var assignDropdownExpanded by remember { mutableStateOf(false) }
    var internalNoteText by remember { mutableStateOf("") }
    var publicResponseText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ticket Header Info
        item {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = complaint.id,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SteadyBlue
                        )
                        StatusBadge(status = complaint.status)
                    }

                    Text(
                        text = complaint.subject,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryChip(categoryName = complaint.categoryName)
                        PriorityBadge(priority = complaint.priority)
                    }

                    HorizontalDivider(color = AppBorder)

                    // Customer details
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Customer Information",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Text(
                            text = "${complaint.submittedByName} (${complaint.submittedByEmail})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Created: ${complaint.createdAt} • Updated: ${complaint.updatedAt}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    // Description
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Original Description:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Text(
                            text = complaint.description,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }

                    // Attachments
                    if (complaint.attachmentNames.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            complaint.attachmentNames.forEach { fn ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AppSurfaceVariant)
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
                                        text = fn,
                                        fontSize = 11.sp,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Assignment & Status Control Card
        item {
            Surface(
                color = AppSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Assign & Workflow Control",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Staff Assignment Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Assigned Support Agent",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppSurfaceVariant)
                                    .border(1.dp, AppBorder, RoundedCornerShape(8.dp))
                                    .clickable { assignDropdownExpanded = true }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("assign_staff_dropdown"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AssignmentInd,
                                        contentDescription = null,
                                        tint = SteadyBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = complaint.assignedStaff?.name ?: "Unassigned (Click to Assign)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (complaint.assignedStaff != null) TextPrimary else Color(0xFFEF4444)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = assignDropdownExpanded,
                                onDismissRequest = { assignDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Unassigned", color = Color(0xFFEF4444)) },
                                    onClick = {
                                        onAssignStaff(null)
                                        assignDropdownExpanded = false
                                    }
                                )
                                staffList.forEach { staff ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(staff.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("${staff.role} • ${staff.department}", fontSize = 11.sp, color = TextSecondary)
                                            }
                                        },
                                        onClick = {
                                            onAssignStaff(staff)
                                            assignDropdownExpanded = false
                                        },
                                        modifier = Modifier.testTag("staff_select_${staff.id}")
                                    )
                                }
                            }
                        }
                    }

                    // Status Update Control
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Ticket Status",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ComplaintStatus.values().forEach { status ->
                                val isSelected = complaint.status == status
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) status.bgColor else AppSurfaceVariant)
                                        .border(
                                            1.5.dp,
                                            if (isSelected) status.textColor else AppBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onUpdateStatus(status) }
                                        .padding(vertical = 8.dp)
                                        .testTag("set_status_${status.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = status.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) status.textColor else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Internal Notes Section (Confidential to Staff)
        item {
            Surface(
                color = Color(0xFFFFFBEB),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("internal_notes_section")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Internal Staff Notes (Hidden from User)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }

                    // Existing internal notes
                    if (complaint.internalNotes.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            complaint.internalNotes.forEach { note ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = note.senderName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF92400E)
                                            )
                                            Text(
                                                text = note.formattedTime,
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = note.content,
                                            fontSize = 12.sp,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No internal notes yet. Use notes to log private investigation details or ticket handoffs.",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                    }

                    // Add Internal Note Input
                    OutlinedTextField(
                        value = internalNoteText,
                        onValueChange = { internalNoteText = it },
                        placeholder = { Text("Write confidential staff note...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("input_internal_note"),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD97706),
                            unfocusedBorderColor = Color(0xFFFCD34D),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            if (internalNoteText.isNotBlank()) {
                                onAddInternalNote(internalNoteText)
                                internalNoteText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(6.dp),
                        enabled = internalNoteText.isNotBlank(),
                        modifier = Modifier.testTag("button_save_internal_note")
                    ) {
                        Text("Add Staff Note", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Public Response / Reply Box to User
        item {
            Surface(
                color = AppSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = SteadyBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Public Customer Response",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "This message will be posted directly to the customer's complaint thread.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = publicResponseText,
                        onValueChange = { publicResponseText = it },
                        placeholder = { Text("Type reply to customer...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp)
                            .testTag("input_admin_public_response"),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SteadyBlue,
                            unfocusedBorderColor = AppBorder,
                            focusedContainerColor = AppSurfaceVariant,
                            unfocusedContainerColor = AppSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            if (publicResponseText.isNotBlank()) {
                                onAddPublicResponse(publicResponseText)
                                publicResponseText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SteadyBlue),
                        shape = RoundedCornerShape(6.dp),
                        enabled = publicResponseText.isNotBlank(),
                        modifier = Modifier.testTag("button_send_admin_response")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Reply to Customer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Complete Public Conversation Stream
        item {
            Text(
                text = "Thread History (${complaint.messages.size} Messages)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(complaint.messages, key = { it.id }) { message ->
            MessageBubble(message = message)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
