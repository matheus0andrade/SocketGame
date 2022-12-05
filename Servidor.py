import decimal as dc
from socket import *
HOST, PORT = "192.168.56.1", 40000

dc.getcontext().prec = 2000

vitCnt, empCnt, derCnt = 0, 0, 0
maxRounds = 2000
# Calcula os 2000 primeiros digitos de e = 2.718...
# O valor que iremos chutar na n-esima rodada é o n-esimo digito de e mod 5
e, p, cnt, rCnt = 1, 1, 2, 1
for i in range(1, 1000):
    p *= i
    e += dc.Decimal(str(1.0))/dc.Decimal(str(p))

meus, dele, resultado = [], [], []
cods = ['Tesoura', 'Papel', 'Pedra', 'Lagarto', 'Spock']
with socket(AF_INET, SOCK_STREAM) as sock:
    sock.bind((HOST, PORT))
    sock.listen()
    ok = True
    while ok:
        conn, addr = sock.accept()
        with conn:
            print('Connected by', addr)
            while True:
                data = conn.recv(1)
                if not data: break
                if(int.from_bytes(data, byteorder='big') == 5):
                    print("\nResumo da partida: %d vitórias, %d derrotas e %d empates" % (vitCnt, derCnt, empCnt))
                    if(vitCnt > derCnt):
                        print("Voc  ê venceu! :)\n")
                    elif(vitCnt < derCnt):
                        print("Você perdeu! :(\n")
                    else:
                        print("Empate! :|\n")
                    # No término de uma partida, reinicia as variáveis
                    rCnt, meus, dele, resultado = 1, [], [], []
                    vitCnt, empCnt, derCnt = 0, 0, 0
                    break
                dele.append(data)
                toTry = (int(str(e)[cnt]) % 5).to_bytes(1, byteorder='big')
                cnt += 1
                if(cnt > 1990):
                    cnt = 2
                meus.append(toTry)
                print("Round " + str(rCnt) + ", " + cods[int.from_bytes(toTry, byteorder='big')] + " vs " + cods[int.from_bytes(data, byteorder='big')] + ": ", end='')
                rCnt += 1
                if(toTry == data):
                    print("Empate.")
                    resultado.append("Empate")
                    empCnt += 1
                elif((int.from_bytes(data, "big") + 2) % 5 == int.from_bytes(toTry, "big") or (int.from_bytes(toTry, "big") + 1) % 5 == int.from_bytes(data, "big")):
                    print("Vitória.")
                    resultado.append("Vitória")
                    vitCnt += 1
                else:
                    print("Derrota.")
                    resultado.append("Derrota")
                    derCnt += 1
                conn.sendall(toTry)
                if(rCnt == maxRounds + 1):
                    print("\nResumo da partida: %d vitórias, %d derrotas e %d empates" % (vitCnt, derCnt, empCnt))
                    if(vitCnt > derCnt):
                        print("Você venceu! :)\n")
                    elif(vitCnt < derCnt):
                        print("Você perdeu! :(\n")
                    else:
                        print("Empate! :|\n")
                    ok = False
                    break
