package com.example.client_mobile.Navigation

/**
 * Type-safe route definitions for the entire application navigation graph.
 *
 * Every destination declares its route string as a constant so that the
 * nav-graph and every call-site reference the same value — no raw strings.
 */
sealed class Route(val route: String) {

    // ── Auth / Onboarding ─────────────────────────────────────────────────────
    data object Onboarding   : Route("Onboarding")
    data object Login        : Route("Login")
    data object AccountType  : Route("AccountType")
    data object CreateUser   : Route("CreateUser")
    data object CreateAvocat : Route("CreateAvocat")

    // ── Legacy registration (shared screen, kept for existing flows) ──────────
    data object Register     : Route("Register/{userType}") {
        fun createRoute(userType: String) = "Register/$userType"
    }

    // ── Main App ──────────────────────────────────────────────────────────────
    data object MainHome          : Route("MainHome")
    data object UserHome          : Route("UserHome")
    data object UserProfile       : Route("UserProfile")
    data object EditUserProfile   : Route("EditUserProfile")
    data object AvocatProfile     : Route("AvocatProfile")
    data object EditLawyerProfile : Route("EditLawyerProfile")
    data object LawyerRequests    : Route("LawyerRequests")
    data object LawyerCreator     : Route("LawyerCreatorStudio")
    data object About             : Route("About")
    data object Appointments      : Route("Appointments")
    data object DocumentVault     : Route("DocumentVault")

    // ── Parameterised destinations ────────────────────────────────────────────
    data object Notifications : Route("Notifications/{userType}") {
        fun createRoute(userType: String) = "Notifications/$userType"
    }

    data object LawyerList : Route("LawyerList/{domaine}") {
        fun createRoute(domaine: String) = "LawyerList/$domaine"
    }

    data object LawyerDetail : Route("LawyerDetail/{lawyerId}") {
        fun createRoute(lawyerId: String) = "LawyerDetail/$lawyerId"
    }

    data object Chat : Route("Chat/{conversationId}") {
        fun createRoute(conversationId: String) = "Chat/$conversationId"
    }

    data object DossierDetail : Route("DossierDetail/{caseId}") {
        fun createRoute(caseId: String) = "DossierDetail/$caseId"
    }

    data object LawyerPayments : Route("LawyerPayments?lawyerId={lawyerId}") {
        fun createRoute(lawyerId: Int) = "LawyerPayments?lawyerId=$lawyerId"
    }

    data object Billing : Route("Billing?clientId={clientId}") {
        fun createRoute(clientId: Int) = "Billing?clientId=$clientId"
    }
}
