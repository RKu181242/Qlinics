package com.example.data.model

enum class UserRole(val displayName: String, val roleSubtitle: String) {
    PATIENT("Patient", "Rehan (Patient View)"),
    RECEPTIONIST("Receptionist", "Sarah (Front Desk Admin)"),
    DOCTOR("Doctor", "Dr. Priya Mehta (Cardiology)")
}

enum class QueueStatus(val label: String) {
    JOINED("Joined"),
    WAITING("Waiting"),
    ALMOST_YOUR_TURN("Almost Your Turn"),
    CALLED("Called - Please Proceed"),
    WITH_DOCTOR("With Doctor"),
    COMPLETED("Completed"),
    SKIPPED("Temporarily Skipped"),
    CANCELLED("Cancelled")
}

enum class VisitMode(val title: String, val description: String) {
    FIXED("Fixed Appointment", "Choose a specific scheduled time slot at the clinic."),
    LIVE_QUEUE("Live Queue", "Join the virtual queue from home and arrive when your turn approaches.")
}

enum class HospitalType(val label: String) {
    ALL("All"),
    MULTI_SPECIALITY("Multi-Speciality"),
    GOVERNMENT("Government"),
    PRIVATE("Private")
}
