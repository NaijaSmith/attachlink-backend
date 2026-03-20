# AttachLink Backend File Upload Refactor TODO

## Status: [IN PROGRESS]

### Step 1: [DONE] Update StorageService.java
- Change store() to return relative path "logs/{studentId}/{filename}"

### Step 2: [DONE] Update LogEntryService.java  
- Add log.setAttachmentOriginalName(file.getOriginalFilename());
- Restrict ALLOWED_EXTENSIONS to "pdf", "docx"

### Step 3: [PENDING] Add download endpoint to LogEntryController.java
- @GetMapping("/download/{*path}")
- Serve from "upload-dir/{path}", attachment disposition

### Step 4: [PENDING] Test changes
- mvn compile
- Manual test multipart POST /api/logs
- Verify relative path in DB, download works

### Step 5: [PENDING] attempt_completion
