/***********************************************
Title       : main.c
Design      : T cliente interface
Author      : Ricardo Chaves e José Leitão
Company     : INESC-ID
Date : 12/10/2013
Copyright (C) 2013
************************************************/
 
#include "util.h"
//#include "counter.h"
#include "com.h"
#include "protocol.h"
#pragma comment( lib, "wsock32.lib" )     // This will link to wsock32.lib

#define LINEMAX 10
#define SHOW 1
#define HIDE 0
#define USB_MODE 'u'
#define ETH_MODE 'e'

#define ENC_MODE 'c'
#define DEC_MODE 'd'

#define N 2000
#define INC 20

#define MODE FILE
packet_t packet;

int main(int argc, char*argv[])
{

	char src_file[100],c;
	char dest_file[100];
	char ret_code;

	FILE * fp_r,*fp_w;
	int sz;
	char com;
	double time,bsent;


	u32 mode;
	u32 keynum = 2;
	u32 data_len,n;
	u32 inc = 20;

	u8 *Key;
	u32 origkey[8+8];
	u8 IV[32]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	u8 tv[16]={0x33,0x22,0x11,0x00,0x77,0x66,0x55,0x44,0xBB,0xAA,0x99,0x88,0xFF,0xEE,0xDD,0xCC};
	u8 *data_in;
	u8 buffer_in[MAX_DATA_IN];
	u8 buffer_out[MAX_DATA_OUT];
	u32 size_out,i,m,j;

	size_out = 0;
	mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| ENCRYPT_FLAG;




	origkey[3] = 0;
	origkey[2] = 0;
	origkey[1] = 1;
	origkey[0] = 0;
	origkey[4] = 0;
	origkey[5] = 0;
	origkey[6] = 0;
	origkey[7] = 0;

	origkey[3+8] = 0x0c0d0e0f;
	origkey[2+8] = 0x08090a0b;
	origkey[1+8] = 0x04050607;
	origkey[0+8] = 0x00010203;
	origkey[4+8] = 0x10111213;
	origkey[5+8] = 0x14151617;
	origkey[6+8] = 0x18191a1b;
	origkey[7+8] = 0x1c1d1e1f;


/* Paramenter input*/

		if( argc<4 )
		{
			printf("\nUsage: Demo -mode src_file dest_file \n\n__-mode parameters__ \n\t -c: cypher mode\n\t -d: decryption mode \n");
			return -1;
		}

		if(argv[1][0]='-')
		{
			if(argv[1][1]=='c')
				mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| ENCRYPT_FLAG;
			else if(argv[1][1]=='d')
				mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG | DECRYPT_FLAG;
			else
			{
				printf("\nUsage: Demo -mode src_file dest_file \n\n__-mode parameters__ \n\t -c: cypher mode\n\t -d: decryption mode \n");
				return -1;
			}
		}


		strcpy(src_file,argv[2]);
		strcpy(dest_file,argv[3]);


	/* Cipher config*/

		size_out = 0;
		keynum = 0;
		Key = (u8 *)origkey; 


		if(	(fp_r = fopen(src_file,"rb")) != NULL)
		{
				fseek(fp_r, 0L, SEEK_END);
				sz = ftell(fp_r);
				printf("Size %d\n",sz);
				fseek(fp_r, 0L, SEEK_SET);
		}
		else
		{
			printf("\n File %s not found \n",src_file);
			return -1;
		}

		fp_w = fopen(dest_file,"wb");

		// ETH I/O MODE:

					/* Initialization*/
					init(  keynum,  mode, Key ,  IV);

					/* File chipher */
					while( (data_len = fread(buffer_in,1,MAX_DATA_IN,fp_r))>0)
					{
						if(data_len == ERROR_CODE)
						{
							printf("\n Error : fread() \n");
							return -1;
						}

						if( (ret_code  = update(buffer_in,data_len,buffer_out,&n) )< 0 )	/*Send data blocks*/
						{
							printf("\n Error : Update()  %d \n", (int) ret_code);
							return -1;
						}

						fwrite(buffer_out,1,n,fp_w);
					}

					if( (ret_code  = doFinal(NULL,0,buffer_out,&n) ) < 0 )
						{
							printf("\n Error : doFinal() %d \n", (int) ret_code);	/*Send last data blocks to finalize*/
							return -1;
						}
					if(n>0)
						fwrite(buffer_out,1,n,fp_w);

					bsent = (double) 8*sz; // bytes to bits

					fclose(fp_r);
					fclose(fp_w);


}
