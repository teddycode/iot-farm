#ifndef __GSM_H
#define __GSM_H

#include "sys.h"


/*************	���غ�������	**************/
void CLR_Buf2(void);
u8 Find(char *a);
void Send_AT_Command(char *b,char *a,u8 wait_time);
void GSM_Wait_CREG(void);
void GSM_Set_Pdu_Mode(void);//���ö���ģʽPDU
void GSM_Send_Pdu_Sms(char *tel,char *txt);//����һ�����Ķ���

#endif
