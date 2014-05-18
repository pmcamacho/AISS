#include <stdlib.h>
#include <jni.h>
#include "protocol.h"
#include "CRYIO_HARDWARE_JNI.h"


JNIEXPORT void JNICALL Java_CRYIO_1HARDWARE_1JNI_init
  (JNIEnv * env, jobject obj, jlong kn, jlong m, jbyteArray key_Java, jbyteArray IV_Java){
	
	unsigned int keynum = (unsigned int) kn;
	unsigned int mode = (unsigned int) m;
	
	unsigned char* key_C;	
	unsigned char* IV_C;
 
	//int key_len = (*env)->GetArrayLength(env, key_Java);
	//int IV_len = (*env)->GetArrayLength(env, IV_Java);
	
 	//unsigned char* key_C = new unsigned char[key_len];
	//unsigned char* IV_C = new unsigned char[IV_len];

	key_C = (unsigned char*) (*env)->GetByteArrayElements(env,key_Java, NULL);
	IV_C = (unsigned char*) (*env)->GetByteArrayElements(env,IV_Java, NULL);

	//env->GetByteArrayRegion (array, 0, key_len,jbyte*>(buf));

	init(keynum, mode,key_C,IV_C);

	(*env)->ReleaseByteArrayElements(env, key_Java, (jbyte*) key_C, 0);
	(*env)->ReleaseByteArrayElements(env, IV_Java, (jbyte*) IV_C, 0);
	
	return;

}



JNIEXPORT jchar JNICALL Java_CRYIO_1HARDWARE_1JNI_update
  (JNIEnv * env, jobject obj, jbyteArray in, jlong len, jbyteArray out, jlongArray c){
	
	unsigned char* buffer_in = (unsigned char*) (*env)->GetByteArrayElements(env, in, NULL);
	unsigned int data_len = (unsigned int) len;

	unsigned char* buffer_out = (unsigned char*) (*env)->GetByteArrayElements(env, out, NULL);
	
	unsigned int * n = (unsigned int *) malloc( sizeof (unsigned int));
	//*n = (*env)->GetArrayLength(env, in);

	//memcpy(buffer_out, buffer_in, *n);
	//char retCode = (char) memcmp ( buffer_out, buffer_in, *n);

	char retCode = update(buffer_in,data_len,buffer_out,n);

	//Copia n para c
	long * t_c = (long *) (*env)->GetLongArrayElements(env, c, NULL);
	memcpy(t_c,n,sizeof(long));


	(*env)->ReleaseByteArrayElements(env, in, (jbyte*) buffer_in, 0);
	(*env)->ReleaseByteArrayElements(env, out, (jbyte*) buffer_out, 0);
	(*env)->ReleaseLongArrayElements(env, c, (jlong*) t_c, 0);

	return retCode;

}


JNIEXPORT jchar JNICALL Java_CRYIO_1HARDWARE_1JNI_doFinal
  (JNIEnv * env, jobject obj, jbyteArray in, jlong len, jbyteArray out, jlongArray c){

	unsigned char* buffer_in = (unsigned char*) (*env)->GetByteArrayElements(env, in, NULL);
	unsigned int data_len = (unsigned int) len;

	unsigned char* buffer_out = (unsigned char*) (*env)->GetByteArrayElements(env, out, NULL);
	
	unsigned int * n = (unsigned int *) malloc( sizeof (unsigned int));
	//*n = (*env)->GetArrayLength(env, in);

	//memcpy(buffer_out, buffer_in, *n);
	//char retCode = (char) memcmp ( buffer_out, buffer_in, *n);

	char retCode =  doFinal(buffer_in, data_len, buffer_out, n);

	long * t_c = (long *) (*env)->GetLongArrayElements(env, c, NULL);
	memcpy(t_c,n,sizeof(long));

	(*env)->ReleaseByteArrayElements(env, in, (jbyte*) buffer_in, 0);
	(*env)->ReleaseByteArrayElements(env, out, (jbyte*) buffer_out, 0);
	(*env)->ReleaseLongArrayElements(env, c, (jlong*) t_c, 0);

	return retCode;
}
