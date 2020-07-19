#ifndef __GSM_H
#define __GSM_H

#include "sys.h"


/*************	本地函数声明	**************/
void CLR_Buf2(void);
u8 Find(char *a);
void Send_AT_Command(char *b,char *a,u8 wait_time);
void GSM_Wait_CREG(void);
void GSM_Set_Pdu_Mode(void);//设置短信模式PDU
void GSM_Send_Pdu_Sms(char *tel,char *txt);//发送一条中文短信

#endif
