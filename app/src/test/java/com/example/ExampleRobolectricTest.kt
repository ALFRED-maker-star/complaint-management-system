package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ComplaintRepository
import com.example.model.ComplaintStatus
import com.example.model.Priority
import com.example.model.SenderRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ResolveIt", appName)
    }

    @Test
    fun `test submit complaint and thread reply`() {
        val repo = ComplaintRepository.instance
        val newComplaint = repo.submitComplaint(
            subject = "Test API Latency Spike",
            description = "Observed 500ms delay on authentication endpoint",
            categoryId = "cat-2",
            priority = Priority.HIGH,
            attachmentNames = listOf("metric_graph.png"),
            userName = "Test User",
            userEmail = "test@example.com"
        )

        assertNotNull(newComplaint.id)
        assertTrue(newComplaint.id.startsWith("RES-"))
        assertEquals(ComplaintStatus.NEW, newComplaint.status)

        // Add user comment
        repo.addMessageComment(
            complaintId = newComplaint.id,
            senderName = "Test User",
            senderRole = SenderRole.USER,
            content = "Any updates on this?"
        )

        val updated = repo.complaints.value.find { it.id == newComplaint.id }
        assertNotNull(updated)
        assertTrue(updated!!.messages.any { it.content == "Any updates on this?" })

        // Update status to Resolved
        repo.updateComplaintStatus(newComplaint.id, ComplaintStatus.RESOLVED)
        val resolved = repo.complaints.value.find { it.id == newComplaint.id }
        assertEquals(ComplaintStatus.RESOLVED, resolved!!.status)

        // Submit Feedback
        repo.submitFeedback(newComplaint.id, 5, "Great speedy resolution")
        val withFeedback = repo.complaints.value.find { it.id == newComplaint.id }
        assertNotNull(withFeedback!!.feedback)
        assertEquals(5, withFeedback.feedback?.rating)
    }
}
