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
const char* RELEASE_SIGN = "308203533082023ba0030201020204519c9c5b300d06092a864886f70d01010b0500305a310d300b060355040613046b657935310d300b060355040813046b657935310d300b060355040713046b657935310d300b060355040a13046b657935310d300b060355040b13046b657935310d300b060355040313046b657935301e170d3137303931393033323335365a170d3432303931333033323335365a305a310d300b060355040613046b657935310d300b060355040813046b657935310d300b060355040713046b657935310d300b060355040a13046b657935310d300b060355040b13046b657935310d300b060355040313046b65793530820122300d06092a864886f70d01010105000382010f003082010a0282010100ac50772c64fb422af2f8078ac06132e7fb0631ff2e95e0ed93ff81c51164b1ff077a1a7c629b3d833a098a78c58ae06b7141915159af46fe8835b957e6c2278122d82b5633696028fc6f9a4532c8de4d628c239a7966b8d7356dd06f507e8cd4731ba1a5ea8758cef8f54f4389e67ee60058f198e7294e83e09149c43b9ee99d7cdba2b274294e5fd7f7b13f47c521f008ae17d5129cecaf2b99f32afdc760d17878ffa0a7fbb213436871bd8d477790dc4d2cfb2e81df36512dbcf169bb044361ad1ef58403b6bed5a8381bc6c4fcbcbb86332da468863e134470efa461fb5b4b13b59111a688b506ebd81fef70d574fe621b0ef97646c20e68577a73b1b28f0203010001a321301f301d0603551d0e04160414a3c18d179aad30de6535727479e7a5843c46a86d300d06092a864886f70d01010b050003820101003cf2ddd3b1584a72b519c2b23a660430e98cf5b849c29b66b600ec37b53a8d931d3ab73978c7d558bb9b0b9b671444b5c97cf7361df113b14c601cd6af8a46bfec1b1f1dd9b76be33b1e314cc1387c2b9b69307ae371e5fe938602a6a9d09456a84fcfa9a21075a9e2cf9b68e767cd5ff0924c33b953b350ba86c368b851a17b06e029c45e7a0897043c210fc683402b7cac6c34d056b1ea8bfb361750ecbcea2d9e52a2040f05a4188419d264031e1f855fd5449a15d4631342aaa19251d3152ce77f3e240229b8be1f6934b53201c7b183a18a481a3d0fcb2653d971efb2ca51c057a45a9b951f940031db23a9f11b51d4a4d52c63cc8492b6cc05f597bfac";
const char* AUTH_KEY = "5d4d629bfe85709f";
/**
 * 发布的app 签名 的HashCode
 */
const int RELEASE_SIGN_HASHCODE = -332752192;

JNIEXPORT jstring JNICALL Java_das_baod_vxm_jni_Key_getPublicKey
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