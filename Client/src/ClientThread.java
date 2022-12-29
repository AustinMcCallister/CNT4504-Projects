import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class ClientThread extends Thread {
  private final Socket socket;
  private final byte choice;
  private final ArrayList<Long> responseTimes;

  public ClientThread(Socket socket, byte choice, ArrayList<Long> responseTimes) {
    this.socket = socket;
    this.choice = choice;
    this.responseTimes = responseTimes;
  }

  public void run() {
    try {
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      long startTime = System.currentTimeMillis();
      writer.println(choice);
      String output;
      String response = "";
      while (!Objects.equals(output = reader.readLine(), "--end--")) {
        response = response.concat("Server: " + output + "\n");
      }
      socket.close();
      long responseTime = (System.currentTimeMillis() - startTime);
      responseTimes.add(responseTime);
      response = response.concat("Time elapsed: " + responseTime + "ms\n");
      System.out.println(response);
    }
    catch (IOException e) {
      System.out.println("I/O error: " + e.getMessage());
    }
  }
}
