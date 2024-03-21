package by.bzh.monitor.repository;

import by.bzh.monitor.domain.entity.Email;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends CrudRepository<Email, Long> {
    public void deleteByEmail(String email);

    public Optional<Email> findByEmail(String email);

}
