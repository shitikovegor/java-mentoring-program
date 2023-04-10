import grpc
import message_service_pb2_grpc as pb2_grpc
import message_service_pb2 as pb2


class UnaryClient(object):
    def __init__(self):
        self.host = 'localhost'
        self.server_port = 9090

        self.channel = grpc.insecure_channel(
            '{}:{}'.format(self.host, self.server_port))

        self.stub = pb2_grpc.MessageServiceStub(self.channel)

    def get_url(self, message):
        message = pb2.MessageRequest(message=message)
        print(f'{message}')
        return self.stub.sendMessage(message)


if __name__ == '__main__':
    client = UnaryClient()
    result = client.get_url(message="Ping")
    print(f'{result}')
