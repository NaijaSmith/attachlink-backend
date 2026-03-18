# AttachLink Backend Refactor: Single User Entity for Registration

## Progress Tracking
- [x] 1. Create this TODO.md file ✅
- [ ] 2. Edit User.java: Add registrationNumber/course fields, remove @OneToOne profile links, update getRegistrationNumber()
- [ ] 3. Edit AuthService.java: Simplify register() to direct field mapping, remove profile creation methods, single save()
- [ ] 4. Test compilation: mvn compile
- [ ] 5. User deletes Student.java, Supervisor.java, Employer.java
- [ ] 6. Update database schema (add columns to users table)
- [ ] 7. Test registration endpoint
- [ ] 8. Mark complete

**Code edits complete. Compilation tested (minor formatting fixed).

Next: 
- Delete Student.java, Supervisor.java, Employer.java files
- Add DB columns: registration_number VARCHAR, course VARCHAR to users table
- Test /api/auth/register endpoint
