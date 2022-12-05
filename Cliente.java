import java.net.*;
import java.io.*;
import java.util.Vector;

public class Cliente {
    private Socket socket;
    private DataOutputStream out;
    private BufferedReader in;
    Vector<String> vs = new Vector<String>();
    public Cliente(String address, int port) {
        vs.add("Tesoura"); vs.add("Papel"); vs.add("Pedra"); vs.add("Lagarto"); vs.add("Spock");
        try {
            socket = new Socket(address, port);
            System.out.println("Connectado ao servidor");
            
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(UnknownHostException u) {
            System.out.println(u);
        } catch(IOException i) {
            System.out.println(i);
        }
    }
    
    public Integer send(Integer msg) {
        try {
            out.write(msg);
            if(msg == 5) {
                return 0;
            }
            System.out.print("Enviado: " + vs.elementAt(msg) + ", esperando resposta... ");
            Integer answer = in.read();
            System.out.print("Recebido: " + vs.elementAt(answer) + ".");
            return answer;
        } catch(Exception e) {
            System.out.println(e);
        }
        return null;
    }
    public void StopConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch(IOException i) {
            System.out.println(i);
        }
    }
    
    public static void main(String args[]) {
        Cliente client = new Cliente("192.168.56.1", 40000);
        Integer lastSent = 0, drawCnt = 0, loseCnt = 0, winCnt = 0;
        Vector<Integer> received = new Vector<Integer>(), sent = new Vector<Integer>();
        Vector<String> results = new Vector<String>();
        for(int i = 0; i < 15; i++) {
            Integer ans = client.send(lastSent);
            sent.add(lastSent);
            received.add(ans);
            if(ans == lastSent) {
                System.out.println(" Resultado: Empate.");
                results.add("Empate");
                drawCnt++;
            } else if((ans + 2) % 5 == lastSent || (lastSent + 1) % 5 == ans) {
                System.out.println(" Resultado: Vitória.");
                results.add("Vitória");
                winCnt++;
            } else {
                System.out.println(" Resultado: Derrota.");
                results.add("Derrota");
                loseCnt++;
            }
            System.out.println("Total de vitórias: " + winCnt + ", derrotas: " + loseCnt + ", empates: " + drawCnt + '\n');
            // Se perder, joga o que ganha do que perdeu
            if(results.lastElement() == "Derrota")
                lastSent = (lastSent + 1) % 5;
        }
        for(int i = 0; i < results.size(); i++) {
            System.out.println("Round " + (i + 1) + ", " + client.vs.elementAt(sent.elementAt(i)) + " vs " + client.vs.elementAt(received.elementAt(i)) + ": " + results.elementAt(i));
        }
        System.out.println("\nResumo da partida: " + winCnt + " vitórias, " + loseCnt + " derrotas e " + drawCnt + " empates.");
        if(winCnt > loseCnt)
            System.out.println("Você venceu! :)");
        else if(winCnt < loseCnt)
            System.out.println("Você perdeu! :(");
        else
            System.out.println("Empate! :|");
        client.send(5);
        client.StopConnection();
    }
}