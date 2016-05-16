import java.io.IOException;

public class Producer extends Thread {
    public BoundedBuffer buffer;

    public Producer(BoundedBuffer buffer){
        this.buffer=buffer;
    }

    public void run(){
        try{
            while (true) {
                buffer.insertChunk();
            }
        }catch(IOException e){
            System.out.println("Producer Thread finished");
        }
    }
}
