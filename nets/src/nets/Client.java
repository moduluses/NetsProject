package nets;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class Sockets_Client
{

    static String OUTPUT = "";

    public static void main(String[] args) throws Exception
    {
        if (args.length >= 0)
        {
            // String host = args[0];

            //Setting up Scanners to get user's inputs
            Scanner userInput = new Scanner(System.in);

            //Creates the initial socket to be used
            Socket clientSocket = new Socket("192.168.100.108", 7315);	//changed host to localhost

            //writing user statements to server side program
            PrintWriter outputStream = new PrintWriter(clientSocket.getOutputStream(), true);

            //reader used to recieve data from server side program
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true)
            {
                int userChoice;
                int num = 1;
                double avgTime = 0;
                int counter = 0;
                boolean check = true;

                //Initial menu for user choices
                System.out.printf("1. Host current Date and Time%n"
                        + "2. Host uptime%n"
                        + "3. Host memory use%n"
                        + "4. Host Netstat%n"
                        + "5. Host current users%n"
                        + "6. Host running processes%n"
                        + "7. Quit%n");

                //get user menu decision
                userChoice = userInput.nextInt();

                switch(userChoice)
                {
                     case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                {
                    System.out.println("Enter Number of Clients To Simulate:");
                    //Get number of threads
                    num = userInput.nextInt();
                }
                    case 7:
                {
                    System.out.println("Program is exiting. No longer connected to server.");
                }
                    default:
                {
                    System.out.println("Invalid Input. Please enter a number from 1 to 7.");
                }
                }
                   

                if (userChoice.equals(1) || userChoice.equals(2) || userChoice.equals(3) || userChoice.equals(4)
                        || userChoice.equals(5) || userChoice.equals(6))
                {

                    threadTime time = new threadTime(num);
                    ClientThreads thread[] = new ClientThreads[100];

                    for (int i = 0; i < num; i++)
                    {

                        // Creates a new thread
                        thread[i] = new ClientThreads(outputStream, inputStream, userChoice, time);
                        thread[i].start();

                    }

                    for (int i = 0; i < num; i++)
                    {

                        try
                        {

                            thread[i].join();

                        }
                        catch (InterruptedException e)
                        {

                            System.out.println(e);
                        }

                        counter = i + 1;

                        if (num == 1)
                        {

                            System.out.println(thread[i].getResults());
                        }
                        if ((counter % 5 == 0) || (counter == 1))
                        {

                            avgTime = time.getAverage(counter);

                            if (check)
                            {

                                System.out.println(OUTPUT);
                                check = false;
                            }
                            System.out.println("Average time of " + counter + " thread(s): " + avgTime);
                        }
                    }
                }
                else if (userChoice == 7)
                {

                    outputStream.println(userChoice);
                    System.exit(0);
                    break;
                }
                else
                {
                    System.out.println("Invalid Input");
                }
            }

            clientSocket.close();
        }
        else
        {
            System.out.println("Please enter a server to connect to.");
        }
    }
}

class ClientThreads extends Thread
{

    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private String choice;
    private threadTime time;
    private String allResults;

    public ClientThreads(PrintWriter os, BufferedReader is, String c, threadTime t)
    {
        this.outputStream = os;
        this.inputStream = is;
        this.choice = c;
        this.time = t;
    }

    public void run()
    {
        long totalTime;
        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();

        try
        {
            outputStream.println(choice);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            String allResults = inputStream.readLine();
            Sockets_Client.OUTPUT = allResults;
            endTime = System.currentTimeMillis();
            totalTime = endTime - startTime;
            time.push(totalTime);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        return;
    }

    public String getResults()
    {
        return allResults;
    }
}

class threadTime
{

    long[] times;
    int counter;

    public threadTime(int n)
    {
        times = new long[n];
        counter = 0;
    }

    public synchronized void push(long t)
    {
        times[counter++] = t;
    }

    public double getAverage(int num)
    {
        double avg = 0;

        for (int i = 0; i < num; i++)
        {
            avg += (double) times[i];
        }

        avg = avg / (double) times.length;

        return avg;
    }
}