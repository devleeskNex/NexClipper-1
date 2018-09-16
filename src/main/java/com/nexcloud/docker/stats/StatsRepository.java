package com.nexcloud.docker.stats;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "stats", path = "stats")
public interface StatsRepository extends PagingAndSortingRepository<Stats, StatsId> {

    @Transactional
    @Modifying
    @Query("delete from Stats st where st.id.time < :expiredTime")
    void deleteExpiredStats(@Param("expiredTime") Long expiredTime);

    List<Stats> findById_ContainerId(@Param("containerId") String containerId, Pageable pageable);
}
