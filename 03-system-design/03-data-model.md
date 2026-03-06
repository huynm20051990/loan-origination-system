# Data Model

This is data model for EasyApply project.

## Tables

- [homes](#homes)
- [home_embeddings](#home_embeddings)
- [applications](#applications)
- [assessments](#applications)
- [notifications](#notifications)
- [outbox](#outbox)

## `homes`

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY | Unique identifier for the property listing. |
| `price` | NUMERIC(38,2)| NOT NULL | The listing price of the home. |
| `beds` | INT | | Number of bedrooms. |
| `baths` | DOUBLE | | Number of bathrooms. |
| `sqft` | INT | | Total square footage. |
| `status` | VARCHAR(50) | NOT NULL | Current status (e.g., AVAILABLE, SOLD). |
| `street` | VARCHAR(255) | NOT NULL | Property street address. |
| `city` | VARCHAR(255) | NOT NULL | City location. |
| `state_code` | CHAR(2) | NOT NULL | Two-letter state code. |
| `description`| TEXT | | Detailed text about the property. |

## `home_embeddings`

| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | UUID (PK) | Unique identifier. |
| `content` | TEXT | Raw property text processed for embedding. |
| `metadata` | JSONB | Additional filtering attributes. |
| `embedding` | vector(768) | 768-dimension vector for semantic search. |

## `applications`

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY | Unique identifier for the application. |
| `application_number`| VARCHAR(50)| UNIQUE, NOT NULL| Human-readable reference (e.g., APP-2026-X). |
| `home_id` | UUID | NOT NULL | Foreign reference to the `homes` table. |
| `status` | VARCHAR(20) | CHECK | PENDING, APPROVED, REJECTED, SUBMITTED. |
| `full_name` | VARCHAR(255) | NOT NULL | Borrower's legal name. |
| `email` | VARCHAR(255) | NOT NULL | Borrower's contact email. |
| `ssn` | VARCHAR(20) | NOT NULL | Social Security Number for credit checks. |
| `loan_amount` | NUMERIC(15,2)| NOT NULL | The total loan amount requested. |

### `assessments`

| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | UUID (PK) | Unique assessment identifier. |
| `application_id` | UUID | Reference to the submitted application. |
| `status` | VARCHAR(50) | Status of the evaluation process. |
| `decision` | VARCHAR(255)| The final automated outcome or logic result. |

### `notifications`
Audit trail of all outbound communications.

| Column | Type | Description |
| :--- | :--- | :--- |
| `application_number`| VARCHAR(50) | Reference to the associated application. |
| `recipient_identifier`| VARCHAR(255)| Target email address or phone number. |
| `type` | VARCHAR(20) | Channel used: EMAIL or SMS. |
| `content` | TEXT | The message body sent to the borrower. |

### `outbox`

| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | UUID (PK) | Event identifier. |
| `aggregate_type` | VARCHAR(255)| The entity type (e.g., "Application"). |
| `aggregate_id` | VARCHAR(255)| The specific ID of the entity. |
| `type` | VARCHAR(255)| Event type (e.g., "ApplicationSubmitted"). |
| `payload` | JSONB | The actual event data for the message broker. |

---