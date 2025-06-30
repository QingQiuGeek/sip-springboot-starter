package org.nkk.sip.utils;


import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.stream.Stream;

@UtilityClass
public class PtzUtil {

    public static String getPtzCmd(String ptzCmd, int speed) {
        return getPtzCmd(PtzCmdEnum.resolve(ptzCmd), speed);
    }

    public static String getPtzCmd(PtzCmdEnum ptzCmdEnum, int speed) {
        return getPtzCmd(ptzCmdEnum.getCmdCode(), speed, speed, speed);
    }

    /**
     * 获取控制命令代码
     *
     * @param cmdCode       命令code
     * @param horizonSpeed  水平速度
     * @param verticalSpeed 垂直速度
     * @param zoomSpeed     缩放速度
     * @return
     */
    public static String getPtzCmd(int cmdCode, int horizonSpeed, int verticalSpeed, int zoomSpeed) {
        StringBuilder builder = new StringBuilder("A50F01");

        String strTmp = String.format("%02X", cmdCode);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", horizonSpeed);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", verticalSpeed);
        builder.append(strTmp, 0, 2);
        //优化zoom变倍速率
        if ((zoomSpeed > 0) && (zoomSpeed < 16)) {
            zoomSpeed = 16;
        }
        strTmp = String.format("%X", zoomSpeed);
        builder.append(strTmp, 0, 1).append("0");
        //计算校验码
        int checkCode = (0XA5 + 0X0F + 0X01 + cmdCode + horizonSpeed + verticalSpeed + (zoomSpeed & 0XF0)) % 0X100;
        strTmp = String.format("%02X", checkCode);
        builder.append(strTmp, 0, 2);
        return builder.toString();
    }

    @Getter
    @AllArgsConstructor
    public enum PtzCmdEnum {
        // 向左移动
        LEFT("left", 2),
        // 向右移动
        RIGHT("right", 1),
        // 向上移动
        UP("up", 8),
        // 向下移动
        DOWN("down", 4),
        // 向左上移动
        UPLEFT("upleft", 10),
        // 向右上移动
        UPRIGHT("upright", 9),
        // 向左下移动
        DOWNLEFT("downleft", 6),
        // 向右下移动
        DOWNRIGHT("downright", 5),
        // 放大
        ZOOMIN("zoomin", 16),
        // 缩小
        ZOOMOUT("zoomout", 32);

        // 命令的字符串表示
        private final String command;
        // 命令的数值编码
        private final int cmdCode;

        public static PtzCmdEnum resolve(String cmd) {
            return Stream.of(PtzCmdEnum.values()).filter(item -> StrUtil.equalsIgnoreCase(item.name(), cmd)).findFirst().orElse(null);
        }
    }
}
