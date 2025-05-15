package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    // 독립 커밋/롤백 다른 작업이 실패 해도 얘는 무조건 실행 돼야 함
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(){
        logRepository.save(new Log());
    }
}
