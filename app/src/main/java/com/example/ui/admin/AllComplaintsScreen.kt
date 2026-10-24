package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category
import com.example.model.Complaint
import com.example.model.ComplaintStatus
import com.example.model.Priority
import com.example.ui.components.CategoryChip
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StaffAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AppBackground
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceVariant
import com.example.ui.theme.SteadyBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SortOption

@Composable
fun AllComplaintsScreen(
    complaints: List<Complaint>,
    categories: List<Category>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatus: ComplaintStatus?,
    onStatusChange: (ComplaintStatus?) -> Unit,
    selectedCategory: String?,
    onCategoryChange: (String?) -> Unit,
    selectedPriority: Priority?,
    onPriorityChange: (Priority?) -> Unit,
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    onComplaintClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Control Bar: Search & Quick Filters
        Surface(
            color = AppSurface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search by ticket #, user, staff or keywords...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SteadyBlue,
                        unfocusedBorderColor = AppBorder,
                        focusedContainerColor = AppSurfaceVariant,
                        unfocusedContainerColor = AppSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_search_complaints")
                )

                // Dropdowns & Filter Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sort Dropdown
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppSurfaceVariant)
                                .border(1.dp, AppBorder, RoundedCornerShape(8.dp))
                                .clickable { sortDropdownExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("sort_dropdown_trigger"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = null,
                                tint = SteadyBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = sortOption.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = sortDropdownExpanded,
                            onDismissRequest = { sortDropdownExpanded = false }
                        ) {
                            SortOption.values().forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.displayName, fontSize = 13.sp) },
                                    onClick = {
                                        onSortOptionChange(opt)
                                        sortDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Category Filter Dropdown
                    Box {
                        val activeCatName = categories.find { it.id == selectedCategory }?.name ?: "All Categories"
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategory != null) SteadyBlue.copy(alpha = 0.1f) else AppSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (selectedCategory != null) SteadyBlue else AppBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { categoryDropdownExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("category_filter_trigger"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = if (selectedCategory != null) SteadyBlue else TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = activeCatName,
                                fontSize = 11.sp,
                                fontWeight = if (selectedCategory != null) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedCategory != null) SteadyBlue else TextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories", fontSize = 13.sp) },
                                onClick = {
                                    onCategoryChange(null)
                                    categoryDropdownExpanded = false
                                }
                            )
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name, fontSize = 13.sp) },
                                    onClick = {
                                        onCategoryChange(cat.id)
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Status Chips
                    ComplaintStatus.values().forEach { status ->
                        val isSelected = selectedStatus == status
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) status.bgColor else AppSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) status.borderColor else AppBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onStatusChange(if (isSelected) null else status)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("admin_status_${status.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = status.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) status.textColor else TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Complaint Count Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing ${complaints.size} Complaints",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            if (selectedStatus != null || selectedCategory != null || selectedPriority != null || searchQuery.isNotBlank()) {
                Text(
                    text = "Clear All Filters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SteadyBlue,
                    modifier = Modifier
                        .clickable {
                            onSearchQueryChange("")
                            onStatusChange(null)
                            onCategoryChange(null)
                            onPriorityChange(null)
                        }
                        .padding(4.dp)
                        .testTag("clear_all_filters")
                )
            }
        }

        // Responsive Adaptive List / Table
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpandedScreen = maxWidth >= 600.dp

            if (isExpandedScreen) {
                // Desktop / Tablet Table View
                DesktopComplaintsTable(
                    complaints = complaints,
                    onComplaintClick = onComplaintClick
                )
            } else {
                // Mobile Ticket Cards View
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(complaints, key = { it.id }) { complaint ->
                        AdminComplaintRowCard(
                            complaint = complaint,
                            onClick = { onComplaintClick(complaint.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AdminComplaintRowCard(
    complaint: Complaint,
    onClick: () -> Unit
) {
    Surface(
        color = AppSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("admin_complaint_card_${complaint.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: ID, Priority, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = complaint.id,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SteadyBlue
                    )
                    PriorityBadge(priority = complaint.priority)
                }

                StatusBadge(status = complaint.status)
            }

            // Row 2: Subject
            Text(
                text = complaint.subject,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Row 3: Category & Submitter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(categoryName = complaint.categoryName)

                Text(
                    text = "By ${complaint.submittedByName}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            HorizontalDivider(color = AppBorder)

            // Row 4: Assigned Staff & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (complaint.assignedStaff != null) {
                    StaffAvatar(
                        initials = complaint.assignedStaff.avatarInitials,
                        name = complaint.assignedStaff.name,
                        role = "Assigned",
                        size = 24
                    )
                } else {
                    Text(
                        text = "Unassigned",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFEF4444)
                    )
                }

                Text(
                    text = complaint.createdAt,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun DesktopComplaintsTable(
    complaints: List<Complaint>,
    onComplaintClick: (String) -> Unit
) {
    Surface(
        color = AppSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppSurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ticket ID",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(90.dp)
                )
                Text(
                    text = "Subject",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    text = "Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(130.dp)
                )
                Text(
                    text = "Priority",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(85.dp)
                )
                Text(
                    text = "Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(110.dp)
                )
                Text(
                    text = "Assigned Staff",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(120.dp)
                )
                Text(
                    text = "Date",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(90.dp)
                )
            }

            HorizontalDivider(color = AppBorder)

            // Table Rows
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(complaints, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onComplaintClick(item.id) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.id,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SteadyBlue,
                            modifier = Modifier.width(90.dp)
                        )
                        Text(
                            text = item.subject,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1.5f)
                        )
                        Box(modifier = Modifier.width(130.dp)) {
                            CategoryChip(categoryName = item.categoryName)
                        }
                        Box(modifier = Modifier.width(85.dp)) {
                            PriorityBadge(priority = item.priority)
                        }
                        Box(modifier = Modifier.width(110.dp)) {
                            StatusBadge(status = item.status)
                        }
                        Text(
                            text = item.assignedStaff?.name ?: "Unassigned",
                            fontSize = 12.sp,
                            color = if (item.assignedStaff != null) TextPrimary else Color(0xFFEF4444),
                            modifier = Modifier.width(120.dp)
                        )
                        Text(
                            text = item.createdAt.take(12),
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.width(90.dp)
                        )
                    }
                    HorizontalDivider(color = AppBorder)
                }
            }
        }
    }
}
