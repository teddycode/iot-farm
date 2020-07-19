
#include "stm32f10x.h"
#include "delay.h"
#include "usart.h"
#include "timer.h"
#include "stdio.h"
#include "adc.h"
#include "led.h"

typedef struct  // lenght=20
{
	u16 ID;
	u32 Press;
	u16 Temp;
	u16 Hum;
	u16 Alt;	
	u16 Rain;
	u16 Soil;	
	u32 Light;
} Data;

int main(void)
{
	u8 sum=0,i=0,flg=0;
	u16 data_16[2]={0};
	Data Sensor={7788,0,0,0,0,0};
	
	extern u8 raw_data[20],light_data[12],Receive_ok,ReceiveLight_ok;
	
	LED_Init();
	DelayInit();  //  延时功能初始化	
	ADC_Conf_Init();
	
	USART1_Init(9600);  // 串口初始化
	USART2_Init(9600);
	
	//TIM3_Init(9999,7199); 	// 定时器3初始化	

	DelayMs(200);//等待模块初始化完成
	
	Send_Com(0x83);//发送读气压温湿度guangqiang指令	
			
	while(1)
	{
		if(ReceiveLight_ok)//串口接收完毕
		{
			for(sum=0,i=0;i<(light_data[3]+4);i++)//light_data[3]
				sum+=light_data[i];
			if(sum==light_data[i])//校验和判断
			{
				data_16[0]=(((u16)light_data[4])<<8)|light_data[5];
				data_16[1]=(((u16)light_data[6])<<8)|light_data[7];
				Sensor.Light = (((u32)data_16[0])<<16)|data_16[1];;
			}		
			ReceiveLight_ok=0;			
		}
		
		if(Receive_ok)//串口接收完毕
		{
			// 读取土壤、雨量计
			Sensor.Soil = ADC_Read_Stable(1);
			Sensor.Rain = ADC_Read_Stable(0);
			
			for(sum=0,i=0;i<(raw_data[3]+4);i++)//rgb_data[3]=3
				sum+=raw_data[i];
			
			if(sum==raw_data[i])//校验和判断
			{
				Sensor.Temp=(raw_data[4]<<8)|raw_data[5];
				data_16[0]=(((u16)raw_data[6])<<8)|raw_data[7];
				data_16[1]=(((u16)raw_data[8])<<8)|raw_data[9];
				Sensor.Press=(((u32)data_16[0])<<16)|data_16[1];
        Sensor.Hum=(raw_data[10]<<8)|raw_data[11];
        Sensor.Alt=(raw_data[12]<<8)|raw_data[13];
				
				if(flg == 0){
					LED0 = 1;
					flg = 1;
				}else{
					LED0 = 0;
					flg = 0;
				}
				USART_Uplaod((u8*)&Sensor,sizeof(Data),0x45);
				//上传给上位机				
			}
			Receive_ok=0;//处理数据完毕标志
		}		
	}		
}
