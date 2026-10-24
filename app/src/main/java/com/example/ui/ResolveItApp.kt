package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LockPerson
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminLoginScreen
import com.example.ui.admin.AllComplaintsScreen
import com.example.ui.admin.AssignUpdateComplaintScreen
import com.example.ui.admin.CategoriesSetupScreen
import com.example.ui.admin.ReportsScreen
import com.example.ui.components.AppHeader
import com.example.ui.theme.AppBackground
import com.example.ui.theme.AppSurface
import com.example.ui.theme.SteadyBlue
import com.example.ui.theme.SteadyBlueContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.user.ComplaintDetailScreen
import com.example.ui.user.MyComplaintsScreen
import com.example.ui.user.SubmitComplaintScreen
import com.example.viewmodel.AdminScreen
import com.example.viewmodel.AppPortal
import com.example.viewmodel.ResolveItViewModel
import com.example.viewmodel.UserScreen

@Composable
fun ResolveItApp(
    viewModel: ResolveItViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val portal by viewModel.currentPortal.collectAsState()
    val userScreen by viewModel.userScreen.collectAsState()
    val adminScreen by viewModel.adminScreen.collectAsState()

    val categories by viewModel.categories.collectAsState()
    val staffList by viewModel.staffList.collectAsState()
    val currentStaff by viewModel.currentLoggedInStaff.collectAsState()

    val filteredUserComplaints by viewModel.filteredUserComplaints.collectAsState()
    val filteredAdminComplaints by viewModel.filteredAdminComplaints.collectAsState()
    val selectedComplaint by viewModel.selectedComplaint.collectAsState()

    val userSearchQuery by viewModel.userSearchQuery.collectAsState()
    val userStatusFilter by viewModel.userStatusFilter.collectAsState()

    val adminSearchQuery by viewModel.adminSearchQuery.collectAsState()
    val adminStatusFilter by viewModel.adminStatusFilter.collectAsState()
    val adminCategoryFilter by viewModel.adminCategoryFilter.collectAsState()
    val adminPriorityFilter by viewModel.adminPriorityFilter.collectAsState()
    val adminSortOption by viewModel.adminSortOption.collectAsState()

    val isUserDetail = portal == AppPortal.USER && userScreen == UserScreen.COMPLAINT_DETAIL
    val isAdminDetail = portal == AppPortal.ADMIN && adminScreen == AdminScreen.ASSIGN_UPDATE
    val showBackButton = isUserDetail || isAdminDetail

    // Predictive / Compose BackHandler navigation
    BackHandler(enabled = isUserDetail) {
        viewModel.navigateToUserScreen(UserScreen.MY_COMPLAINTS)
    }
    BackHandler(enabled = isAdminDetail) {
        viewModel.navigateToAdminScreen(AdminScreen.ALL_COMPLAINTS)
    }
    BackHandler(enabled = portal == AppPortal.USER && userScreen == UserScreen.SUBMIT_COMPLAINT) {
        viewModel.navigateToUserScreen(UserScreen.MY_COMPLAINTS)
    }
    BackHandler(enabled = portal == AppPortal.ADMIN && adminScreen == AdminScreen.LOGIN) {
        viewModel.navigateToAdminScreen(AdminScreen.DASHBOARD)
    }

    Scaffold(
        topBar = {
            AppHeader(
                portal = portal,
                onPortalChanged = { newPortal -> viewModel.switchPortal(newPortal) },
                title = if (portal == AppPortal.USER) {
                    when (userScreen) {
                        UserScreen.MY_COMPLAINTS -> "My Complaints"
                        UserScreen.SUBMIT_COMPLAINT -> "New Complaint"
                        UserScreen.COMPLAINT_DETAIL -> selectedComplaint?.id ?: "Complaint Detail"
                    }
                } else {
                    when (adminScreen) {
                        AdminScreen.DASHBOARD -> "Admin Dashboard"
                        AdminScreen.ALL_COMPLAINTS -> "All Complaints"
                        AdminScreen.ASSIGN_UPDATE -> selectedComplaint?.id ?: "Assign & Update"
                        AdminScreen.CATEGORIES -> "Categories Setup"
                        AdminScreen.REPORTS -> "Reports & Analytics"
                        AdminScreen.LOGIN -> "Staff Login"
                    }
                },
                showBack = showBackButton,
                onBack = {
                    if (isUserDetail) {
                        viewModel.navigateToUserScreen(UserScreen.MY_COMPLAINTS)
                    } else if (isAdminDetail) {
                        viewModel.navigateToAdminScreen(AdminScreen.ALL_COMPLAINTS)
                    }
                }
            )
        },
        bottomBar = {
            // Mobile bottom navigation bar
            if (portal == AppPortal.USER) {
                UserBottomNav(
                    currentScreen = userScreen,
                    onNavigate = { viewModel.navigateToUserScreen(it) }
                )
            } else {
                AdminBottomNav(
                    currentScreen = adminScreen,
                    onNavigate = { viewModel.navigateToAdminScreen(it) }
                )
            }
        },
        containerColor = AppBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (portal == AppPortal.USER) {
                when (userScreen) {
                    UserScreen.MY_COMPLAINTS -> {
                        MyComplaintsScreen(
                            complaints = filteredUserComplaints,
                            searchQuery = userSearchQuery,
                            onSearchQueryChange = { viewModel.setUserSearchQuery(it) },
                            selectedStatusFilter = userStatusFilter,
                            onStatusFilterChange = { viewModel.setUserStatusFilter(it) },
                            onComplaintClick = { id ->
                                viewModel.navigateToUserScreen(UserScreen.COMPLAINT_DETAIL, id)
                            },
                            onNewComplaintClick = {
                                viewModel.navigateToUserScreen(UserScreen.SUBMIT_COMPLAINT)
                            }
                        )
                    }

                    UserScreen.SUBMIT_COMPLAINT -> {
                        SubmitComplaintScreen(
                            categories = categories,
                            onSubmitComplaint = { subject, desc, catId, prio, attachments, name, email ->
                                viewModel.submitComplaint(subject, desc, catId, prio, attachments, name, email)
                            }
                        )
                    }

                    UserScreen.COMPLAINT_DETAIL -> {
                        selectedComplaint?.let { comp ->
                            ComplaintDetailScreen(
                                complaint = comp,
                                onAddComment = { viewModel.addUserComment(comp.id, it) },
                                onSubmitFeedback = { rating, comment ->
                                    viewModel.submitFeedback(comp.id, rating, comment)
                                }
                            )
                        } ?: run {
                            viewModel.navigateToUserScreen(UserScreen.MY_COMPLAINTS)
                        }
                    }
                }
            } else {
                when (adminScreen) {
                    AdminScreen.DASHBOARD -> {
                        AdminDashboardScreen(
                            stats = viewModel.getDashboardStats(),
                            recentComplaints = filteredAdminComplaints,
                            currentStaff = currentStaff,
                            onNavigateAdmin = { screen, id ->
                                viewModel.navigateToAdminScreen(screen, id)
                            },
                            onFilterByStatus = { status ->
                                viewModel.setAdminStatusFilter(status)
                            }
                        )
                    }

                    AdminScreen.ALL_COMPLAINTS -> {
                        AllComplaintsScreen(
                            complaints = filteredAdminComplaints,
                            categories = categories,
                            searchQuery = adminSearchQuery,
                            onSearchQueryChange = { viewModel.setAdminSearchQuery(it) },
                            selectedStatus = adminStatusFilter,
                            onStatusChange = { viewModel.setAdminStatusFilter(it) },
                            selectedCategory = adminCategoryFilter,
                            onCategoryChange = { viewModel.setAdminCategoryFilter(it) },
                            selectedPriority = adminPriorityFilter,
                            onPriorityChange = { viewModel.setAdminPriorityFilter(it) },
                            sortOption = adminSortOption,
                            onSortOptionChange = { viewModel.setAdminSortOption(it) },
                            onComplaintClick = { id ->
                                viewModel.navigateToAdminScreen(AdminScreen.ASSIGN_UPDATE, id)
                            }
                        )
                    }

                    AdminScreen.ASSIGN_UPDATE -> {
                        selectedComplaint?.let { comp ->
                            AssignUpdateComplaintScreen(
                                complaint = comp,
                                staffList = staffList,
                                onAssignStaff = { staff -> viewModel.assignStaff(comp.id, staff) },
                                onUpdateStatus = { status -> viewModel.updateComplaintStatus(comp.id, status) },
                                onAddInternalNote = { note -> viewModel.addInternalNote(comp.id, note) },
                                onAddPublicResponse = { reply -> viewModel.addAdminResponse(comp.id, reply) }
                            )
                        } ?: run {
                            viewModel.navigateToAdminScreen(AdminScreen.ALL_COMPLAINTS)
                        }
                    }

                    AdminScreen.CATEGORIES -> {
                        CategoriesSetupScreen(
                            categories = categories,
                            onAddCategory = { name, desc, sla, prio ->
                                viewModel.addCategory(name, desc, sla, prio)
                            },
                            onUpdateCategory = { cat -> viewModel.updateCategory(cat) },
                            onDeleteCategory = { id -> viewModel.deleteCategory(id) }
                        )
                    }

                    AdminScreen.REPORTS -> {
                        ReportsScreen(
                            stats = viewModel.getDashboardStats(),
                            complaints = filteredAdminComplaints
                        )
                    }

                    AdminScreen.LOGIN -> {
                        AdminLoginScreen(
                            staffList = staffList,
                            currentStaff = currentStaff,
                            onStaffSelected = { staff -> viewModel.loginAsStaff(staff) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserBottomNav(
    currentScreen: UserScreen,
    onNavigate: (UserScreen) -> Unit
) {
    NavigationBar(
        containerColor = AppSurface,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = currentScreen == UserScreen.MY_COMPLAINTS || currentScreen == UserScreen.COMPLAINT_DETAIL,
            onClick = { onNavigate(UserScreen.MY_COMPLAINTS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = "My Complaints"
                )
            },
            label = { Text("My Complaints", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SteadyBlue,
                selectedTextColor = SteadyBlue,
                indicatorColor = SteadyBlueContainer
            ),
            modifier = Modifier.testTag("nav_my_complaints")
        )

        NavigationBarItem(
            selected = currentScreen == UserScreen.SUBMIT_COMPLAINT,
            onClick = { onNavigate(UserScreen.SUBMIT_COMPLAINT) },
            icon = {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Submit Complaint"
                )
            },
            label = { Text("Submit Complaint", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SteadyBlue,
                selectedTextColor = SteadyBlue,
                indicatorColor = SteadyBlueContainer
            ),
            modifier = Modifier.testTag("nav_submit_complaint")
        )
    }
}

@Composable
fun AdminBottomNav(
    currentScreen: AdminScreen,
    onNavigate: (AdminScreen) -> Unit
) {
    NavigationBar(
        containerColor = AppSurface,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = currentScreen == AdminScreen.DASHBOARD,
            onClick = { onNavigate(AdminScreen.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SteadyBlue,
                selectedTextColor = SteadyBlue,
                indicatorColor = SteadyBlueContainer
            ),
            modifier = Modifier.testTag("admin_nav_dashboard")
        )

        NavigationBarItem(
            selected = currentScreen == AdminScreen.ALL_COMPLAINTS || currentScreen == AdminScreen.ASSIGN_UPDATE,
            onClick = { onNavigate(AdminScreen.ALL_COMPLAINTS) },
            icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Complaints") },
            label = { Text("Tickets", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SteadyBlue,
                selectedTextColor = SteadyBlue,
                indicatorColor = SteadyBlueContainer
            ),
            modifier = Modifier.testTag("admin_nav_all_complaints")
        )

        NavigationBarItem(
            selected = currentScreen == AdminScreen.CATEGORIES,
            onClick = { onNavigate(AdminScreen.CATEGORIES) },
            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
            label = { Text("Categories", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SteadyBlue,
                selectedTextColor = SteadyBlue,
                indicatorColor = SteadyBlueContainer
            ),
            modifier = Modifier.testTag("admin_nav_categories")
        )

        NavigationBarItem(
            selected = currentScreen == AdminScreen.REPORTS,
            onClick = { onNavigate(AdminScreen.REPORTS) },
            icon = { Icon(Icons.Default.Insights, contentDescription = "Reports") },
            label = { Text("Reports", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SteadyBlue,
                selectedTextColor = SteadyBlue,
                indicatorColor = SteadyBlueContainer
            ),
            modifier = Modifier.testTag("admin_nav_reports")
        )

        NavigationBarItem(
            selected = currentScreen == AdminScreen.LOGIN,
            onClick = { onNavigate(AdminScreen.LOGIN) },
            icon = { Icon(Icons.Default.LockPerson, contentDescription = "Staff") },
            label = { Text("Staff", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SteadyBlue,
                selectedTextColor = SteadyBlue,
                indicatorColor = SteadyBlueContainer
            ),
            modifier = Modifier.testTag("admin_nav_staff")
        )
    }
}
