#!/usr/bin/python
# -*- coding: UTF-8 -*-
import paho.mqtt.client as mqtt
import socket
import json
import logging
import threading
import os
import os.path
import time

global mq_client
global client_list

#HOST_MQTT   = "192.168.43.243"
#HOST_TCP    = "192.168.43.243"
HOST_MQTT   = "172.16.0.4"
HOST_TCP    = "172.16.0.4"

PORT_MQTT   = 1883
PORT_TCP    = 8896

# 初始化日志模块
def logger_init():
    global Logger
    Logger = logging.getLogger()
    Logger.setLevel(logging.INFO)  # Log等级总开关
    # 控制台logger
    ch = logging.StreamHandler()
    ch.setLevel(logging.INFO)  # 输出到console的log等级的开关
    formatter = logging.Formatter("%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s")
    ch.setFormatter(formatter)
    Logger.addHandler(ch)

 
# 初始化mqtt连接
def mqtt_init():
    try:
        client = mqtt.Client('mqtt')
        client.connect(HOST_MQTT, PORT_MQTT, 60)
        client.username_pw_set('admin', '1234')
        client.on_connect = mqtt_on_connect
        client.on_message = mqtt_on_message_callback
        return client
    except Exception as e:
        print(e)
        exit(0) 

def mqtt_on_message_callback(client, userdata, message):
    global client_list
    msg=str(message.payload,encoding="utf-8")
    Logger.info("获取消息：{}".format(msg))
    if "*APP:G" in msg:
        for client_socket, client_addr in client_list: 
            client_socket.send(bytes(msg.replace("*APP:","*"),encoding='utf-8'))


def mqtt_on_connect(client, userdata, flags, rc):
    Logger.info("Mqtt 已连接：{}".format(str(rc)))
    client.subscribe(r"mqtt")
    Logger.info("Mqtt 已订阅")

# 通过回调函数来处理mqtt服务
def mqtt_processor():
    global mq_client 
    mq_client= mqtt_init()
    mq_client.loop_forever()
    return

    
# 初始化tcp连接
def tcp_init():
    try:
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.setblocking(False)   # 将socket设置为非阻塞. 在创建socket对象后就进行该操作.
        server.bind((HOST_TCP, PORT_TCP))
        server.listen(100)
        return server
    except Exception as e:
        Logger.critical("Tcp server init failed: {}".format(e))
        exit(0) 
    Logger.info("Tcp server init success.")


# 处理tcp服务
def tcp_processor():
    global mq_client
    global client_list
    server=tcp_init()        
    client_list = []
    while True:
        try:
            connection, addr = server.accept()
            client_list.append((connection, addr))
            Logger.info("connected:{}".format(addr))
           # connection.send(bytes("hello",encoding='utf-8'))

        # accept原本是阻塞的, 等待connect, 设置setblocking(False)后, accept不再阻塞,
        # 它会(不断的轮询)要求必须有connect来连接, 不然就引发BlockingIOError, 所以为了在没有connect时,
        # 我们捕捉这个异常并pass掉.
        except BlockingIOError:
            pass

        for client_socket, client_addr in client_list:
            try:
                client_recv = client_socket.recv(1024)
                if client_recv:
                    Logger.info("receive:{}>>>{}".format(client_addr, client_recv))
                    mq_client.publish("mqtt",str(client_recv,encoding="utf-8"))
                    
                   
                else:
                    client_socket.close()
                    Logger.info("offline:{}".format(client_addr))
                    client_list.remove((client_socket, client_addr))

            except (BlockingIOError, ConnectionResetError):
                pass


def main():
    # 初始化日志模块
    logger_init()
    # 多线程执行任务
    t1 = threading.Thread(target=tcp_processor,args=())
    t2 = threading.Thread(target=mqtt_processor,args=())
    t1.start()
    Logger.info('线程1 已启动')
    t2.start()
    Logger.info('线程2 已启动')


if __name__ == '__main__':
    main()
