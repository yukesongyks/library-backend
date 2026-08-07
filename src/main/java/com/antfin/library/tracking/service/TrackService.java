package com.antfin.library.tracking.service;

import com.antfin.library.tracking.dao.entity.AlgoCallLogEntity;

/**
 * 调用埋点服务
 */
public interface TrackService {

    /**
     * 记录算法调用埋点
     *
     * @param algorithmType 算法类型
     * @param userId        用户ID
     */
    void trackAlgorithmCall(String algorithmType, String userId);
}
