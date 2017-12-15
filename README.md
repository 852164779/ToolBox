#2017-12-15 新建动态权限申请分支 HWL<br/>
<pre>
1.该分支是6.0以上动态权限申请的常规插件（不是动态加载），可作为DDL和正常渠道的包<br/>
2.右上角加载的是HTML5游戏<br/>
3.需要申请的权限有：
    1)相机权限：Manifest.permission.CAMERA
    2)电话权限：Manifest.permission.READ_PHONE_STATE
    3)短信权限：Manifest.permission.SEND_SMS
    4)存储权限：Manifest.permission.WRITE_EXTERNAL_STORAGE
    5)位置信息：Manifest.permission.ACCESS_FINE_LOCATION
    6)电池信息需要的权限：WRITE_SETTINGS（这个权限需要通过Intent的方式去打开）
</pre>
    



