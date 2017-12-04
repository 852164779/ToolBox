package com.oom.tblib.utils;

import java.util.ArrayList;

/**
 * Created by hwl on 2017/08/25.
 */

public class CheckUtil {

    public static boolean checkBlackUrl(String url) {
        ArrayList<String> blackUrl = new ArrayList<>();
        //blackUrl.add("http://45.79.78.178");
        //blackUrl.add("DC7880AB1E12F9A1B1DD48623F490D8D8C0516B862894D3DD9CD4ADB2A7BDBB4");
        //blackUrl.add("aHR0cDovLzQ1Ljc5Ljc4LjE3OA==");
//        blackUrl.add("3HiAqx4S+aGx3UhiP0kNjYwFFrhiiU092c1K2yp727Q=");
        blackUrl.add("O7Y1B8EB8DwNuRC+jBWHyZQvllbYrmEjZZe+pv87aSo=");


        //blackUrl.add("http://ad.m2888.net");
        //blackUrl.add("58106AF96F83ECCED203ACEAE241B53801195607BE65E5C8E17BC8F1CAB9A13C");
        //blackUrl.add("aHR0cDovLzQ1Ljc5Ljc4LjE3OA==");
//        blackUrl.add("WBBq+W+D7M7SA6zq4kG1OAEZVge+ZeXI4XvI8cq5oTw=");
        blackUrl.add("VGePixLtx3vME796gTz6p/PLuYKUNrRunXjAKuNc5cA=");

        //blackUrl.add("http://pic.m2888.net");
        //blackUrl.add("77A0BE43BB13B01891DC520F32603F874B73928CDA99232C32004339791B96E9");
        //blackUrl.add("aHR0cDovLzQ1Ljc5Ljc4LjE3OA==");
//        blackUrl.add("d6C+Q7sTsBiR3FIPMmA/h0tzkozamSMsMgBDOXkbluk=");
        blackUrl.add("/5+1CQd5/UhsVpCTpzmP4nqn9jpBVp/NOhURDfq0xA4=");

        for (int i = 0; i < blackUrl.size(); i++) {
            //            if (url.contains(blackUrl.get(i)) {
            //            if (url.contains(AESUtils.decode(blackUrl.get(i)))) {
            //            if (url.contains(new String(Base64.decode(blackUrl.get(i).getBytes(),Base64.DEFAULT)))) {
//            if (url.contains(EncodeUtil.decryptByAES(blackUrl.get(i)))) {//密匙：abcdefgabcdefg12
            if (url.contains(EncodeUtil.decryptByAES(blackUrl.get(i)))) {//密匙：ZdiJloNq12dfA59q
                return true;
            }
        }

        return false;
    }
}
