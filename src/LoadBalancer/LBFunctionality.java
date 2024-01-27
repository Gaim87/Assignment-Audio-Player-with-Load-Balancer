package LoadBalancer;

//A static load balancer using the weighted round robin technique. Each server is assigned a weight, depending on its capablities (software, hardware, connection speed etc.).
//Server A is the first to be assigned all the clients it can serve based on its weight, then server B and finally C. After that, the procedure starts over (from A).
//The sequence of assignment does not change.
public class LBFunctionality
{
    private static int _nextPortNumberHelper = 0;
    
    public synchronized String assignNextServer()            //Weights: 5001 3, 5002 3, 5003 2
    {
        String[][] availablePortsAndIPs = new String [][] { {"127.0.0.1", "5001"}, {"127.0.0.1", "5002"}, {"127.0.0.1", "5003"} };
        
        if (_nextPortNumberHelper < 3)                                                      //Helper variable == 0 up to 2 => Server 1, Port 5001
        {                                                                                   //Helper variable == 3 up to 5 => Server 2, Port 5002
            _nextPortNumberHelper += 1;                                                     //Helper variable == 6 or 7 => Server 3, Port 5003
            return availablePortsAndIPs[0][0] + " " + availablePortsAndIPs[0][1];
        }
        else if (_nextPortNumberHelper > 2 && _nextPortNumberHelper < 6)
        {
            _nextPortNumberHelper += 1;
            return availablePortsAndIPs[1][0] + " " + availablePortsAndIPs[1][1];
        }
        else
        {
            if (_nextPortNumberHelper == 6)
            {
                _nextPortNumberHelper += 1;
                return availablePortsAndIPs[2][0] + " " + availablePortsAndIPs[2][1];
            }
            else
            {
                _nextPortNumberHelper = 0;
                return availablePortsAndIPs[2][0] + " " + availablePortsAndIPs[2][1];
            }
        }
    }
}



    //Previous, dynamic least-connections load balancer iteration. One method for assigning a port number and increasing the active connections counter
    //and another to decrease the counter, when a client quits. The assigning method was run by the LBClientSetup thread and the decreasing one
    //by the server's thread (I also tried running it from the client's) by creating a LBFunctionality object and calling the method.
    //The assignment was running correctly and the counter, being a static variable, would keep its values for the next thread/client that would require a service, but
    //when the decreasing method ran, the counter would always have 0 as a value. Moreover, if I created another user, right after using the decreasing algorithm,
    //the increasing algorithm would access the counter's correct values/version?: e.g. 5001 3 connections, 5002 3, 5003 3 -> the port 5003 client exited and the decreasing algorithm
    //would output 5001 -1, 5002 0, 5003 0 instead of 3, 3, 2, but if I created another client, the assigning algorithm would correctly assign him/her to 5003 and output
    //5001 3, 5002 3, 5003 3.
    //As it is, you can only test if it works correctly by manually changing the counter's values.
    
    
      //Two-dimensional array. Supposing there exist 3 audio servers, each row contains a specific server's port number and a counter of its active connections.
//    private static int[][] portConnectionsCounter = { {5001, 0}, {5002, 0}, {5003, 0} };
//
//    public synchronized String chooseServerPort ()
//    {
//        int portNumber = portConnectionsCounter[0][0];        //Always assigns 5001, first, by default. This is the variable that is returned to the client.
//        String IPAddress = "127.0.0.1";                       //Always the same.
//        int helperForPort = portConnectionsCounter[0][1];     //The first server's counter (5001's).
//        int helperForPort2 = 0;                               //Guarantees persistence.
//        String helperForIP = IPAddress;
//        
//        System.out.println("[ReturnEchoServersIP_Port]: Timestamp:" + Instant.now());
//        System.out.println("[ReturnEchoServersIP_Port]: Active connections before server assignment:\n" +
//                           "port " + portConnectionsCounter[0][0] + ": " + portConnectionsCounter[0][1] + ", " +
//                           "port " + portConnectionsCounter[1][0] + ": " + portConnectionsCounter[1][1] + ", " +
//                           "port " + portConnectionsCounter[2][0] + ": " + portConnectionsCounter[2][1]);
//        
          //If all counters have the same value, the client is serviced by the 1st (5001), whose counter is then raised by 1.
          //Otherwise, the most recently forwarded server's counter is compared with the other two servers' and the data of the server with the smallest counter number
          //is sent to the user.
