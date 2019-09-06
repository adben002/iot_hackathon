#!/usr/bin/env python
"""
    Utility wrapper module for the aws sdk, provide an easy interface to put data to AWS.
    Requires boto3 (AWS Python SDK) library to be installed. Further it will also need
    aws credentials configured in one way or another, DO NOT add them to a file that is committed to git!

    TODOs:
        Check message delivery
        Handle other data types, e.g. raw data
        Handle failure/retry
        Other sns methods, e.g. batch post
        Improve on the one thread per method
"""
import boto3
import threading

class threadedMessageSender (threading.Thread):

    interesting_buttons='arn:aws:sns:eu-west-1:820731746647:pi-data-receive'
    """
        Threaded message sender, asynchronously posts a message to an sns queue, TODO does this
        really need to be its own class?
    """
    def __init__(self, queueUrl, deviceId, sensorType, message):
        threading.Thread.__init__(self)
        self.queueUrl = queueUrl
        self.client = boto3.client('sns')
        self.message = message
        self.deviceId = deviceId
        self.sensorType = sensorType


    def run(self):
        self.client.publish(TopicArn=self.interesting_buttons,
                            Message = self.message,
                            Subject = 'IOT Message',
                            MessageAttributes={
                                'attr_deviceId': {
                                    'StringValue': self.deviceId,
                                    'DataType': 'String'
                                            },
                                'attr_sensorType': {
                                    'StringValue': self.sensorType,
                                    'DataType': 'String'
                                            }
                                               })


class snsMessager():
    """
        Utility class, contains several fucntions to post to aws.
    """
    def __init__(self, queueUrl, deviceId, sensorType):
        self.queueUrl = queueUrl
        self.deviceId = deviceId
        self.sensorType = sensorType

    def sendMessageAsync(self, message):
        """
            Creates a new thread and posts the given message.
        """
        threadedMessageSender(self.queueUrl, self.deviceId, self.sensorType, message).start()

    def send(self, message):
        """
            'Default send' currently justs uses the normal async message but some logic
            could be added to handle different types of message.
        """
        self.sendMessageAsync(message)


def tester():
    """
        Tester method allowing the module to be called directly from command line
    """
    print("running tester class")
    thread1 = threadedMessageSender('https://sqs.eu-west-1.amazonaws.com/820731746647/iothackathon-mhbrooks', "iot1", "hi there")
    thread1.start()


if __name__ == "__main__":
    tester()