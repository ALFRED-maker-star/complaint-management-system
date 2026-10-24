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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category
import com.example.model.Priority
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.AppBackground
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppBorderSubtle
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceVariant
import com.example.ui.theme.SteadyBlue
import com.example.ui.theme.SteadyBlueContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitComplaintScreen(
    categories: List<Category>,
    onSubmitComplaint: (
        subject: String,
        description: String,
        categoryId: String,
        priority: Priority,
        attachments: List<String>,
        userName: String,
        userEmail: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("Elena Rostova") }
    var userEmail by remember { mutableStateOf("elena.r@acme-corp.com") }

    var selectedCategory by remember(categories) {
        mutableStateOf(categories.firstOrNull() ?: Category("default", "General", "General inquiry", 24))
    }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Priority: Can be auto-detected or manually chosen
    var manualPriorityOverride by remember { mutableStateOf<Priority?>(null) }
    val autoDetectedPriority = remember(subject, description) {
        Priority.suggestPriority(subject, description)
    }
    val effectivePriority = manualPriorityOverride ?: autoDetectedPriority

    // Attachments
    val attachments = remember { mutableStateListOf<String>() }
    var showAttachmentOptions by remember { mutableStateOf(false) }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Surface(
            color = SteadyBlue,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Submit a Complaint",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Provide details below. Our team reviews tickets within SLA guaranteed response times.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Contact Info Row
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
                Text(
                    text = "Your Information",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Your Name") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_user_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SteadyBlue,
                            unfocusedBorderColor = AppBorder
                        )
                    )

                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_user_email"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SteadyBlue,
                            unfocusedBorderColor = AppBorder
                        )
                    )
                }
            }
        }

        // Category & Priority
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
                Text(
                    text = "Category & Priority",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                // Category Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = "${selectedCategory.name} (${selectedCategory.slaHours}h SLA)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Complaint Category") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryDropdownExpanded = true }
                            .testTag("category_dropdown"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SteadyBlue,
                            unfocusedBorderColor = AppBorder
                        )
                    )

                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(AppSurface)
                    ) {
                        categories.filter { it.isActive }.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = cat.name,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${cat.description} • SLA: ${cat.slaHours} hrs",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                },
                                modifier = Modifier.testTag("category_item_${cat.id}")
                            )
                        }
                    }
                }

                // Priority Indicator Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppSurfaceVariant)
                        .padding(12.dp)
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
                            if (manualPriorityOverride == null) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Auto-detected",
                                    tint = SteadyBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Auto-suggested Priority",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            } else {
                                Text(
                                    text = "Custom Priority",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }

                        PriorityBadge(priority = effectivePriority)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Priority selector pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Priority.values().forEach { prio ->
                            val isChosen = effectivePriority == prio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChosen) prio.color else AppSurface)
                                    .border(
                                        1.dp,
                                        if (isChosen) prio.color else AppBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { manualPriorityOverride = prio }
                                    .padding(vertical = 6.dp)
                                    .testTag("priority_pill_${prio.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prio.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isChosen) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Complaint Details
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
                Text(
                    text = "Complaint Details",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = {
                        subject = it
                        errorMsg = null
                    },
                    label = { Text("Subject / Summary") },
                    placeholder = { Text("e.g., Billing discrepancy on invoice #1042") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_subject"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SteadyBlue,
                        unfocusedBorderColor = AppBorder
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        errorMsg = null
                    },
                    label = { Text("Description") },
                    placeholder = { Text("Explain what happened, steps to reproduce, and any impact...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("input_description"),
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SteadyBlue,
                        unfocusedBorderColor = AppBorder
                    )
                )

                // Attachment Section
                Text(
                    text = "Attachments & Evidence",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                // List of attached files
                if (attachments.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        attachments.forEach { fileName ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppSurfaceVariant)
                                    .border(1.dp, AppBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = null,
                                        tint = SteadyBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = fileName,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { attachments.remove(fileName) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Add Attachment Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SteadyBlueContainer)
                            .clickable {
                                // Add mock/sample attachment
                                val sampleFiles = listOf(
                                    "photo_damage_${System.currentTimeMillis() % 1000}.jpg",
                                    "invoice_receipt_${System.currentTimeMillis() % 1000}.pdf",
                                    "system_error_log.txt"
                                )
                                attachments.add(sampleFiles[attachments.size % sampleFiles.size])
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("button_add_attachment"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = SteadyBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Attach File / Photo",
                                color = SteadyBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        if (errorMsg != null) {
            Text(
                text = errorMsg ?: "",
                color = Color.Red,
                fontSize = 12.sp
            )
        }

        // Submit Button
        Button(
            onClick = {
                if (subject.isBlank()) {
                    errorMsg = "Please enter a subject for the complaint."
                    return@Button
                }
                if (description.isBlank()) {
                    errorMsg = "Please describe the complaint details."
                    return@Button
                }

                onSubmitComplaint(
                    subject,
                    description,
                    selectedCategory.id,
                    effectivePriority,
                    attachments.toList(),
                    userName,
                    userEmail
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = SteadyBlue
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_complaint_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Submit Complaint Ticket",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
