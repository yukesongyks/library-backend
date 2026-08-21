package com.example.library.repository;

import com.example.library.domain.InvocationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface InvocationEventRepository extends JpaRepository<InvocationEvent, Long> {
    List<InvocationEvent> findByCalledAtBetweenOrderByCalledAtAsc(LocalDateTime from, LocalDateTime to);
}
