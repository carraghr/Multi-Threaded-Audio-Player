import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;

public class BoundedBuffer{

    private int insertSlot, exportSlot, byteSize, occupiedSpaces;

    private final int bufferSize = 10;

    private AudioInputStream songStream;

    private SourceDataLine line;

    boolean dataAvailable, spaceAvailable, ispaused = false;

    byte[][] buffer;

    BoundedBuffer(int byteSize, AudioInputStream songStream, SourceDataLine line){

        dataAvailable = false;
        spaceAvailable = true;

        insertSlot = 0;
        exportSlot = 0;

        occupiedSpaces = 0;

        this.byteSize = byteSize;
        buffer = new byte[bufferSize][byteSize];

        this.songStream = songStream;
        this.line = line;
        this.line.start();
    }

    synchronized void insertChunk() throws IOException {

        while(!spaceAvailable){
            try{
                wait();
            }catch(InterruptedException e){}//do nothing
        }

        byte[] inputBuffer = new byte[byteSize];

        try{
             if(songStream.read(inputBuffer) == -1){
                 throw new  IOException();
             }
        }catch (IOException e){

            buffer[insertSlot] = new byte[0];
            occupiedSpaces++;
            spaceAvailable = (occupiedSpaces < bufferSize);
            dataAvailable = true;
            notifyAll();
            throw new IOException();
        }

        buffer[insertSlot] = inputBuffer;
        insertSlot = (insertSlot + 1) % bufferSize;
        occupiedSpaces++;
        spaceAvailable = (occupiedSpaces < bufferSize);
        dataAvailable = true;

        notify();

    }

    synchronized void removeChunk() throws Exception {

        while(!dataAvailable || ispaused){
            try{
                wait();
            }catch(InterruptedException e){}//do nothing
        }

        byte [] export = buffer[exportSlot];

        if (export.length != 0) {
            line.write(export, 0, export.length);
        }else{
            line.drain();
            line.stop();
            line.close();
            throw new Exception();
        }

        exportSlot = (exportSlot + 1) % 10;
        occupiedSpaces--;
        dataAvailable = (occupiedSpaces > 0);
        spaceAvailable = (occupiedSpaces < bufferSize);

        notify();
    }

    void pause(){
        ispaused = true;
    }

    synchronized public void resume() {
        ispaused = false;
        notifyAll();
    }

}
