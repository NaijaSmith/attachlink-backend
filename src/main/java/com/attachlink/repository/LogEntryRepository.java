package com.attachlink.repository;

import com.attachlink.entity.LogEntry;
import com.attachlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByStudent(User student);
}
