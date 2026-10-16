package com.emergency.panicbutton.panic.repository;

import com.emergency.panicbutton.panic.entity.PanicEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PanicEventRepository extends JpaRepository<PanicEvent, UUID> {

    Optional<PanicEvent> findByEventId(String eventId);

    List<PanicEvent> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<PanicEvent> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
