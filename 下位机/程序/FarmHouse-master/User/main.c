
#include "stm32f10x.h"
#include "oled.h"
#include "delay.h"
#include "led.h"
#include "usart.h"
#include "timer.h"
#include "stdio.h"
#include "esp8266.h"
#include "relay.h"
#include "gsm.h"

char ui_buff[32];

static char *telnum="AT+CMGS=\"00310033003700330037003500360031003200310034\"";
static char *txt_light="51497EBF592A6697FF01";  // 光线太暗！
static char *txt_soil="571F58E47F3A6C34FF01";   // 土壤缺水！
static char *txt_hum="7A7A6C145E7271E5FF01";   // 空气干燥！

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

// Thresholds
u16 TH_HUM = 50;  //0~100
u16 TH_SOIL = 4096; //4096~0  // 添加阈值设定
u16 TH_LIGHT = 1000;  //188000~0 / 添加阈值设定

u8 SMS_sw=0;

u8  SMS_Sended=0;

int main(void)
{
	extern u8 raw_data[32],Receive_ok;
	
	u8 sum=0,i=0;
	Data *Sensor=0;
	
	DelayInit();  		// 延时功能初始化	
//	LED_Init();
	Relay_Init();
	USART1_Init(115200);
	USART2_Init(9600);
	USART3_Init(115200);
	OLED_Init();  		// OLED 显示初始化	
	OLED_ShowStr(0,2,"Wifi Initing...",2);	
	Esp8266_Init();		
	OLED_CLS();	
	OLED_ShowStr(0,2,"GSM Initing...",2);	
//	GSM_Wait_CREG();
//	GSM_Set_Pdu_Mode();	
  Timer2_Init_Config();	
	OLED_CLS();	
//	LED0=1;
	SMS_sw = 0; // 关闭短信提醒
	
	while(1)
	{	
		// 1、接收、显示数据 2、阈值判断、异常处理  3、转发上传数据		
		if(Receive_ok)//串口接收完毕
		{
			for(sum=0,i=0;i<(raw_data[3]+4);i++)//rgb_data[3]=3
				sum+=raw_data[i];			
			if(sum==raw_data[i])//校验和判断
			{
				Sensor = (Data*)(raw_data+4); // data process				

				sprintf(ui_buff,"ID:%-5d",Sensor->ID);  //ID
				OLED_ShowStr(0,1,ui_buff,1);				
				sprintf(ui_buff,"P:%-8.1fpa",Sensor->Press/100.0); // 气压
				OLED_ShowStr(50,1,ui_buff,1);
				sprintf(ui_buff,"T:%-4.2fC",Sensor->Temp/100.0);  //温度
				OLED_ShowStr(0,2,ui_buff,1);
				sprintf(ui_buff,"H:%5.2f%%",Sensor->Hum/100.0);  // 湿度
				OLED_ShowStr(63,2,ui_buff,1);				
				sprintf(ui_buff,"A:%-6dm",Sensor->Alt);  // 海拔
				OLED_ShowStr(0,3,ui_buff,1);				
				sprintf(ui_buff,"L:%-7.2ffx",Sensor->Light/100.0);   // 光强
				OLED_ShowStr(63,3,ui_buff,1);
				sprintf(ui_buff,"S:%-4d",Sensor->Soil);   // soil
				OLED_ShowStr(0,4,ui_buff,1);
				sprintf(ui_buff,"R:%-4d",Sensor->Rain);   // rain
				OLED_ShowStr(63,4,ui_buff,1);

				// 显示阈值
				sprintf(ui_buff,"TS:%-4d",TH_SOIL);   // soil
				OLED_ShowStr(0,5,ui_buff,1);
				sprintf(ui_buff,"TL:%-4d",TH_LIGHT);   // soil
				OLED_ShowStr(63,5,ui_buff,1);
		
				// 阈值判断
				if(Sensor->Hum < TH_HUM && SMS_sw)  // 补湿
				{
					OLED_ShowStr(0,6,"H:ON",1);
					Relay_Bushi(ON);
					if(SMS_Sended == 0){						
						GSM_Send_Pdu_Sms(telnum,txt_hum);
						SMS_Sended = 1;
					}
				}
				else{
					Relay_Bushi(OFF);
					OLED_ShowStr(0,6,"H:OFF",1);
				}
				
				if(Sensor->Soil > TH_SOIL && SMS_sw)  // jiaoshui
				{
					Relay_Jiaoshui(ON);
					OLED_ShowStr(40,6,"S:ON ",1);
					if(SMS_Sended == 0){	
						sprintf(ui_buff,"Soil=%d ",Sensor->Soil);  // 
						GSM_Send_Pdu_Sms(telnum,txt_soil);
						SMS_Sended = 1;
					}
					
				}
				else {
					Relay_Bushi(OFF);
					OLED_ShowStr(40,6,"S:OFF",1);
				}
				
				if(Sensor->Light < TH_LIGHT && SMS_sw)
				{
					Relay_Dadeng(ON);
					OLED_ShowStr(80,6,"L:ON ",1);
					if(SMS_Sended == 0){	
						sprintf(ui_buff,"Light =%d ",Sensor->Light);  // 
						GSM_Send_Pdu_Sms(telnum,txt_light);
						SMS_Sended = 1;
					}
				}else{
					Relay_Dadeng(OFF);
					OLED_ShowStr(80,6,"L:OFF",1);
				}				
				// 上传服务器
				USART_Printf(USART1,"{\"id\":%d,\"p\":%d,\"t\":%d,\"h\":%.d,\"a\":%d,\"r\":%d,\"s\":%d,\"l\":%d}\n",\
				Sensor->ID,Sensor->Press,Sensor->Temp,Sensor->Hum,\
				Sensor->Alt,Sensor->Rain,Sensor->Soil,Sensor->Light);//	u16 ID;  json
			}
			Receive_ok=0;//处理数据完毕标志
		}		
	}
}
