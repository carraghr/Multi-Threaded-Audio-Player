import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer implements Runnable{

    AudioInputStream songStream;

    int oneSecond;

    SourceDataLine line;

    BoundedBuffer buffer;

    Producer pThread;
    Consumer cThread;

    String fileName;

    Controller controller;

    AudioPlayer(String fileName, Controller controller){

        this.fileName = fileName;
        this.controller = controller;
        new Thread(this).start();
    }

    void changeVolume(float level)throws Exception{
        FloatControl control = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
        System.out.println("    volCtrl.getValue() = " + control.getValue());

        control.setValue(limit(control,level));

    }

    private static float limit(FloatControl control, float level){
        return Math.min(control.getMaximum(), Math.max(control.getMinimum(), level));
    }

    public void stop(){
        try{
            if (songStream != null) {
                songStream.close();
            }if (line != null){
                line.drain();
                line.stop();
                line.close();
            }if (buffer != null) {
                buffer.resume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try{
            String testFile = fileName;
            songStream = AudioSystem.getAudioInputStream(new File(testFile));

            AudioFormat format = songStream.getFormat();
            System.out.println("Audio format: " + format.toString());

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                throw new UnsupportedAudioFileException();
            }

            oneSecond = (int) (format.getChannels() * format.getSampleRate() *
                    format.getSampleSizeInBits() / 8);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);


            buffer = new BoundedBuffer(oneSecond,songStream,line);
            pThread = new Producer(buffer);
            cThread = new Consumer(buffer);

            pThread.start();
            cThread.start();

            pThread.join();
            cThread.join();

        } catch (UnsupportedAudioFileException |
                LineUnavailableException | InterruptedException | IOException e) {
            controller.setMessage("Error reading song file: " + fileName);
            controller.removePlayer();
            e.printStackTrace();
        }
    }

    public void pause() {
        buffer.pause();
    }

    public void resume(){
        buffer.resume();
    }
}
