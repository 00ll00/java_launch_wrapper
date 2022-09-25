#include "string"
#include "windows.h"

#include "oolloo_jlw_ArgLoader.h"

JNIEXPORT jstring JNICALL Java_oolloo_jlw_ArgLoader_getCommandLine (JNIEnv *jenv, jobject) {
    LPWSTR arg_wchar = GetCommandLineW();
    int src_len = wcslen(arg_wchar);
    jchar* arg = new jchar[wcslen(arg_wchar) + 1];
    memset(arg,0,sizeof(jchar)*(src_len+1));
    
    for(int i =0 ;i<src_len;i++)
        memcpy(&arg[i],&arg_wchar[i],2);
    jstring dst = jenv->NewString(arg,src_len);
    delete [] arg;
    return dst;
}