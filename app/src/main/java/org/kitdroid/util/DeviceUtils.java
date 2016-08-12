package org.kitdroid.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by 远航 on 2015/9/8.
 * http://blog.csdn.net/aminfo/article/details/7603302
 */
public class DeviceUtils {

    private static final String TAG = "DeviceUtils";

    public static String[] getCpuInfo() {
        String path = "/proc/cpuinfo";
        String tempLine = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(path);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            tempLine = localBufferedReader.readLine();
            arrayOfString = tempLine.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            tempLine = localBufferedReader.readLine();
            arrayOfString = tempLine.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuInfo;
    }

    /*
        第一行是总内存大小(即用户可以使用的ram的大小)！其他各项的介绍大家可以看这儿：
        MemTotal: 所有可用RAM大小。
        MemFree: LowFree与HighFree的总和，被系统留着未使用的内存。
        Buffers: 用来给文件做缓冲大小。
        Cached: 被高速缓冲存储器（cache memory）用的内存的大小（等于diskcache minus SwapCache）。
        SwapCached:被高速缓冲存储器（cache memory）用的交换空间的大小。已经被交换出来的内存，仍然被存放在swapfile中，用来在需要的时候很快的被替换而不需要再次打开I/O端口。
        Active: 在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要，否则不会被移作他用。
        Inactive: 在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径。
        SwapTotal: 交换空间的总大小。
        SwapFree: 未被使用交换空间的大小。
        Dirty: 等待被写回到磁盘的内存大小。
        Writeback: 正在被写回到磁盘的内存大小。
        AnonPages：未映射页的内存大小。
        Mapped: 设备和文件等映射的大小。
        Slab: 内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗。
        SReclaimable:可收回Slab的大小。
        SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）。
        PageTables：管理内存分页页面的索引表的大小。
        NFS_Unstable:不稳定页表的大小。
        要获取android手机总内存大小，只需读取”/proc/meminfo”文件的第1行，并进行简单的字符串处理即可。
     */
    public static void getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2 = "";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                Log.i(TAG, "---" + str2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
