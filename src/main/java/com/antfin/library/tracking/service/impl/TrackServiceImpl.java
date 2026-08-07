package com.antfin.library.tracking.service.impl;

import com.antfin.library.common.enums.AlgorithmTypeEnum;
import com.antfin.library.tracking.dao.entity.AlgoCallLogEntity;
import com.antfin.library.tracking.dao.mapper.AlgoCallLogMapper;
import com.antfin.library.tracking.service.TrackService;
import com.antfin.library.usercenter.dao.entity.SysUserEntity;
import com.antfin.library.usercenter.dao.mapper.SysUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 调用埋点服务实现
 */
@Service
public class TrackServiceImpl implements TrackService {

    private static final Logger log = LoggerFactory.getLogger(TrackServiceImpl.class);

    @Autowired
    private AlgoCallLogMapper algoCallLogMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void trackAlgorithmCall(String algorithmType, String userId) {
        if (!AlgorithmTypeEnum.isValid(algorithmType)) {
            log.warn("无效的算法类型，跳过埋点: {}", algorithmType);
            return;
        }
        if (userId == null || userId.isEmpty()) {
            log.warn("用户ID为空，跳过埋点: algorithmType={}", algorithmType);
            return;
        }

        try {
            SysUserEntity user = sysUserMapper.selectByUserId(userId);

            AlgoCallLogEntity entity = new AlgoCallLogEntity();
            entity.setAlgorithmType(algorithmType);
            entity.setUserId(userId);
            entity.setUserType(user != null ? user.getUserType() : "");
            entity.setUserLevel(user != null ? user.getUserLevel() : "");
            entity.setDepartment(user != null ? user.getDepartment() : "");
            entity.setCallTime(new Date());

            algoCallLogMapper.insert(entity);
        } catch (Exception e) {
            // 埋点失败不影响主流程
            log.error("埋点记录失败: algorithmType={}, userId={}", algorithmType, userId, e);
        }
    }
}
