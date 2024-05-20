package com.yanyan.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 图床静态图片URL
 */
public class PicBedConstants {
    public static final String HEAD_DEFAULT_PIC = "https://img2.imgtp.com/2024/04/15/f1XZ8GcI.png";// 默认
    public static final String HEAD_PIC_LUFEI = "https://img2.imgtp.com/2024/05/20/GePPmSPK.jpg"; // 路飞
    public static final String HEAD_PIC_SUOLONG = "https://img2.imgtp.com/2024/05/20/idTQ72Qy.jpg";// 索隆
    public static final String HEAD_PIC_GIRL = "https://img2.imgtp.com/2024/05/20/ukj35qfx.jpg";// 黄帽女孩
    public static final String HEAD_PIC_TUTU = "https://img2.imgtp.com/2024/05/20/lGVW51px.jpg";// 兔兔
    public static final List<String> HEAD_PIC_LIST = new ArrayList<String>() {
        {
            add(HEAD_DEFAULT_PIC);
            add(HEAD_PIC_LUFEI);
            add(HEAD_PIC_SUOLONG);
            add(HEAD_PIC_GIRL);
            add(HEAD_PIC_TUTU);
        }
    };
}