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
        System.out.println("The ticket number you entered is " + startTicket +" and the number of customers is " + count);

        for(int i=startTicket;i<startTicket+count;i++){
            System.out.println("Now serving ticket #" + i);
        }
        }

}