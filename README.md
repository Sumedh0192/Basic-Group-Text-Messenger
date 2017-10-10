################# Basic Group Messager ################

### Description: 
A basic group messenger which handles multicasting of a message from one AVD to a fixed set of AVDs and store the message in a key value storage system

### Implementation:
* The application is launched on 5 different AVDs each with a specified port.
* Each application has a server port which is constantly active to receive messages and a client which sends messages to the other AVDs.
* Messages are sent over the devices using TCP protocol.
* The Client and Server ports are implemented using Java Sockets and Java AsycTasks.
* The Client AVD sends accross message to the other 4 AVDs.
* Once a message is received by a Server AVD it saves the message in its local database as a key value pair using SQLiteDatabase.
* Packet loss or AVD Failure is not handled in this implementation.
* Messages are displayed on the screen of individual AVDs in the order they receive them.

### Link to the Code files:
https://github.com/Sumedh0192/Distributed-Systems/tree/master/GroupMessenger1/app/src/main/java/edu/buffalo/cse/cse486586/groupmessenger1/

### Link to official project description:
https://github.com/Sumedh0192/Distributed-Systems/blob/master/GroupMessenger1/PA%202%20Part%20A%20Specification.pdf

