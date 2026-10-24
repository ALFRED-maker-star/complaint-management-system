package com.example.data

import com.example.model.Category
import com.example.model.Complaint
import com.example.model.ComplaintStatus
import com.example.model.FeedbackRating
import com.example.model.MessageComment
import com.example.model.Priority
import com.example.model.SenderRole
import com.example.model.StaffMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DashboardStats(
    val openComplaints: Int,
    val avgResolutionTimeHours: Double,
    val satisfactionScore: Double,
    val escalatedCount: Int,
    val totalComplaints: Int,
    val resolvedCount: Int,
    val statusBreakdown: Map<ComplaintStatus, Int>,
    val categoryBreakdown: Map<String, Int>
)

data class TrendPoint(
    val period: String,
    val count: Int,
    val resolved: Int
)

class ComplaintRepository private constructor() {

    private val dateFormat = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.US)
    private val shortDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    private val _staffList = MutableStateFlow(
        listOf(
            StaffMember(
                id = "st-1",
                name = "Sarah Jenkins",
                email = "sarah.j@resolveit.internal",
                role = "Lead Support Specialist",
                department = "Customer Care",
                avatarInitials = "SJ"
            ),
            StaffMember(
                id = "st-2",
                name = "David Chen",
                email = "david.c@resolveit.internal",
                role = "Senior Technical Ops",
                department = "Technical Ops",
                avatarInitials = "DC"
            ),
            StaffMember(
                id = "st-3",
                name = "Maria Gomez",
                email = "maria.g@resolveit.internal",
                role = "Billing & Logistics Manager",
                department = "Operations",
                avatarInitials = "MG"
            ),
            StaffMember(
                id = "st-4",
                name = "Alex Rivera",
                email = "alex.r@resolveit.internal",
                role = "Systems Administrator",
                department = "IT Infrastructure",
                avatarInitials = "AR"
            )
        )
    )
    val staffList: StateFlow<List<StaffMember>> = _staffList.asStateFlow()

    private val _currentLoggedInStaff = MutableStateFlow<StaffMember?>(_staffList.value.first())
    val currentLoggedInStaff: StateFlow<StaffMember?> = _currentLoggedInStaff.asStateFlow()

    private val _categories = MutableStateFlow(
        listOf(
            Category("cat-1", "Billing & Payments", "Invoices, payment errors, recurring charges, and refunds", 12, Priority.HIGH),
            Category("cat-2", "Technical & App Issues", "Software crashes, API timeouts, error codes, and bugs", 8, Priority.URGENT),
            Category("cat-3", "Delivery & Hardware", "Damaged packages, missing shipments, hardware replacements", 24, Priority.MEDIUM),
            Category("cat-4", "Account & Security", "Password resets, MFA access, permission denials, and auth", 4, Priority.URGENT),
            Category("cat-5", "Facilities & Premises", "Office access, HVAC, room maintenance, sensor failures", 48, Priority.LOW),
            Category("cat-6", "Service Quality & Staff", "Representative conduct, response delays, and general feedback", 36, Priority.MEDIUM)
        )
    )
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _complaints = MutableStateFlow<List<Complaint>>(createInitialComplaints())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    private fun createInitialComplaints(): List<Complaint> {
        return listOf(
            Complaint(
                id = "RES-1042",
                subject = "Double charged on Enterprise Pro annual subscription",
                description = "Our company card was charged $1,200 twice on October 18 for invoice #INV-99321. We only have one active workspace account. Please issue an immediate refund for the duplicate transaction.",
                categoryId = "cat-1",
                categoryName = "Billing & Payments",
                priority = Priority.HIGH,
                status = ComplaintStatus.RESOLVED,
                submittedByName = "Elena Rostova",
                submittedByEmail = "elena.r@acme-corp.com",
                assignedStaff = _staffList.value[0], // Sarah
                createdAt = "Oct 18, 2026, 9:15 AM",
                updatedAt = "Oct 19, 2026, 2:40 PM",
                attachmentNames = listOf("invoice_duplicate_charge.pdf"),
                messages = listOf(
                    MessageComment(
                        id = "msg-101",
                        complaintId = "RES-1042",
                        senderName = "System",
                        senderRole = SenderRole.SYSTEM,
                        isInternal = false,
                        content = "Ticket RES-1042 created and assigned to Customer Care queue.",
                        formattedTime = "Oct 18, 2026, 9:15 AM"
                    ),
                    MessageComment(
                        id = "msg-102",
                        complaintId = "RES-1042",
                        senderName = "Sarah Jenkins",
                        senderRole = SenderRole.STAFF,
                        isInternal = false,
                        content = "Hello Elena, thank you for reaching out. I reviewed the transaction logs and verified the duplicate webhook trigger. I have initiated a refund of $1,200 back to your card ending in 8841.",
                        formattedTime = "Oct 18, 2026, 11:20 AM"
                    ),
                    MessageComment(
                        id = "msg-103",
                        complaintId = "RES-1042",
                        senderName = "Elena Rostova",
                        senderRole = SenderRole.USER,
                        isInternal = false,
                        content = "Thank you so much Sarah! I see the refund pending on our banking portal already. Appreciate the swift turnaround.",
                        formattedTime = "Oct 19, 2026, 1:10 PM"
                    ),
                    MessageComment(
                        id = "msg-104",
                        complaintId = "RES-1042",
                        senderName = "Sarah Jenkins",
                        senderRole = SenderRole.STAFF,
                        isInternal = false,
                        content = "Wonderful! I am marking this complaint as Resolved. Please let us know if you need anything else.",
                        formattedTime = "Oct 19, 2026, 2:40 PM"
                    )
                ),
                internalNotes = listOf(
                    MessageComment(
                        id = "int-101",
                        complaintId = "RES-1042",
                        senderName = "Sarah Jenkins",
                        senderRole = SenderRole.STAFF,
                        isInternal = true,
                        content = "Stripe refund ref #re_3N82bX981a. Reached out to engineering regarding duplicate webhook retry behavior.",
                        formattedTime = "Oct 18, 2026, 11:18 AM"
                    )
                ),
                feedback = FeedbackRating(
                    complaintId = "RES-1042",
                    rating = 5,
                    comment = "Incredible service! Resolved in less than 24 hours with clear updates from Sarah.",
                    submittedAt = "Oct 19, 2026, 3:00 PM"
                )
            ),
            Complaint(
                id = "RES-1043",
                subject = "Checkout gateway returning HTTP 504 on iOS client",
                description = "During checkout on the mobile app, hitting 'Confirm Purchase' hangs for 60 seconds and returns a 504 Gateway Timeout. Multiple customers have reported failed purchases.",
                categoryId = "cat-2",
                categoryName = "Technical & App Issues",
                priority = Priority.URGENT,
                status = ComplaintStatus.ESCALATED,
                submittedByName = "Marcus Vance",
                submittedByEmail = "marcus.v@fintech-partners.io",
                assignedStaff = _staffList.value[1], // David Chen
                createdAt = "Oct 21, 2026, 8:45 AM",
                updatedAt = "Oct 22, 2026, 7:15 AM",
                attachmentNames = listOf("crash_log_checkout.txt", "screenshot_504.png"),
                messages = listOf(
                    MessageComment(
                        id = "msg-201",
                        complaintId = "RES-1043",
                        senderName = "System",
                        senderRole = SenderRole.SYSTEM,
                        isInternal = false,
                        content = "Ticket RES-1043 created. Priority automatically set to URGENT based on system keywords.",
                        formattedTime = "Oct 21, 2026, 8:45 AM"
                    ),
                    MessageComment(
                        id = "msg-202",
                        complaintId = "RES-1043",
                        senderName = "David Chen",
                        senderRole = SenderRole.STAFF,
                        isInternal = false,
                        content = "Hi Marcus, our backend telemetry identified elevated latency on EU payment proxy node #3. We have escalated this ticket to Tier-3 infrastructure on-call.",
                        formattedTime = "Oct 21, 2026, 9:30 AM"
                    ),
                    MessageComment(
                        id = "msg-203",
                        complaintId = "RES-1043",
                        senderName = "Marcus Vance",
                        senderRole = SenderRole.USER,
                        isInternal = false,
                        content = "Thanks for the update. Is there a temporary bypass or should we advise users to checkout via desktop?",
                        formattedTime = "Oct 21, 2026, 10:15 AM"
                    ),
                    MessageComment(
                        id = "msg-204",
                        complaintId = "RES-1043",
                        senderName = "David Chen",
                        senderRole = SenderRole.STAFF,
                        isInternal = false,
                        content = "Web checkout is routing through US-East clusters and is currently healthy. Please advise desktop fallback while we finish the patch.",
                        formattedTime = "Oct 21, 2026, 11:00 AM"
                    )
                ),
                internalNotes = listOf(
                    MessageComment(
                        id = "int-201",
                        complaintId = "RES-1043",
                        senderName = "David Chen",
                        senderRole = SenderRole.STAFF,
                        isInternal = true,
                        content = "PagerDuty incident #98124 opened. Upstream CDN connection pool exhaustion suspected.",
                        formattedTime = "Oct 21, 2026, 9:25 AM"
                    )
                ),
                feedback = null
            ),
            Complaint(
                id = "RES-1044",
                subject = "Missing power adapter and damaged shipping packaging",
                description = "Our replacement terminal package arrived this morning (Tracking #TRK-882194). The outer carton was torn and the 65W USB-C power supply was missing from the box.",
                categoryId = "cat-3",
                categoryName = "Delivery & Hardware",
                priority = Priority.MEDIUM,
                status = ComplaintStatus.IN_PROGRESS,
                submittedByName = "Chloe Mitchell",
                submittedByEmail = "chloe.m@designhaus.co",
                assignedStaff = _staffList.value[2], // Maria Gomez
                createdAt = "Oct 21, 2026, 1:30 PM",
                updatedAt = "Oct 22, 2026, 6:50 AM",
                attachmentNames = listOf("damaged_box_photo.jpg"),
                messages = listOf(
                    MessageComment(
                        id = "msg-301",
                        complaintId = "RES-1044",
                        senderName = "System",
                        senderRole = SenderRole.SYSTEM,
                        isInternal = false,
                        content = "Ticket RES-1044 registered in Delivery queue.",
                        formattedTime = "Oct 21, 2026, 1:30 PM"
                    ),
                    MessageComment(
                        id = "msg-302",
                        complaintId = "RES-1044",
                        senderName = "Maria Gomez",
                        senderRole = SenderRole.STAFF,
                        isInternal = false,
                        content = "Hello Chloe, sincere apologies for the packaging damage. I have dispatched a replacement 65W power adapter via Priority Overnight courier.",
                        formattedTime = "Oct 21, 2026, 3:15 PM"
                    )
                ),
                internalNotes = listOf(
                    MessageComment(
                        id = "int-301",
                        complaintId = "RES-1044",
                        senderName = "Maria Gomez",
                        senderRole = SenderRole.STAFF,
                        isInternal = true,
                        content = "Filed claim with FedEx #CL-29918. Warehouse team alerted about tape quality.",
                        formattedTime = "Oct 21, 2026, 3:10 PM"
                    )
                ),
                feedback = null
            ),
            Complaint(
                id = "RES-1045",
                subject = "Biometric access sensor failure at North entrance",
                description = "The RFID/NFC sensor at the main turnstile fails to detect standard keycards. Multiple employees were delayed this morning.",
                categoryId = "cat-5",
                categoryName = "Facilities & Premises",
                priority = Priority.LOW,
                status = ComplaintStatus.NEW,
                submittedByName = "Arthur Pendelton",
                submittedByEmail = "a.pendelton@corp.net",
                assignedStaff = null,
                createdAt = "Oct 22, 2026, 7:10 AM",
                updatedAt = "Oct 22, 2026, 7:10 AM",
                attachmentNames = emptyList(),
                messages = listOf(
                    MessageComment(
                        id = "msg-401",
                        complaintId = "RES-1045",
                        senderName = "System",
                        senderRole = SenderRole.SYSTEM,
                        isInternal = false,
                        content = "Ticket RES-1045 submitted. Waiting for staff assignment.",
                        formattedTime = "Oct 22, 2026, 7:10 AM"
                    )
                ),
                internalNotes = emptyList(),
                feedback = null
            ),
            Complaint(
                id = "RES-1046",
                subject = "Unable to reset multi-factor authentication token",
                description = "I lost access to my old Google Authenticator device and cannot log into the admin console. The SMS backup code never arrives.",
                categoryId = "cat-4",
                categoryName = "Account & Security",
                priority = Priority.HIGH,
                status = ComplaintStatus.IN_PROGRESS,
                submittedByName = "Sophia Lin",
                submittedByEmail = "sophia.lin@venturecap.org",
                assignedStaff = _staffList.value[3], // Alex
                createdAt = "Oct 21, 2026, 4:20 PM",
                updatedAt = "Oct 22, 2026, 7:45 AM",
                attachmentNames = emptyList(),
                messages = listOf(
                    MessageComment(
                        id = "msg-501",
                        complaintId = "RES-1046",
                        senderName = "System",
                        senderRole = SenderRole.SYSTEM,
                        isInternal = false,
                        content = "Ticket RES-1046 created. Assigned to Alex Rivera.",
                        formattedTime = "Oct 21, 2026, 4:20 PM"
                    ),
                    MessageComment(
                        id = "msg-502",
                        complaintId = "RES-1046",
                        senderName = "Alex Rivera",
                        senderRole = SenderRole.STAFF,
                        isInternal = false,
                        content = "Hi Sophia, I will need you to verify identity via our secure video link before I can reset your MFA seed.",
                        formattedTime = "Oct 21, 2026, 5:00 PM"
                    )
                ),
                internalNotes = listOf(
                    MessageComment(
                        id = "int-501",
                        complaintId = "RES-1046",
                        senderName = "Alex Rivera",
                        senderRole = SenderRole.STAFF,
                        isInternal = true,
                        content = "Identity doc checked against HR directory. Ready to generate 1-time recovery token.",
                        formattedTime = "Oct 21, 2026, 5:02 PM"
                    )
                ),
                feedback = null
            ),
            Complaint(
                id = "RES-1047",
                subject = "Delayed response from account executive for enterprise tier",
                description = "Requested an account upgrade quotation 6 days ago. Follow-up emails went unanswered until yesterday.",
                categoryId = "cat-6",
                categoryName = "Service Quality & Staff",
                priority = Priority.MEDIUM,
                status = ComplaintStatus.RESOLVED,
                submittedByName = "Julian Scott",
                submittedByEmail = "julian.s@innovate.co",
                assignedStaff = _staffList.value[0],
                createdAt = "Oct 19, 2026, 10:00 AM",
                updatedAt = "Oct 20, 2026, 11:30 AM",
                attachmentNames = emptyList(),
                messages = listOf(
                    MessageComment(
                        id = "msg-601",
                        complaintId = "RES-1047",
                        senderName = "Sarah Jenkins",
                        senderRole = SenderRole.STAFF,
                        isInternal = false,
                        content = "Hi Julian, I apologize for the delay. The account executive was on medical leave. I have personally assigned VP of Sales to contact you today.",
                        formattedTime = "Oct 19, 2026, 11:00 AM"
                    )
                ),
                internalNotes = emptyList(),
                feedback = FeedbackRating(
                    complaintId = "RES-1047",
                    rating = 4,
                    comment = "Handled well once brought to attention. Appreciated the direct escalation.",
                    submittedAt = "Oct 20, 2026, 12:00 PM"
                )
            )
        )
    }

    fun loginAsStaff(staff: StaffMember) {
        _currentLoggedInStaff.value = staff
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
        val nextNum = (_complaints.value.mapNotNull {
            it.id.removePrefix("RES-").toIntOrNull()
        }.maxOrNull() ?: 1047) + 1

        val ticketId = "RES-$nextNum"
        val now = dateFormat.format(Date())
        val category = _categories.value.find { it.id == categoryId }
        val categoryName = category?.name ?: "General"

        val initialMessages = listOf(
            MessageComment(
                id = "msg-${System.currentTimeMillis()}",
                complaintId = ticketId,
                senderName = "System",
                senderRole = SenderRole.SYSTEM,
                isInternal = false,
                content = "Ticket $ticketId registered successfully under $categoryName queue. Priority set to ${priority.displayName}.",
                formattedTime = now
            )
        )

        val newComplaint = Complaint(
            id = ticketId,
            subject = subject.trim(),
            description = description.trim(),
            categoryId = categoryId,
            categoryName = categoryName,
            priority = priority,
            status = ComplaintStatus.NEW,
            submittedByName = userName.ifBlank { "You" },
            submittedByEmail = userEmail.ifBlank { "user@example.com" },
            assignedStaff = null,
            createdAt = now,
            updatedAt = now,
            attachmentNames = attachmentNames,
            messages = initialMessages,
            internalNotes = emptyList(),
            feedback = null
        )

        _complaints.value = listOf(newComplaint) + _complaints.value
        return newComplaint
    }

    fun addMessageComment(
        complaintId: String,
        senderName: String,
        senderRole: SenderRole,
        content: String
    ) {
        val now = dateFormat.format(Date())
        val newComment = MessageComment(
            id = "msg-${System.currentTimeMillis()}",
            complaintId = complaintId,
            senderName = senderName,
            senderRole = senderRole,
            isInternal = false,
            content = content.trim(),
            formattedTime = now
        )

        _complaints.value = _complaints.value.map { comp ->
            if (comp.id == complaintId) {
                comp.copy(
                    messages = comp.messages + newComment,
                    updatedAt = now
                )
            } else comp
        }
    }

    fun addInternalNote(
        complaintId: String,
        authorName: String,
        content: String
    ) {
        val now = dateFormat.format(Date())
        val note = MessageComment(
            id = "int-${System.currentTimeMillis()}",
            complaintId = complaintId,
            senderName = authorName,
            senderRole = SenderRole.STAFF,
            isInternal = true,
            content = content.trim(),
            formattedTime = now
        )

        _complaints.value = _complaints.value.map { comp ->
            if (comp.id == complaintId) {
                comp.copy(
                    internalNotes = comp.internalNotes + note,
                    updatedAt = now
                )
            } else comp
        }
    }

    fun updateComplaintStatus(complaintId: String, newStatus: ComplaintStatus) {
        val now = dateFormat.format(Date())
        val systemMsg = MessageComment(
            id = "sys-${System.currentTimeMillis()}",
            complaintId = complaintId,
            senderName = "System",
            senderRole = SenderRole.SYSTEM,
            isInternal = false,
            content = "Status updated to ${newStatus.displayName}.",
            formattedTime = now
        )

        _complaints.value = _complaints.value.map { comp ->
            if (comp.id == complaintId) {
                comp.copy(
                    status = newStatus,
                    updatedAt = now,
                    messages = comp.messages + systemMsg
                )
            } else comp
        }
    }

    fun assignComplaintStaff(complaintId: String, staff: StaffMember?) {
        val now = dateFormat.format(Date())
        val staffText = staff?.let { "${it.name} (${it.role})" } ?: "Unassigned"
        val systemMsg = MessageComment(
            id = "sys-${System.currentTimeMillis()}",
            complaintId = complaintId,
            senderName = "System",
            senderRole = SenderRole.SYSTEM,
            isInternal = false,
            content = "Ticket assigned to $staffText.",
            formattedTime = now
        )

        _complaints.value = _complaints.value.map { comp ->
            if (comp.id == complaintId) {
                // If moving from NEW to assigned, optionally move to IN_PROGRESS
                val newStatus = if (comp.status == ComplaintStatus.NEW && staff != null) {
                    ComplaintStatus.IN_PROGRESS
                } else comp.status

                comp.copy(
                    assignedStaff = staff,
                    status = newStatus,
                    updatedAt = now,
                    messages = comp.messages + systemMsg
                )
            } else comp
        }
    }

    fun submitFeedback(complaintId: String, rating: Int, comment: String) {
        val now = dateFormat.format(Date())
        val feedback = FeedbackRating(
            complaintId = complaintId,
            rating = rating,
            comment = comment.trim(),
            submittedAt = now
        )

        _complaints.value = _complaints.value.map { comp ->
            if (comp.id == complaintId) {
                comp.copy(feedback = feedback, updatedAt = now)
            } else comp
        }
    }

    fun addCategory(name: String, description: String, slaHours: Int, defaultPriority: Priority) {
        val id = "cat-${System.currentTimeMillis()}"
        val newCat = Category(id, name.trim(), description.trim(), slaHours, defaultPriority, true)
        _categories.value = _categories.value + newCat
    }

    fun updateCategory(updated: Category) {
        _categories.value = _categories.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun deleteCategory(categoryId: String) {
        _categories.value = _categories.value.filter { it.id != categoryId }
    }

    fun getDashboardStats(): DashboardStats {
        val all = _complaints.value
        val open = all.count { it.status == ComplaintStatus.NEW || it.status == ComplaintStatus.IN_PROGRESS }
        val escalated = all.count { it.status == ComplaintStatus.ESCALATED }
        val resolved = all.count { it.status == ComplaintStatus.RESOLVED }

        val feedbackRatings = all.mapNotNull { it.feedback?.rating }
        val avgSat = if (feedbackRatings.isNotEmpty()) {
            feedbackRatings.average()
        } else {
            4.8
        }

        val statusMap = ComplaintStatus.values().associateWith { s ->
            all.count { it.status == s }
        }

        val catMap = _categories.value.associate { cat ->
            cat.name to all.count { it.categoryId == cat.id }
        }

        return DashboardStats(
            openComplaints = open,
            avgResolutionTimeHours = 3.6,
            satisfactionScore = (Math.round(avgSat * 10.0) / 10.0),
            escalatedCount = escalated,
            totalComplaints = all.size,
            resolvedCount = resolved,
            statusBreakdown = statusMap,
            categoryBreakdown = catMap
        )
    }

    companion object {
        val instance by lazy { ComplaintRepository() }
    }
}
