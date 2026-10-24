package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ComplaintStatus
import com.example.model.Priority
import com.example.ui.theme.AppBorder
import com.example.ui.theme.AppSurface
import com.example.ui.theme.AppSurfaceVariant
import com.example.ui.theme.SteadyBlue
import com.example.ui.theme.SteadyBlueContainer
import com.example.ui.theme.SteadyBlueOnContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppPortal

@Composable
fun StatusBadge(
    status: ComplaintStatus,
    modifier: Modifier = Modifier
) {
    val icon = when (status) {
        ComplaintStatus.NEW -> Icons.Default.NewReleases
        ComplaintStatus.IN_PROGRESS -> Icons.Default.HourglassTop
        ComplaintStatus.RESOLVED -> Icons.Default.CheckCircle
        ComplaintStatus.ESCALATED -> Icons.Default.ErrorOutline
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(status.bgColor)
            .border(1.dp, status.borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = status.textColor,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = status.displayName,
            color = status.textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(priority.color.copy(alpha = 0.12f))
            .border(1.dp, priority.color.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(priority.color)
        )
        Text(
            text = priority.displayName,
            color = priority.color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CategoryChip(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(AppSurfaceVariant)
            .border(1.dp, AppBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = categoryName,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun StaffAvatar(
    initials: String,
    name: String,
    role: String? = null,
    size: Int = 36,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(SteadyBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = SteadyBlueOnContainer,
                fontSize = (size / 2.5).sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (role != null) {
            Column {
                Text(
                    text = name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = role,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Int,
    maxRating: Int = 5,
    onRatingChanged: ((Int) -> Unit)? = null,
    starSize: Int = 26,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val isSelected = i <= rating
            val starModifier = if (onRatingChanged != null) {
                Modifier
                    .size(starSize.dp)
                    .clickable { onRatingChanged(i) }
                    .testTag("star_rating_$i")
            } else {
                Modifier.size(starSize.dp)
            }

            Icon(
                imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Rating $i",
                tint = if (isSelected) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                modifier = starModifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    portal: AppPortal,
    onPortalChanged: (AppPortal) -> Unit,
    title: String = "ResolveIt",
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppSurface,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SteadyBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (portal == AppPortal.USER) "Customer Portal" else "Staff Control Center",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            },
            navigationIcon = {
                if (showBack) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                }
            },
            actions = {
                // Role Switcher Toggle
                Row(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppSurfaceVariant)
                        .border(1.dp, AppBorder, RoundedCornerShape(20.dp))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isUser = portal == AppPortal.USER
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isUser) SteadyBlue else Color.Transparent)
                            .clickable { onPortalChanged(AppPortal.USER) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("switch_to_user_portal"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isUser) Color.White else TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "User",
                                color = if (isUser) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (!isUser) SteadyBlue else Color.Transparent)
                            .clickable { onPortalChanged(AppPortal.ADMIN) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("switch_to_admin_portal"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = if (!isUser) Color.White else TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Admin",
                                color = if (!isUser) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (!isUser) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppSurface
            )
        )
    }
}
