#include "esp8266.h"
#include "delay.h"

// 默认连接的ssid：test-wifi：1234567888
// ip 端口可以自己修改
void Esp8266_Init(void)
{
	DelayMs(500);
	USART_Printf(USART1,"AT+RST\r\n");
	DelayS(6);//初始化重启，等待连接wifi	
	USART_Printf(USART1,"AT+CIPMUX=0\r\n");
	DelayMs(500);//单连接模式
	USART_Printf(USART1,"AT+CIPSTART=\"TCP\",\"182.61.55.46\",8898\r\n");
	DelayS(3);//连接远程服务器端口
	USART_Printf(USART1,"AT+CIPMODE=1\r\n");
	DelayMs(500);//设置模块传输模式为透传模式
	USART_Printf(USART1,"AT+CIPSEND\r\n");
	DelayMs(500);//开启透传模式向服务器发送数据
}


