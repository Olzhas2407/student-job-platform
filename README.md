# Product

We build a Student Jobs application tracking system.

# Core item

People track an Application from draft to approval.

# Status table

| From | To | Result |
|---|---|---|
| DRAFT | SUBMITTED | Allowed |
| SUBMITTED | APPROVED | Allowed |
| DRAFT | APPROVED | Forbidden |
| APPROVED | DRAFT | Forbidden |

# Forbidden - why

DRAFT → APPROVED is forbidden because an application must be submitted before it can be approved.

APPROVED → DRAFT is forbidden because an approved application cannot return to an editable draft state.

# Package diagram

dto             client           handler          config
(JSON later)   (HTTP later)    (HTTP week 9)      Application
ApplicationService
RuleConfig
\              \                /                 |
\              \              /           injects Rule
\              \            /                  |
domain
ApplicationId
ApplicationStatus
ApplicationPolicy
ApplicationRule
ApplicationTransitionRule
ApprovedApplicationCannotReturnToDraftRule

                    no Spring imports