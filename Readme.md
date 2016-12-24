[Google Play Link]()

##App Description##

- Robot controller is a remote control for UDP enabled electronics, sending packets over WiFi. 
- There are two joysticks, three normal buttons, and three toggle buttons.
- The address and port to send to can be configured.  The rate to send packets can also be configured.
- This app also supports telemetry! Any UDP packets sent to this device’s address and specified port will be displayed above the controller.
- Packets are in the form of the text representation of a dictionary. See the help screen for more info.
- This app has been tested with a Raspberry Pi as a receiver, but it should work with other UDP-enabled devices, such as a WiFi enabled Arduino or an ESP8266.

##Packets##

An example packet: `{'btn6':0, 'btn1':0, 'rightTouchPadX':-10, 'btn5':0, 'rightTouchPadY':-17, 'btn2':0, 'leftTouchPadY':-89, 'btn4':0, 'btn3':0, 'leftTouchPadX':-9}`.

The touchpad values range from -100 to 100, while the button values range from 0 to 1. "btn1" is the top button and "btn6" is the bottom button.

In Python, the received message can simply be evaluated to a dict with `ast.literal_eval(message.decode())`. See the below for actual code.

##Usage##

See the Examples folder. `ReceiverTesting.py` is an example of how to receive the UDP packets. `TelemetryTesting` is an example of how to send telemetry back to the controller. Simply run `cd Examples` and `python ReceiverTesting.py` or `python TelemetryTesting.py` — there is a dialog that asks for ports and addresses.

 
