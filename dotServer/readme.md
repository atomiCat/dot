服务端
======
打包测试
--------
* 1.运行build-dx，会在build下打包生成shd.jar和shd.dex  
* 2.运行push-to-device 会将shd.dex发送到已经连接到adb设备的/data/local/tmp目录下  
* 3.运行adb-run会在android设备上执行shd.dex，按ctrl+c停止  

打包至app中
* 1.运行build-dx
* 2.运行copy-to-app会将shd.dex复制到app中的assets目录中，然后正常打包app即可