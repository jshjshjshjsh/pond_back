package com.itjamz.pond_back.calendar.infra.handler;

import com.itjamz.pond_back.calendar.domain.event.WorkSummarySharedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkSummaryNotificationHandler {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkSummarySharedEvent(WorkSummarySharedEvent event) {
        log.info("[알림 발송 시작] 작성자: {}, 연월: {}-{}",
                event.getWriterName(), event.getYear(), event.getMonth());

        try {
            // 실제 메일 전송 등 오래 걸리는 작업을 시뮬레이션 (2초 대기)
            Thread.sleep(2000);

            // 실제 로직이 들어갈 자리 (TeamRepository 조회해서 팀원들에게 메일 / Slack 알림 전송)
            log.info("[알림 발송 완료] ID: {} 번 요약글 알림이 팀원들에게 전송되었습니다.", event.getWorkSummaryId());

        } catch (InterruptedException e) {
            log.error("알림 발송 중 인터럽트 발생", e);
            Thread.currentThread().interrupt();
        }
    }
}
