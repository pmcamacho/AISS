/***********************************************
Title       : protocol.c
Design      : T cliente interface
Author      : Ricardo Chaves e José Leitão
Company     : INESC-ID
Date : 12/10/2013
Copyright (C) 2013
************************************************/

#include "protocol.h"
#include "com.h"
#include <stdio.h>
#include <stdlib.h>

extern packet_t packet;

#define DEBUG OFF


u8* _key = NULL;
u32 _mode = NULL;


char  __xor(u8 * data_in, u32 size, u8 * data_out, u32 * size_out)
{


	
	if(size == 0){
		*size_out = 0;
		return '0';
	}


	//for(jj=0; jj < 16*4; jj++){
		
		//printf("key: %d\n", (int) _key[jj]);
	
	//}
	
	//for(jj=0; jj < 16*4; jj++){
		
		//printf("data_in: %d\n", (int) data_in[jj]);
	
	//}

    u8 * s = data_in;
    size_t length = 16*4;
    
    int i;

    for(i=0; i < size; i++) {
            s[i]  = s[i] ^ _key[i % length];
            //printf("%c\n", (u8)s[i]);
            //printf("%c\n", (u8)_key[i % length]);

    }
    
    *size_out = i;

    //printf("N: %d\n", *size_out);
    
    memcpy((u8*) data_out, (u8*) s, i);

    //for(jj=0; jj < 16*4; jj++){
		
		//printf("s_xor: %c\n", (int) s[jj]);
		//printf("data_out_xor: %c\n", (int) data_out[jj]);
	
	//}

    
    return '0';
}

void reset( )
{
	u8 buffer[MAX_PACKET_DATA];
	u32 size;

	/* Create reset packet */
 	form_packet(&packet, NULL,0, RESET_CODE);
	/* Send reset packet */
	sendPacket(packet.raw,PACKET_HEADER_SIZE);

}

char init( u32 keynum, u32 mode, u8 * Key , u8 * IV)
{
	u8 buffer[MAX_PACKET_DATA];
	u32 size;

	//COMinit();
	
	/* Create initialization packet */
	
	//form_packet_init(&packet, keynum , mode,Key ,IV,INIT_CODE);
	
	/* Send initialization packet */
	
	//sendPacket(packet.raw,PACKET_HEADER_SIZE+INIT_SIZE);
	
	/* Receive status confirmation */
	
	//recvPacket((u8 *) packet.raw, MAX_PACKET_RAW_DATA);
	
	/* Get data from incoming packet */
	//return get_packet_data( &packet, buffer, &size);

	//NEW
	int length = 16*4;
	//sizeof(Key)/sizeof(u8);

	//printf("%d\n", length);
	
	_key = (u8*) malloc(length);
	memcpy((u8*) _key, (u8*) Key,length);

	//int jj;
	//for(jj=0; jj < 16*4; jj++){
		
		//printf("Key: %d\n", (int) _key[jj]);
	
	//}
	
	_mode = mode;

}


char encrypt(u8 * data_in, u32 size_in, u8* data_out, u32 *size_out, u32 keynum,u8 * keyin,u8* IV)
{
	u32 mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| ENCRYPT_FLAG;
  	init(  keynum,  mode, keyin ,  IV);
	return doFinal(data_in, size_in, data_out,size_out);


}
char decrypt(u8 * data_in, u32 size_in, u8* data_out, u32 * size_out,u32 keynum,u8* keyin,u8 *IV)
{
	u32 mode = ROUNDS_10 | CBC_FLAG |FIRST_FLAG| DECRYPT_FLAG;
	init(  keynum,  mode, keyin ,  IV);
	return doFinal(data_in, size_in, data_out,size_out);

}

