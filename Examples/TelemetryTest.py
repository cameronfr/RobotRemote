#!/usr/bin/env python
#Telemetry Testing/Example: Sending telemetry back to the controller.

import socket
import sys
import threading
import time

class ControllerServer:

    SEND_FREQUENCY = 100

    def __init__(self, port, addr):
        self.port = port
        self.addr = addr

    def start_server(self):
        thread = threading.Thread(target = self.send_messages)
        self._stop = threading.Event()
        thread.start()

    def stop_server(self):
        self._stop.set()

    def send_messages(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        while(not self._stop.isSet()):

            #This message is the telemetry that is being sent back to the controller app
            
            #Each line is a different test, uncomment one
            print("Input a telemetry message:"); message = str.encode(input())
            #message = str.encode("Telemetry Test: "+ str(time.time()))
            #message = str.encode(' '.join(str(i) for i in range(1,100))+(str(time.time())))
            #message = str.encode("Telemetry:  VEL: 10.22  ALT: 98.33  TIME: 16.44")
            sock.sendto(message,(self.addr,self.port))
            time.sleep(1.0/self.SEND_FREQUENCY)
        sock.close()

if __name__ == "__main__":
    try:
        print("Enter port to send to")
        port = int(input())
        print("Enter address to send to")
        address = input()
        server=ControllerServer(port,address)
        print("Starting server...")
        server.start_server();
        while True:
            time.sleep(1)
    except (IndexError,ValueError):
        print("Invalid port and address input, exiting...")
    except KeyboardInterrupt:
        print("Shutting down server...") 
        server.stop_server();
