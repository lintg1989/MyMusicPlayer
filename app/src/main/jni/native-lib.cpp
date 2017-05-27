#include <jni.h>
#include <string>
#include <iostream>
using namespace std;

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_lin_mymusicplayer_MainActivity_stringFromJNI(
JNIEnv* env,
jobject /* this */) {
std::string hello = "Hello from C++";
return env->NewStringUTF(hello.c_str());
}
JNIEXPORT jint JNICALL Java_com_lin_mymusicplayer_MainActivity_doAdd(JNIEnv* env, jobject, jint a, jint b){
return (a + b);
}
}