char  update_int(u8 * data_in, u32 size, u8 * data_out,u32 * rbytes, char fin_code)
{

	static u32 bbytes = 0;
	static u8 buffer[MAX_PACKET_RAW_DATA];
	u32 size_int,n,i;
	u32 sbytes;
	char ret_code;

	ret_code = 0;
	size_int = size;
	sbytes = 0;
	*rbytes = 0;

	while(size_int>0)
	{

		if( (bbytes + size_int) >= MAX_PACKET_DATA)
		{
			/* Fill the buffer until MAX_DATA */
			memcpy( &(buffer[bbytes]), &(data_in[sbytes]), MAX_PACKET_DATA - bbytes );

			/* Update input stream index and length*/
			sbytes   += (MAX_PACKET_DATA - bbytes);
			size_int -= (MAX_PACKET_DATA - bbytes);

			/* Update number of buffered bytes*/
			bbytes = MAX_PACKET_DATA;

			/* Create packet */
			if(size_int == 0) /* if it's the last packet to be sent*/
				form_packet(&packet,buffer, bbytes,fin_code);
			else			/* if there is still more data to be sent*/
				form_packet(&packet,buffer, bbytes,UPDATE_CODE);

			/* Send buffered data */
			sendPacket((u8 *) packet.raw, bbytes + PACKET_HEADER_SIZE);

			/* Receive data */
			n = recvPacket((u8 *) packet.raw, MAX_PACKET_RAW_DATA);

			if(n == -1)
				return -1;

			//for(i=0;i<n;i++)
			//	printf("%02X",packet.raw[i]);

			/* Get data from incoming packet */
			ret_code = get_packet_data( &packet, &(data_out[*rbytes]),&n );


			//	for(i=0;i<n;i++)
			//	printf("%02X",data_out[i]);

			/* STOP if an error is detected*/
			if(ret_code < 0 )
				return ret_code;

			/* Update received bytes counter*/
			*rbytes += n;

			/* Update buffered bytes counter*/
			bbytes = 0;
		}
		else
		{
			/* Copy the input data to the buffer */
			memcpy( &(buffer[bbytes]), &(data_in[sbytes]), size_int );


			/* Update number of buffered bytes*/
			bbytes += size_int;

			/* Update input stream index and length*/
			sbytes += size_int;
			size_int = 0;


			/* If number of buffered bytes is > than the minimum packet data*/
			if( bbytes >= MIN_PACKET_DATA )
			{
				/* Create packet */
				form_packet(&packet,buffer, bbytes, fin_code);

				/* Send buffered data */
				sendPacket((u8 *) packet.raw, bbytes + PACKET_HEADER_SIZE);

				/* Receive data */
				recvPacket((u8 *) packet.raw, MAX_PACKET_RAW_DATA);

				/* Get data from incoming packet */
				ret_code = get_packet_data( &packet, &(data_out[*rbytes]),&n );

				/* STOP if an error is detected*/
				if(ret_code < 0 )
					return ret_code;
				/* Update received bytes counter*/
				*rbytes += n;

				/* Update buffered bytes counter*/
				bbytes = 0;
			}

		}
	}

	/* If it is a DoFinal and there is still buffered data on the PC
	 !OR! on the FPGA, ask for the last (padded) block*/
	if( (fin_code == DOFINAL_CODE) && ( (bbytes >0) || (ret_code != DATA_COMP_CODE) ) )
	{

			/* Create packet - emptying the buffer */
			form_packet(&packet,buffer, bbytes, DOFINAL_CODE);

			/* Send all the buffered data */
			sendPacket((u8 *) packet.raw, bbytes + PACKET_HEADER_SIZE);

			/* Receive last packet */
			recvPacket((u8 *) packet.raw, MAX_PACKET_RAW_DATA);

			/* Get data from incoming packet */
			ret_code = get_packet_data( &packet, &(data_out[*rbytes]),&n );

			/* HALT if an error is detected*/
			if(ret_code < 0  )
				return ret_code;
			/* Update received bytes counter*/
			*rbytes += n;

			/* Update buffered bytes counter*/
			bbytes = 0;

	}

		/* Return code*/
		return ret_code;

}


char  update(u8 * data_in, u32 size, u8 * data_out,u32 * size_out)
{	
	
	//NEW
	//size_out = n

	return __xor(data_in,size, data_out, size_out);

	//return update_int(data_in, size,data_out, size_out,UPDATE_CODE);

}

char   doFinal_int(u8 * data_out,u32 *size_out)
{
	u32 n;
	char ret_code;

	/* Clear the update function internal buffer */
	ret_code  = update_int(NULL,0,data_out,&n,DOFINAL_CODE);
	/* Return number of received bytes*/
	*size_out = n;
	/* Return code*/
	return ret_code;
}

char   doFinal(u8 * data_in, u32 size,u8 * data_out,u32 *size_out)
{
	u32 n;
	char ret_code;

	/* Update the last block of the stream */
	
	//ret_code  = update(data_in,size,data_out,&n);

	//NEW
	ret_code = __xor(data_in,size,data_out, &n);

	/* STOP if an error is detected*/
	if(ret_code < 0 )
		return ret_code;

	/* update number of received bytes*/
	*size_out = n;

	/* Final transfer, ask for the last block (padded if needed)*/
	//ret_code = doFinal_int(&(data_out[n]),&n);

	//NEW
	ret_code = __xor(data_in, size, data_out, &n);

	/* update number of received bytes*/
	*size_out += n;
	/* Return code*/
	return ret_code;
}


 char get_packet_data(packet_t * p, u8 * data, u32 *size)
{
	u32 aux = 0 ;

	memcpy(&aux, p->field.size,4) ;


	#if DEBUG == ON
	//printf("DEBUG: Received packet with %d bytes\n",aux);
	#endif
	*size = aux;

	if(*size >0)
		memcpy(data,(u8*)(p->field.data),*size);

	return p->field.code_op[0] ;
}

void   form_packet_init(packet_t * p, u32 keynum ,u32 mode, u8 * Key , u8 * IV,char code)
{

	int size = INIT_SIZE;

	memcpy(p->param.keynum, &keynum,4);
	memcpy(p->param.mode,&mode,4);
	memcpy(p->param.IV,IV,IV_SIZE);
	memcpy(p->param.Key, Key , KEY_SIZE);

	p->param.code_op[0] = code;
	p->param.code_op[1] = code;

	memcpy(p->field.size,&size,4);


	#if DEBUG == ON
	//printf("DEBUG: Formatted packet with %d bytes\n",INIT_SIZE);
	#endif
}

 void  form_packet(packet_t * p, u8 * data, u32 size, char  code)
{

 	int fake = 0;

	if(data!=NULL)
		memcpy((u8*)p->field.data,data,size);

	p->field.code_op[0] = code;
	p->field.code_op[1] = code;

	#if DEBUG == ON
	//printf("DEBUG: Formatted packet with %d bytes\n",size);
	#endif



		memcpy(p->field.size,&size,4);

		//memcpy(p->field.size,&fake,4);

}

void   form_packet_header(packet_t * p, u32 size,char code)
{
	p->field.code_op[0] = code;
	p->field.code_op[1] = code;

	memcpy(p->field.size,&size,4);
}