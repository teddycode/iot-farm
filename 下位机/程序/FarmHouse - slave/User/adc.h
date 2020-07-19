#ifndef __ADC_H
#define __ADC_H

#include "sys.h"

void ADC_Conf_Init(void);
u16 ADC_Read(u8 ch);
u16 ADC_Read_Stable(u8 ch);

#endif
