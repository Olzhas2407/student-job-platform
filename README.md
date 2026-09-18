# Product

We build a student job application management system.

# Core item

People track student job applications.

# Status table

| From | To | Result |
|---|---|---|
| DRAFT | SUBMITTED | Allowed |
| SUBMITTED | APPROVED | Allowed |
| DRAFT | APPROVED | Forbidden |
| APPROVED | DRAFT | Forbidden |

# Forbidden — why

DRAFT → APPROVED is forbidden because an application must be submitted before it can be approved.

APPROVED → DRAFT is forbidden because an approved application cannot return to draft status.