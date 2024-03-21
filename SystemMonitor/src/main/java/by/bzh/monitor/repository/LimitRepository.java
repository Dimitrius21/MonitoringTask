package by.bzh.monitor.repository;

import by.bzh.monitor.domain.entity.Limit;
import by.bzh.monitor.domain.entity.LimitKey;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LimitRepository extends CrudRepository<Limit, LimitKey> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Optional<Limit> findByComponentIdAndLevel(long componentId, int level);

    public void deleteByComponentIdAndLevel(long componentId, int level);
}
