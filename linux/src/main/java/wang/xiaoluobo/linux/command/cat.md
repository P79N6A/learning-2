# cat
> 显示行号
```bash
dongzai1005@Dongzai1005-3:~/soft/hadoop-2.8.4$ cat -n README.txt
     1	For the latest information about Hadoop, please visit our website at:
     2
     3	   http://hadoop.apache.org/core/
     4
     5	and our wiki, at:
     6
     7	   http://wiki.apache.org/hadoop/
     8
     9	This distribution includes cryptographic software.  The country in
    10	which you currently reside may have restrictions on the import,
    11	possession, use, and/or re-export to another country, of
    12	encryption software.  BEFORE using any encryption software, please
    13	check your country's laws, regulations and policies concerning the
    14	import, possession, or use, and re-export of encryption software, to
    15	see if this is permitted.  See <http://www.wassenaar.org/> for more
    16	information.
    17
    18	The U.S. Government Department of Commerce, Bureau of Industry and
    19	Security (BIS), has classified this software as Export Commodity
    20	Control Number (ECCN) 5D002.C.1, which includes information security
    21	software using or performing cryptographic functions with asymmetric
    22	algorithms.  The form and manner of this Apache Software Foundation
    23	distribution makes it eligible for export under the License Exception
    24	ENC Technology Software Unrestricted (TSU) exception (see the BIS
    25	Export Administration Regulations, Section 740.13) for both object
    26	code and source code.
    27
    28	The following provides more details on the included cryptographic
    29	software:
    30	  Hadoop Core uses the SSL libraries from the Jetty project written
    31	by mortbay.org.
```

> 显示行号(忽略空行)
```bash
dongzai1005@Dongzai1005-3:~/soft/hadoop-2.8.4$ cat -b README.txt
     1	For the latest information about Hadoop, please visit our website at:

     2	   http://hadoop.apache.org/core/

     3	and our wiki, at:

     4	   http://wiki.apache.org/hadoop/

     5	This distribution includes cryptographic software.  The country in
     6	which you currently reside may have restrictions on the import,
     7	possession, use, and/or re-export to another country, of
     8	encryption software.  BEFORE using any encryption software, please
     9	check your country's laws, regulations and policies concerning the
    10	import, possession, or use, and re-export of encryption software, to
    11	see if this is permitted.  See <http://www.wassenaar.org/> for more
    12	information.

    13	The U.S. Government Department of Commerce, Bureau of Industry and
    14	Security (BIS), has classified this software as Export Commodity
    15	Control Number (ECCN) 5D002.C.1, which includes information security
    16	software using or performing cryptographic functions with asymmetric
    17	algorithms.  The form and manner of this Apache Software Foundation
    18	distribution makes it eligible for export under the License Exception
    19	ENC Technology Software Unrestricted (TSU) exception (see the BIS
    20	Export Administration Regulations, Section 740.13) for both object
    21	code and source code.

    22	The following provides more details on the included cryptographic
    23	software:
    24	  Hadoop Core uses the SSL libraries from the Jetty project written
    25	by mortbay.org.
```

> 合并多个空行为一个空行
```bash
dongzai1005@Dongzai1005-3:~/shell$ cat -b t
     1	abcdefg





     2	abcdefg
dongzai1005@Dongzai1005-3:~/shell$ cat -sb t
     1	abcdefg

     2	abcdefg
dongzai1005@Dongzai1005-3:~/shell$
```


