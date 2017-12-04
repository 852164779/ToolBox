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
const char* RELEASE_SIGN = "3082033130820219a00302010202043f3a3d23300d06092a864886f70d01010b05003049310b3009060355040613026363310a30080603550408130173310a30080603550407130163310a3008060355040a13016f310a3008060355040b130171310a30080603550403130166301e170d3137303832333033343931365a170d3432303831373033343931365a3049310b3009060355040613026363310a30080603550408130173310a30080603550407130163310a3008060355040a13016f310a3008060355040b130171310a3008060355040313016630820122300d06092a864886f70d01010105000382010f003082010a028201010086cae5e975c82c878c0df35c63a2144a3fde75953a0f8ed2e23b63f804b710cba4fdba10fefd6eee6e619191b65ad8332867d2a8566c6545709100225843b24201a5268a37e1bbe294e37bc46ecbb88e5a875c9059fca521ae010e383b4a2493ad977efbbbff927e31766e77b587a03bb52a249a9de70f1534db8b3a434eb860fe578be0741e084423a97c326b605dfd9b0b2c6c7a908246f5d16d379757ee5bbc5fb2f91193cacecfb835009da57a71432c7df012b3c29b349407e26b2b6b65d219a51f84f70634b514f86d59bc99623e8a275adac404fb729b9b931ace586ca14ae4cba9d448a9523bba5d6d3748c74b3303a7be386bb54917850ee3a0e4730203010001a321301f301d0603551d0e04160414db431edc97eb41703950c473950478b1040f7ad5300d06092a864886f70d01010b050003820101007e78d0ee64de10499c630ad9ad9137174cc42ba441c69dad8c6b720591ae66e50a2688b01f6a7e68477813bd36b22738cace9c4a4f08c175f5080f54cefdaeb0fe0276c8080802c1f0285c6793011d03514aecabc6f7ccde1c2c61748137549254b686aebdc61d089f117186dbf23bb38fa74b16185875608cf2ac3b414f8cb3a9f45a65189848c811696c354f02b3e7cfaaba37ae1cf5b40dc1e0114b9d67d47d72252fe3e9369cb89a0ef556e5d57752dc6d5e8e115199ea560ed7b9d1eae652a098d68c33baeecb61ecf50e65b4dc3e17d5e1a26daf41670c4796d326851c81147fbf32f450c7a207ce5e20c99e2e0dc0aa51eb166dcd1081db9eb6c8b589";
const char* AUTH_KEY = "5d4d629bfe85709f";
/**
 * 发布的app 签名 的HashCode
 */
const int RELEASE_SIGN_HASHCODE = -332752192;

JNIEXPORT jstring JNICALL Java_oom_tblib_sub_jni_Ja_getPublicKey
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