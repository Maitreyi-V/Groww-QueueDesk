package com.queuedesk;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the ticket number ");
        int startTicket=scanner.nextInt();
        System.out.println("Enter the count of customers ");
        int count =scanner.nextInt();
        scanner.close();
        System.out.println("The ticket number you entered is " + startTicket +" and the number of customers is " + count);

        int VIPcount=0;
        int recheckcount=0;
        int served=0;
        if(count<=0){
            System.out.println("Count must be atleast 1");
            return;
        }
        for(int i=startTicket;i<startTicket+count;i++){

            System.out.println("Now serving ticket #" + i);
            served++;
            if(i%5==0){
                System.out.println("VIP Lane");
                VIPcount++;
            }
            if(i%10==0){
                System.out.println("Priority recheck");
                recheckcount++;
            }
        }
        System.out.println("Total served :"+served);
        System.out.println("Total VIP:" +VIPcount);
        System.out.println("Total priority recheck:"+recheckcount);
    }

}