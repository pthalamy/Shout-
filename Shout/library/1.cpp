#include "LPC17xx.h"
#include "serial.h"
#include 
#include 

#define RXBUFSIZE 40

char CharacterBuffer[RXBUFSIZE];
volatile uint8_t Writepos;

int main(void) {
    Serial_begin(3, 115200);  //open UART3 with 15200 baud
    Serial_printString(3, "LPC1769 Start");  //send string
    Serial_println (3);   //send new line
    printf("LPC1769 Start\n");  //sent strig to Debug Console
    Writepos = 0;
    Serial_flush(3);    //clear RX buffer
    while(1) {                                                    //main loop
        while (Serial_available(3))                              //check if RX byta available
        {
            uint8_t RxChar;
            RxChar=Serial_read(3);            //read received byte
            //printf("Character received: >%c", RxChar);
            //printf("<, DEC %d\n", RxChar);
            switch(RxChar)
            {
                case 10:
                {
                    //printf("LF Character received, Ignore it!\n");
                    break;
                }
                case 13:
                {
                    printf("CR Character received\n");
                    printf("Buffer = %s\n", CharacterBuffer);  //Print out receiced buffer
                    float val1 = atof(CharacterBuffer+2);      //Convert String to Float on position 3
                    printf("Value1 = %e\n", val1);
                    int val2 = atoi(CharacterBuffer+8);        //Convert String to int on position 9
                    printf("Value2 = %i\n", val2);
                    Writepos=0;
                    CharacterBuffer[Writepos] = 0;
                    break;
                }
                default:
                {
                    CharacterBuffer[Writepos++] = RxChar;
                    if (Writepos == RXBUFSIZE)
                    {
                        printf("RX Buffer Overflow!\n");
                        Writepos=0;
                    }
                    CharacterBuffer[Writepos] = 0;
                    //printf("Buffer = %s\n", CharacterBuffer);
                    break;
                }
            }

        }
    }
    return 0 ;
}