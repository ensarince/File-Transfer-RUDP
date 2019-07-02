
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        int port = 50001;
        udpSERVER server = new udpSERVER();
        udpCLIENT client = new udpCLIENT();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(server);
        executorService.submit(client);
    }
}