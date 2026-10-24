package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ComplaintRepository
import com.example.data.DashboardStats
import com.example.model.Category
import com.example.model.Complaint
import com.example.model.ComplaintStatus
import com.example.model.Priority
import com.example.model.SenderRole
import com.example.model.StaffMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class AppPortal {
    USER,
    ADMIN
}

enum class UserScreen {
    MY_COMPLAINTS,
    SUBMIT_COMPLAINT,
    COMPLAINT_DETAIL
}

enum class AdminScreen {
    DASHBOARD,
    ALL_COMPLAINTS,
    ASSIGN_UPDATE,
    CATEGORIES,
    REPORTS,
    LOGIN
}

enum class SortOption(val displayName: String) {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    HIGHEST_PRIORITY("Highest Priority")
}

class ResolveItViewModel(
    private val repository: ComplaintRepository = ComplaintRepository.instance
) : ViewModel() {

    // Portal & Navigation State
    private val _currentPortal = MutableStateFlow(AppPortal.USER)
    val currentPortal: StateFlow<AppPortal> = _currentPortal.asStateFlow()

    private val _userScreen = MutableStateFlow(UserScreen.MY_COMPLAINTS)
    val userScreen: StateFlow<UserScreen> = _userScreen.asStateFlow()

    private val _adminScreen = MutableStateFlow(AdminScreen.DASHBOARD)
    val adminScreen: StateFlow<AdminScreen> = _adminScreen.asStateFlow()

    private val _selectedComplaintId = MutableStateFlow<String?>(null)
    val selectedComplaintId: StateFlow<String?> = _selectedComplaintId.asStateFlow()

    // Data streams from repository
    val complaints: StateFlow<List<Complaint>> = repository.complaints
    val categories: StateFlow<List<Category>> = repository.categories
    val staffList: StateFlow<List<StaffMember>> = repository.staffList
    val currentLoggedInStaff: StateFlow<StaffMember?> = repository.currentLoggedInStaff

    // User Filters
    private val _userSearchQuery = MutableStateFlow("")
    val userSearchQuery: StateFlow<String> = _userSearchQuery.asStateFlow()

    private val _userStatusFilter = MutableStateFlow<ComplaintStatus?>(null)
    val userStatusFilter: StateFlow<ComplaintStatus?> = _userStatusFilter.asStateFlow()

    // Admin Filters
    private val _adminSearchQuery = MutableStateFlow("")
    val adminSearchQuery: StateFlow<String> = _adminSearchQuery.asStateFlow()

    private val _adminStatusFilter = MutableStateFlow<ComplaintStatus?>(null)
    val adminStatusFilter: StateFlow<ComplaintStatus?> = _adminStatusFilter.asStateFlow()

    private val _adminCategoryFilter = MutableStateFlow<String?>(null)
    val adminCategoryFilter: StateFlow<String?> = _adminCategoryFilter.asStateFlow()

    private val _adminPriorityFilter = MutableStateFlow<Priority?>(null)
    val adminPriorityFilter: StateFlow<Priority?> = _adminPriorityFilter.asStateFlow()

    private val _adminSortOption = MutableStateFlow(SortOption.NEWEST)
    val adminSortOption: StateFlow<SortOption> = _adminSortOption.asStateFlow()

    // Filtered User Complaints
    val filteredUserComplaints: StateFlow<List<Complaint>> = combine(
        complaints,
        _userSearchQuery,
        _userStatusFilter
    ) { all, query, status ->
        all.filter { comp ->
            val matchesQuery = query.isBlank() ||
                    comp.subject.contains(query, ignoreCase = true) ||
                    comp.id.contains(query, ignoreCase = true) ||
                    comp.categoryName.contains(query, ignoreCase = true)
            val matchesStatus = status == null || comp.status == status
            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Admin Complaints
    val filteredAdminComplaints: StateFlow<List<Complaint>> = combine(
        complaints,
        _adminSearchQuery,
        _adminStatusFilter,
        _adminCategoryFilter,
        _adminPriorityFilter
    ) { all, query, status, categoryId, priority ->
        var result = all.filter { comp ->
            val matchesQuery = query.isBlank() ||
                    comp.subject.contains(query, ignoreCase = true) ||
                    comp.id.contains(query, ignoreCase = true) ||
                    comp.categoryName.contains(query, ignoreCase = true) ||
                    comp.submittedByName.contains(query, ignoreCase = true) ||
                    (comp.assignedStaff?.name?.contains(query, ignoreCase = true) == true)
            val matchesStatus = status == null || comp.status == status
            val matchesCategory = categoryId == null || comp.categoryId == categoryId
            val matchesPriority = priority == null || comp.priority == priority
            matchesQuery && matchesStatus && matchesCategory && matchesPriority
        }
        when (_adminSortOption.value) {
            SortOption.NEWEST -> result
            SortOption.OLDEST -> result.reversed()
            SortOption.HIGHEST_PRIORITY -> result.sortedByDescending { it.priority.ordinal }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently viewed complaint
    val selectedComplaint: StateFlow<Complaint?> = combine(
        complaints,
        _selectedComplaintId
    ) { all, id ->
        all.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun switchPortal(portal: AppPortal) {
        _currentPortal.value = portal
    }

    fun navigateToUserScreen(screen: UserScreen, complaintId: String? = null) {
        if (complaintId != null) {
            _selectedComplaintId.value = complaintId
        }
        _userScreen.value = screen
    }

    fun navigateToAdminScreen(screen: AdminScreen, complaintId: String? = null) {
        if (complaintId != null) {
            _selectedComplaintId.value = complaintId
        }
        _adminScreen.value = screen
    }

    fun setUserSearchQuery(query: String) {
        _userSearchQuery.value = query
    }

    fun setUserStatusFilter(status: ComplaintStatus?) {
        _userStatusFilter.value = status
    }

    fun setAdminSearchQuery(query: String) {
        _adminSearchQuery.value = query
    }

    fun setAdminStatusFilter(status: ComplaintStatus?) {
        _adminStatusFilter.value = status
    }

    fun setAdminCategoryFilter(categoryId: String?) {
        _adminCategoryFilter.value = categoryId
    }

    fun setAdminPriorityFilter(priority: Priority?) {
        _adminPriorityFilter.value = priority
    }

    fun setAdminSortOption(sortOption: SortOption) {
        _adminSortOption.value = sortOption
    }

    fun submitComplaint(
        subject: String,
        description: String,
        categoryId: String,
        priority: Priority,
        attachmentNames: List<String>,
        userName: String,
        userEmail: String
    ): Complaint {
        val created = repository.submitComplaint(
            subject = subject,
            description = description,
            categoryId = categoryId,
            priority = priority,
            attachmentNames = attachmentNames,
            userName = userName,
            userEmail = userEmail
        )
        _selectedComplaintId.value = created.id
        _userScreen.value = UserScreen.COMPLAINT_DETAIL
        return created
    }

    fun addUserComment(complaintId: String, content: String) {
        repository.addMessageComment(
            complaintId = complaintId,
            senderName = "You",
            senderRole = SenderRole.USER,
            content = content
        )
    }

    fun addAdminResponse(complaintId: String, content: String) {
        val staff = currentLoggedInStaff.value?.name ?: "Staff Support"
        repository.addMessageComment(
            complaintId = complaintId,
            senderName = staff,
            senderRole = SenderRole.STAFF,
            content = content
        )
    }

    fun addInternalNote(complaintId: String, content: String) {
        val staff = currentLoggedInStaff.value?.name ?: "Staff Support"
        repository.addInternalNote(
            complaintId = complaintId,
            authorName = staff,
            content = content
        )
    }

    fun updateComplaintStatus(complaintId: String, status: ComplaintStatus) {
        repository.updateComplaintStatus(complaintId, status)
    }

    fun assignStaff(complaintId: String, staff: StaffMember?) {
        repository.assignComplaintStaff(complaintId, staff)
    }

    fun submitFeedback(complaintId: String, rating: Int, comment: String) {
        repository.submitFeedback(complaintId, rating, comment)
    }

    fun addCategory(name: String, description: String, slaHours: Int, defaultPriority: Priority) {
        repository.addCategory(name, description, slaHours, defaultPriority)
    }

    fun updateCategory(category: Category) {
        repository.updateCategory(category)
    }

    fun deleteCategory(categoryId: String) {
        repository.deleteCategory(categoryId)
    }

    fun loginAsStaff(staff: StaffMember) {
        repository.loginAsStaff(staff)
        _adminScreen.value = AdminScreen.DASHBOARD
    }

    fun getDashboardStats(): DashboardStats {
        return repository.getDashboardStats()
    }
}
