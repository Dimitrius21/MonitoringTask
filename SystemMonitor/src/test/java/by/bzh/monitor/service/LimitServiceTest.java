package by.bzh.monitor.service;

import by.bzh.monitor.domain.dto.LimitInDto;
import by.bzh.monitor.domain.entity.Limit;
import by.bzh.monitor.repository.LimitRepository;
import by.bzh.monitor.util.mapper.LimitMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimitServiceTest {
    @Mock
    private LimitRepository repo;
    @Spy
    private LimitMapper mapper = Mappers.getMapper(LimitMapper.class);

    @InjectMocks
    LimitService service;

    @Test
    void hasLimitFalseTest() {
        boolean contain = service.hasLimit(1, 1);
        Assertions.assertThat(contain).isFalse();
    }

    @Test
    void hasLimitTrueTest() {
        long componentId = 1;
        int level = 1;
        LimitInDto dto = getLimitInDto(componentId, level, 2);
        Limit limit = getLimit(componentId, level, 2);
        when(repo.findByComponentIdAndLevel(componentId, level)).thenReturn(Optional.empty());
        when(repo.save(limit)).thenReturn(limit);
        service.createLimit(dto);
        boolean contain = service.hasLimit(componentId, level);

        Assertions.assertThat(contain).isTrue();
    }


    @Test
    void createLimitTest() {
        long componentId = 1;
        int level = 1;
        LimitInDto dto = getLimitInDto(componentId, level, 2);
        Limit limit = getLimit(componentId, level, 2);
        when(repo.findByComponentIdAndLevel(componentId, level)).thenReturn(Optional.empty());
        when(repo.save(limit)).thenReturn(limit);

        Limit res = service.createLimit(dto);
        boolean contain = service.hasLimit(componentId, level);

        Assertions.assertThat(res).isEqualTo(limit);
        Assertions.assertThat(contain).isTrue();
    }

    @Test
    void updateLimitTest() {
        long componentId = 1;
        int level = 1;
        LimitInDto dto = getLimitInDto(componentId, level, 3);
        Limit limitInDb = getLimit(componentId, level, 2);
        Limit limitForSave = getLimit(componentId, level, 3);
        when(repo.findByComponentIdAndLevel(componentId, level)).thenReturn(Optional.of(limitInDb));
        when(repo.save(limitForSave)).thenReturn(limitForSave);

        Limit res = service.updateLimit(dto);
        Assertions.assertThat(res).isEqualTo(limitForSave);
    }

    @Test
    void changeQuantityTest() {
        long componentId = 1;
        int level = 1;
        int change = 1;
        Limit limitInDb = getLimit(componentId, level, 2);
        Limit limitForSave = getLimit(componentId, level, 2);
        limitForSave.setQuantity(limitInDb.getQuantity() + change);
        when(repo.findByComponentIdAndLevel(componentId, level)).thenReturn(Optional.of(limitInDb));
        when(repo.save(limitForSave)).thenReturn(limitForSave);

        Limit res = service.changeQuantity(componentId, level, change);

        Assertions.assertThat(res).isEqualTo(limitForSave);
    }

    @Test
    void deleteLimitTest() {
        long componentId = 1;
        int level = 1;
        LimitInDto dto = getLimitInDto(componentId, level, 2);
        Limit limit = getLimit(componentId, level, 2);
        when(repo.findByComponentIdAndLevel(componentId, level)).thenReturn(Optional.empty());
        when(repo.save(limit)).thenReturn(limit);
        service.createLimit(dto);

        service.deleteLimit(componentId, level);

        verify(repo).deleteByComponentIdAndLevel(componentId, level);
        boolean contain = service.hasLimit(componentId, level);
        Assertions.assertThat(contain).isFalse();
    }

    @Test
    void getLimitTest() {
        long componentId = 1;
        int level = 1;
        Limit limit = getLimit(componentId, level, 2);
        when(repo.findByComponentIdAndLevel(componentId, level)).thenReturn(Optional.of(limit));
        Limit res = service.getLimit(componentId, level);
        Assertions.assertThat(res).isEqualTo(limit);
    }

    @Test
    void getAllLimitsTest() {
        long componentId = 1;
        int level = 1;
        Limit limit1 = getLimit(componentId, level, 2);
        Limit limit2 = getLimit(componentId, level + 1, 3);
        List<Limit> collection = List.of(limit1, limit2);
        when(repo.findAll()).thenReturn(collection);
        List<Limit> res = service.getAllLimits();
        Assertions.assertThat(res).isEqualTo(collection);
    }

    private Limit getLimit(long componentId, int level, int threshold) {
        Limit limit = new Limit();
        limit.setComponentId(componentId);
        limit.setLevel(level);
        limit.setThreshold(threshold);
        limit.setQuantity(0);
        return limit;
    }

    private LimitInDto getLimitInDto(long componentId, int level, int threshold) {
        LimitInDto dto = new LimitInDto();
        dto.setComponentId(componentId);
        dto.setLevel(level);
        dto.setThreshold(threshold);
        return dto;
    }
}