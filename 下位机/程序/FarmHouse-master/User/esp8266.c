#include "esp8266.h"
#include "delay.h"

// Ĭ�����ӵ�ssid��test-wifi��1234567888
// ip �˿ڿ����Լ��޸�
void Esp8266_Init(void)
{
	DelayMs(500);
	USART_Printf(USART1,"AT+RST\r\n");
	DelayS(6);//��ʼ���������ȴ�����wifi	
	USART_Printf(USART1,"AT+CIPMUX=0\r\n");
	DelayMs(500);//������ģʽ
	USART_Printf(USART1,"AT+CIPSTART=\"TCP\",\"182.61.55.46\",8898\r\n");
	DelayS(3);//����Զ�̷������˿�
	USART_Printf(USART1,"AT+CIPMODE=1\r\n");
	DelayMs(500);//����ģ�鴫��ģʽΪ͸��ģʽ
	USART_Printf(USART1,"AT+CIPSEND\r\n");
	DelayMs(500);//����͸��ģʽ���������������
}


