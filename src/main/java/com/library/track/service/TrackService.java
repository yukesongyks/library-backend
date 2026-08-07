package com.library.track.service;

import com.library.track.mapper.CallRecordMapper;
import com.library.track.model.CallRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 埋点记录服务（S05）
 * 降级兜底：埋点失败仅记日志不阻断主流程（异常兜底方案 6.1.2）
 */
@Service
public class TrackService {

    private static final Logger log = LoggerFactory.getLogger(TrackService.class);

    @Autowired
    private CallRecordMapper callRecordMapper;

    @Value("${demo.track.enabled:true}")
    private boolean trackEnabled;

    /**
     * 记录调用埋点
     */
    public void recordCall(CallRecord record) {
        if (!trackEnabled) {
            return;
        }
        try {
            callRecordMapper.insert(record);
        } catch (Exception e) {
            // 降级：埋点失败仅记日志，不抛出异常
            log.warn("埋点记录失败(降级忽略): bizType={}, callerId={}, error={}",
                    record.getBizType(), record.getCallerId(), e.getMessage());
        }
    }
}
