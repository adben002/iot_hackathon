#!/usr/bin/env python

import time
import urllib.request, urllib.parse, urllib.error
import urllib.request, urllib.error, urllib.parse
import datetime
import aws
import RPi.GPIO as GPIO
import ADC


queueUrl='https://sns.eu-west-1.amazonaws.com/820731746647/iothackathon-mhbrooks'
deviceId='Front Door'
sensorType='Door Detector'
sns = aws.snsMessager(queueUrl, deviceId, sensorType)
awsEnabled=True
input_state = False
temperature_sensor_state=0

AFTER_DATE_TIME = datetime.time(20, 00)
BEFORE_DATE_TIME = datetime.time(7, 00)

GPIO.setmode(GPIO.BOARD)
#blue light
GPIO.setup(16, GPIO.OUT)
#push button
GPIO.setup(11, GPIO.IN)
#green light
GPIO.setup(18, GPIO.OUT)
#red light
GPIO.setup(22, GPIO.OUT)
#temperature sensor
GPIO.setup(12, GPIO.IN)
#buzzer
GPIO.setup(13, GPIO.OUT)

def send_signal(message):
    """
        Sends a signal/message to the backend, if enabled this is aws/sns, if disabled
        it is sent to a server.
    """
    if awsEnabled:
        send_signal_to_aws(message)
    else:
        send_signal_to_server(message)

def send_signal_to_aws(message):
    """
        Posts the given message to an aws sns queue for processing
    """
    print("Trying to post message to aws")
    sns.send(message)

def send_signal_to_server(message):
    """
        Useful if running in an environment with no aws config
    """
    print('Trying to communicate with the server.')
    url = 'http://example.com/...'
    values = { 'productslug': 'bar',
        'qty': 'bar' }
    data = urllib.parse.urlencode(values)
    req = urllib.request.Request(url, data)
    response = urllib.request.urlopen(req)
    result = response.read()
    print(result)

def is_now_between_range():
    now = datetime.datetime.now().time()
    return (now >= AFTER_DATE_TIME or now <=BEFORE_DATE_TIME)

def listen_to_push_button():
    global input_state
    if input_state != GPIO.input(11):
        state_changed = True
        input_state = GPIO.input(11)
    else:
        state_changed = False

    turn_on_led_when_button_pressed()
    if (state_changed):
        #if (is_now_between_range())
        if True:
            if input_state:
                send_signal('open')
            else:
                send_signal('closed')

def listen_to_temperature_sensor():
    global temperature_sensor_state
    #print(GPIO.input(12))
    #if temperature_sensor_state != GPIO.input(12):
    #temperature_sensor_state = GPIO.input(12)
    #temp_level = GPIO.input(12)
    #temp_volts = ADC.ConvertVolts(temp_level,2)
    #temp       = ADC.ConvertTemp(temp_level,2)
    #print(temp)

def turn_on_led_when_button_pressed():
    if GPIO.input(11):
        GPIO.output(22, True)
        GPIO.output(18, False)
    else:
        GPIO.output(18, True)
        GPIO.output(22, False)

def main():
    GPIO.output(22, True)
    GPIO.output(18, True)
    GPIO.output(16, False)

    try:
        while True:
            try:
                listen_to_push_button()
                listen_to_temperature_sensor()
            except Exception as e:
                print(e)
            time.sleep(1/20)
    except KeyboardInterrupt:
        GPIO.output(22, True)
        GPIO.output(18, True)
        GPIO.output(16, True)
        GPIO.cleanup()

if __name__ == '__main__':
   main()


                                   