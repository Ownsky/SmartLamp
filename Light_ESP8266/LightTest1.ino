// version to compare with udp broadcast
#define VERSION 0b00000101//"0.1.1b"

#include <ESP8266WiFi.h>
#include <WiFiUdp.h>

//
//int bright, temp;
int L1, L2;

// wifi settings
char* ssid = "Xiaomi_00F8";
char* passwd = "wifi@1055";

// tcp and udp packet buffer
char packetBuffer[255];

// port to receive broadcast
uint16_t udpPort = 30320;

// tcp and udp client
//WiFiClient tcpListener;
WiFiUDP udpListener;

void listenUDP();

void changeLight(int bright, int temp) {
  L1 = 1023 - int((float)bright * temp / 1023 + 0.5);
  L2 = 2046 - bright - L1;
  analogWrite(D7, L1);
  analogWrite(D8, L2);
}
void setUltraMode() {
  analogWrite(D7, 0);
  analogWrite(D8, 0);
}

void setup() {
  // setup
//  pinMode(D0, INPUT);
//  pinMode(D1, INPUT);
//  pinMode(D2, INPUT);
////  pinMode(D6, INPUT);
////  pinMode(D4, INPUT);
//  pinMode(D5, INPUT);
//  pinMode(D6, INPUT);
  pinMode(D7, OUTPUT);
  pinMode(D8, OUTPUT);
  digitalWrite(D7, LOW);
  digitalWrite(D8, LOW);
  Serial.begin(74880);

  // connect WiFi
  WiFi.begin(ssid, passwd);
  Serial.print("Connecting WiFi");
  int wifiTimeout = 10000;
  while (WiFi.status() != WL_CONNECTED && wifiTimeout > 0) {
    delay(500);
    Serial.print(".");
    wifiTimeout -= 500;
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  // start udp listener to receive broadcast from server
  udpListener.begin(udpPort);

  // set bright and temperature
//  bright = 1023;
//  temp = 511;
  //changeLight(1023, 511);
  setUltraMode();
}

//void checkUdp();
//void connectTcp(IPAddress ipAddr, int port);
//void buttonControl();

void loop() {
  // put your main code here, to run repeatedly:
  //buttonControl();
//  L1 = 1023 - int((float)bright * temp / 1023 + 0.5);
//  L2 = 2046 - bright - L1;
//  analogWrite(D7, L1);
//  analogWrite(D8, L2);
//  if (!tcpListener.connected()) checkUdp();
//  else listenTcp();
  listenUDP();

  //Serial.print(L1);
  //Serial.print(" ");
  //Serial.println(L2);
  delay(5);
}

//void listenUDP() {
//  
//}
//
//void buttonControl() {
//  bright += (bright < 1023) ? digitalRead(D1) : 0;
//  bright -= (bright > 0) ? digitalRead(D2) : 0;
//  temp += (temp < 1023) ? digitalRead(D5) : 0;
//  temp -= (temp > 0) ? digitalRead(D6) : 0;
//}

void listenUDP() {
  
  int packetSize = udpListener.parsePacket();
  if (packetSize) {
    Serial.print("Received packet of size ");
    Serial.println(packetSize);
    Serial.print("From ");
    IPAddress remoteIp = udpListener.remoteIP();
    Serial.print(remoteIp);
    Serial.print(", port ");
    Serial.println(udpListener.remotePort());

    // read the packet into packetBufffer
    int len = udpListener.read(packetBuffer, 255);
    if (len > 0) {
      packetBuffer[len] = 0;
    }
//    Serial.println("Contents:");
//    Serial.println(packetBuffer);

//    // seprate & check the packet
//    String packetString = packetBuffer;
//    int cmaIndex1 = packetString.indexOf(',');
//    int cmaIndex2 = packetString.indexOf(',', cmaIndex1 + 1);
//    String checkWord = packetString.substring(0, cmaIndex1),
//           versionWord = packetString.substring(cmaIndex1 + 1, cmaIndex2),
//           portWord = packetString.substring(cmaIndex2 + 1, packetString.length());
//    Serial.println(checkWord);
//    Serial.println(versionWord);
//    Serial.println(portWord);
//    if (checkWord == "smartPlatform" && versionWord == VERSION) {
//      int port = portWord.toInt();
//      if (port > 0 && port < 65535) {
//        Serial.println("Check Success. Trying to connect to TCP server.");
//        connectTcp(remoteIp, port);
//      }
//    }
    switch(packetBuffer[0]) {
      case 0:
        // TODO:
        break;
      case 1:
      {
        int bright1 = packetBuffer[1] * 256 + packetBuffer[2];
        int temp1 = packetBuffer[3] * 256 + packetBuffer[4];
        if (bright1 > 1023 || bright1 < 0 || temp1 > 1023 || temp1 < 0) {
          Serial.println("data err");
          Serial.println(bright1);
          Serial.println(temp1);
          Serial.println(packetBuffer);
          break;
        }
        Serial.print(bright1);
        Serial.print(" ");
        Serial.println(temp1);
        changeLight(bright1, temp1);
        break;
      }
      case 2:
        Serial.println("Ultra Mode On");
        setUltraMode();
        break;
      default:
        Serial.println("Unknown msg");
        break; 
    }
  }
}

//void connectTcp(IPAddress ipAddr, int port) {
//
//  if (!tcpListener.connect(ipAddr, port)) {
//    Serial.println("Can't reach servAddr");
//  } else {
//    Serial.println("TCP connected.");
//    udpListener.flush();
//  }
//}
//
//void listenTcp() {
//  // receive tcp packet
//  if (tcpListener.available() == 0)return;
//  String recvstr = tcpListener.readStringUntil('\0');
//  Serial.print(recvstr);
//  //tcpListener.print("hi...~");
//  // packet analysis
//  int cmaIndex = recvstr.indexOf(',');
//  if (cmaIndex < 0) return;
//  String part1 = recvstr.substring(0, cmaIndex);
//  String part2 = recvstr.substring(cmaIndex + 1, recvstr.length());
//  int msg = part2.toInt();
//  if (part1 == "brightadjust") {
//    if (msg >= 0 && msg < 1024) {
//      bright = msg;
//      Serial.println("bright adjusted to " + msg);
//    }
//  } else if (part1 == "tempadjust") {
//    if (msg >= 0 && msg < 1024) {
//      temp = msg;
//      Serial.println("temp adjusted to " + msg);
//    }
//  } else if (part1 == "getbright") {
//    tcpListener.print(bright);
//  } else if (part1 == "gettemp") {
//    tcpListener.print(temp);
//  }
//}

