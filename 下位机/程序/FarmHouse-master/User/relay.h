#ifndef __RELAY_H
#define __RELAY_H

#include "sys.h" 

#define RELAY1 PCout(13)// PC13  // ��ʪ
#define RELAY2 PCout(14)// PC14	 // ��ˮ
#define RELAY3 PCout(15)// PC15  // ���

#define SW1 PBout(12) 
#define SW2 PBout(13) 


#define ON  1
#define OFF 0

void Relay_Init(void);//��ʼ��
void Relay_Bushi(u8 s);
void Relay_Jiaoshui(u8 s);
void Relay_Dadeng(u8 s);

#endif

