import java.util.Random;
import javax.swing.Timer;
public class inden implements  enemypro{
    private static final Random rand = new Random();
    // Define roomss
    private static final rooms a = new rooms("chestrooms");
    private static final rooms b = new rooms("enemy1");
    private static final rooms c = new rooms("enemy2");
    private static final rooms d = new rooms("enemy3");
    private static final rooms e = new rooms("enchant");
    
    private static final rooms[] roomsS = {a, b, c , d, e};
    @Override
    public void health (int healthe){ 
        
    }
    @Override
    public void dam(int damg){

    }
    private static final int[] WEIGHTS = {5, 60, 20, 5,10}; // sum = 100%

    public static rooms getRandomrooms() {
        int totalWeight = 0;
        for (int w : WEIGHTS) totalWeight += w;

        int randomValue = rand.nextInt(totalWeight) + 1; // 1 to totalWeight
        int currentSum = 0;

        for (int i = 0; i < roomsS.length; i++) {
            currentSum += WEIGHTS[i];
            if (randomValue <= currentSum) {
                return roomsS[i];
            }
        }

        return roomsS[roomsS.length - 1]; // Fallback (shouldn't reach here)
    }
    public static void pause(){
        try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
    }
    public rooms[] getRooms(){
        return roomsS;
    }
}
