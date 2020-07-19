#ifndef __USART_H
#define __USART_H

#include "stdio.h"	
#include "sys.h" 
#include "stdarg.h"
#include "stdlib.h"


#define Uart3_Buf_Max 200 					  //����3���泤��

//����3���ͻس�����
#define UART3_SendLR() UART3_SendString("\r\n")
//											 USART_SendData(USART2,0X0D);\
//											 USART_SendData(USART2,0X0A)

#define EN_USART1_RX 			  1		//ʹ�ܣ�1��/��ֹ��0������1����
	  	

void USART1_Init(u32 baudRate);
void USART2_Init(u32 baudRate);
void USART3_Init(u32 baudRate);
void USART_SendStr(USART_TypeDef *USARTx, char *str, u8 len);
void USART_Printf(USART_TypeDef *USARTx, char *fmt,...);
void UART3_SendString(char* s);

#endif


