//
// Created by wxmylife on 2017/3/7.
//

#include <jni.h>
#include <string.h>
#include <stdio.h>
#include "com_xxm_sublibrary_jni_Ja.h"

#include <android/log.h> //导入log.h

#define LOG_TAG "love"  //指定打印到logcat的Tag
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

/**
 *这个key是和服务器之间通信的秘钥
 */

/**
 * 发布的app 签名,只有和本签名一致的app 才会返回 AUTH_KEY
 * 这个RELEASE_SIGN的值是上一步用java代码获取的值
 */
const char* RELEASE_SIGN = "308203533082023ba00302010202040a3508b1300d06092a864886f70d01010b0500305a310d300b060355040613046b657937310d300b060355040813046b657937310d300b060355040713046b657937310d300b060355040a13046b657937310d300b060355040b13046b657937310d300b060355040313046b657937301e170d3137303932383036313331355a170d3432303932323036313331355a305a310d300b060355040613046b657937310d300b060355040813046b657937310d300b060355040713046b657937310d300b060355040a13046b657937310d300b060355040b13046b657937310d300b060355040313046b65793730820122300d06092a864886f70d01010105000382010f003082010a028201010098065cd58383dac8dbb970a0e653bbcf5198ead51b24f7ded715658d6deca2db4b9d72adf251e0a7b534f15c1bc75b632b231d8728df04501979f429fbe502d70a7c9591e501aa3d99b6ea4ebc4eecc91fe20644f791e6670dc5fd04487e1e2617f61735c279721ee50cee58d0b4c175cf5ef2546569bcb1b8583b90bc1f7177ef5581f529815d486a15dcfc970c98b1205f761425b5dc1f0fa7acedec4635c9300d4310fc75b73dfa8f772fc4d693fefc3823990f4c85fd63a43dd2213959c403fcc81b3094935c5f923775bcd77d9f24fc26f2cca73d493a4d1c4da889f8e1e24875f0427485dfb77559e1dc2d612f6a6157d20bf2546005d513b4ae64c9730203010001a321301f301d0603551d0e04160414e784b7543c46421ce57ccc59fa51b704720200e9300d06092a864886f70d01010b050003820101006a5ba2e2cb0d0d540560f160c2c1060202ed7e26c976ccf269dc2ec655e0f3c553a9c1eea7a485949813fb576ce1bd0bb075114da06b32a3e2a22336576503e88bca98ac4dc67e470f96274280846103b06ce004a58e79d966e6b07f73b91c3aaac6d1caa130618358fe6df945be9dd7983778adc4427d108e9fa462d6b6022f719e97724dc8396586ac8958f6a90e5947fcad90a462b81250bac9202ed29ee705b57c0da2d106dcc72009a14fbbf8d37a78330049bb3f769d42d99973193fad13fb5b775c20e826056d7063a3aaa9ce13666a96bdd56dcde37b9ec188dc41a050f36cd1181cee7a943f16fb77414697e1c5e480c5a3fb152f4dfe2d59724b0e";
const char* AUTH_KEY = "5d4d629bfe85709f";
/**
 * 发布的app 签名 的HashCode
 */
const int RELEASE_SIGN_HASHCODE = -332752192;

JNIEXPORT jstring JNICALL Java_a_d_b_jni_Ja_getPublicKey
  (JNIEnv *env, jclass jclazz, jobject contextObject){

    jclass native_class = env->GetObjectClass(contextObject);
    jmethodID pm_id = env->GetMethodID(native_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject pm_obj = env->CallObjectMethod(contextObject, pm_id);
    jclass pm_clazz = env->GetObjectClass(pm_obj);
    // 得到 getPackageInfo 方法的 ID
    jmethodID package_info_id = env->GetMethodID(pm_clazz, "getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jclass native_classs = env->GetObjectClass(contextObject);
    jmethodID mId = env->GetMethodID(native_classs, "getPackageName", "()Ljava/lang/String;");
    jstring pkg_str = static_cast<jstring>(env->CallObjectMethod(contextObject, mId));
    // 获得应用包的信息
    jobject pi_obj = env->CallObjectMethod(pm_obj, package_info_id, pkg_str, 64);
    // 获得 PackageInfo 类
    jclass pi_clazz = env->GetObjectClass(pi_obj);
    // 获得签名数组属性的 ID
    jfieldID signatures_fieldId = env->GetFieldID(pi_clazz, "signatures", "[Landroid/content/pm/Signature;");
    jobject signatures_obj = env->GetObjectField(pi_obj, signatures_fieldId);
    jobjectArray signaturesArray = (jobjectArray)signatures_obj;
    jsize size = env->GetArrayLength(signaturesArray);
    jobject signature_obj = env->GetObjectArrayElement(signaturesArray, 0);
    jclass signature_clazz = env->GetObjectClass(signature_obj);

    //第一种方式--检查签名字符串的方式
    jmethodID string_id = env->GetMethodID(signature_clazz, "toCharsString", "()Ljava/lang/String;");
    jstring str = static_cast<jstring>(env->CallObjectMethod(signature_obj, string_id));
    char *c_msg = (char*)env->GetStringUTFChars(str,0);

    if(strcmp(c_msg,RELEASE_SIGN)==0)//签名一致  返回合法的 api key，否则返回错误
    {

        return (env)->NewStringUTF(AUTH_KEY);

    }else
    {
        return (env)->NewStringUTF("error");
    }

    //第二种方式--检查签名的hashCode的方式
    /*
    jmethodID int_hashcode = env->GetMethodID(signature_clazz, "hashCode", "()I");
    jint hashCode = env->CallIntMethod(signature_obj, int_hashcode);
    if(hashCode == RELEASE_SIGN_HASHCODE)
    {
        return (env)->NewStringUTF(AUTH_KEY);
    }else{
        return (env)->NewStringUTF("错误");
    }
     */
}