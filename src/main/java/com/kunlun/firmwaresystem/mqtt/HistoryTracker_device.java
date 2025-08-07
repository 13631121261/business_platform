package com.kunlun.firmwaresystem.mqtt;

import com.kunlun.firmwaresystem.entity.History;

import com.kunlun.firmwaresystem.entity.device.Devicep;
import com.kunlun.firmwaresystem.mappers.HistoryMapper;
import com.kunlun.firmwaresystem.sql.History_Sql;

import java.util.Objects;

import static com.kunlun.firmwaresystem.NewSystemApplication.devicePMap;
import static com.kunlun.firmwaresystem.NewSystemApplication.historyMap;

public class HistoryTracker_device {
    // 使用线程安全的ConcurrentHashMap

    private final HistoryMapper historyMapper;
    private final History_Sql historyService;

    // 可配置的时间阈值(毫秒)
    private static final long TIME_THRESHOLD_MS = 120000;

    public HistoryTracker_device(HistoryMapper historyMapper, History_Sql historyService) {
        this.historyMapper = historyMapper;
        this.historyService = historyService;
    }
    /**
     * 处理人员位置更新，记录或更新历史轨迹
     * @param devicep 包含位置信息的人员对象
     */
    public void processDeviceUpdate(Devicep devicep) {
        if (!isValidDevice(devicep)) {
            return;
        }
        String idCard = devicep.getSn();
        History existingHistory = historyMap.get(idCard);
        if (existingHistory == null) {
            createNewHistoryRecord(devicep);
        } else {
            updateExistingHistory(devicep, existingHistory);
        }
    }
    // 验证人员信息有效性
    private boolean isValidDevice(Devicep devicep) {
        return devicep != null
                && devicep.getMap_key() != null
                && !devicep.getMap_key().isEmpty()
                && devicep.getSn() != null;
    }
    // 创建新的历史记录
    private void createNewHistoryRecord(Devicep devicep) {
        History newHistory = buildHistoryFromDevice(devicep);
        try {
            historyService.addHistory(historyMapper, newHistory);
            historyMap.put(devicep.getSn(), newHistory);
           // logDebug("新建历史记录: " + newHistory.getId());
        } catch (Exception e) {
            logError("创建历史记录失败", e);
        }
    }
    // 更新现有历史记录
    private void updateExistingHistory(Devicep devicep, History existingHistory) {
        if (isSameStation(devicep, existingHistory)) {
            handleSameStationUpdate(devicep, existingHistory);
        } else {
            handleStationSwitch(devicep, existingHistory);
        }
    }
    // 处理同一基站内的更新
    private void handleSameStationUpdate(Devicep devicep, History existingHistory) {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastUpdate = currentTime - existingHistory.getEnd_time();

        if (timeSinceLastUpdate > TIME_THRESHOLD_MS) {
            // 超时视为新记录
            createNewHistoryAfterUpdate(existingHistory, devicep);
        } else {
            // 连续更新
            updateCurrentHistory(existingHistory, devicep, currentTime);
        }
    }
    // 处理基站切换
    private void handleStationSwitch(Devicep devicep, History existingHistory) {
        createNewHistoryAfterUpdate(existingHistory, devicep);
    }

    // 更新现有记录后创建新记录
    private void createNewHistoryAfterUpdate(History existingHistory, Devicep devicep) {
        try {
            // 先更新旧记录
          //  existingHistory.setEnd_time(System.currentTimeMillis());
            int updateStatus = historyMapper.updateById(existingHistory);
            logDebug("更新历史记录状态: " + updateStatus);

            // 再创建新记录
            createNewHistoryRecord(devicep);
        } catch (Exception e) {
            logError("更新历史记录失败", e);
        }
    }

    // 更新当前记录信息
    private void updateCurrentHistory(History history, Devicep devicep, long currentTime) {
        history.setEnd_time(currentTime);
        history.setX(devicep.getX());
        history.setY(devicep.getY());
        // 注意: 这里没有立即持久化，可以考虑定时批量更新
    }

    // 构建History对象
    private History buildHistoryFromDevice(Devicep devicep) {
        History history = new History();
        history.setMap_key(devicep.getMap_key());
        history.setSn(devicep.getSn());
        long now = System.currentTimeMillis();
        history.setStart_time(now);
        history.setEnd_time(now);
        history.setType("device");
        history.setX(devicep.getX());
        history.setY(devicep.getY());
        history.setProject_key(devicep.getProject_key());
        history.setName(devicep.getName());
        history.setStation_mac(devicep.getNear_s_address());
        history.setCompany_id(devicep.getCompany_id());
        return history;
    }
    // 检查是否同一基站
    private boolean isSameStation(Devicep devicep, History history) {
        return Objects.equals(devicep.getNear_s_address(), history.getStation_mac());
    }

    private void logDebug(String message) {
        System.out.println("[DEBUG] " + message);
        // 实际项目中应使用日志框架如SLF4J
    }

    private void logError(String message, Exception e) {
        System.err.println("[ERROR] " + message);
        e.printStackTrace();
        // 实际项目中应使用日志框架如SLF4J
    }
}