# API Design
![Design Good API](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/09-how-to-design-good-api.png)

## Content

- [Home APIs](#home-apis)
- [Application APIs](#application-apis)

## Home APIs

| API                                           | Detail |
|:----------------------------------------------| :--- |
| **GET** `/api/v1/homes?page=[int]&size=[int]` | Get a list of all available homes for browsing. |
| **GET** `/api/v1/homes/{id}`                  | Get detailed information about a specific home using its unique ID (UUID). |
| **GET** `/api/v1/homes/search`                | AI-powered semantic search. Use the `query` parameter (e.g., `?query=modern+apartment`) to find homes via natural language. |
| **POST** `/api/v1/homes`                      | Add a new home listing. Requires a JSON body containing price, beds, baths, sqft, and address details. |
| **DELETE** `/api/v1/homes/{id}`               | Delete a home listing from the system by its unique ID. |

---

## Application APIs

| API                                                | Detail                                                                                                                           |
|:---------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------|
| **POST** `/api/v1/applications`                    | Submit a loan application. This is an atomic operation that triggers the Transactional Outbox pattern for downstream processing. |
| **GET** `/api/v1/applications/{id}`                | Retrieve application details by the internal system ID (UUID).                                                                   |
| **GET** `/api/v1/applications/{applicationNumber}` | Retrieve application details by the human-readable reference number (e.g., APP-2026-X).                                          |
| **GET** `/api/v1/applications`                     | List applications associated with a email. Requires an `email` query parameter.                                                  |
| **DELETE** `/api/v1/applications/{id}`             | Permanently delete a loan application by its internal ID.                                                                        |
