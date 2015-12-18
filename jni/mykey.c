#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

jstring Java_com_yikang_app_yikangserver_utils_AES_getKey(JNIEnv* env, jobject obj){
	//c语言的字符串
	char* cstr = "1234567890abcDEF";
	//把C语言的字符串转换成java的字符串
	// jstring     (*NewStringUTF)(JNIEnv*, const char*);
//	jstring jstr = (*(*env)).NewStringUTF(env, cstr);
	jstring jstr = (*env)->NewStringUTF(env, cstr);
	return jstr;
}
