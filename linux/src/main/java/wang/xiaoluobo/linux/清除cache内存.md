```sbtshell
[root@bz39 data1]# cat /proc/meminfo
MemTotal:      8184876 kB
MemFree:       2673288 kB
Buffers:        189708 kB
Cached:        5023952 kB
SwapCached:          0 kB
Active:        2966104 kB
Inactive:      2298864 kB
HighTotal:           0 kB
HighFree:            0 kB
LowTotal:      8184876 kB
LowFree:       2673288 kB
SwapTotal:           0 kB
SwapFree:            0 kB
Dirty:             104 kB
Writeback:           0 kB
AnonPages:       51268 kB
Mapped:          26292 kB
Slab:           226196 kB
PageTables:       2336 kB
NFS_Unstable:        0 kB
Bounce:              0 kB
CommitLimit:   4092436 kB
Committed_AS:   530320 kB
VmallocTotal: 34359738367 kB
VmallocUsed:       460 kB
VmallocChunk: 34359737723 kB
HugePages_Total:     0
HugePages_Free:      0
HugePages_Rsvd:      0
Hugepagesize:     2048 kB

[root@bz39 data1]# free -m
             total       used       free     shared    buffers     cached
Mem:          7993       5382       2610          0        185       4906
-/+ buffers/cache:        290       7702
Swap:            0          0          0

[root@bz39 data1]# sync

[root@bz39 data1]# sysctl -w vm.drop_caches=3
vm.drop_caches = 3

[root@bz39 data1]# free -m
             total       used       free     shared    buffers     cached
Mem:          7993        109       7883          0          0         26
-/+ buffers/cache:         82       7910
Swap:            0          0          0
```