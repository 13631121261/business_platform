package com.kunlun.firmwaresystem.util;
import com.kunlun.firmwaresystem.entity.Real_Point;

import java.text.SimpleDateFormat;
import java.util.*;

public class PIDTimeTracker {

    public static class TimeSegment {
        long startTime; // 开始时间（毫秒）
        long endTime;   // 结束时间（毫秒）
        long duration;  // 停留时间（毫秒）
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

        public TimeSegment(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = endTime - startTime;
        }

        @Override
        public String toString() {
            return String.format(
                    "[开始: %s, 结束: %s, 停留: %d 秒]",
                    df.format(startTime*1000),  df.format(endTime*1000), duration
            );
        }
    }

    public static Map<Integer, List<TimeSegment>> calculateStayTimeSegments(List<Real_Point> records) {
        Map<Integer, List<TimeSegment>> stayTimeMap = new HashMap<>();
        if (records == null || records.isEmpty()) {
            return stayTimeMap;
        }

        // 按时间戳排序（确保输入有序）
     //   records.sort(Comparator.comparingLong(r -> r.timestamp));

        // 遍历记录，计算停留时间段
        for (int i = 0; i < records.size(); ) {
            Real_Point current = records.get(i);
            int pid = current.getPartol_id();
            long startTime = current.getCreate_time();
            long endTime = startTime;

            // 检查后续记录是否属于同一 p-id 且时间间隔 ≤ 30 秒
            int j = i + 1;
            while (j < records.size()) {
                Real_Point next = records.get(j);
                if (next.getPartol_id()!=pid) {
                    break; // 不同 p-id，结束当前段
                }
                if (next.getCreate_time() - endTime > 30) { // 间隔 > 30 秒，分段
                    break;
                }
                endTime = next.getCreate_time();
                j++;
            }

            // 记录当前时间段
            stayTimeMap.computeIfAbsent(pid, k -> new ArrayList<>())
                    .add(new TimeSegment(startTime, endTime));

            i = j; // 跳到下一个待处理记录
        }

        return stayTimeMap;
    }

    public static void main(String[] args) {
        // 示例数据

    }
}