//        if (portConnectionsCounter[0][1] == (portConnectionsCounter[1][1]) && portConnectionsCounter[1][1] == (portConnectionsCounter[2][1]))
//        {
//            portConnectionsCounter [0][1] += 1;
//            
//            System.out.println("[ReturnEchoServersIP_Port]: Timestamp:" + Instant.now());
//            System.out.println("[ReturnEchoServersIP_Port]: Active connections after server assignment:\n" +
//                           "port " + portConnectionsCounter[0][0] + ": " + portConnectionsCounter[0][1] + ", " +
//                           "port " + portConnectionsCounter[1][0] + ": " + portConnectionsCounter[1][1] + ", " +
//                           "port " + portConnectionsCounter[2][0] + ": " + portConnectionsCounter[2][1]);
//            
//            return IPAddress + " " + portNumber;                                              //For 3 servers, the rest of the possible scenarios are:
//        }                                                                                     //Server a's counter equals b's and a<c (returns a - 5001)
//        else                                                                                  //Server a's counter equals b's and a>c (returns c - 5003)
//        {                                                                                     //Server a's counter equals c's and a<b (returns a - 5001)
//            for (int i = 1; i < portConnectionsCounter.length; i += 1)                        //Server a's counter equals c's and a>b (returns b - 5002)
//            {                                                                                 //Server b's counter equals c's and b<a (returns b - 5002)
//                if (portConnectionsCounter[i][1] < helperForPort)                             //Server b's counter equals c's and b>a (returns a - 5001)
//                {                                                                             
//                    portNumber = portConnectionsCounter[i][0];                                //"portNumber" returned to the client.
//                    helperForPort = portConnectionsCounter[i][1];                             //After the server with the least active clients is found, the helperForPort variable
//                    helperForPort2 = i;                                                       //is assigned its counter's value (the most recently forwarded).
//                }                                                                             //the least active clients.
//            }
//        }
//        portConnectionsCounter [helperForPort2][1] += 1;                                      //The the most recently forwarded server's active clients counter is raised by one.
//        
//        System.out.println("[ReturnEchoServersIP_Port]: Timestamp:" + Instant.now());
//        System.out.println("[ReturnEchoServersIP_Port]: Active connections after server assignment:\n" +
//                           "port " + portConnectionsCounter[0][0] + ": " + portConnectionsCounter[0][1] + ", " +
//                           "port " + portConnectionsCounter[1][0] + ": " + portConnectionsCounter[1][1] + ", " +
//                           "port " + portConnectionsCounter[2][0] + ": " + portConnectionsCounter[2][1]);
//
//        return IPAddress + " " + portNumber;
//    }
//    
//    public synchronized void decreaseServerCounter (int serverPortNumber)     //Receives a server's port number as a parameter and decreases its active clients counter by 1.
//    {
//        for (int i = 0; i < portConnectionsCounter.length; i += 1)            //Static variable always resets to 5001 0, 5002 0, 5003 0 (problem).
//        {
//            if (portConnectionsCounter[i][0] == serverPortNumber)
//            {
//                portConnectionsCounter[i][1] -= 1;
//
//                System.out.println("[ReturnEchoServersIP_Port]: Timestamp:" + Instant.now());
//                System.out.println("[ReturnEchoServersIP_Port]: Active connections after client disconnection:\n" +
//                       "port " + portConnectionsCounter[0][0] + ": " + portConnectionsCounter[0][1] + ", " +
//                       "port " + portConnectionsCounter[1][0] + ": " + portConnectionsCounter[1][1] + ", " +
//                       "port " + portConnectionsCounter[2][0] + ": " + portConnectionsCounter[2][1]);
//                return;
//            }
//        }
//    }