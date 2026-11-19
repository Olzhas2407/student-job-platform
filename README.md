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

## Lab 5 Security Rules

| Action                  | RECRUITER | MANAGER | No token |
|-------------------------|---|---|---|
| POST /api/v1/auth/login | allowed | allowed | allowed |
| Create application      | allowed | allowed | 401 |
| Read application        | allowed | allowed | 401 |
| Submit application      | 403 | allowed | 401 |
| GET /api/me             | allowed | allowed | 401 |
| GET /actuator/health    | allowed | allowed | allowed |
| Swagger / OpenAPI       | allowed | allowed | allowed |
| Anything else           | 403 | 403 | 401 |