package ex01;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// Головний клас де запускається весь процес
public class Main {
    private static final int NUM_SECTIONS = 100; // Кількість секцій у лісі, які слід перевірити
    private static final int NUM_BEES = 3; // Кількість бджіл, які здійснюють пошуки

    // Головний метод, який ініціює ліс і бджіл, а також запускає процес пошуку
    public static void main(String[] args) {
        Forest forest = new Forest(NUM_SECTIONS); // Створення лісу з вказаною кількістю секцій
        Bee[] bees = new Bee[NUM_BEES]; // Створення масиву для бджіл

        // Цикл для ініціалізації бджіл та запуску потоків для кожної з них
        for (int i = 0; i < NUM_BEES; i++) {
            bees[i] = new Bee(forest, i); // Ініціалізація бджоли
            new Thread(bees[i]).start(); // Запуск потоку для кожної бджоли
        }
    }
}

// Клас, який представляє ліс і містить метод для пошуку Вінні в секціях
class Forest {
    private final boolean[] sections; // Масив, що представляє секції лісу

    // Атомарна булева змінна використовується для безпечного оновлення стану змінної в мультитредовому середовищі.
    // Атомарні змінні використовують спеціальні атомарні операції для забезпечення того, що декілька потоків може
    // безпечно оновлювати та читати змінну без необхідності в синхронізації.
    // У цьому випадку використовується для відстеження того, чи був знайдений Вінні.
    private final AtomicBoolean winnieFound = new AtomicBoolean(false);

    // Атомарна цілочисельна змінна використовується для безпечного оновлення номера секції, яку слід перевірити наступною.
    // Вона дозволяє вам атомарно оновлювати та отримувати значення, гарантуючи, що всі потоки будуть мати консистентний
    // та правильний погляд на значення змінної, незалежно від того, скільки потоків і коли вони намагаються читати або
    // оновити значення.
    private final AtomicInteger sectionToCheck = new AtomicInteger(0);


    // Конструктор для ініціалізації лісу із визначеною кількістю секцій
    public Forest(int size) {
        this.sections = new boolean[size];
    }

    // Метод для пошуку Вінні в секції, що повертає true, якщо Вінні знайдено або всі секції перевірено
    public boolean searchSection(int beeId) {
        if (winnieFound.get() || sectionToCheck.get() >= sections.length) return true;

        int sectionIndex = sectionToCheck.getAndIncrement(); // Отримання індексу наступної секції для перевірки та інкрементація лічильника

        // Виведення повідомлення про те, яку секцію перевіряє бджола
        System.out.println("Бджола " + beeId + " перевіряє секцію " + sectionIndex);

        // Симуляція часу, необхідного для перевірки секції
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Генерація випадкового числа для визначення, чи був знайдений Вінні в даній секції
        if (Math.random() < 0.05) {
            // В цьому блоку коду ми використовуємо метод compareAndSet для атомарного оновлення змінної winnieFound.
            // Це робиться для того, щоб уникнути ситуації, коли дві або більше бджіл "знаходять" Вінні в один і той же момент часу.
            // Якщо winnieFound ще не встановлено в true (тобто Вінні ще не знайдено),
            // цей метод встановлює його в true і повертає true, інакше повертає false.
            if (winnieFound.compareAndSet(false, true)) {
                System.out.println("Вінні знайдено в секції " + sectionIndex + " бджолою " + beeId);
                return true;
            }

        }

        return false;
    }
}

// Клас, що представляє бджолу та реалізує інтерфейс Runnable для можливості запуску в потоці
class Bee implements Runnable {
    private final Forest forest; // Посилання на об'єкт лісу, де відбувається пошук
    private final int id; // Унікальний ID бджоли

    // Конструктор для ініціалізації бджоли з даним лісом та ID
    public Bee(Forest forest, int id) {
        this.forest = forest;
        this.id = id;
    }

    // Метод, який викликається при запуску потоку та запускає процес пошуку для бджоли
    @Override
    public void run() {
        // Цикл продовжується, поки не буде знайдено Вінні або не буде перевірено всі секції
        while (!forest.searchSection(id)) { }

        // Повідомлення, яке виводиться, коли бджола повертається назад у вулик після завершення пошуку
        System.out.println("Бджола " + id + " повертається до вулика.");
    }
}
