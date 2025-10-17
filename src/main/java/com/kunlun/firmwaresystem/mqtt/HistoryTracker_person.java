package com.kunlun.firmwaresystem.mqtt;

import com.kunlun.firmwaresystem.entity.History;
import com.kunlun.firmwaresystem.entity.Person;
import com.kunlun.firmwaresystem.mappers.HistoryMapper;
import com.kunlun.firmwaresystem.sql.History_Sql;

import java.util.Objects;

import static com.kunlun.firmwaresystem.NewSystemApplication.historyMap;
import static com.kunlun.firmwaresystem.NewSystemApplication.myPrintln;

public class HistoryTracker_person {
    // 使用线程安全的ConcurrentHashMap

    private final HistoryMapper historyMapper;
    private final History_Sql historyService;

    // 可配置的时间阈值(毫秒)
    private static final long TIME_THRESHOLD_MS = 120000;

    public HistoryTracker_person(HistoryMapper historyMapper, History_Sql historyService) {
        this.historyMapper = historyMapper;
        this.historyService = historyService;
    }
    /**
     * 处理人员位置更新，记录或更新历史轨迹
     * @param person 包含位置信息的人员对象
     */
    public void processPersonUpdate(Person person) {
        if (!isValidPerson(person)) {
            myPrintln("直接返回");
            return;
        }
        String idCard = person.getIdcard();
        History existingHistory = historyMap.get(idCard);
        if (existingHistory == null) {
            createNewHistoryRecord(person);
        } else {
            updateExistingHistory(person, existingHistory);
        }
    }
    // 验证人员信息有效性
    private boolean isValidPerson(Person person) {
        return person != null
                && person.getMap_key() != null
                && !person.getMap_key().isEmpty()
                && person.getIdcard() != null;
    }
    // 创建新的历史记录
    private void createNewHistoryRecord(Person person) {
        History newHistory = buildHistoryFromPerson(person);
        try {
          //  myPrintln("Add new historical records");
            historyService.addHistory(historyMapper, newHistory);
            historyMap.put(person.getIdcard(), newHistory);
           // myPrintln("Add new historical records   "+newHistory.getId());

        } catch (Exception e) {
            logError("创建历史记录失败", e);
        }
    }
    // 更新现有历史记录
    private void updateExistingHistory(Person person, History existingHistory) {
        if (isSameStation(person, existingHistory)) {
            handleSameStationUpdate(person, existingHistory);
        } else {
            handleStationSwitch(person, existingHistory);
        }
    }
    // 处理同一基站内的更新
    private void handleSameStationUpdate(Person person, History existingHistory) {

        long currentTime = System.currentTimeMillis();
        long timeSinceLastUpdate = currentTime - existingHistory.getEnd_time();

        if (timeSinceLastUpdate > TIME_THRESHOLD_MS) {
            // 超时视为新记录
            createNewHistoryAfterUpdate(existingHistory, person);
        } else {
            // 连续更新
            updateCurrentHistory(existingHistory, person, currentTime);
        }
    }
    // 处理基站切换
    private void handleStationSwitch(Person person, History existingHistory) {
        createNewHistoryAfterUpdate(existingHistory, person);
    }

    // 更新现有记录后创建新记录
    private void createNewHistoryAfterUpdate(History existingHistory, Person person) {
        try {
            // 先更新旧记录
          //  existingHistory.setEnd_time(System.currentTimeMillis());
            int updateStatus = historyMapper.updateById(existingHistory);
            logDebug("更新历史记录状态: " + updateStatus);

            // 再创建新记录
            createNewHistoryRecord(person);
        } catch (Exception e) {
            logError("更新历史记录失败", e);
        }
    }

    // 更新当前记录信息
    private void updateCurrentHistory(History history, Person person, long currentTime) {
        history.setEnd_time(currentTime);
        history.setX(person.getX());
        history.setY(person.getY());
        // 注意: 这里没有立即持久化，可以考虑定时批量更新
    }

    // 构建History对象
    private History buildHistoryFromPerson(Person person) {
        History history = new History();
        history.setMap_key(person.getMap_key());
        history.setSn(person.getIdcard());
        long now = System.currentTimeMillis();
        history.setStart_time(now);
        history.setEnd_time(now);
        history.setType("person");
        history.setX(person.getX());
        history.setY(person.getY());
        history.setProject_key(person.getProject_key());
        history.setName(person.getName());
        history.setStation_mac(person.getStation_mac());
        history.setCompany_id(person.getCompany_id());
        return history;
    }
    // 检查是否同一基站
    private boolean isSameStation(Person person, History history) {
        return Objects.equals(person.getStation_mac(), history.getStation_mac());
    }

    private void logDebug(String message) {
      //  System.out.println("[DEBUG] " + message);
        // 实际项目中应使用日志框架如SLF4J
    }

    private void logError(String message, Exception e) {
       // System.err.println("[ERROR] " + message);
        e.printStackTrace();
        // 实际项目中应使用日志框架如SLF4J
    }
}