
# File Upload API (Spring Boot + PostgreSQL)

## Purpose

Build a centralized **File Upload API** that external applications can call to upload and retrieve files, so those applications do **not** store or manage files themselves. This service is responsible for authentication, validation, storage, and metadata tracking.

any client-facing application can use the api


---

## High-Level Architecture

**Clients (Other Applications)**
→ REST API (File Upload Service)
→ Local File System (Shared Base Path)
→ PostgreSQL (Metadata, Users, API Keys)

Key principles:

* Stateless REST API
* Files stored on disk, not in the database
* Database stores metadata and access control
* API-key–based authentication

---

## Phase 1: Requirements Clarification

Before building, lock these decisions:

* Maximum file size (per file and per request)
 - 50mb/file
* Allowed file types (by extension and/or MIME type)
 - documents file types (excel,pdf, word, png, jpg, mpeg)
* Expected upload volume (files/day)
- maybe 100 files per day
* Retention policy (delete, archive, or permanent)
 - delete every 6 mos
* Read access requirements (download, list only, upload-only)
 - upload, download, and view documents.
This prevents accidental DoS-by-design.

---

## Phase 2: Data Model Design (PostgreSQL)

### Core Tables

1. **users**

   * Represents external systems or tenants, not humans
   * Each user owns one or more API keys

2. **api_keys**

   * One-to-many with users
   * Keys are stored **hashed**, never plaintext
   * Status flags: active, revoked, expired

3. **stored_files**

   * Stores metadata only
   * Examples of metadata:

     * Original filename
     * Stored filename (UUID-based)
     * File size
     * MIME type
     * Upload timestamp
     * Owner (user)
     * Storage path

4. **upload_batches** (optional but recommended)

   * Groups multi-file uploads
   * Useful for traceability and retries

Design goal: **the database never stores file content**.

---

## Phase 3: File Storage Strategy

### Storage Rules

* Single configurable **base directory**, e.g. `/data/uploads`
* All files live under this base path
* Subdirectory strategy:

  * By date (YYYY/MM/DD)
  * Or by user ID
  * Or hybrid (recommended)

Example (conceptual):

* `/uploads/{user-id}/{yyyy}/{mm}/{uuid}`

### Naming Strategy

* Never trust client filenames
* Use generated identifiers (UUID)
* Preserve original filename in metadata only

This avoids:

* Name collisions
* Path traversal attacks
* OS-level conflicts

---

## Phase 4: API Authentication (API Keys)

### Authentication Model

* Clients send API key via HTTP header
* No sessions, no cookies
* Every request is authenticated

### Validation Flow

1. Extract API key from request header
2. Hash and compare with stored keys
3. Validate:

   * Active
   * Not expired
   * Linked to a valid user
4. Attach user context to request

If authentication fails, **reject early**.

---

## Phase 5: REST API Design

### Core Endpoints (Conceptual)

1. **Upload files**

   * Accepts multipart/form-data
   * Supports multiple files in one request
   * Optional metadata per file or per batch

2. **List files**

   * Filter by user, date, batch, or filename

3. **Download file**

   * Streams file from disk
   * Authorization enforced per owner

4. **Delete file** (optional)

   * Soft-delete metadata or hard-delete disk file

Design endpoints around **resources**, not actions.

---

## Phase 6: Multi-File Upload Handling

### Request Handling

* Accept multiple files in a single request
* Treat the request as a single transaction boundary

### Processing Strategy

1. Authenticate request
2. Validate all files first (size, type)
3. Create upload batch record
4. Save files to disk
5. Persist metadata per file
6. Return a structured response

### Failure Strategy

* Partial success vs all-or-nothing must be decided upfront
* Recommended:

  * Fail fast if any file is invalid
  * Avoid partial disk writes when possible

---

## Phase 7: Response Design

### Upload Response Should Include

* Batch ID
* Per-file status
* Stored filename or file ID
* Original filename
* Size
* Error details (if any)

This makes the API usable without guesswork.

---

## Phase 8: Validation & Security

### Input Validation

* File size limits
* File type restrictions
* Filename sanitization
* Request rate limiting (per API key)

### Security Controls

* No direct filesystem exposure
* No directory listing
* Path normalization before access
* Strict ownership checks on download/delete

Assume every client is buggy at best, hostile at worst.

---

## Phase 9: Configuration & Environment

### Externalized Configuration

* Upload base path
* Max file size
* Allowed MIME types
* API key expiration policy

All configurable per environment (dev/test/prod).

---

## Phase 10: Logging & Observability

### Logging

* Upload attempts
* Authentication failures
* Disk write failures
* Batch-level summaries

### Metrics (Optional but Valuable)

* Files uploaded per user
* Average upload size
* Error rates per API key

If something breaks at 2 a.m., logs should answer *why*.

---

## Phase 11: Testing Strategy

### Test Levels

* Unit tests: validation, metadata logic
* Integration tests: multipart uploads
* Security tests: invalid keys, revoked access

### Edge Cases

* Empty upload
* Duplicate filenames
* Interrupted uploads
* Disk full scenario

---

## Phase 12: Documentation for Consumers

Provide a short **API Consumer Guide**:

* How to obtain an API key
* Required headers
* Upload examples
* Response formats
* Error codes

A good API fails loudly and documents clearly.

---

## Final Notes

This service should feel boring.
That is a compliment.

If it is boring, predictable, and hard to misuse—then it is doing its job.
