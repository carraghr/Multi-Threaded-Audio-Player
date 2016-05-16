
public class Consumer extends Thread{

    public BoundedBuffer buffer;

    public Consumer(BoundedBuffer buffer){
        this.buffer = buffer;
    }

    public void run(){

        try {
            while (true) {
                buffer.removeChunk();
            }
        }catch (Exception e) {
            System.out.println("Consumer Thread Ended");
        }
    }
}
