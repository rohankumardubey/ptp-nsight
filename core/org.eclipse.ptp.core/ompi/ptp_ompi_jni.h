/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem */

#ifndef _Included_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
#define _Included_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
#ifdef __cplusplus
extern "C" {
#endif
/* Inaccessible static: failed_load */
/*
 * Class:     org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
 * Method:    OMPIGetError
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem_OMPIGetError
  (JNIEnv *, jobject);

/*
 * Class:     org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
 * Method:    OMPIInit
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem_OMPIInit
  (JNIEnv *, jobject);

/*
 * Class:     org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
 * Method:    OMPIStartDaemon
 * Signature: (Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem_OMPIStartDaemon
  (JNIEnv *, jobject, jstring, jstring, jobjectArray);

/*
 * Class:     org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
 * Method:    OMPIShutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem_OMPIShutdown
  (JNIEnv *, jobject);

/*
 * Class:     org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
 * Method:    OMPIFinalize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem_OMPIFinalize
  (JNIEnv *, jobject);

/*
 * Class:     org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
 * Method:    OMPIProgress
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem_OMPIProgress
  (JNIEnv *, jobject);

/*
 * Class:     org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem
 * Method:    OMPIRun
 * Signature: ([Ljava/lang/String;)V
 */
JNIEXPORT jint JNICALL Java_org_eclipse_ptp_rtsystem_ompi_OMPIControlSystem_OMPIRun
  (JNIEnv *, jobject, jobjectArray);

#ifdef __cplusplus
}
#endif
#endif
