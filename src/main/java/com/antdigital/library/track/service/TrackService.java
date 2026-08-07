package com.antdigital.library.track.service;

import com.antdigital.library.track.model.vo.TrackStatisticsVO;

/**
 * 埋点统计服务接口。
 *
 * @author library-backend
 */
public interface TrackService {

    /**
     * 保存埋点记录。
     *
     * @param apiPath       接口路径
     * @param userId        用户ID
     * @param userName      用户名
     * @param userType      人员类型
     * @param userLevel     人员层级
     * @param userDepartment 人员部门
     */
    void saveTrackRecord(String apiPath, String userId, String userName,
                         String userType, String userLevel, String userDepartment);

    /**
     * 按维度和图表类型查询统计数据。
     *
     * @param dimension 维度（user_type / user_level / user_department / user_id）
     * @param chartType 图表类型（pie / bar / line）
     * @return 统计结果
     */
    TrackStatisticsVO getStatistics(String dimension, String chartType);
}
