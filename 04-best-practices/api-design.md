# 🌐 API Design Best Practices

Designing a good API is about **clarity**, **consistency**, and **predictability**.  
Follow these best practices to make your APIs robust, secure, and easy to use.

---

## 🧩 1. Use RESTful Principles

- Use **resources** (nouns), not actions (verbs).  
  ✅ `/users`  
  ❌ `/getUsers`

- Use **HTTP methods** correctly:
    - `GET` → Retrieve data
    - `POST` → Create resource
    - `PUT` → Update or replace resource
    - `PATCH` → Partially update resource
    - `DELETE` → Remove resource

- Keep URIs simple and hierarchical:
  ```
  /products/{id}/reviews
  /customers/{id}/orders
  ```

---

## 📦 2. Use Consistent Naming Conventions

- Use **lowercase letters** and **hyphens** (`-`) instead of underscores (`_`).
  ```
  /user-profiles
  /product-reviews
  ```

- Use **plural nouns** for collections:
    - ✅ `/products`
    - ❌ `/product`

- Keep resource names consistent across services.

---

## 🧭 3. Version Your API

- Always include a version number in the URL or header.
  ```
  /api/v1/products
  ```
- Never make breaking changes without changing the version.

---

## 🛡️ 4. Handle Errors Gracefully

Use consistent, structured error responses:
```json
{
  "timestamp": "2025-10-29T13:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found",
  "path": "/products/13"
}
```

| HTTP Code | Meaning |
|------------|----------|
| 200 | OK |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 422 | Unprocessable Entity |
| 500 | Internal Server Error |

---

## 🔐 5. Secure Your API

- Use **HTTPS** everywhere.
- Require **authentication & authorization** (e.g., OAuth2, JWT).
- Never expose sensitive data in URLs or error messages.
- Validate all input to prevent injection attacks.

---

## ⚙️ 6. Use Query Parameters for Filtering, Sorting, and Pagination

Example:
```
GET /products?category=books&sort=price,desc&page=2&size=20
```

- **Filtering** → `/products?category=electronics`
- **Sorting** → `/products?sort=price,asc`
- **Pagination** → `/products?page=1&size=10`

Return metadata:
```json
{
  "content": [ ... ],
  "page": 1,
  "size": 10,
  "totalPages": 5,
  "totalElements": 50
}
```

---

## 🧾 7. Use Standard Data Formats

- Prefer **JSON** over XML.
- Use **camelCase** for field names.
- Always specify `Content-Type` and `Accept` headers:
  ```
  Content-Type: application/json
  Accept: application/json
  ```

---

## 🧱 8. Design for Extensibility

- Anticipate change: add optional fields, not breaking ones.
- Use **HAL**, **JSON:API**, or **OpenAPI (Swagger)** for consistent structure and documentation.
- Support **hypermedia links** when useful.

---

## 📘 9. Provide Clear Documentation

- Use **OpenAPI (Swagger)** to generate API docs.
- Include:
    - Authentication details
    - Example requests and responses
    - Error message samples
- Keep docs **up to date** with each API version.

---

## 🧪 10. Ensure Proper Testing and Monitoring

- Write tests for:
    - ✅ Unit
    - ✅ Integration
    - ✅ End-to-End
- Use tools like **Postman**, **Newman**, **RestAssured**, or **Karate**.
- Add **logging** and **metrics** (e.g., Prometheus, ELK, OpenTelemetry).

---

## 🚀 11. Optimize for Performance

- Use **caching** (ETag, Cache-Control).
- Compress responses (GZIP).
- Limit payload size and use pagination.
- Avoid N+1 calls — use aggregation endpoints or GraphQL if needed.

---

## 🧮 12. Maintain Backward Compatibility

- Avoid breaking changes to existing clients.
- Mark deprecated endpoints but don’t remove them immediately.
- Provide a clear migration path for new versions.

---

## 🌍 13. Use HATEOAS (When Applicable)

Provide navigation links in responses:
```json
{
  "productId": 1,
  "name": "Book",
  "_links": {
    "self": { "href": "/products/1" },
    "reviews": { "href": "/products/1/reviews" }
  }
}
```

---

## 🧰 14. Keep Responses Lean

- Return only necessary data.
- Avoid deeply nested objects.
- Use lightweight summaries for list responses and full objects for details.

---

## 🧑‍💻 15. Follow Consistent API Lifecycle

1. Design with **OpenAPI / Swagger**.
2. Review collaboratively.
3. Implement and test thoroughly.
4. Deploy versioned endpoints.
5. Monitor and iterate safely.

---

## ✅ Final Thought

> A well-designed API is not only functional but also **predictable**, **secure**, and **developer-friendly**.  
> Simplicity is power — the best APIs are the ones that feel intuitive to use.

---
