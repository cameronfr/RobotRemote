#!/usr/bin/env python
#Receiver Testing/Example: Receiving the controller state from the controller.

import socket
import time
import threading
import ast

class ControllerServer:

    start_controller_state = {'btn6':0, 'btn1':0, 'rightTouchPadX':0, 'btn5':0, 'leftTouchPadX':0, 'rightTouchPadY':0, 'btn2':0, 'leftTouchPadY':0, 'btn4':0, 'btn3':0, 'leftTouchPadX':0}

    def __init__(self, port):
        self.port = port
        self.controller_state = self.start_controller_state

    def start_server(self):
        thread = threading.Thread(target = self.recieve_messages)
        self._stop = threading.Event()
        thread.start()

    def stop_server(self):
        self._stop.set()

    def recieve_messages(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.bind(('',self.port))
        sock.settimeout(1.0)
        while(not self._stop.isSet()):
                try:
                    message, address = sock.recvfrom(1024)
                    print("Updating controller satte: " + str(message))
                    try:
                        self.controller_state.update(ast.literal_eval(message.decode()))
                    except (SyntaxError,ValueError):
                        print("Malformed UDP message to controller server")
                except socket.timeout:
                    print("Didn't receive command before timeout, listening again")
        sock.close()

    def get_controller_state_ref(self):
        return self.controller_state

if __name__ == "__main__":

    try:
        print("Enter the port to receive on")
        port = int(input())
        server = ControllerServer(port);
        print("Starting server...")
        server.start_server()
        controller_ref = server.get_controller_state_ref()
        
        #This controller reference can be passed to a class and used to get the controller state 

        while True:
            time.sleep(1)
   except ValueError:
        print("Invalid port input, exiting...")
   except KeyboardInterrupt:
        print("Shutting down server...")
        server.stop_server()

