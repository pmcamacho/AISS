/***********************************************
Title       : util.c
Design      : T cliente interface
Author      : Ricardo Chaves e José Leitão
Company     : INESC-ID
Date : 12/10/2013
Copyright (C) 2013
************************************************/

#include "util.h"

int InsPadding(unsigned char barr[EPLENGTH],int n)
{
	int i, extra;

	extra =  (n%32 == 0) ?  32 : 32-n%32 ;
	
	for(i = n ; i < (n+extra) ; i++)
		barr[i] = extra;

	return extra;
}

int RemPadding(unsigned char barr[EPLENGTH],int n)
{
	int aux = barr[n-1];
	return aux;
}