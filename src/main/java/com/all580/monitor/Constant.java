package com.all580.monitor;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 常量
 * @date 2018/5/16 9:27
 */
public class Constant {
    public static final class HistoryStatus {
        /**
         * 通道沉默
         */
        public static final int SILENCE = 1;
        /**
         * 正在报警
         */
        public static final int ALARM = 2;
        /**
         * 恢复正常
         */
        public static final int NORMAL = 3;
    }

    /**
     * 通知类型
     */
    public static final class NoticeType {
        /**
         * 邮箱
         */
        public static final int EMAIL = 1;
        /**
         * 微信公众号
         */
        public static final int WX = 2;
        /**
         * 回调地址
         */
        public static final int URL = 3;
        /**
         * 钉钉
         */
        public static final int DING = 4;
    }
}
