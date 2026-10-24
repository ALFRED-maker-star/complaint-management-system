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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DashboardStats
import com.example.model.Complaint
import com.example.model.ComplaintStatus
import com.example.model.Priority
import com.example.model.StaffMember
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
import com.example.viewmodel.AdminScreen

@Composable
fun AdminDashboardScreen(
    stats: DashboardStats,
    recentComplaints: List<Complaint>,
    currentStaff: StaffMember?,
    onNavigateAdmin: (AdminScreen, String?) -> Unit,
    onFilterByStatus: (ComplaintStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Staff Greeting Banner
        if (currentStaff != null) {
            Surface(
                color = AppSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StaffAvatar(
                        initials = currentStaff.avatarInitials,
                        name = "Welcome back, ${currentStaff.name}",
                        role = "${currentStaff.role} • ${currentStaff.department}",
                        size = 40
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(SteadyBlueContainer)
                            .clickable { onNavigateAdmin(AdminScreen.LOGIN, null) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("switch_staff_account"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Switch Staff",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SteadyBlue
                        )
                    }
                }
            }
        }

        // Section Title: Key Metrics
        Text(
            text = "Resolution Overview",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        // 4 Stat Cards Grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Open Complaints",
                value = "${stats.openComplaints}",
                subtitle = "${stats.totalComplaints} total tickets",
                icon = Icons.Default.HourglassBottom,
                iconColor = Color(0xFFF59E0B),
                bgColor = Color(0xFFFFFBEB),
                onClick = {
                    onFilterByStatus(ComplaintStatus.IN_PROGRESS)
                    onNavigateAdmin(AdminScreen.ALL_COMPLAINTS, null)
                },
                modifier = Modifier.weight(1f),
                testTag = "stat_card_open"
            )

            StatCard(
                title = "Avg Resolution",
                value = "${stats.avgResolutionTimeHours}h",
                subtitle = "Within 8h target",
                icon = Icons.Default.AccessTime,
                iconColor = SteadyBlue,
                bgColor = SteadyBlueContainer.copy(alpha = 0.5f),
                onClick = { onNavigateAdmin(AdminScreen.REPORTS, null) },
                modifier = Modifier.weight(1f),
                testTag = "stat_card_resolution_time"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Satisfaction",
                value = "${stats.satisfactionScore}",
                subtitle = "Out of 5.0 rating",
                icon = Icons.Default.Star,
                iconColor = Color(0xFFEAB308),
                bgColor = Color(0xFFFEFCE8),
                onClick = { onNavigateAdmin(AdminScreen.REPORTS, null) },
                modifier = Modifier.weight(1f),
                testTag = "stat_card_satisfaction"
            )

            StatCard(
                title = "Escalated",
                value = "${stats.escalatedCount}",
                subtitle = "Needs urgent action",
                icon = Icons.Default.ErrorOutline,
                iconColor = Color(0xFFEF4444),
                bgColor = Color(0xFFFEF2F2),
                onClick = {
                    onFilterByStatus(ComplaintStatus.ESCALATED)
                    onNavigateAdmin(AdminScreen.ALL_COMPLAINTS, null)
                },
                modifier = Modifier.weight(1f),
                testTag = "stat_card_escalated"
            )
        }

        // Status Breakdown Chart Card
        StatusBreakdownChartCard(
            statusCounts = stats.statusBreakdown,
            total = stats.totalComplaints,
            onStatusSelected = { status ->
                onFilterByStatus(status)
                onNavigateAdmin(AdminScreen.ALL_COMPLAINTS, null)
            }
        )

        // Quick Navigation Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionTile(
                title = "All Complaints",
                subtitle = "Manage & assign tickets",
                icon = Icons.Default.Assignment,
                onClick = {
                    onFilterByStatus(null)
                    onNavigateAdmin(AdminScreen.ALL_COMPLAINTS, null)
                },
                modifier = Modifier.weight(1f),
                testTag = "action_tile_all_complaints"
            )

            QuickActionTile(
                title = "Category Setup",
                subtitle = "Configure SLAs & tiers",
                icon = Icons.Default.Category,
                onClick = { onNavigateAdmin(AdminScreen.CATEGORIES, null) },
                modifier = Modifier.weight(1f),
                testTag = "action_tile_categories"
            )
        }

        // Actionable / Urgent Tickets
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
                        text = "Priority & Action Required",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier
                            .clickable {
                                onFilterByStatus(null)
                                onNavigateAdmin(AdminScreen.ALL_COMPLAINTS, null)
                            }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SteadyBlue
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SteadyBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                val priorityTickets = recentComplaints.filter {
                    it.priority == Priority.URGENT || it.priority == Priority.HIGH || it.status == ComplaintStatus.NEW
                }.take(3)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    priorityTickets.forEach { ticket ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppSurfaceVariant)
                                .clickable {
                                    onNavigateAdmin(AdminScreen.ASSIGN_UPDATE, ticket.id)
                                }
                                .padding(10.dp)
                                .testTag("urgent_ticket_row_${ticket.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = ticket.id,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SteadyBlue
                                    )
                                    PriorityBadge(priority = ticket.priority)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = ticket.subject,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                            }

                            StatusBadge(status = ticket.status)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        color = AppSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
        shadowElevation = 0.5.dp,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun StatusBreakdownChartCard(
    statusCounts: Map<ComplaintStatus, Int>,
    total: Int,
    onStatusSelected: (ComplaintStatus) -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status Breakdown Distribution",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "$total Total",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Horizontal Distribution Bar
            if (total > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                ) {
                    ComplaintStatus.values().forEach { status ->
                        val count = statusCounts[status] ?: 0
                        val weight = if (total > 0) (count.toFloat() / total.toFloat()).coerceAtLeast(0.01f) else 1f

                        val segmentColor = when (status) {
                            ComplaintStatus.NEW -> Color(0xFF64748B)
                            ComplaintStatus.IN_PROGRESS -> Color(0xFFF59E0B)
                            ComplaintStatus.RESOLVED -> Color(0xFF10B981)
                            ComplaintStatus.ESCALATED -> Color(0xFFEF4444)
                        }

                        if (count > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(weight)
                                    .height(14.dp)
                                    .background(segmentColor)
                            )
                        }
                    }
                }
            }

            // Legend Grid with percentages
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ComplaintStatus.values().forEach { status ->
                    val count = statusCounts[status] ?: 0
                    val pct = if (total > 0) (count * 100 / total) else 0

                    val dotColor = when (status) {
                        ComplaintStatus.NEW -> Color(0xFF64748B)
                        ComplaintStatus.IN_PROGRESS -> Color(0xFFF59E0B)
                        ComplaintStatus.RESOLVED -> Color(0xFF10B981)
                        ComplaintStatus.ESCALATED -> Color(0xFFEF4444)
                    }

                    Column(
                        modifier = Modifier
                            .clickable { onStatusSelected(status) }
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                            Text(
                                text = status.displayName,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$count ($pct%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        color = AppSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SteadyBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SteadyBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
