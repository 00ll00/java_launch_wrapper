#include <string>
#include "windows.h"

#include "oolloo_jlw_ArgLoader.h"

void wchar_t_to_string(std::string& szDst, wchar_t* wchar) {
    UINT codePage = GetACP();
    wchar_t *wText = wchar;
    DWORD dwNum = WideCharToMultiByte(codePage, NULL, wText, -1, NULL, 0, NULL, FALSE);
    char *psText;
    psText = new char[dwNum];
    WideCharToMultiByte(codePage, NULL, wText, -1, psText, dwNum, NULL, FALSE);
    szDst = psText;
    delete[]psText;
}

JNIEXPORT jstring JNICALL Java_oolloo_jlw_ArgLoader_getCommandLine (JNIEnv *jenv, jobject) {
    LPWSTR arg_wchar = GetCommandLineW();
    std::string arg;
    wchar_t_to_string(arg, arg_wchar);
    jstring jarg = jenv->NewStringUTF(arg.c_str());
    return jarg;
}