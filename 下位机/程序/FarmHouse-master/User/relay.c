#include "relay.h"

void Relay_Init(void)
{
	GPIO_InitTypeDef  GPIO_InitStructure; 	
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOC, ENABLE);	 //ʹ��PA�˿�ʱ��	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_13 | GPIO_Pin_14 |GPIO_Pin_15;// �˿�����
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP; 		 //�������
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;		 //IO���ٶ�Ϊ50MHz
	GPIO_Init(GPIOC, &GPIO_InitStructure);					 //�����趨������ʼ��
	
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB, ENABLE);	 //ʹ��PB�˿�ʱ��	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12| GPIO_Pin_13;// �˿�����
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;		//�������Ź���ģʽΪ�������� 		
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;	//������������������Ϊ50MHz
	GPIO_Init(GPIOB, &GPIO_InitStructure);					 //�����趨������ʼ��
	
	Relay_Bushi(OFF);
	Relay_Jiaoshui(OFF);
	Relay_Dadeng(OFF);
}

void Relay_Bushi(u8 s)
{
	if(s==ON){
		RELAY1 = ON;
	}else if(s==OFF){
		RELAY1 = OFF;
	}
}

void Relay_Jiaoshui(u8 s)
{
	if(s==ON){
		RELAY2 = ON;
	}else if(s==OFF){
		RELAY2 = OFF;
	}
}

void Relay_Dadeng(u8 s)
{
	if(s==ON){
		RELAY3 = ON;
	}else if(s==OFF){
		RELAY3 = OFF;
	}
}
