#include "relay.h"

void Relay_Init(void)
{
	GPIO_InitTypeDef  GPIO_InitStructure; 	
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOC, ENABLE);	 //使能PA端口时钟	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_13 | GPIO_Pin_14 |GPIO_Pin_15;// 端口配置
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP; 		 //推挽输出
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;		 //IO口速度为50MHz
	GPIO_Init(GPIOC, &GPIO_InitStructure);					 //根据设定参数初始化
	
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB, ENABLE);	 //使能PB端口时钟	
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12| GPIO_Pin_13;// 端口配置
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;		//设置引脚工作模式为上拉输入 		
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;	//设置引脚输出最大速率为50MHz
	GPIO_Init(GPIOB, &GPIO_InitStructure);					 //根据设定参数初始化
	
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
