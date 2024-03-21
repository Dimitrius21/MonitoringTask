package by.bzh.monitor.service;

import by.bzh.monitor.domain.dto.LimitInDto;
import by.bzh.monitor.domain.entity.Limit;
import by.bzh.monitor.exception.NotValidRequestParametersException;
import by.bzh.monitor.exception.ResourceNotFountException;
import by.bzh.monitor.repository.LimitRepository;
import by.bzh.monitor.util.mapper.LimitMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LimitService {

    private final Map<Long, List<Integer>> limits = new ConcurrentHashMap<>();
    private final LimitRepository repo;
    private final LimitMapper mapper;

    public LimitService(LimitRepository repo, LimitMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
        Iterable<Limit> limitList = repo.findAll();
        limitList.forEach(this::addLevelToLimits);
        limits.forEach((id, list) -> Collections.sort(list));
    }

    public boolean hasLimit(long componentId, int level) {
        boolean res = false;
        List<Integer> levels = limits.get(componentId);
        if (Objects.nonNull(levels) && Collections.binarySearch(levels, level) >= 0) res = true;
        return res;
    }

    @Transactional
    public Limit createLimit(LimitInDto dto) {
        Limit limit = mapper.toLimit(dto);
        Optional<Limit> limitInDb = repo.findByComponentIdAndLevel(limit.getComponentId(), limit.getLevel());
        if (limitInDb.isPresent()) {
            throw new NotValidRequestParametersException("Limit for " + limitInDb.get().getComponentId() + " already exist");
        }
        limit = repo.save(limit);
        addLevelToLimits(limit);
        //Collections.sort(limits.get(limit.getComponentId()));
        return limit;
    }

    @Transactional
    public Limit updateLimit(LimitInDto dto) {
        Limit limit = mapper.toLimit(dto);
        Limit limitInDb = repo.findByComponentIdAndLevel(limit.getComponentId(), limit.getLevel())
                .orElseThrow(() -> new NotValidRequestParametersException("Limitation with indicated data is absent"));
        limitInDb.setThreshold(limit.getThreshold());
        repo.save(limitInDb);
        return limitInDb;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Limit changeQuantity(long componentId, int level, int qtity) {
        Limit limitInDb = repo.findByComponentIdAndLevel(componentId, level)
                .orElseThrow(() -> new NotValidRequestParametersException("Limitation with indicated data is absent"));
        if (qtity != 0) {
            qtity = limitInDb.getQuantity() + qtity;
        }
        limitInDb.setQuantity(qtity);
        return repo.save(limitInDb);
    }

    @Transactional
    public void deleteLimit(long componentId, int level) {
        repo.deleteByComponentIdAndLevel(componentId, level);
        List<Integer> levels = limits.get(componentId);
        if (Objects.nonNull(levels)) {
            levels.remove(Integer.valueOf(level));
        }
    }

    @Transactional(readOnly = true)
    public Limit getLimit(long componentId, int level) {
        return repo.findByComponentIdAndLevel(componentId, level).orElseThrow(() -> new ResourceNotFountException(
                    String.format("Limitation for indicated component=%d and level=%d not found", componentId, level)));
    }

    @Transactional(readOnly = true)
    public List<Limit> getAllLimits() {
        List<Limit> limitList = new ArrayList<>();
        repo.findAll().forEach(limitList::add);
        return limitList;
    }

    private void addLevelToLimits(Limit limit) {
        long id = limit.getComponentId();
        List<Integer> levels = limits.get(id);
        if (Objects.nonNull(levels)) {
            levels.add(limit.getLevel());
            Collections.sort(levels);
        } else {
            limits.put(id, new ArrayList<>(List.of(limit.getLevel())));
        }
    }

}
