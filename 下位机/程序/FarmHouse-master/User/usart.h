#ifndef __USART_H
#define __USART_H

#include "stdio.h"	
#include "sys.h" 
#include "stdarg.h"
#include "stdlib.h"


#define Uart3_Buf_Max 200 					  //串口3缓存长度

//串口3发送回车换行
#define UART3_SendLR() UART3_SendString("\r\n")
//											 USART_SendData(USART2,0X0D);\
//											 USART_SendData(USART2,0X0A)

#define EN_USART1_RX 			  1		//使能（1）/禁止（0）串口1接收
	  	

void USART1_Init(u32 baudRate);
void USART2_Init(u32 baudRate);
void USART3_Init(u32 baudRate);
void USART_SendStr(USART_TypeDef *USARTx, char *str, u8 len);
void USART_Printf(USART_TypeDef *USARTx, char *fmt,...);
void UART3_SendString(char* s);

#endif


