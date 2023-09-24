package ex02;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static void main(String[] args) {
        // Створюємо черги з обмеженим розміром для зберігання і переносу предметів
        BlockingQueue<Integer> storageQueue = new ArrayBlockingQueue<>(10);
        BlockingQueue<Integer> truckQueue = new ArrayBlockingQueue<>(10);

        // Створюємо та запускаємо потоки для кожного з персонажів
        Thread ivanov = new Thread(new Ivanov(storageQueue));
        Thread petrov = new Thread(new Petrov(storageQueue, truckQueue));
        Thread nechiporchuk = new Thread(new Nechiporchuk(truckQueue));

        ivanov.start();
        petrov.start();
        nechiporchuk.start();
    }
}

class Ivanov implements Runnable {
    private BlockingQueue<Integer> queue; // Черга, в яку Іванов буде додавати предмети

    public Ivanov(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            // Додаємо предмети з значенням від 1 до 50 до черги
            //Можна зробити значення рандомним, але так зручніше бачити коректність роботи програми. Шо, наприклад, Петров
            // не завантажує предмети раніше, ніж Іванов їх дістане. Або, що працюють дійсно кілька паралельних потоків,
            // а не послідовність операцій.
            // Завдяки різним затримкам, можна бачити, що Іванов не чекає, поки інші опрацюють один предмет, а дістає наступний.
            for (int itemValue = 1; itemValue <= 50; itemValue++) {
                System.out.println("Іванов виніс предмет вартістю: " + itemValue);
                queue.put(itemValue);  // додаємо предмет до черги
                Thread.sleep(200);  // імітуємо затримку
            }
            queue.put(-1);  // відправляємо спеціальне значення для індикації завершення (без цього потоки будуть працювати, навіть, коли закінчаться предмети)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // встановлюємо статус переривання
        }
    }
}

class Petrov implements Runnable {
    private BlockingQueue<Integer> storageQueue; // Черга для зберігання предметів, які буде взято Петровим
    private BlockingQueue<Integer> truckQueue;   // Черга для вантажівки, куди Петров буде завантажувати предмети

    public Petrov(BlockingQueue<Integer> storageQueue, BlockingQueue<Integer> truckQueue) {
        this.storageQueue = storageQueue;
        this.truckQueue = truckQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Беремо предмет із черги для зберігання і перевіряємо, чи це сигнал до завершення
                int itemValue = storageQueue.take();
                if (itemValue == -1) {
                    truckQueue.put(-1);  // передаємо сигнал про завершення далі
                    break;
                }
                System.out.println("Петров завантажив предмет вартістю: " + itemValue);
                truckQueue.put(itemValue);  // додаємо предмет до наступної черги
                Thread.sleep(100);  // імітуємо затримку
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // встановлюємо статус переривання
        }
    }
}

class Nechiporchuk implements Runnable {
    private BlockingQueue<Integer> queue;  // Черга, з якої Нечипорчук буде брати предмети для обрахунку їх значення

    public Nechiporchuk(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Беремо предмет із черги і перевіряємо, чи це сигнал до завершення
                int itemValue = queue.take();
                if (itemValue == -1) {
                    break;  // завершуємо роботу при отриманні сигналу про завершення
                }
                System.out.println("Нечипорчук підсумував предмет вартістю: " + itemValue);
                Thread.sleep(300);  // імітуємо затримку
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // встановлюємо статус переривання
        }
    }
}
