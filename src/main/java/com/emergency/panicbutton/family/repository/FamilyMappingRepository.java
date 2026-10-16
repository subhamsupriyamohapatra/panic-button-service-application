package com.emergency.panicbutton.family.repository;

import com.emergency.panicbutton.family.entity.FamilyMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FamilyMappingRepository extends JpaRepository<FamilyMapping, UUID> {

    @Query("select fm from FamilyMapping fm "
            + "join fetch fm.familyMember "
            + "where fm.user.id = :userId "
            + "order by fm.createdAt")
    List<FamilyMapping> findByUserId(@Param("userId") UUID userId);

    @Query("select fm from FamilyMapping fm "
            + "where fm.user.id = :userId and fm.familyMember.id = :familyMemberId")
    List<FamilyMapping> findByUserIdAndFamilyMemberId(
            @Param("userId") UUID userId,
            @Param("familyMemberId") UUID familyMemberId);
}
